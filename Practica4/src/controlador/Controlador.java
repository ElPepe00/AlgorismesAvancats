package controlador;

import modelo.IProgresListener;
import modelo.Modelo;
import vista.Vista;

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

    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        inicialitzarControladors();
    }

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

    private void iniciarProcesCompressio() {
        modelo.reiniciarCancelacio(); // Vital per si venim d'una cancel·lació anterior
        
        File arxiuOriginal = modelo.getFitxerActual();
        
        // --- 1. LÒGICA D'AUTOGUARDAT ---
        File directoriPare = arxiuOriginal.getParentFile();
        File carpetaDesti = new File(directoriPare, "comprimits");
        
        if (!carpetaDesti.exists()) {
            carpetaDesti.mkdirs(); 
        }
        
        String nomOriginal = arxiuOriginal.getName();
        String nomSenseExtensio = nomOriginal;
        int ultimPunt = nomOriginal.lastIndexOf('.');
        if (ultimPunt > 0) {
            nomSenseExtensio = nomOriginal.substring(0, ultimPunt);
        }
        
        File fitxerDesti = new File(carpetaDesti, nomSenseExtensio + ".huff");
        this.fitxerDestiGenerat = fitxerDesti; // Ho guardem per si l'usuari fa "Guardar com..."

        // --- 2. PREPARACIÓ DE LA INTERFÍCIE ---
        vista.setProcessant(true);
        vista.setEstat("Analitzant freqüències...", new Color(52, 152, 219));
        vista.netejarTaula();

        // El receptor del Walkie-Talkie
        IProgresListener listenerProgres = (percentatge, tempsRestant) -> {
            SwingUtilities.invokeLater(() -> vista.actualitzarProgres(percentatge, tempsRestant));
        };

        // --- 3. EXECUCIÓ EN FIL SECUNDARI ---
        new Thread(() -> {
            try {
                long tempsInici = System.currentTimeMillis();

                // Fase d'anàlisi
                modelo.analitzarFitxer();
                
                if (modelo.isCancelat()) throw new Exception("Operació cancel·lada per l'usuari.");
                
                SwingUtilities.invokeLater(() -> vista.mostrarArbreHuffman(modelo.getArrelArbre()));
                SwingUtilities.invokeLater(() -> vista.setEstat("Comprimint i escrivint a disc...", new Color(52, 152, 219)));
                
                // Fase de compressió (passem el destí i el listener)
                long pesNou = modelo.comprimir(fitxerDesti, listenerProgres);
                
                long tempsFi = System.currentTimeMillis();
                long tempsTotal = tempsFi - tempsInici;

                long pesOriginal = arxiuOriginal.length();
                final double taxaCompressio = (pesOriginal > 0) 
                        ? (1.0 - ((double) pesNou / pesOriginal)) * 100.0 
                        : 0.0;
                final double longMitjana = modelo.calcularLongitudMitjana();

                SwingUtilities.invokeLater(() -> {
                    omplirTaulaVista();
                    vista.mostrarEstadistiques(taxaCompressio, tempsTotal, longMitjana);
                    vista.setEstat("Completat! Guardat a: comprimits/" + fitxerDesti.getName(), new Color(39, 174, 96));
                    vista.actualitzarProgres(100, "00:00");
                    vista.setProcessant(false);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    if (modelo.isCancelat()) {
                        vista.setEstat("Acció cancel·lada.", Color.RED);
                    } else {
                        vista.setEstat("Error crític: " + ex.getMessage(), Color.RED);
                        ex.printStackTrace();
                    }
                    vista.actualitzarProgres(0, "--:--");
                    vista.setProcessant(false);
                });
            }
        }).start();
    }

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

    private void omplirTaulaVista() {
        Map<Integer, String> codis = modelo.getCodisHuffman();
        long[] frequencies = modelo.getFrequencies();

        if (codis == null || frequencies == null) return;

        for (Map.Entry<Integer, String> entrada : codis.entrySet()) {
            int byteValor = entrada.getKey();
            String simbolLlegible;
            
            if (byteValor >= 32 && byteValor <= 126) {
                simbolLlegible = "'" + (char) byteValor + "'"; 
            } else if (byteValor == 10) {
                simbolLlegible = "[LF] Salt Línia";
            } else if (byteValor == 13) {
                simbolLlegible = "[CR] Retorn Carro";
            } else if (byteValor == 9) {
                simbolLlegible = "[TAB] Tabulació";
            } else {
                simbolLlegible = "0x" + String.format("%02X", byteValor);
            }

            vista.afegirFilaTaula(simbolLlegible, (int) frequencies[byteValor], entrada.getValue());
        }
    }
    private void iniciarProcesDescompressio() {

        modelo.reiniciarCancelacio(); 

        File arxiuOrigen = modelo.getFitxerActual();

        if (arxiuOrigen == null) {
            vista.setEstat("Cap arxiu seleccionat.", Color.RED);
            return;
        }

        
        File carpetaComprimits = arxiuOrigen.getParentFile();

        
        File carpetaPare = carpetaComprimits.getParentFile();

        
        File carpetaDesti = new File(carpetaPare, "decomprimits");

        
        if (!carpetaDesti.exists()) {
            carpetaDesti.mkdirs();
        }

        // Nombre del archivo de salida
        String nom = arxiuOrigen.getName();
        String nomSortida;

        if (nom.endsWith(".huff")) {
            nomSortida = nom.substring(0, nom.length() - 5);
        } else {
            nomSortida = nom + "_descomprimit";
        }

        // Archivo destino final
        File desti = new File(carpetaDesti, nomSortida);

        // Preparar interfaz
        vista.setProcessant(true);
        vista.setEstat("Descomprimint...", new Color(52, 152, 219));
        vista.actualitzarProgres(0, "--:--");

        // Listener de progreso (igual que en compresión)
        IProgresListener listenerProgres = (percentatge, tempsRestant) -> {
            SwingUtilities.invokeLater(() -> vista.actualitzarProgres(percentatge, tempsRestant));
        };

        new Thread(() -> {
            try {
                modelo.descomprimir(arxiuOrigen, desti, listenerProgres);

                SwingUtilities.invokeLater(() -> {
                    vista.setEstat("Descompressió completada: " + desti.getAbsolutePath(),
                            new Color(39, 174, 96));
                    vista.actualitzarProgres(100, "00:00");
                    vista.setProcessant(false);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    if (modelo.isCancelat()) {
                        vista.setEstat("Acció cancel·lada.", Color.RED);
                    } else {
                        vista.setEstat("Error en descompressió: " + ex.getMessage(), Color.RED);
                        ex.printStackTrace();
                    }
                    vista.actualitzarProgres(0, "--:--");
                    vista.setProcessant(false);
                });
            }
        }).start();
    }
}