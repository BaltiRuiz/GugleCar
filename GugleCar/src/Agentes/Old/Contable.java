package Agentes.Old;

import Agentes.Agente;
import Agentes.Data.MensajeRecarga;
import Agentes.Old.MensajeSolicitudAviso;
import Agentes.Utilities.JsonWrapper;
import Agentes.Utilities.SQLWrapper;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.*;

/**
 * Clase encargada de organizar la gestión de las recargas de batería
 *
 * @deprecated
 * @author Daniel Soto del Ojo: implementar esqueleto de la clase
 * @author Manuel Lafuente Aranda: implementación base del agente
 * @author Baltasar Ruiz: modificaciones puntuales
 */
public class Contable extends Agente {

    private JsonWrapper interprete_json;
    private Queue<Solicitante> solicitantes;
    private SQLWrapper sql;
    private int moviles;
    private static final int LIMITE_BATERIA = 400;

    /**
     * Método constructor del agente
     *
     * @author Daniel Soto del Ojo
     * @author Manuel Lafuente Aranda: adición instanciación del intérprete JSON
     *
     * @param aid id del agente
     * @throws Exception si aid no válido
     */
    public Contable(AgentID aid, String map) throws Exception {
        super(aid);
        interprete_json = JsonWrapper.getInstance();
        solicitantes = new PriorityQueue<>();
        sql = new SQLWrapper(map);
    }

    /**
     * Método encargado de inicializar al agente
     *
     * @author Daniel Soto del Ojo: implementación base
     * @author Manuel Lafuente Aranda: corrección de implementación base
     */
    @Override
    public void init() {
        imprimir(this.getName(), " ha comenzado");
    }

    /**
     * Método encargado de controlar la lógica del agente
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @author Manuel Lafuente Aranda: implementación de la lógica del agente
     */
    @Override
    public void execute() {
        boolean salir = false;
        ACLMessage inbox;
        while(!salir){
            inbox = recibirMensaje();
            switch (inbox.getPerformativeInt()){
                case ACLMessage.QUERY_IF:
                    decisionRecarga(inbox);
                    break;

                case ACLMessage.REQUEST_WHEN:
                    encolar(inbox);
                    break;

                case ACLMessage.INFORM:
                    salir = true;
                    break;
                case ACLMessage.REQUEST:
                    --moviles;
                    solicitantes.remove(new Solicitante(inbox.getSender()));
                    avanzarCola();
                    break;
            }
        }
    }

    /**
     * Método encargado de controlar la finalización del agente
     *
     * @author Daniel Soto del Ojo
     */
    @Override
    public void finalize() {
        imprimir(this.getName(), " ha finalizado");
    }

    /**
     * Método en el que se decide si un agente solicitante tiene permitido solicitar una recarga de batería o no
     *
     * @author Manuel Lafuente Aranda
     *
     * @param inbox ACLMessage que contiene la información necesaria para la decisión
     */
    private void decisionRecarga(ACLMessage inbox) {

        MensajeRecarga datos = interprete_json.interpretarMensajeRecarga(inbox.getContent());

        imprimir("Datos interpretados en Contable: [bateria: " + datos.bateria_agente + " | energia: " + datos.energia_restante
                + " | distancia: " + datos.distancia + " | gasto: " + datos.gasto + " | exploracion: " + datos.exploracion + "]");

        if(datos.energia_restante == 0) {

            enviarMensaje(inbox.getSender(), "MUERETE", ACLMessage.DISCONFIRM);
            while(!solicitantes.isEmpty()){
                enviarMensaje((solicitantes.poll()).getID(), "MUERETE", ACLMessage.DISCONFIRM);
            }

        } else if(datos.energia_restante > LIMITE_BATERIA) {

            enviarMensaje(inbox.getSender(), "RESPUESTA_RECARGA", ACLMessage.CONFIRM);

        } else if (datos.exploracion){

            enviarMensaje(inbox.getSender(), "RESPUESTA_RECARGA", ACLMessage.CONFIRM);

        } else {

            moviles = sql.obtenerMoviles().size();
            enviarMensaje(inbox.getSender(), "RESPUESTA_RECARGA", ACLMessage.DISCONFIRM);

        }

    }

    /**
     * Método con el que se almacena en la cola con prioridad a un solicitante de aviso de recarga
     *
     * @author Manuel Lafuente Aranda
     *
     * @param inbox ACLMessage que contiene la información necesaria para el encolado
     */
    private void encolar(ACLMessage inbox){

        MensajeSolicitudAviso datos = interprete_json.interpretarMensajeSolicitudAviso(inbox.getContent());
        AgentID id = inbox.getSender();

        if(datos.energia != 0){
            Solicitante s = new Solicitante(id, datos.distancia, datos.bateria, datos.gasto);
            solicitantes.add(s);

            enviarMensaje(id, "OK", ACLMessage.AGREE);
        }
        else {
            enviarMensaje(id, "MUERETE", ACLMessage.REFUSE);
        }

        avanzarCola();
    }

    private boolean avanzarCola() {
        if(moviles == solicitantes.size()) {
            enviarMensaje(solicitantes.poll().getID(), "RESPUESTA_RECARGA", ACLMessage.INFORM);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clase con la que se facilita la gestión de los agentes que solicitan ser avisados cuando se les deniega la carga de batería
     *
     * @author Manuel Lafuente Aranda
     */
    private static class Solicitante implements Comparable<Solicitante>{
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

        public int getGasto() {
            return gasto;
        }

        @Override
        public boolean equals(Object other) {
            Solicitante s = (Solicitante)other;
            return id.equals(s.getID());
        }
        @Override
        public int compareTo(Solicitante solicitante) {
            // TODO arreglar la formula.
            if(this.distancia < solicitante.getDistancia())
                return 0; //Es más prioritario
            else if(this.gasto < solicitante.getGasto())
                return 0; //Es más prioritario
            else
                return 1;
        }
    }

    /**
     * Clase con la que se gestiona la cola con prioridad en la que se almacenan las solicitudes de los agentes para ser avisados
     * cuando puedan pedir una recarga al servidor
     *
     * @author Manuel Lafuente Aranda
     */
    private class ListaEspera{
        private Queue<Solicitante> cola;

        /**
         * Método constructor de la cola priorizada
         *
         * @author Manuel Lafuente Aranda
         */
        public ListaEspera(){
            cola = new PriorityQueue<Solicitante>();
        }

        /**
         * Método con el que se añade a un nuevo solicitante a la cola priorizada
         *
         * @author Manuel Lafuente Aranda
         *
         * @param solicitante Solicitante a añadir en la cola priorizada
         */
        public void nuevaSolicitud(Solicitante solicitante){
            cola.add(solicitante);
        }

        /**
         * Método con el que se elimina de la cola priorizada al primer solicitante
         *
         * @author Manuel Lafuente Aranda
         *
         */
        public void atenderSolicitud() {
            if(!cola.isEmpty())
                cola.remove();
        }

        /**
         * Método para obtener al primer solicitante de la cola priorizada
         * @return
         */
        public Solicitante getPrimero(){
            return cola.element();
        }

        /**
         * Método que informa de si la cola priorizada está vacía o no
         *
         * @author Manuel Lafuente Aranda
         *
         * @return boolean que indica si la cola está vacía o no
         */
        public boolean isEmpty(){
            return cola.isEmpty();
        }

        public int size() {
            return cola.size();
        }
    }
}
