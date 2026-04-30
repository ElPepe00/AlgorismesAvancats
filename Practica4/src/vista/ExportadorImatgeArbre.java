package vista;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ExportadorImatgeArbre {

    private static final String CARPETA_DESTI = "imatgesArbres";

    public static void guardarPanellComImatge(PanelArbreHuffman panel, String nomBase) {
        // 1. Assegurar que la carpeta existeix
        File carpeta = new File(CARPETA_DESTI);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        // 2. Obtenir les dimensions reals de l'arbre (encara que estigui en un scroll)
        Dimension mida = panel.getPreferredSize();
        if (mida.width <= 0 || mida.height <= 0) return;

        // 3. Crear la imatge en memòria
        BufferedImage imatge = new BufferedImage(mida.width, mida.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imatge.createGraphics();

        // 4. Dibuixar el panell dins la imatge
        panel.setSize(mida); // Ens assegurem que el panell té la mida correcta per dibuixar
        panel.printAll(g2d);
        g2d.dispose();

        // 5. Guardar al disc
        try {
            File fitxerSortida = new File(carpeta, nomBase + ".png");
            ImageIO.write(imatge, "png", fitxerSortida);
            System.out.println("Arbre guardat a: " + fitxerSortida.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error guardant l'imatge: " + e.getMessage());
        }
    }
}
