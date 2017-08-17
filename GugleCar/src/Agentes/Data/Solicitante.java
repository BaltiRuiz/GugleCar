package Agentes.Data;

/**
 * Created by Eila on 18/01/2017.
 */

import es.upv.dsic.gti_ia.core.AgentID;

/**
 /**
 * Clase con la que se facilita la gestión de los agentes que solicitan ser avisados cuando se les deniega la carga de batería
 *
 * @author Manuel Lafuente Aranda
 * @author Elías Méndez García
 */
public class Solicitante implements Comparable<Solicitante>{
    private AgentID id;
    private int distancia, bateria, gasto;


    public Solicitante(AgentID id) {
        this.id = id;
    }

    /**
     * Método constructor de un solicitante
     *
     * @author Manuel Lafuente Aranda
     *
     * @param identificador Identificador del agente
     * @param dist Distancia a la que se encuentra el solicitante del objetivo
     * @param bat Batería restante del solicitante
     */
    public Solicitante(AgentID identificador, int dist, int bat, int gast){
        id = identificador;
        distancia = dist;
        bateria = bat;
        gasto = gast;
    }

    /**
     * Método que devuelve el identificador del solicitante
     *
     * @author Manuel Lafuente Aranda
     *
     * @return AgentID con el identificador del agente solicitante
     */
    public AgentID getID(){
        return id;
    }

    /**
     * Método que devuelve la distancia del solicitante al objetivo
     *
     * @author Manuel Lafuente Aranda
     *
     * @return int con la distancia del solicitante al objetivo
     */
    public int getDistancia(){
        return distancia;
    }

    /**
     * Método que devuelve la batería restante del solicitante
     *
     * @author Manuel Lafuente Aranda
     *
     * @return int con la batería restante del solicitante
     */
    public int getBateria(){
        return bateria;
    }

    /**
     * Método que devuelve el gasto del solicitante.
     *
     * @author Manuel Lafuente Aranda
     *
     * @return int con el gasto
     */
    public int getGasto() {
        return gasto;
    }

    /**
     * Método para comparar dos Solicitantes.
     * @param other otro solicitante.
     * @return true si es el mismo solicitante, false en otro caso
     */
    @Override
    public boolean equals(Object other) {
        Solicitante s = (Solicitante)other;
        return id.equals(s.getID());
    }

    /**
     * Método para comparar dos solicitantes según su gasto  y distancia.
     * @param solicitante otro solicitante
     * @return 0 si tiene mayor prioridad, 1 en otro caso.
     */
    @Override
    public int compareTo(Solicitante solicitante) {

        if(this.distancia < solicitante.getDistancia())
            return 0; //Es más prioritario
        else if(this.gasto < solicitante.getGasto())
            return 0; //Es más prioritario
        else
            return 1;
    }

}
