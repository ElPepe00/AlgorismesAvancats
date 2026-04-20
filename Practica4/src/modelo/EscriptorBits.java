package modelo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Josep Oliver i Hugo Valls
 * @name EscriptorBits
 * * Classe encarregada d'acumular bits individuals i escriure'ls
 * al disc dur agrupats en blocs de 8 (1 byte).
 */
public class EscriptorBits implements AutoCloseable {

    private final BufferedOutputStream bos;
    private int bufferBits; // Aquí anirem acumulant els bits
    private int bitsOcupats; // Comptador de quants bits tenim actualment (0-8)

    public EscriptorBits(File fitxerDesti) throws IOException {
        // Obro un flux d'escriptura estàndard (que només entén bytes)
        this.bos = new BufferedOutputStream(new FileOutputStream(fitxerDesti, true)); // 'true' per fer append si cal
        this.bufferBits = 0;
        this.bitsOcupats = 0;
    }

    /**
     * Rep un String amb zeros i uns (ex: "101") i els escriu bit a bit.
     */
    public void escriureCodiString(String codiBinari) throws IOException {
        for (char c : codiBinari.toCharArray()) {
            escriureBit(c == '1' ? 1 : 0);
        }
    }

    /**
     * Empaqueta un únic bit al buffer. Si el buffer s'omple (8 bits), l'aboca al disc.
     */
    private void escriureBit(int bit) throws IOException {
        // Desplacem el buffer actual 1 posició cap a l'esquerra i afegim el nou bit al final
        bufferBits = (bufferBits << 1) | bit;
        bitsOcupats++;

        // Quan arribem a 8 bits, ja tenim 1 byte sencer per escriure al disc
        if (bitsOcupats == 8) {
            bos.write(bufferBits);
            bufferBits = 0;  // Reiniciem el contenidor
            bitsOcupats = 0; // Reiniciem el comptador
        }
    }

    /**
     * Tanca el flux. Si han quedat bits "penjats" (ex: només en teníem 3 de 8),
     * els empeny cap a l'esquerra omplint la resta amb zeros per fer el darrer byte.
     */
    @Override
    public void close() throws IOException {
        if (bitsOcupats > 0) {
            bufferBits = bufferBits << (8 - bitsOcupats); // Desplaçament final
            bos.write(bufferBits);
        }
        bos.close();
    }
}
