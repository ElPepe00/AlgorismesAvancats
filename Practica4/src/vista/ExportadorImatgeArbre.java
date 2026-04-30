package vista;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ExportadorImatgeArbre {

    private static final String CARPETA_DESTI = "imatgesArbres";

    /** Captura el contingut del panell de l'arbre i el guarda com a fitxer PNG. */
    public static void guardarPanellComImatge(PanelArbreHuffman panel, String nomBase) {
        File carpeta = new File(CARPETA_DESTI);
        if (!carpeta.exists()) carpeta.mkdirs();

        Dimension mida = panel.getPreferredSize();
        if (mida.width <= 0 || mida.height <= 0) return;

        BufferedImage imatge = new BufferedImage(mida.width, mida.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imatge.createGraphics();

        panel.setSize(mida);
        panel.printAll(g2d);
        g2d.dispose();

        try {
            File fitxerSortida = new File(carpeta, nomBase + ".png");
            ImageIO.write(imatge, "png", fitxerSortida);
            System.out.println("Arbre guardat a: " + fitxerSortida.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error guardant l'imatge: " + e.getMessage());
        }
    }
}
