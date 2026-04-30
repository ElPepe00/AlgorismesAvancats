package modelo.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Classe encarregada de llegir bits individuals des d'un InputStream.
 */
public class LectorBits {

    private final InputStream in;
    private int bufferActual;
    private int bitsRestants;

    /** Prepara el lector amb un flux d'entrada. */
    public LectorBits(InputStream in) {
        this.in = in;
        this.bufferActual = 0;
        this.bitsRestants = 0;
    }

    /** Llegeix un bit (0 o 1) del fitxer. Retorna -1 si s'ha arribat al final. */
    public int llegirBit() throws IOException {
        if (bitsRestants == 0) {
            bufferActual = in.read();
            if (bufferActual == -1) return -1;
            bitsRestants = 8;
        }

        bitsRestants--;
        return (bufferActual >> bitsRestants) & 1;
    }
}