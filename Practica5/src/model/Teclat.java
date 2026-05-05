package model;

import java.awt.List;
import java.util.ArrayList;

/**
 * @author Josep Oliver y Hugo Valls
 * @date 5 may 2026
 * @name Teclat
 */
public class Teclat {
    
    private final int amplada; // Columnes (W)
    private final int alcada;  // Files (H)
    private final int maxNombre;
    
    /**
     * Mètode Constructor
     * @param amplada num de columnes
     * @param alcada num de files
     */
    public Teclat(int amplada, int alcada) {
        this.amplada = amplada;
        this.alcada = alcada;
        this.maxNombre = amplada * alcada;
    }
    
    /**
     * Metode que retorna una llista amb tots els moviments vàlids
     * @param darrerNombre Darrer valor introduit al teclat
     * @return retorna un ArrayList amb els moviments possibles
     */
    public ArrayList<Integer> getMovimentsValids(int darrerNombre) {
        ArrayList<Integer> movimentsValids = new ArrayList<>();
        
        //Al inici tots els moviments son valids (afegim tots els movs)
        if (darrerNombre == 0) {
            for (int i = 1; i <= maxNombre; i++) {
                movimentsValids.add(i);
            }
            
            return movimentsValids;
        }
        
        // Calculem les coordenades (fila i columna) del darrer nombre.
        // Com que la numeració va de 'maxNombre' fins a 1 d'esquerra a dreta:
        int indexDarrer = maxNombre - darrerNombre;
        int filaDarrer = indexDarrer / amplada;
        int colDarrer = indexDarrer % amplada;
        
        // Comprovem la resta de números del 1 al màxim
        for (int i = 1; i <= maxNombre; i++) {
            if (i == darrerNombre) {
                continue; // No es pot jugar el mateix número
            }

            int indexActual = maxNombre - i;
            int filaActual = indexActual / amplada;
            int colActual = indexActual % amplada;

            // Si comparteixen fila o columna (moviment de torre d'escacs)
            if (filaActual == filaDarrer || colActual == colDarrer) {
                movimentsValids.add(i);
            }
        }
        
        return movimentsValids;
    }
    
    /**
     * Mètode GET
     * @return la variable maxNombre
     */
    public int getMaxNombre() {
        return maxNombre;
    }
}
