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
    private static final String CARPETA_PROVES = "fitxersProva";

    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        inicialitzarControladors();
    }

    private void inicialitzarControladors() {
        vista.setControladorCarregar(e -> obrirCercadorFitxers());
        vista.setControladorComprimir(e -> iniciarProcesCompressio());

        // Listener per al pròxim pas: La Descompressió
        vista.setControladorDescomprimir(e -> {
            vista.setEstat("Funció de descompressió pendent d'implementar...", Color.ORANGE);
        });
    }

    private void obrirCercadorFitxers() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona l'arxiu original");
        fileChooser.setCurrentDirectory(new File(CARPETA_PROVES));

        if (fileChooser.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            File fitxerSeleccionat = fileChooser.getSelectedFile();
            modelo.setFitxerActual(fitxerSeleccionat);
            vista.setFitxerActual(fitxerSeleccionat);
            vista.setEstat("Arxiu carregat: " + fitxerSeleccionat.getName(), new Color(39, 174, 96));
        }
    }

    private void iniciarProcesCompressio() {
        JFileChooser fileChooser = new JFileChooser(new File(CARPETA_PROVES));
        fileChooser.setDialogTitle("Guardar arxiu comprimit (.huff)");

        File arxiuOriginal = modelo.getFitxerActual();

        // --- LÒGICA PER TREURE L'EXTENSIÓ ---
        String nomOriginal = arxiuOriginal.getName();
        String nomSenseExtensio = nomOriginal;

        int ultimPunt = nomOriginal.lastIndexOf('.');
        if (ultimPunt > 0) { // Si hi ha un punt i no és el primer caràcter
            nomSenseExtensio = nomOriginal.substring(0, ultimPunt);
        }

        fileChooser.setSelectedFile(new File(nomSenseExtensio + ".huff"));

        if (fileChooser.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fitxerDesti = fileChooser.getSelectedFile();

        vista.setProcessant(true);
        vista.setEstat("Processant...", new Color(52, 152, 219));
        vista.netejarTaula();

        new Thread(() -> {
            try {
                long tempsInici = System.currentTimeMillis();

                modelo.analitzarFitxer();

                SwingUtilities.invokeLater(() -> vista.mostrarArbreHuffman(modelo.getArrelArbre()));

                // Passem el pes total per a la capçalera (per a la futura descompressió)
                long pesNou = modelo.comprimir(fitxerDesti);

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
                    vista.setEstat("Completat: " + fitxerDesti.getName(), new Color(39, 174, 96));
                    vista.setProcessant(false);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    vista.setEstat("Error: " + ex.getMessage(), Color.RED);
                    vista.setProcessant(false);
                });
                ex.printStackTrace();
            }
        }).start();
    }

    private void omplirTaulaVista() {
        Map<Integer, String> codis = modelo.getCodisHuffman();
        long[] frequencies = modelo.getFrequencies();

        if (codis == null || frequencies == null) {
            return;
        }

        for (Map.Entry<Integer, String> entrada : codis.entrySet()) {
            int byteValor = entrada.getKey();
            String codiBinari = entrada.getValue();
            long freq = frequencies[byteValor];

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
