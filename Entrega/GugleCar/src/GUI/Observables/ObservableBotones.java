package GUI.Observables;

import GUI.Observador;

/**
 * Clase para notificar a los observadores botones de cuando se han terminado de ejecutar todos los pasos.
 * @author Elías Méndez García
 */
public class ObservableBotones extends Observable {

    /**
     * Método para actualizar el estado del observable.
     * @param estado nuevo estado para al que actualizar el observable.
     */
    @Override
    public void setEstado(Object estado) {
        notificarObservadores();
    }

    /**
     * Método para notificar a todos los observadores de que se ha actualizado el observable.
     * @author Elías Méndez García
     */
    @Override
    public void notificarObservadores() {
        for (Observador o : observadores) {
            o.manejarEvento(null);
        }
    }
}
