package model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Josep Oliver y Hugo Valls
 * @date 18 may 2026
 * @name Teclat
 */
public class Teclat {
    
    private final int amplada; 
    private final int alcada;  
    private final int maxNombre;
    private final int[][] grid;
    
    // Posicions dels nombres en fila/columna per a cerca O(1)
    private final int[] files;
    private final int[] columnes;

    public Teclat(int amplada, int alcada) {
        this(amplada, alcada, false);
    }
    
    public Teclat(int amplada, int alcada, boolean aleatori) {
        this.amplada = amplada;
        this.alcada = alcada;
        this.maxNombre = amplada * alcada;
        this.grid = new int[alcada][amplada];
        this.files = new int[maxNombre + 1];
        this.columnes = new int[maxNombre + 1];
        
        inicialitzarTeclat(aleatori);
    }
    
    // Omple la graella física amb els botons
    private void inicialitzarTeclat(boolean aleatori) {
        if (!aleatori) {
            // Teclat estàndard (Bottom-up, left-to-right)
            for (int r = 0; r < alcada; r++) {
                for (int c = 0; c < amplada; c++) {
                    int val = (alcada - 1 - r) * amplada + c + 1;
                    grid[r][c] = val;
                    files[val] = r;
                    columnes[val] = c;
                }
            }
        } else {
            // Teclat barrejat
            ArrayList<Integer> valors = new ArrayList<>();
            for (int i = 1; i <= maxNombre; i++) {
                valors.add(i);
            }
            Collections.shuffle(valors);
            
            int idx = 0;
            for (int r = 0; r < alcada; r++) {
                for (int c = 0; c < amplada; c++) {
                    int val = valors.get(idx++);
                    grid[r][c] = val;
                    files[val] = r;
                    columnes[val] = c;
                }
            }
        }
    }
    
    // Retorna els moviments vàlids (mateixa fila o columna)
    public ArrayList<Integer> getMovimentsValids(int darrerNombre) {
        ArrayList<Integer> movimentsValids = new ArrayList<>();
        
        if (darrerNombre == 0) {
            for (int i = 1; i <= maxNombre; i++) {
                movimentsValids.add(i);
            }
            return movimentsValids;
        }
        
        int filaDarrer = files[darrerNombre];
        int colDarrer = columnes[darrerNombre];
        
        for (int r = 0; r < alcada; r++) {
            for (int c = 0; c < amplada; c++) {
                int val = grid[r][c];
                if (val == darrerNombre) continue;
                
                if (r == filaDarrer || c == colDarrer) {
                    movimentsValids.add(val);
                }
            }
        }
        
        return movimentsValids;
    }
    
    public int[][] getGrid() {
        return grid;
    }
    
    public int getAmplada() {
        return amplada;
    }
    
    public int getAlcada() {
        return alcada;
    }
    
    public int getMaxNombre() {
        return maxNombre;
    }
}
