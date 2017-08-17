/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Observables;

import GUI.Observador;

import java.util.ArrayList;

/**
 * Clase abstracta para que después la implementen los observables concretos.
 * @author Daniel Soto del Ojo crear el esqueleto.
 * @author Elías Méndez García corregir una excepción NullPointer.
 * @author Eila Gómez Hidalgo implementar la inclusión de observadores.
 */
public abstract class Observable {
    // Vector de observadores
    protected ArrayList<Observador> observadores;

    /**
     * Contructor de los observables.
     * @author Elías Méndez García
     */
    public Observable() {
        observadores = new ArrayList<>();
    }

    /**
     * Método para incluir un observador al observable.
     * @author Eila Gómez Hidalgo
     */
    public void incluirObservador(Observador ob) {
        observadores.add(ob);
    }

    /**
     * Método abstracto para actualizar el estado del observable.
     * @author Eila Gómez Hidalgo
     * @param estado nuevo estado para al que actualizar el observable.
     */
    public abstract void setEstado(Object estado);

    /**
     * Método para notificar a todos los observadores de que se ha actualizado el observable.
     * @author Elías Méndez García
     */
    public abstract void notificarObservadores();

}
