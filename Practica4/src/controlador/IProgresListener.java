package controlador;

/**
 * Interfície per comunicar el progrés des del Model fins a la Vista.
 */
public interface IProgresListener {
    // Aquest mètode s'executarà cada vegada que l'IO vulgui avisar d'un canvi
    void actualitzar(int percentatge, String tempsRestant);
}