package modelo.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Classe de baix nivell per empaquetar cadenes de bits ('0' i '1') en Bytes reals
 * i escriure'ls al disc dur.
 */
public class EscriptorBits {
    
    private OutputStream out;
    private int bufferActual;
    private int bitsAcumulats;

    /** Prepara l'escriptor amb un flux de sortida. */
    public EscriptorBits(OutputStream out) {
        this.out = out;
        this.bufferActual = 0;
        this.bitsAcumulats = 0;
    }

    /** Rep una cadena de '0' i '1' i els escriu com a bytes al disc. */
    public void escriureBits(String codiBits) throws IOException {
        for (int i = 0; i < codiBits.length(); i++) {
            char bit = codiBits.charAt(i);
            
            bufferActual = (bufferActual << 1);
            if (bit == '1') {
                bufferActual = bufferActual | 1;
            }
            
            bitsAcumulats++;

            if (bitsAcumulats == 8) {
                out.write(bufferActual);
                bufferActual = 0;
                bitsAcumulats = 0;
            }
        }
    }

    /** Escriu els bits sobrants que no han arribat a completar un byte. */
    public void buidar() throws IOException {
        if (bitsAcumulats > 0) {
            bufferActual = bufferActual << (8 - bitsAcumulats);
            out.write(bufferActual);
            bufferActual = 0;
            bitsAcumulats = 0;
        }
    }
}
