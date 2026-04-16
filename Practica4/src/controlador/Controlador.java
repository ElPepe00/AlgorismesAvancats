package controlador;

import modelo.Modelo;
import vista.Vista;

import javax.swing.*;
import java.awt.Color;
import java.io.File;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 16 abr 2026
 * @name Controlador
 */
public class Controlador {

    private Vista vista;
    private Modelo modelo;
    private File fitxerActual;

    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        inicialitzarControladors();
    }

    private void inicialitzarControladors() {
        // Enllacem els botons de la vista amb els mètodes del controlador
        vista.setControladorCarregar(e -> obrirCercadorFitxers());

        // Deixem preparats els altres botons per a les properes fases
        vista.setControladorComprimir(e -> comprimirFitxer());
        vista.setControladorDescomprimir(e -> descomprimirFitxer());
        vista.setControladorGuardar(e -> guardarFitxer());
    }

    /**
     * Obre un JFileChooser perquè l'usuari seleccioni l'arxiu a processar.
     */
    private void obrirCercadorFitxers() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona l'arxiu per comprimir/descomprimir");

        // Obre el selector a la carpeta arrel del projecte per comoditat
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        // Mostra la finestra de diàleg
        int seleccio = fileChooser.showOpenDialog(vista);

        if (seleccio == JFileChooser.APPROVE_OPTION) {
            fitxerActual = fileChooser.getSelectedFile();

            // 1. Actualitzem la Vista (això canviarà l'etiqueta i activarà els botons)
            vista.setFitxerActual(fitxerActual);
            vista.setEstat("Arxiu '" + fitxerActual.getName() + "' carregat amb èxit. Llest per processar.", new Color(39, 174, 96)); // Verd

            // 2. Passem l'arxiu al Model
            modelo.setFitxerActual(fitxerActual);

        } else {
            vista.setEstat("S'ha cancel·lat la selecció de l'arxiu.", new Color(192, 57, 43)); // Vermell
        }
    }

    private void comprimirFitxer() {
        vista.setEstat("Iniciant anàlisi i compressió...", new Color(52, 152, 219)); // Blau

        // 1. Bloquegem la interfície perquè no es pugui tocar res més
        vista.setProcessant(true);

        // Aquí és on, en el futur, cridarem al Model (en un fil secundari):
        // modelo.iniciarCompressio(...);
        // --- SIMULACIÓ TEMPORAL ---
        // Simulem que triga 2 segons i després desbloqueja la UI
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            javax.swing.SwingUtilities.invokeLater(() -> {
                vista.setEstat("Compressió finalitzada.", new Color(39, 174, 96)); // Verd
                vista.setProcessant(false); // 2. Alliberem la interfície
            });
        }).start();
    }

    private void descomprimirFitxer() {
        vista.setEstat("Iniciant descompressió...", new Color(230, 126, 34)); // Taronja
        // Aquí cridarem al model per llegir l'arxiu .huff i reconstruir-lo
    }

    private void guardarFitxer() {
        // Aquí implementarem un altre JFileChooser (showSaveDialog) per desar el resultat
    }
}
