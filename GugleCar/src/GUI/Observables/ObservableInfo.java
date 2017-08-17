/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Observables;

import GUI.Observador;

/**
 * Clase para notificar de nueva información recibida de la batería o envío de mensajes
 * @author Daniel Soto del Ojo Creación del esqueleto
 * @author Elías Méndez García Implementación de los métodos
 */
public class ObservableInfo extends Observable {
    // variable que contiene información sobre los agentes
    private String info;

    /**
     * Método para actualizar el estado del observable.
     * @author Elías Méndez García
     * @param estado contiene información extraída de los agentes
     */
    @Override
    public void setEstado(Object estado) {

        this.info = (String) estado;

        notificarObservadores();
    }

    /**
     * Método para notificar a todos los observadores de que se ha actualizado el observable.
     * @author Elías Méndez García
     */
    @Override
    public void notificarObservadores() {
        for (Observador o : observadores) {
            o.manejarEvento(info);
        }
    }
}
