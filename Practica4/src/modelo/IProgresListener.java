package modelo;

/**
 * Interfície per comunicar el progrés des del Model fins a la Vista.
 */
public interface IProgresListener {
    /** Actualitza el percentatge i el temps a la interfície. */
    void actualitzar(int percentatge, String tempsRestant);
}