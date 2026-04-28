package modelo;

import java.io.IOException;
import java.io.InputStream;

/**
 * Classe encarregada de llegir bits individuals des d'un InputStream.
 */
public class LectorBits {

    private final InputStream in;
    private int bufferActual;
    private int bitsRestants;

    public LectorBits(InputStream in) {
        this.in = in;
        this.bufferActual = 0;
        this.bitsRestants = 0;
    }

    /**
     * Llegeix un únic bit del flux.
     * @return 0 o 1, o -1 si s'ha acabat el flux
     */
    public int llegirBit() throws IOException {

        // Si no queden bits al buffer, llegim un nou byte
        if (bitsRestants == 0) {
            bufferActual = in.read();

            if (bufferActual == -1) {
                return -1; // Final del fitxer
            }

            bitsRestants = 8;
        }

        // Extreiem el bit més significatiu (esquerra)
        bitsRestants--;
        return (bufferActual >> bitsRestants) & 1;
    }
}