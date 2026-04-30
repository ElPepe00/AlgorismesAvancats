package modelo;

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

    public EscriptorBits(OutputStream out) {
        this.out = out;
        this.bufferActual = 0;
        this.bitsAcumulats = 0;
    }

    /**
     * Rep un String compost de "0" i "1" (Ex: "1011") i els va empaquetant.
     */
    public void escriureBits(String codiBits) throws IOException {
        for (int i = 0; i < codiBits.length(); i++) {
            char bit = codiBits.charAt(i);
            
            // 1. Desplacem tots els bits actuals una posició cap a l'esquerra
            // per fer espai (un forat) per al nou bit.
            bufferActual = (bufferActual << 1);
            
            // 2. Si el bit és '1', activem l'últim bit del nostre buffer
            if (bit == '1') {
                bufferActual = bufferActual | 1; // Operació lògica OR
            }
            
            bitsAcumulats++;

            // 3. Quan l'ouera s'omple (8 bits), la guardem al disc dur i la buidem
            if (bitsAcumulats == 8) {
                out.write(bufferActual);
                bufferActual = 0;
                bitsAcumulats = 0;
            }
        }
    }

    /**
     * S'ha de cridar sempre al final de l'arxiu. 
     * Si han sobrat bits que no arriben a 8, s'omplen amb zeros a la dreta (Padding)
     * perquè es puguin escriure al disc.
     */
    public void buidar() throws IOException {
        if (bitsAcumulats > 0) {
            // Desplacem cap a l'esquerra els espais que falten fins a 8
            // Això afegeix zeros automàticament a la dreta.
            bufferActual = bufferActual << (8 - bitsAcumulats);
            out.write(bufferActual);
            
            // Reiniciem
            bufferActual = 0;
            bitsAcumulats = 0;
        }
    }
}
