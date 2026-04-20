package controlador;

import modelo.Modelo;
import vista.Vista;

import javax.swing.*;
import java.awt.Color;
import java.io.File;
import java.util.Map;

/**
 * @author Josep Oliver i Hugo Valls
 * @name Controlador
 */
public class Controlador {

    private final Vista vista;
    private final Modelo modelo;

    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        inicialitzarControladors();
    }

    /**
     * Enllaça els botons de la Vista amb les funcions del Controlador.
     */
    private void inicialitzarControladors() {
        vista.setControladorCarregar(e -> obrirCercadorFitxers());
        vista.setControladorComprimir(e -> iniciarProcesCompressio());
        
        // Deixem el listener preparat per a la fase de descompressió
        vista.setControladorDescomprimir(e -> {
            vista.setEstat("Funció de descompressió en desenvolupament...", Color.ORANGE);
        });
    }

    /**
     * Gestiona la càrrega del fitxer original mitjançant JFileChooser.
     */
    private void obrirCercadorFitxers() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona l'arxiu original");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        if (fileChooser.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            File fitxerSeleccionat = fileChooser.getSelectedFile();
            modelo.setFitxerActual(fitxerSeleccionat);
            vista.setFitxerActual(fitxerSeleccionat);
            vista.setEstat("Arxiu carregat: " + fitxerSeleccionat.getName(), new Color(39, 174, 96));
        }
    }

    /**
     * Executa el procés de compressió.
     * Inclou la gestió de la interfície i el càlcul de les estadístiques.
     */
    private void iniciarProcesCompressio() {
        // 1. Selecció de la ruta de destí
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar arxiu comprimit (.huff)");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        
        File arxiuOriginal = modelo.getFitxerActual();
        fileChooser.setSelectedFile(new File(arxiuOriginal.getName() + ".huff"));

        if (fileChooser.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return; 
        }
        
        File fitxerDesti = fileChooser.getSelectedFile();

        // 2. Preparació de la UI
        vista.setProcessant(true);
        vista.setEstat("Comprimint... si us plau, espera.", new Color(52, 152, 219));
        vista.netejarTaula();

        // 3. Fil d'execució secundari per no congelar la Vista
        new Thread(() -> {
            try {
                long tempsInici = System.currentTimeMillis();

                // FASE 1: Anàlisi (Frequencies, Arbre i Codis)
                modelo.analitzarFitxer();
                
                // FASE 2: Escriptura física del fitxer .huff (a nivell de bits)
                long pesNou = modelo.comprimir(fitxerDesti);
                
                long tempsFi = System.currentTimeMillis();
                long tempsTotal = tempsFi - tempsInici;

                // FASE 3: Càlcul de mètriques de rendiment
                long pesOriginal = arxiuOriginal.length();
                double taxaCompressio = 0.0;
                
                if (pesOriginal > 0) {
                    // La taxa representa el percentatge d'espai estalviat
                    taxaCompressio = (1.0 - ((double) pesNou / pesOriginal)) * 100.0;
                }
                
                double longMitjana = modelo.calcularLongitudMitjana();
                final double tc = taxaCompressio;
                
                // FASE 4: Sincronització amb el fil de la UI (EDT)
                SwingUtilities.invokeLater(() -> {
                    omplirTaulaVista();
                    vista.mostrarEstadistiques(tc, tempsTotal, longMitjana);
                    vista.setEstat("Procés finalitzat: " + fitxerDesti.getName(), new Color(39, 174, 96));
                    vista.setProcessant(false);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    vista.setEstat("Error crític: " + ex.getMessage(), Color.RED);
                    vista.setProcessant(false);
                });
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Bolca el diccionari de Huffman i les freqüències a la taula de la Vista.
     */
    private void omplirTaulaVista() {
        Map<Integer, String> codis = modelo.getCodisHuffman();
        long[] frequencies = modelo.getFrequencies();

        if (codis == null || frequencies == null) return;

        for (Map.Entry<Integer, String> entrada : codis.entrySet()) {
            int byteValor = entrada.getKey();
            String codiBinari = entrada.getValue();
            long freq = frequencies[byteValor];

            // Formatem el símbol per fer-lo entenedor a la taula
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

            vista.afegirFilaTaula(simbolLlegible, (int) freq, codiBinari);
        }
    }
}