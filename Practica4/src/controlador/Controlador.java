package controlador;

import modelo.IProgresListener;
import modelo.Modelo;
import vista.Vista;
import vista.ExportadorImatgeArbre;

import javax.swing.*;
import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

/**
 * @author Josep Oliver i Hugo Valls
 * @name Controlador
 */
public class Controlador {

    private final Vista vista;
    private final Modelo modelo;
    
    private static final String CARPETA_PROVES = "fitxersProva";
    private File fitxerDestiGenerat; // Guardem la referència per al botó "Guardar com..."

    /** Constructor que vincula la vista amb el model i prepara els botons. */
    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        inicialitzarControladors();
    }

    /** Assigna les accions als botons de la vista. */
    private void inicialitzarControladors() {
        vista.setControladorCarregar(e -> obrirCercadorFitxers());
        vista.setControladorComprimir(e -> iniciarProcesCompressio());
        vista.setControladorAturar(e -> {
            modelo.cancelarOperacio();
            vista.setEstat("Aturant operació de forma segura...", Color.RED);
        });
        vista.setControladorGuardar(e -> guardarCom());
        vista.setControladorDescomprimir(e -> iniciarProcesDescompressio());
    }

    /** Obre un diàleg per triar el fitxer a tractar. */
    private void obrirCercadorFitxers() {
        JFileChooser fileChooser = new JFileChooser(new File(CARPETA_PROVES));
        fileChooser.setDialogTitle("Selecciona l'arxiu original");

        if (fileChooser.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            File fitxerSeleccionat = fileChooser.getSelectedFile();
            modelo.setFitxerActual(fitxerSeleccionat);
            vista.setFitxerActual(fitxerSeleccionat);
            vista.setEstat("Arxiu carregat: " + fitxerSeleccionat.getName(), new Color(39, 174, 96));
            vista.actualitzarProgres(0, "--:--");
        }
    }

    /** Orquestra el procés de compressió en un fil secundari. */
    private void iniciarProcesCompressio() {
        modelo.reiniciarCancelacio();
        File arxiuOriginal = modelo.getFitxerActual();
        File directoriPare = arxiuOriginal.getParentFile();
        File carpetaDesti = new File(directoriPare, "comprimits");
        if (!carpetaDesti.exists()) carpetaDesti.mkdirs(); 
        
        String nomOriginal = arxiuOriginal.getName();
        String tipus = vista.isFibonacciSeleccionat() ? "-fibonacci" : "-binary";
        
        // Generem el nom mantenint l'extensió: fitxer.txt -> fitxer-binary.txt.huff
        String nouNom;
        int ultimPunt = nomOriginal.lastIndexOf('.');
        if (ultimPunt > 0) {
            nouNom = nomOriginal.substring(0, ultimPunt) + tipus + nomOriginal.substring(ultimPunt) + ".huff";
        } else {
            nouNom = nomOriginal + tipus + ".huff";
        }
        
        File fitxerDesti = new File(carpetaDesti, nouNom);
        this.fitxerDestiGenerat = fitxerDesti;

        vista.setProcessant(true);
        vista.setEstat("Analitzant freqüències...", new Color(52, 152, 219));
        vista.netejarTaula();

        IProgresListener listenerProgres = (percentatge, tempsRestant) -> {
            SwingUtilities.invokeLater(() -> vista.actualitzarProgres(percentatge, tempsRestant));
        };

        new Thread(() -> {
            try {
                long tempsInici = System.currentTimeMillis();
                modelo.setUsarFibonacci(vista.isFibonacciSeleccionat());
                modelo.analitzarFitxer();
                
                if (modelo.isCancelat()) throw new Exception("Operació cancel·lada per l'usuari.");
                
                SwingUtilities.invokeLater(() -> {
                    vista.mostrarArbreHuffman(modelo.getArrelArbre());
                    ExportadorImatgeArbre.guardarPanellComImatge(vista.getPanelArbre(), "arbre_" + arxiuOriginal.getName());
                });
                
                SwingUtilities.invokeLater(() -> vista.setEstat("Comprimint i escrivint a disc...", new Color(52, 152, 219)));
                long pesNou = modelo.comprimir(fitxerDesti, listenerProgres);
                
                long tempsFi = System.currentTimeMillis();
                long tempsTotal = tempsFi - tempsInici;
                long pesOriginal = arxiuOriginal.length();
                double taxaCompressio = (pesOriginal > 0) ? (1.0 - ((double) pesNou / pesOriginal)) * 100.0 : 0.0;
                double longMitjana = modelo.calcularLongitudMitjana();

                SwingUtilities.invokeLater(() -> {
                    omplirTaulaVista();
                    vista.mostrarEstadistiques(taxaCompressio, tempsTotal, longMitjana);
                    vista.setEstat("Completat! Guardat a: comprimits/" + fitxerDesti.getName(), new Color(39, 174, 96));
                    vista.actualitzarProgres(100, "00:00");
                    vista.setProcessant(false);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    vista.setEstat(modelo.isCancelat() ? "Acció cancel·lada." : "Error: " + ex.getMessage(), Color.RED);
                    vista.actualitzarProgres(0, "--:--");
                    vista.setProcessant(false);
                });
            }
        }).start();
    }

    /** Permet a l'usuari triar on copiar el fitxer comprimit generat. */
    private void guardarCom() {
        if (fitxerDestiGenerat == null || !fitxerDestiGenerat.exists()) return;

        JFileChooser fileChooser = new JFileChooser(new File(CARPETA_PROVES));
        fileChooser.setDialogTitle("Exportar fitxer comprimit (.huff)");
        fileChooser.setSelectedFile(new File(fitxerDestiGenerat.getName()));

        if (fileChooser.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            File destiFinal = fileChooser.getSelectedFile();
            try {
                Files.copy(fitxerDestiGenerat.toPath(), destiFinal.toPath(), StandardCopyOption.REPLACE_EXISTING);
                vista.setEstat("Fitxer exportat correctament a: " + destiFinal.getName(), new Color(39, 174, 96));
            } catch (Exception ex) {
                vista.setEstat("Error en exportar l'arxiu: " + ex.getMessage(), Color.RED);
            }
        }
    }

    /** Tradueix els codis de Huffman del model a la taula visual de la vista. */
    private void omplirTaulaVista() {
        Map<Integer, String> codis = modelo.getCodisHuffman();
        long[] frequencies = modelo.getFrequencies();
        if (codis == null || frequencies == null) return;

        for (Map.Entry<Integer, String> entrada : codis.entrySet()) {
            int byteValor = entrada.getKey();
            String simbolLlegible;
            if (byteValor >= 32 && byteValor <= 126) simbolLlegible = "'" + (char) byteValor + "'"; 
            else if (byteValor == 10) simbolLlegible = "[LF] Salt Línia";
            else if (byteValor == 13) simbolLlegible = "[CR] Retorn Carro";
            else if (byteValor == 9) simbolLlegible = "[TAB] Tabulació";
            else simbolLlegible = "0x" + String.format("%02X", byteValor);

            vista.afegirFilaTaula(simbolLlegible, (int) frequencies[byteValor], entrada.getValue());
        }
    }
    /** Orquestra el procés de descompressió en un fil secundari. */
    private void iniciarProcesDescompressio() {
        modelo.reiniciarCancelacio(); 
        File arxiuOrigen = modelo.getFitxerActual();
        if (arxiuOrigen == null) return;

        // Si el fitxer està dins 'comprimits', pugem un nivell per crear 'decomprimits' al costat
        File pare = arxiuOrigen.getParentFile();
        File base = (pare != null && pare.getName().equals("comprimits")) ? pare.getParentFile() : pare;
        
        File carpetaDesti = new File(base, "decomprimits");
        if (!carpetaDesti.exists()) carpetaDesti.mkdirs();

        String nom = arxiuOrigen.getName();
        // Simplement eliminem el ".huff" per recuperar l'extensió original (que hem guardat en comprimir)
        String nomSortida = nom.endsWith(".huff") ? nom.substring(0, nom.length() - 5) : nom + "_descomprimit";
        File desti = new File(carpetaDesti, nomSortida);

        vista.setProcessant(true);
        vista.setEstat("Descomprimint...", new Color(52, 152, 219));
        IProgresListener listenerProgres = (percentatge, tempsRestant) -> {
            SwingUtilities.invokeLater(() -> vista.actualitzarProgres(percentatge, tempsRestant));
        };

        new Thread(() -> {
            try {
                modelo.descomprimir(arxiuOrigen, desti, listenerProgres);
                SwingUtilities.invokeLater(() -> {
                    vista.setEstat("Completat! Fitxer a: decomprimits/" + desti.getName(), new Color(39, 174, 96));
                    vista.actualitzarProgres(100, "00:00");
                    vista.setProcessant(false);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    vista.setEstat(modelo.isCancelat() ? "Acció cancel·lada." : "Error: " + ex.getMessage(), Color.RED);
                    vista.actualitzarProgres(0, "--:--");
                    vista.setProcessant(false);
                });
            }
        }).start();
    }
}