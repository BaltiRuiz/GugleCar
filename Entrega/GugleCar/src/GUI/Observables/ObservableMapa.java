/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Observables;

import GUI.Observador;

/**
 * Clase para notificar de nueva información recibida del Radar o del Scanner
 * @author Daniel Soto del Ojo Creación del esqueleto
 * @author Elías Méndez García Implementación del método para cambiar el estado
 * @author Eila Gómez Hidalgo Implementación del método para notificar observadores
 */
public class ObservableMapa extends Observable {

    /**
     * Método para actualizar el estado del observable.
     * @author Elías Méndez García
     * @param estado
     */
    @Override
    public void setEstado(Object estado) {
        notificarObservadores();
    }

    /**
     * Método para notificar a todos los observadores de que se ha actualizado el observable.
     * @author Eila Gómez Hidalgo
     */
    @Override
    public void notificarObservadores() {
        for (Observador o : observadores) {
            o.manejarEvento(null);
        }
    }


}
