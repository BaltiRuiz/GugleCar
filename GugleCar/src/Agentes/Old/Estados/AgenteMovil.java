package Agentes.Old.Estados;

import Agentes.Agente;
import Agentes.Old.Contable;
import Agentes.Data.*;
import Agentes.Old.Conflictos;
import Agentes.Utilities.JsonWrapper;
import Agentes.Utilities.Posiciones;
import Agentes.Utilities.SQLWrapper;

import GUI.Observables.Observable;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

import java.util.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * Clase que contiene la definición del agente que será empleado para recorrer el mundo
 *
 * @deprecated
 * @author Daniel Soto del Ojo: Implementar esqueleto de la clase
 */
public class AgenteMovil extends Agente {
    private boolean first;

    private String map;

    private ACLMessage msg;
    private String conversation_id;
    private String in_reply_to;

    private Capacidades capacidades;
    private Percepcion percepcion;

    private Estado estado = null;

    private Map<PairInt, PairInt> mapa;

    private Comando direccion;
    private int fase_comando;

    private int pasos;
    private int pasos_en_solucion;
    private boolean pause;
    private boolean durmiendo;
    private boolean exit;
    private boolean explorando;
    private Comando siguiente_movimiento;
    private int contador_movimientos_terminados;

    private final float PORCENTAJE_MINIMO;
    private final int UMBRAL_BATERIA;

    private SQLWrapper bd;
    private JsonWrapper json_wrapper;

    private Observable observable_mapa;
    private Observable observable_mensajes;
    private Observable observable_estado;
    private Observable observable_botones;

    private int confirmaciones_actualizacion_mapa;
    private int confirmaciones_conflicto_movimiento;
    private Conflictos conflictos;

    private boolean mapa_explorado;
    private boolean avisar_fin_exploracion;

    // Array que contiene las columnas que han sido exploradas (utilizado por los métodos de exploración)
    private ArrayList<Integer> columnas_exploradas = new ArrayList<>();

    // Otros agentes móviles a los que enviar mensajes.
    private List<AgentName> moviles;

    // Pila de movimientos de esquive
    private Stack<Comando> pila;

    /**
     * Método constructor del agente
     *
     * @author Daniel Soto del Ojo: implementación base
     * @author Elías Méndez García: asiganción de valores y modificaciones
     *
     * @param name contiene el nombre del agente
     * @param first contiene si es el primer agente que ha sido creado
     * @param porcentaje contiene el porcentaje de mapa que explorará
     * @param umbral valor a partir del cual se considera que la batería está en estado crítico
     * @param ob_map instancia del objeto encargada de notificar a interfaz sobre el estado del mapa
     * @param ob_men instancia del objeto encargada de notificar a interfaz las acciones del agente
     * @param ob_estado instancia del objeto encargada de notificar a interfaz sobre el estado del agente
     * @param map nombre del mapa que va a ser cargado
     */
    public AgenteMovil(AgentID name, boolean first, float porcentaje, int umbral, Observable ob_map, Observable ob_men, Observable ob_estado,  Observable ob_botones,  String map) throws Exception {
        super(name);
        this.first = first;

        this.map = map;
        this.siguiente_movimiento = null;

        fase_comando = 1;
        exit = false;
        explorando = true;
        durmiendo = true;

        PORCENTAJE_MINIMO = porcentaje;
        UMBRAL_BATERIA = umbral;

        this.json_wrapper = JsonWrapper.getInstance();
        this.bd = new SQLWrapper(map);
        this.percepcion = new Percepcion();
        this.percepcion.goal = false;
        this.siguiente_movimiento = Comando.DORMIR;

        this.observable_mapa = ob_map;
        this.observable_mensajes = ob_men;
        this.observable_estado = ob_estado;
        this.observable_botones = ob_botones;

        this.mapa_explorado = false;
        this.avisar_fin_exploracion = false;

        this.pila = new Stack<>();

        this.conflictos = new Conflictos();

        pasos_en_solucion = 0;
    }

    /**
     * Método encargado de inicializar al agente
     *
     * @author Daniel Soto del Ojo: implementación base
     */
    @Override
    public void init() {
        // Obtener todos los agentes móviles menos yo mismo

        this.moviles = AgentName.moviles(this.getName());

        imprimir(this.getName(), " inicializado");
    }

    /**
     * Método encargado de controlar la lógica del agente
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @author Baltasar Ruiz: implementación comunicaciones base, implementación de comunicación con Contable
     * @author Manuel Lafuente Aranda: implementación de comunicación con Contable y entre los agentes a la hora de preguntar por un movimiento
     */
    @Override
    public void execute() {


        Map<Estado, Queue<ACLMessage>> cola_mensajes = inflateCola();
        try {
    
            if (this.first) {
    
                enviarMensajeSubscribe();
                enviarMensajeCheckin();
    
                while (!capacidades.vuela) {
                    enviarMensajeSubscribe();
                    enviarMensajeCheckin();
                }
    
                // Reenviar dicho conversation_id al resto de agentes móviles
                divulgarMensaje(json_wrapper.generateConversationID(conversation_id), ACLMessage.INFORM);
    
            } else {
                // Si no es el primer agente móvil en ser creado, simplemente recibe el mensaje del primer compañero
                // con el conversation_id del servidor
                msg = recibirMensaje();
                conversation_id = json_wrapper.interpretarConversationID(msg.getContent());
    
                // Enviar mensaje con el comando CHECKIN al servidor
                enviarMensajeCheckin();
            }

            bd.insertMovil(this.getName(), capacidades.gasto);

            // Enviar mensaje de solicitud de información del entorno
            enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), "", ACLMessage.QUERY_REF, conversation_id, in_reply_to);
            confirmaciones_actualizacion_mapa = moviles.size();

            estado = Estado.RECEPCION_INFORMACION;

            // BALTI: BUCLE SWITCH - CASE (TODAVÍA NO ESTÁ TERMINADO EL TRASPASO, EL CÓDIGO ORIGINAL LO HE GUARDADO
            // EN UN DOCUMENTO PROPIO)

            Queue<ACLMessage> cola_estado = null;
            Queue<ACLMessage> cola_mensaje = null;

            while (!exit) {
                msg = recibirMensaje();
                Estado estado_msg = Estado.toEstado(msg);

                switch (estado_msg) {
                    // En caso de recibir el mensaje que avisa al agente que debe actualizar su mapa
                    case AVISO_ACTUALIZACION:
                        if (explorando)
                            this.actualizarMiMapaPases();
                        else
                            this.actualizarMiMapaCostes();

                        enviarMensaje(msg.getSender(), "CONFIRMAR_ACTUALIZAR_MAPA", ACLMessage.INFORM);

                        break;

                    default:
                        cola_estado = cola_mensajes.get(estado);
                        cola_mensaje = cola_mensajes.get(estado_msg);

                        if (estado != estado_msg) {
                            cola_mensaje.add(msg);
                        } else if(estado == estado_msg) {
                            cola_estado.add(msg);
                        }

                        if(!cola_estado.isEmpty()) {
                            msg = cola_estado.poll();
                            while(msg != null) {
                                logica();
                                msg = cola_estado.poll();
                            }
                        }
                        break;
                }


            }

            bd.setEstadoMovil(this.getName(), SQLWrapper.BLOQUED);
            divulgarMuerte();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void logica() {
        switch (estado) {
            // En caso de recibir el mensaje con la percepción del mundo
            case RECEPCION_INFORMACION:
                percepcion = json_wrapper.interpretPercepcion(msg.getContent());
                bd.updateMovil(this.getName(), percepcion.posicion, percepcion.combustible);

                in_reply_to = msg.getReplyWith();

                imprimir("Percepcion interpretada como " + percepcion.combustible + " de combustible y "
                        + percepcion.posicion.first + "|" + percepcion.posicion.second + " de posicion");

                // Actualizar el estado del agente en la interfaz con su combustible y posición.
                observable_estado.setEstado(json_wrapper.generarJsonObservablePercecpcion(percepcion.combustible, percepcion.posicion));

                // Actualizar mapas en todos los agentes
                realizarActualizaciones();

                // Avisar a la interfaz 3D de que se ha actualizado el mapa.
                observable_mapa.setEstado(json_wrapper.generarJsonObservableMapa(this.getName(), percepcion.posicion));

                estado = Estado.SIGUIENTE_MOVIMIENTO;
                imprimir("Agente " + this.getName() + " finalizado case RECEPCION_INFORMACION");
                break;

            // En caso de recibir un mensaje de confirmación de actualización de mapa realizada
            case SIGUIENTE_MOVIMIENTO:
                confirmaciones_actualizacion_mapa--;

                if (confirmaciones_actualizacion_mapa == 0) {
                    if (percepcion.combustible < UMBRAL_BATERIA) {

                        if (siguiente_movimiento != Comando.DORMIR) {
                            int distance = (explorando) ? 0 : mapa.get(percepcion.posicion).second;
                            bd.setEstadoMovil(this.getName(), SQLWrapper.BLOQUED);
                            enviarMensaje(AgentName.CONTABLE.toAgentId(), json_wrapper.generarMensajeRecarga(percepcion.combustible, percepcion.energy, distance, capacidades.gasto, explorando), ACLMessage.QUERY_IF);
                        }

                    } else {
                        bd.setEstadoMovil(this.getName(), SQLWrapper.WORKING);
                        siguiente_movimiento = siguienteMovimiento();

                        if (siguiente_movimiento != Comando.DORMIR) {
                            coordinarMovimiento();
                            durmiendo = false;
                        }

                        confirmaciones_conflicto_movimiento = moviles.size();
                    }
                }
                break;

            // En caso de recibir el mensaje de respuesta de Contable
            case RESPUESTA_CONTABLE:
                switch (msg.getPerformativeInt()) {
                    case ACLMessage.CONFIRM:
                        siguiente_movimiento = Comando.RECARGAR;
                        break;

                    case ACLMessage.DISCONFIRM:
                        int distance = (explorando) ? 0 : mapa.get(percepcion.posicion).second;
                        enviarMensaje(AgentName.CONTABLE.toAgentId(), json_wrapper.generarMensajeSolicitudAviso(percepcion.combustible, percepcion.energy, distance, capacidades.gasto), ACLMessage.REQUEST_WHEN);
                        break;

                    case ACLMessage.AGREE:
                        siguiente_movimiento = Comando.DORMIR;
                        break;

                    case ACLMessage.REFUSE:
                        exit = true;

                        if (moviles.size() == 0)
                            enviarMensaje(AgentName.CONTABLE.toAgentId(), "TODOS FUERA", ACLMessage.INFORM);
                        break;
                }
                break;

            // En caso de recibir la consulta de un agente sobre si existe o no conflicto a la hora de desplazarse a una casilla
            case RESPUESTA_COMANDO_AGENTES:
                in_reply_to = msg.getReplyWith();

                switch (msg.getPerformativeInt()) {
                    case ACLMessage.INFORM:
                        divulgarMensaje("MOVIMIENTO_REALIZADO", ACLMessage.INFORM);
                        break;

                    case ACLMessage.FAILURE:
                    case ACLMessage.REFUSE:
                        exit = true;

                        if (moviles.size() == 0)
                            enviarMensaje(AgentName.CONTABLE.toAgentId(), "TODOS FUERA", ACLMessage.INFORM);
                        break;
                }
                break;

            case RESOLVER_CONFLICTO_MOVIMIENTO:
                PairInt siguiente_movimiento_otro_agente = json_wrapper.interpretarDesplazamiento(msg.getContent());
                PairInt mi_siguiente_movimiento = calculaPosicionDestino(percepcion.posicion, siguiente_movimiento);

                if (percepcion.posicion.equals(siguiente_movimiento_otro_agente))
                    enviarMensaje(msg.getSender(), "CONFLICTO_POSICION_OCUPADA", ACLMessage.CONFIRM);

                else if (mi_siguiente_movimiento.equals(siguiente_movimiento_otro_agente))
                    enviarMensaje(msg.getSender(), "CONFLICTO_MISMA_POSICION", ACLMessage.CONFIRM);

                else
                    enviarMensaje(msg.getSender(), "NO_CONFLICTO_MOVIMIENTO", ACLMessage.DISCONFIRM);

                break;

            case ACTUAR_CONFLICTOS:
                conflictos.add(msg.getSender().name, msg.getContent());
                confirmaciones_conflicto_movimiento--;

                if (confirmaciones_conflicto_movimiento == 0) {
                    if (conflictos.only("NO_CONFLICTO_MOVIMIENTO")) {
                        enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(siguiente_movimiento), ACLMessage.REQUEST, conversation_id, in_reply_to);
                    } else if (conflictos.contains("CONFLICTO_POSICION_OCUPADA")) {
                        siguiente_movimiento = Comando.DORMIR;
                    } else {

                        Map<String, Integer> prioridad_agentes = bd.obtenerMovilesPrioridad();
                        List<String> agentes_conflicto = conflictos.get("CONFLICTO_MISMA_POSICION");

                        int mi_prioridad = prioridad_agentes.get(this.getName());
                        boolean tengo_prioridad = true;

                        for (String n : agentes_conflicto) {
                            if (mi_prioridad > prioridad_agentes.get(n)) {
                                tengo_prioridad = false;
                            }
                        }

                        if (tengo_prioridad) {
                            enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(siguiente_movimiento), ACLMessage.REQUEST, conversation_id, in_reply_to);
                        } else {
                            siguiente_movimiento = Comando.DORMIR;
                        }
                    }


                    if (siguiente_movimiento == Comando.DORMIR) {
                        durmiendo = true;
                    }
                    contador_movimientos_terminados = moviles.size();
                    conflictos.clear();
                }

                break;

            case MOVIMIENTO_REALIZADO:
                --contador_movimientos_terminados;

                if (durmiendo) {
                    divulgarMensaje("MOVIMIENTO_REALIZADO", ACLMessage.INFORM);
                    durmiendo = false;
                }

                if (contador_movimientos_terminados == 0) {
                    // Enviar mensaje de solicitud de información del entorno
                    enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), "", ACLMessage.QUERY_REF, conversation_id, in_reply_to);
                    confirmaciones_actualizacion_mapa = moviles.size();
                    durmiendo = true;
                }

                break;

            case PROCESAR_MUERTE:
                --confirmaciones_conflicto_movimiento;
                --contador_movimientos_terminados;
                --confirmaciones_actualizacion_mapa;
                moviles.remove(msg.getSender().name);
                break;

            case IMPRIMIR_TRAZA:
                exit = true;

                enviarMensaje(AgentName.CONTABLE.toAgentId(), "TODOS FUERA", ACLMessage.INFORM);
                imprimeTraza(msg, json_wrapper, map, observable_estado);
                break;

            default:
                break;
        }
    }

    private Map<Estado, Queue<ACLMessage>> inflateCola() {
        Map<Estado, Queue<ACLMessage>> cola = new HashMap<>();
        for(Estado e : Estado.values()) {
            cola.put(e, new LinkedBlockingDeque<>());
        }

        return cola;
    }

    /**
     * Método encargado de controlar la finalización del agente
     *
     * @author Daniel Soto del Ojo
     */
    @Override
    public void finalize(){
        imprimir(this.getName(), " ha finalizado");
    }

    /**
     * Método que comunica a todos los agentes que se va a morir.
     *
     * @author Manuel Lafuente Aranda
     */
    private void divulgarMuerte() {
        divulgarMensaje("MUERTO", ACLMessage.INFORM);
        enviarMensaje(AgentName.CONTABLE.toAgentId(), "MUERTO", ACLMessage.REQUEST);
    }

    /**
     * Método encargado de enviar al servidor el mensaje de subscripción y reenviar el conversation_id al resto de agentes
     *
     * @author Baltasar Ruiz Hernández
     */
    private void enviarMensajeSubscribe() {

        // Enviar mensaje de subscripción

        enviarMensaje(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarSubscribe(map), ACLMessage.SUBSCRIBE);
        msg = recibirMensaje();

        while( !( msg.getPerformativeInt() == ACLMessage.INFORM && msg.getContent().contains("result") ) ) {
            msg = recibirMensaje();
        }

        // Recibir el conversation_id del servidor

        conversation_id = msg.getConversationId();
    }

    /**
     * Método encargado de enviar al servidor el mensaje con el comando CHECKIN de obtención de capacidades
     *
     * @author Baltasar Ruiz Hernández
     */
    private void enviarMensajeCheckin(){
        // Enviar mensaje para hacer checkin y obtener las capacidades
        enviarMensajeConversationID(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(Comando.CHECKIN), ACLMessage.REQUEST, conversation_id);
        msg = recibirMensaje();

        while( !(  msg.getPerformativeInt() == ACLMessage.INFORM  && msg.getContent().contains("capabilities") ) ){
            msg = recibirMensaje();
        }

        in_reply_to = msg.getReplyWith();
        capacidades = json_wrapper.interpretarMensajeCapacidades(msg.getContent());

        imprimir(this.getName(), " las capacidades recibidas son [rango: " + capacidades.rango
                + " | gasto: " + capacidades.gasto + " | vuela: " + capacidades.vuela + "]");
    }

    /**
     * Método encargado de divulgar un mensaje concreto al resto de agentes móviles
     *
     * @author Baltasar Ruiz Hernández
     */
    private void divulgarMensaje(String content, int performative) {
        for (AgentName n : moviles) {
            enviarMensaje(n.toAgentId(), content, performative);
        }
    }

    /**
     * Método encargado de actualizar las variables internas del agente y la BD y comunicar dichas actualizaciones a los otros agentes
     *
     * @author Baltasar Ruiz Hernández
     */
    private void realizarActualizaciones(){
        this.actualizarMapaBaseDatos(percepcion);
        this.actualizarMiMapaPases();
        this.comunicarMapaActualizado();
    }

    /**
     * Método encargado de enviar a todos los agentes disponibles la posición a la cual el agente actual va a esplazarse
     *
     * @author Baltasar Ruiz Hernández
     */
    private void coordinarMovimiento(){
        PairInt destino = calculaPosicionDestino(percepcion.posicion, siguiente_movimiento);
        divulgarMensaje(json_wrapper.generateDesplazamiento(this.getName(), destino), ACLMessage.QUERY_IF);
    }

    /**
     * Método encargado de preguntar por el siguiente movimiento
     *
     * @author Daniel Soto del Ojo
     * @return mensaje que contiene el siguiente movimiento a realizar
     */
    private Comando siguienteMovimiento() {
        Comando direccion = null;

        this.mapa_explorado = (bd.porcentajeMapaExplorado() >= PORCENTAJE_MINIMO && bd.existeSolución());

        if (!this.mapa_explorado) {
            direccion = siguienteMovimientoExplorar();
        }
        else {
            if(explorando){
                explorando = false;
                bd.calcularSuperficiesDeCostes();
                mapa = bd.obtenerMapaCoste(capacidades.vuela);
            }
            direccion = siguienteMovimientoCalcular();
        }

        return direccion;
    }

    /**
     * Método encargado de preguntar por la siguiente acción a realizar para explorar
     *
     * @author Daniel Soto del Ojo: implementar esqueleto y método.
     * @return mensaje que contiene la siguiente acción a realizar
     */
    private Comando siguienteMovimientoExplorar() {
        // Importante que una vez se haya cargado el mapa de la base de datos, se llame al método de checkColsExploradas
        Comando direccion = null;

        // Si es el dron
        if (this.first) {
            imprimir(this.getName(), "ANALIZANDO PILA DE MOVIMIENTOS");

            // Si la pila no está vacía
            if (!this.pila.empty()) {
                direccion = deshacerMovimientoPila(this.pila.peek());
            } else if (direccion == null) {

                imprimir(this.getName(), "ANALIZANDO NORTE-SUR");

                int analizarNorteSur = analizarNorteSur(percepcion.posicion);

                if (analizarNorteSur == 2 || analizarNorteSur == 4) {

                    imprimir(this.getName(), "ANALIZANDO COLUMNA EXPLORADA");

                    // Si hemos detectado borde, comprobamos si ha sido explorada la columna
                    boolean columna_explorada = checkExploracionColumna(percepcion.posicion);

                    // Si la columna ha sido explorada, nos movemos a los lados
                    if (columna_explorada) {

                        imprimir(this.getName(), "ANALIZANDO OESTE-ESTE");

                        int analizarOesteEste = analizarOesteEste(percepcion.posicion);

                        direccion = traducirDirAnalisis(analizarOesteEste);
                    } else {
                        // TODO FALTA METER FUNCIÓN PARA GREEDY N-S
                        // Si el borde se encontraba en el Norte, viajamos al Sur y viceversa
                        direccion = (analizarNorteSur == 2 ? Comando.SUR : Comando.NORTE);
                    }
                }
                else {
                    direccion = traducirDirAnalisis(analizarNorteSur);
                }
            }
        }
        else {
            direccion = Comando.DORMIR;
        }

        return direccion;
    }

    /**
     * Método encargado de deshacer un movimiento que fue usado para esquivar.
     *
     * @author Daniel Soto del Ojo
     * @param dir dirección que se quiere deshacer
     * @return nueva direccion
     */
    private Comando deshacerMovimientoPila(Comando dir) {
        Comando direccion = null;

        switch (dir) {
            case NORTE: direccion = Comando.SUR;
                break;
            case SUR: direccion = Comando.NORTE;
                break;
            case OESTE: direccion = Comando.ESTE;
                break;
            case ESTE: direccion = Comando.OESTE;
                break;
            case NOROESTE: direccion = Comando.NORESTE;
                break;
            case NORESTE: direccion = Comando.NOROESTE;
                break;
            case SUROESTE: direccion = Comando.SURESTE;
                break;
            case SURESTE: direccion = Comando.SUROESTE;
                break;
        }

        if (direccion != null)
            pila.pop();

        return direccion;
    }

    /**
     * Método encargado de traducir un valor numérico a una posición dada la función de análisis
     *
     * @author Daniel Soto del Ojo
     * @param valor valor hacia donde queremos ir
     * @return dirección en forma de Comando
     */
    private Comando traducirDirAnalisis(int valor) {
        Comando comando = null;
        switch (valor) {
            case 1: comando = Comando.NORTE;
                break;
            case 3: comando = Comando.SUR;
                break;
            case 5: comando = Comando.OESTE;
                break;
            case 6: comando = Comando.ESTE;
                break;
            case 7: comando = Comando.NORESTE;
                break;
            case 8: comando = Comando.NOROESTE;
                break;
            case 9: comando = Comando.SURESTE;
                break;
            case 10: comando = Comando.SUROESTE;
                break;
        }
        return comando;
    }

    /**
     * Método usado por el dron para saber si las casillas al este y al oeste pueden ser exploradas. En caso de que
     * ambas hayan sido exploradas, se optará por la que menos veces ha sido pisada
     *
     * @author Daniel Soto del Ojo
     * @param posicion coordenadas del mapa sobre las que se encuentra
     * @return 1 si hay que viajar al norte
     * @return 3 si hay que viajar al sur
     * @return 5 si hay que viajar al oeste
     * @return 6 si hay que viajar al este
     * @return 7 si hay que viajar al noreste
     * @return 8 si hay que viajar al noroeste
     * @return 9 si hay que viajar al sureste
     * @return 10 si hay que viajar al suroeste
     */
    private int analizarOesteEste(PairInt posicion) {
        int resultado = 0;

        // Obtener Oeste
        PairInt valorW1 = mapa.get(PairInt.valueOf(posicion.first - 1, posicion.second));

        boolean existeW2 = (mapa.containsKey(PairInt.valueOf(posicion.first - 2, posicion.second)));

        PairInt valorW2 = null;
        if (existeW2)
            valorW1 = mapa.get(PairInt.valueOf(posicion.first - 2, posicion.second));


        // Obtener Este
        PairInt valorE1 = mapa.get(PairInt.valueOf(posicion.first + 1, posicion.second));

        boolean existeE2 = (mapa.containsKey(PairInt.valueOf(posicion.first + 2, posicion.second)));

        PairInt valorE2 = null;
        if (existeE2) {
            valorE2 = mapa.get(PairInt.valueOf(posicion.first + 2, posicion.second));
        }

        // Analizar Oeste
        boolean viajarOeste = ((valorW1.first == 0 || valorW1.first == 3) && valorW1.second == 0) ||
                (existeW2 ? ((valorW2.first == 0 || valorW2.first == 3) && valorW2.second == 0):false);

        boolean bordeOeste = ((valorW1.first == 2));
        boolean agenteOeste = ((valorW1.first == 4));

        // Analizar Este
        boolean viajarEste = ((valorE1.first == 0 || valorE1.first == 3) && valorE1.second == 0) ||
                (existeE2 ? ((valorE2.first == 0 || valorE2.first == 3) && valorE2.second == 0):false);

        boolean bordeEste = ((valorE1.first == 2));
        boolean agenteEste = ((valorE1.first == 4));

        // Tomar decisión
        if (viajarOeste) {
            if (agenteOeste)
                resultado = esquivarAgente(Comando.OESTE);
            else
                resultado = 5;
        }
        else if (viajarEste) {
            if (agenteEste)
                resultado = esquivarAgente(Comando.ESTE);
            else
                resultado = 6;
        }
        else if (!bordeOeste && valorW1.second <= valorE1.second) {
            if (agenteOeste)
                resultado = esquivarAgente(Comando.OESTE);
            else
                resultado = 5;
        }
        else if (!bordeEste && valorW1.second > valorE1.second) {
            if (agenteEste)
                resultado = esquivarAgente(Comando.ESTE);
            else
                resultado = 6;
        }
        else if (!bordeOeste && !agenteOeste)
            resultado = 5;
        else
            resultado = 6;

        return resultado;
    }

    /**
     * Método encargado de esquivar al agente situado en la posición dada
     *
     * @author Daniel Soto del Ojo
     * @param dir dirección hacia la que queremos ir y está ocupada
     * @return 1 Si Norte
     * @return 3 Si Sur
     * @return 5 Si Oeste
     * @return 6 Si Este
     * @return 7 Si NE
     * @return 8 Si NW
     * @return 9 Si SE
     * @return 10 Si SW
     */
    private int esquivarAgente(Comando dir) {
        int mov = -1;

        PairInt pos = this.percepcion.posicion;

        PairInt nw = mapa.get(PairInt.valueOf(pos.first-1, pos.second-1));
        PairInt ne = mapa.get(PairInt.valueOf(pos.first-1, pos.second+1));

        boolean libreNW = nw.first == 0 || nw.first == 3;
        boolean libreNE = ne.first == 0 || ne.first == 3;

        PairInt sw = mapa.get(PairInt.valueOf(pos.first+1, pos.second-1));
        PairInt se = mapa.get(PairInt.valueOf(pos.first+1, pos.second+1));

        boolean libreSW = sw.first == 0 || sw.first == 3;
        boolean libreSE = se.first == 0 || se.first == 3;

        switch (dir) {
            case NORTE:

                if ( libreNW && libreNE ) {

                    if (nw.second < ne.second)
                        mov = 8;
                    else
                        mov = 7;
                }
                else if ( libreNW ) {
                    mov = 8;
                }
                else if ( libreNE ) {
                    mov = 7;
                }
                else
                    imprimir("ERROR, DOS AGENTES CONTIGUOS");

                break;
            case SUR:

                if ( libreSW && libreSE ) {

                    if (sw.second < se.second)
                        mov = 10;
                    else
                        mov = 9;
                }
                else if ( libreSW ) {
                    mov = 10;
                }
                else if ( libreSE ) {
                    mov = 9;
                }
                else
                    imprimir("ERROR, DOS AGENTES CONTIGUOS");

                break;
            case OESTE:

                if ( libreNW && libreSW ) {

                    if (nw.second < sw.second)
                        mov = 8;
                    else
                        mov = 10;
                }
                else if ( libreNW ) {
                    mov = 8;
                }
                else if ( libreSW ) {
                    mov = 10;
                }
                else
                    imprimir("ERROR, DOS AGENTES CONTIGUOS");

                break;
            case ESTE:

                if ( libreNE && libreSE ) {

                    if (ne.second < se.second)
                        mov = 7;
                    else
                        mov = 9;
                }
                else if ( libreNE ) {
                    mov = 7;
                }
                else if ( libreSE ) {
                    mov = 9;
                }
                else
                    imprimir("ERROR, DOS AGENTES CONTIGUOS");

                break;

            default:
                imprimir("ERROR, NO DEBERÍA ESTAR AQUI");
                mov = -2;
        }

        // Guardamos el movimiento en la pila
        Comando esquivar = null;

        switch (mov) {
            case 1: esquivar = Comando.NORTE;
                break;
            case 3: esquivar = Comando.SUR;
                break;
            case 5: esquivar = Comando.OESTE;
                break;
            case 6: esquivar = Comando.ESTE;
                break;
            case 7: esquivar = Comando.NORESTE;
                break;
            case 8: esquivar = Comando.NOROESTE;
                break;
            case 9: esquivar = Comando.SURESTE;
                break;
            case 10: esquivar = Comando.SUROESTE;
                break;
            default:
                imprimir("NO DEBERÍA ESTAR AQUI");
        }

        pila.push(esquivar);

        return mov;
    }

    /**
     * Método usado por el dron para saber si la columna en la que se encuentra ha sido ya explorada o no
     *
     * @author Daniel Soto del Ojo
     * @param posicion coordenadas del mapa sobre las que se encuentra
     * @return true si la columna ya ha sido explorada
     * @return false si la columna no ha sido explorada
     */
    private boolean checkExploracionColumna(PairInt posicion) {
        boolean explorada = true;

        // Buscamos en el array de columnas exploradas
        boolean encontrado = false;
        for (int i=0; i<columnas_exploradas.size() && !encontrado; i++) {
            if (columnas_exploradas.get(i) == posicion.second)
                encontrado = true;
        }

        // Buscamos en la columna en la cual nos encontramos
        if (!encontrado) {
            boolean salir = false;
            int tmp = 1;
            boolean mirarNorte = true;
            boolean mirarSur = true;

            while (!salir) {

                if (mirarNorte) {

                    if (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second - tmp))
                            && mapa.get(PairInt.valueOf(posicion.first, posicion.second - tmp)).first != 2) {
                        tmp++;

                        if (mapa.get(PairInt.valueOf(posicion.first, posicion.second - tmp)).first == 2) {
                            mirarNorte = false;
                            tmp = 1;
                        }
                    } else {
                        explorada = false;
                        salir = true;
                    }
                } else if (mirarSur) {

                    if (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second + tmp))
                            && mapa.get(PairInt.valueOf(posicion.first, posicion.second + tmp)).first != 2) {
                        tmp++;

                        if (mapa.get(new PairInt(posicion.first, posicion.second + tmp)).first == 2) {
                            mirarSur = false;
                            tmp = 1;
                        }
                    } else {
                        explorada = false;
                        salir = true;
                    }
                } else {
                    salir = true;

                    // Añadimos la columna a la lista para no tener que volver a explorarla
                    if (explorada)
                        columnas_exploradas.add(posicion.first);
                }

            }
        }

        return explorada;
    }

    /**
     * Método usado por el dron para evaluar lo que hay dos casillas al norte y al sur
     *
     * @author Daniel Soto del Ojo
     * @param posicion coordenadas del mapa sobre las que se encuentra
     * @return 1 si hay que viajar al norte
     * @return 2 si la inmediatamente superior es borde de mapa
     * @return 3 si hay que viajar al sur
     * @return 4 si la inmediatamente inferior es borde de mapa
     * @return 5 si hay que viajar al oeste
     * @return 6 si hay que viajar al este
     * @return 7 si hay que viajar al noreste
     * @return 8 si hay que viajar al noroeste
     * @return 9 si hay que viajar al sureste
     * @return 10 si hay que viajar al suroeste
     */
    private int analizarNorteSur(PairInt posicion) {
        int resultado = 0;

        // Obtener Norte
        PairInt valorN1 = mapa.get(PairInt.valueOf(posicion.first, posicion.second - 1));
        if (valorN1 == null)
            imprimir("CUIDADO", "VALORN1: " + posicion.first + ": " + (posicion.second-1) + " no existe");

        boolean existeN2 = (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second - 2)));

        PairInt valorN2 = null;
        if (existeN2)
            valorN2 = mapa.get(PairInt.valueOf(posicion.first, posicion.second - 2));


        // Obtener Sur
        PairInt valorS1 = mapa.get(PairInt.valueOf(posicion.first, posicion.second + 1));
        if (valorS1 == null)
            imprimir("CUIDADO", "VALORS1: " + posicion.first + ": " + (posicion.second+1) + " no existe");

        boolean existeS2 = (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second + 2)));

        PairInt valorS2 = null;
        if (existeS2)
            valorS2 = mapa.get(PairInt.valueOf(posicion.first, posicion.second + 2));

        imprimir("Valores de AnalizarNorteSur: valorN1: " + valorN1 + " | valorN2: " + valorN2
        + " | valorS1: " + valorS1 + " | valorS2: " + valorS2);

        // Analizar Norte
        boolean viajarNorte = ((valorN1.first == 0 || valorN1.first == 3) && valorN1.second == 0) ||
                (existeN2 ? (((valorN2.first == 0 || valorN2.first == 3) && valorN2.second == 0)):false);

        boolean bordeNorte = ((valorN1.first == 2));
        boolean agenteNorte = ((valorN1.first == 4));

        // Analizar Sur
        boolean viajarSur = ((valorS1.first == 0 || valorS1.first == 3) && valorS1.second == 0) ||
                (existeS2 ? ((valorS2.first == 0 || valorS2.first == 3) && valorS2.second == 0):false);
        boolean bordeSur = ((valorS1.first == 2));
        boolean agenteSur = ((valorS1.first == 4));

        if (viajarNorte) {
            if (agenteNorte)
                resultado = esquivarAgente(Comando.NORTE);
            else
                resultado = 1;
        }
        else if (viajarSur) {
            if (agenteSur)
                resultado = esquivarAgente(Comando.SUR);
            else
                resultado = 3;
        }
        else if (bordeNorte)
            resultado = 2;
        else if (bordeSur)
            resultado = 4;
        else                // Por defecto viajamos al norte cuando no hay casillas nuevas que explorar
            resultado = 1;

        return resultado;
    }

    /**
     * Método encargado de preguntar por la siguiente acción a realizar para llegar al destino
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @return mensaje que contiene la siguiente acción a realizar
     */
    private Comando siguienteMovimientoCalcular() {

        List<PairInt> posiciones = Posiciones.posicionesAlrededor(percepcion.posicion.first, percepcion.posicion.second);
        List<PairInt> posiciones_bloqueadas = bd.getPosicionesBloqueadas();

        if(!posiciones_bloqueadas.isEmpty())
            posiciones.removeAll(posiciones_bloqueadas);

        int coste = Integer.MAX_VALUE;
        int nuevo_coste;

        Comando siguiente = Comando.NOROESTE;

        for (PairInt p: posiciones) {
            nuevo_coste = mapa.get(p).second;
            if(coste > nuevo_coste){
                coste = nuevo_coste;
                siguiente = Posiciones.toComando(percepcion.posicion, p);
            }
        }
        return siguiente;
    }

    /**
     * Método encargado de actualizar el mapa local del agente
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @author Baltasar Ruiz: implementación del método
     */
    private void actualizarMiMapaPases() {
        mapa = bd.obtenerMapaPases();
    }

    /**
     * Método encargado de actualizar el mapa local del agente
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @author Baltasar Ruiz: implementación del método
     */
    private void actualizarMiMapaCostes() {
        mapa = bd.obtenerMapaCoste(capacidades.vuela);
    }

    /**
     * Método encargado de actualizar el mapa de la base de datos
     *
     * @author Elías Méndez: implementación del método
     */
    private void actualizarMapaBaseDatos(Percepcion p) {
        bd.actualizarMapaPases(p.mapa);
        bd.actualizarPases(p.posicion.first, p.posicion.second);
    }

    /**
     * Método encargado de informar al resto de agentes sobre la última actualización del mapa
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @author Baltasar Ruiz: implementación del método
     */
    private void comunicarMapaActualizado() {
        divulgarMensaje(json_wrapper.generateComunicarMapa(), ACLMessage.INFORM);
    }

    /**
     * Método que calcula la posición de destino de un movimiento
     *
     * @author Manuel Lafuente Aranda
     *
     * @param posicion posición actual del agente
     * @param direccion dirección en la que se moverá el agente
     */
    private PairInt calculaPosicionDestino(PairInt posicion, Comando direccion){
        int x = posicion.first, y = posicion.second;
        PairInt destino;

//        Comando movimiento = direccion;
//
//        if(direccion == null) {
//            movimiento = Comando.DORMIR;
//        }

        switch(direccion){
            case NORTE: y--; break;
            case NORESTE: x++; y--; break;
            case ESTE: x++; break;
            case SURESTE: x++; y++; break;
            case SUR: y++; break;
            case SUROESTE: x--; y++; break;
            case OESTE: x--; break;
            case NOROESTE: x--; y++; break;
            default:break;
        }

        destino = new PairInt(x, y);

        return destino;
    }

    /**
     * Método encargado de imprimir la traza.
     *
     * @author Manuel Lafuente Aranda
     */
    private void imprimeTraza(ACLMessage inbox, JsonWrapper json, String map, Observable obs) {
        byte[] img = json.interpretarTraza(inbox.getContent());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(map + ".png");
            fos.write(img);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        obs.setEstado("Traza guardada.");
    }

    private enum Estado{
        RECEPCION_INFORMACION, AVISO_ACTUALIZACION, SIGUIENTE_MOVIMIENTO, RESPUESTA_CONTABLE, RESPUESTA_COMANDO_AGENTES,
        RESOLVER_CONFLICTO_MOVIMIENTO, ACTUAR_CONFLICTOS, MOVIMIENTO_REALIZADO, PROCESAR_MUERTE, IMPRIMIR_TRAZA;

        public static Estado toEstado(ACLMessage msg){
            String msg_content = msg.getContent();
            int msg_performative = msg.getPerformativeInt();

            if (msg_content.contains("battery") && msg_content.contains("energy")
                    && msg_content.contains("goal") && msg_performative == ACLMessage.INFORM)
                return Estado.RECEPCION_INFORMACION;

            else if (msg_content.contains("CONFIRMAR_ACTUALIZAR_MAPA") && msg_performative == ACLMessage.INFORM)
                return Estado.SIGUIENTE_MOVIMIENTO;

            else if (msg_content.contains("ACTUALIZAR_MAPA") && msg_performative == ACLMessage.INFORM)
                return Estado.AVISO_ACTUALIZACION;

            else if(msg.getSender().equals(AgentName.CONTABLE.toAgentId()))
                return Estado.RESPUESTA_CONTABLE;

            else if(msg_content.contains("trace"))
                return Estado.IMPRIMIR_TRAZA;

            else if(msg_content.contains("result"))
                return Estado.RESPUESTA_COMANDO_AGENTES;

            else if (msg_content.contains("COMPROBAR_CONFLICTO_MOVIMIENTO") && msg_performative == ACLMessage.QUERY_IF)
                return Estado.RESOLVER_CONFLICTO_MOVIMIENTO;

            else if (msg_content.contains("CONFLICTO") && (msg_performative == ACLMessage.CONFIRM
                    || msg_performative == ACLMessage.DISCONFIRM))
                return Estado.ACTUAR_CONFLICTOS;

            else if(msg_content.contains("REALIZADO"))
                return Estado.MOVIMIENTO_REALIZADO;

            else if(msg_content.equals("MUERTO"))
                return Estado.PROCESAR_MUERTE;



            else
                return null;
        }
    }
}
