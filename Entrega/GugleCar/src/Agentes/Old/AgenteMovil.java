//package Agentes.Old;
//
//import Agentes.Agente;
//import Agentes.Old.Contable;
//import Agentes.Data.*;
//import Agentes.Utilities.JsonWrapper;
//import Agentes.Utilities.Posiciones;
//import Agentes.Utilities.SQLWrapper;
//import GUI.Observables.Observable;
//import es.upv.dsic.gti_ia.core.ACLMessage;
//import es.upv.dsic.gti_ia.core.AgentID;
//import es.upv.dsic.gti_ia.core.AgentsConnection;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Clase que contiene la definición del agente que será empleado para recorrer el mundo
// *
// * @author Daniel Soto del Ojo: Implementar esqueleto de la clase
// */
//public class AgenteMovil extends Agente {
//    private boolean first;
//    private String agente_first;
//
//    private TipoAgente tipo_agente;
//
//    private String map;
//
//    private ACLMessage msg;
//    private String conversation_id;
//    private String in_reply_to;
//
//    private Capacidades capacidades;
//    private Percepcion percepcion;
//
//    private int combustible;
//    private PairInt posicion;
//    private Map<PairInt, PairInt> mapa;
//
//    private Comando direccion;
//    private int fase_comando;
//
//    private int pasos;
//    private boolean pause;
//    private boolean exit;
//    private boolean exit_dron;
//    private boolean explorando;
//
//    private final float PORCENTAJE_MINIMO;
//    private final int UMBRAL_BATERIA;
//    public static boolean VIVO1;
//    public static boolean VIVO2;
//    public static boolean VIVO3;
//    public static boolean VIVO4;
//
//    private SQLWrapper bd;
//    private JsonWrapper json_wrapper;
//
//    private Observable observable_mapa;
//    private Observable observable_mensajes;
//    private Observable observable_estado;
//
//    private int confirmaciones_en_espera;
//
//    private boolean mapa_explorado;
//    private boolean avisar_fin_exploracion;
//
//    // Array que contiene las columnas que han sido exploradas (utilizado por los métodos de exploración)
//    private ArrayList<Integer> columnas_exploradas = new ArrayList<>();
//
//    /**
//     * Método constructor del agente
//     *
//     * @author Daniel Soto del Ojo: implementación base
//     * @author Elías Méndez García: asiganción de valores y modificaciones
//     *
//     * @param name contiene el nombre del agente
//     * @param first contiene si es el primer agente que ha sido creado
//     * @param porcentaje contiene el porcentaje de mapa que explorará
//     * @param umbral valor a partir del cual se considera que la batería está en estado crítico
//     * @param ob_map instancia del objeto encargada de notificar a interfaz sobre el estado del mapa
//     * @param ob_men instancia del objeto encargada de notificar a interfaz las acciones del agente
//     * @param ob_estado instancia del objeto encargada de notificar a interfaz sobre el estado del agente
//     * @param map nombre del mapa que va a ser cargado
//     */
//    public AgenteMovil(AgentID name, boolean first, float porcentaje, int umbral, Observable ob_map, Observable ob_men, Observable ob_estado, String map) throws Exception {
//        super(name);
//        this.first = first;
//
//        this.map = map;
//
//        this.capacidades = new Capacidades(0, 0, false);
//
//        fase_comando = 1;
//        exit = false;
//        exit_dron = false;
//        explorando = true;
//
//        PORCENTAJE_MINIMO = porcentaje;
//        UMBRAL_BATERIA = umbral;
//        VIVO1 = VIVO2 = VIVO3 = VIVO4 = true;
//
//        this.json_wrapper = JsonWrapper.getInstance();
//        this.bd = new SQLWrapper(map);
//
//        this.observable_mapa = ob_map;
//        this.observable_mensajes = ob_men;
//        this.observable_estado = ob_estado;
//
//        this.mapa_explorado = false;
//        this.avisar_fin_exploracion = false;
//    }
//
//    /**
//     * Método encargado de inicializar al agente
//     *
//     * @author Daniel Soto del Ojo: implementación base
//     */
//    @Override
//    public void init() {
//        imprimir(this.getName(), " inicializado");
//    }
//
//    /**
//     * Método encargado de controlar la lógica del agente
//     *
//     * @author Daniel Soto del Ojo: implementar esqueleto
//     * @author Baltasar Ruiz: implementación comunicaciones base, implementación de comunicación con Contable
//     * @author Manuel Lafuente Aranda: implementación de comunicación con Contable y entre los agentes a la hora de preguntar por un movimiento
//     */
//    @Override
//    public void execute() {
//    try {
//        imprimir(this.getName(), " ejecutándose");
//
//        // Si es el primer agente móvil en ser creado, se encargará de enviar el mensaje de subscripción
//        // al servidor y reenviar el conversation_id recibido a sus compañeros
//
//        while(!exit_dron) {
//            if (this.first) {
//                agente_first = this.getName();
//
//                List<AgentName> moviles = AgentName.moviles();
//
//                for (AgentName n : moviles) {
//                    if (!n.toAgentId().equals(this.getAid())) {
//                        enviarMensaje(n.toAgentId(), json_wrapper.generateFirstAgentName(agente_first), ACLMessage.INFORM);
//                    }
//                }
//
//                enviarMensajeSubscribe();
//                enviarMensajeCheckin();
//
//                while (!capacidades.vuela) {
//                    enviarMensajeCancel();
//                    enviarMensajeSubscribe();
//                    enviarMensajeCheckin();
//                }
//
//                // Reenviar dicho conversation_id al resto de agentes móviles
//
//                this.divulgarConversationID();
//
//                exit_dron = true;
//
////                 boolean solo_un_dron = true;
////
////                for (int i = 0; i < 3; i++) {
////                    msg = recibirMensaje();
////
////                    if (msg.getContent().contains("DRON"))
////                        solo_un_dron = false;
////                }
////
////                if (solo_un_dron) {
////                    List<AgentName> moviles = AgentName.moviles();
////
////                    for (AgentName n : moviles) {
////                        if (!n.toAgentId().equals(this.getAid())) {
////                            enviarMensaje(n.toAgentId(), "COMENZAR_TRAZA", ACLMessage.INFORM);
////                        }
////                    }
////
////                    msg = recibirMensaje();
////                    msg = recibirMensaje();
////                    msg = recibirMensaje();
////
////                    exit_dron = true;
////                }
////
////                else {
////                    List<AgentName> moviles = AgentName.moviles();
////
////                    for (AgentName n : moviles) {
////                        if (!n.toAgentId().equals(this.getAid())) {
////                            enviarMensaje(n.toAgentId(), "REPETIR_TRAZA", ACLMessage.INFORM);
////                        }
////                    }
////
////                    msg = recibirMensaje();
////                    msg = recibirMensaje();
////                    msg = recibirMensaje();
////
////                    enviarMensajeCancel();
////                }
//
//            } else {
//                // Si no es el primer agente móvil en ser creado, simplemente recibe el mensaje del primer compañero
//                // con el conversation_id del servidor
//
//                msg = recibirMensaje();
//                agente_first = json_wrapper.interpretarFirstAgentName(msg.getContent());
//
//                msg = recibirMensaje();
//                conversation_id = json_wrapper.interpretarConversationID(msg.getContent());
//
//                // Enviar mensaje con el comando CHECKIN al servidor
//
//                enviarMensajeCheckin();
//
//                exit_dron = true;
//
////                 if (!capacidades.vuela)
////                    enviarMensaje(new AgentID(AGENTE_FIRST), "TERRESTRE", ACLMessage.INFORM);
////                else
////                    enviarMensaje(new AgentID(AGENTE_FIRST), "DRON", ACLMessage.INFORM);
////
////                msg = recibirMensaje();
////
////                if (msg.getContent().contains("COMENZAR_TRAZA")) {
////                    exit_dron = true;
////                    enviarMensaje(new AgentID(AGENTE_FIRST), "CONFIRMAR_COMENZAR_TRAZA", ACLMessage.INFORM);
////                }
////
////                else if (msg.getContent().contains("REPETIR_TRAZA")) {
////                    enviarMensajeCancel();
////                    enviarMensaje(new AgentID(AGENTE_FIRST), "CONFIRMAR_REPETIR_TRAZA", ACLMessage.INFORM);
////                }
//            }
//        }
//
//        // Enviar mensaje de solicitud de información del entorno
//
//        enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), "", ACLMessage.QUERY_REF, conversation_id, in_reply_to);
//
//        // BALTI: BUCLE SWITCH - CASE (TODAVÍA NO ESTÁ TERMINADO EL TRASPASO, EL CÓDIGO ORIGINAL LO HE GUARDADO
//        // EN UN DOCUMENTO PROPIO)
//
//        while (!exit) {
//            msg = recibirMensaje();
//
//            switch (Mensajes.toEstado(msg)) {
//                case RECEPCION_INFORMACION:
//                    System.out.println("Agente " + this.getName() + " entrando a case RECEPCION_INFORMACION");
//                    percepcion = json_wrapper.interpretPercepcion(msg.getContent());
//
//                    combustible = percepcion.combustible;
//                    posicion = percepcion.posicion;
//
//                    System.out.println("Percepcion interpretada como " + percepcion.combustible + " de combustible y "
//                            + percepcion.posicion.first + "|" + percepcion.posicion.second + " de posicion");
//
//                    observable_estado.setEstado(json_wrapper.generarJsonObservablePercecpcion(combustible, posicion));
//
//                    realizarActualizaciones(0);
//                    in_reply_to = msg.getReplyWith();
//
//                    boolean continuar_traza_normal = comunicacionContable();
//
//                    if (continuar_traza_normal){
//                        // Crear la cadena con la que se mandará la posición a la que nos queremos mover
//
//                        direccion = siguienteMovimiento();
//
//                        if (direccion != null) {
//                            PairInt pos_destino = calculaPosicionDestino(posicion, direccion);
//
//                            String destino = "x: " + pos_destino.first + ", y: " + pos_destino.second;
//
//                            // Mandar el mensaje al resto de agentes
//
//                            preguntarPorMovimiento(destino);
//
//                            confirmaciones_en_espera = agentesVivos();
//                        }
//                    }
//                    System.out.println("Agente " + this.getName() + " finalizado case RECEPCION_INFORMACION");
//                    break;
//
//                case AVISO_ACTUALIZACION:
//                    System.out.println("Agente " + this.getName() + " entrando a case AVISO_ACTUALIZACION");
//                    realizarActualizaciones(1);
//                    System.out.println("Agente " + this.getName() + " finalizado case AVISO_ACTUALIZACION");
//                    break;
//
//                case CONSULTA_DRON:
//                    System.out.println("Agente " + this.getName() + " entrando a case CONSULTA_DRON");
//                    if (capacidades.vuela)
//                        enviarMensaje(msg.getSender(), "YES", ACLMessage.INFORM);
//
//                    else
//                        enviarMensaje(msg.getSender(), "NO", ACLMessage.INFORM);
//
//                    System.out.println("Agente " + this.getName() + " finalizado case CONSULTA_DRON");
//                    break;
//
//                case CONSULTA_GASTO:
//                    System.out.println("Agente " + this.getName() + " entrando a case CONSULTA_GASTO");
//                    enviarMensaje(msg.getSender(), Integer.toString(capacidades.gasto), ACLMessage.INFORM);
//                    System.out.println("Agente " + this.getName() + " finalizado case CONSULTA_GASTO");
//                    break;
//
//                case RECARGA_APROBADA:
//                    System.out.println("Agente " + this.getName() + " entrando a case RECARGA_APROBADA");
//                    enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(Comando.RECARGAR), ACLMessage.REQUEST, conversation_id, in_reply_to);
//                    fase_comando = 1;
//                    System.out.println("Agente " + this.getName() + " finalizado case RECARGA_APROBADA");
//                    break;
//
//                case COMANDO_APROBADO:
//                    System.out.println("Agente " + this.getName() + " entrando a case COMANDO_APROBADO");
//                    in_reply_to = msg.getReplyWith();
//                    if (fase_comando == 1) {
//                        Comando direccion = siguienteMovimiento();
//                        if (direccion != null) {
//                            PairInt pos_destino = calculaPosicionDestino(posicion, direccion);
//                            String destino = "x: " + pos_destino.first + ", y: " + pos_destino.second;
//                            preguntarPorMovimiento(destino);
//                            confirmaciones_en_espera = agentesVivos();
//                        }
//                    } else if (fase_comando == 2)
//                        enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), "", ACLMessage.QUERY_REF, conversation_id, in_reply_to);
//
//                    System.out.println("Agente " + this.getName() + " finalizado case COMANDO_APROBADO");
//                    break;
//
//                case CONFIRMAR_CONFLICTO:
//                    System.out.println("Agente " + this.getName() + " entrando a case CONFIRMAR_CONCLICTO");
//                    int gasto_colisionante = preguntarGastoAgente(msg.getSender());
//
//                    if(bd.porcentajeMapaExplorado() < PORCENTAJE_MINIMO){ // Modo de Exploración
//                        if(preguntarSiVuelaAgente(msg.getSender())){
//                            esperar();
//                        }
//
//                        else{
//                            if(this.capacidades.gasto < gasto_colisionante){
//                                enviarMensaje(msg.getSender(), "MENOS_GASTO", ACLMessage.INFORM);
//                            }
//                            else if(this.capacidades.gasto > gasto_colisionante){
//                                enviarMensaje(msg.getSender(), "MAS_GASTO", ACLMessage.INFORM);
//                                esperar();
//                            }
//                            else{
//                                esperar();
//                            }
//                        }
//                    }
//
//                    else{ // Modo de Avance
//                        if(this.capacidades.gasto < gasto_colisionante){
//                            enviarMensaje(msg.getSender(), "MENOS_GASTO", ACLMessage.INFORM);
//                            enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(direccion), ACLMessage.REQUEST, conversation_id, in_reply_to);
//                            fase_comando = 2;
//                            enviarMensaje(msg.getSender(), "MOVIMIENTO_REALIZADO", ACLMessage.INFORM); // PROVISIONALMENTE AQUÍ, NO SÉ SI REALMENTE VA AQUÍ
//                        }
//
//                        else if(this.capacidades.gasto > gasto_colisionante){
//                            enviarMensaje(msg.getSender(), "MAS_GASTO", ACLMessage.INFORM);
//                            esperar();
//                        }
//
//                        else{
//                            esperar();
//                        }
//                    }
//                    System.out.println("Agente " + this.getName() + " finalizado case CONFIRMAR_CONCLICTO");
//                    break;
//
//                case CONFIRMAR_NO_CONFLICTO:
//                    System.out.println("Agente " + this.getName() + " entrando a case CONFIRMAR_NO_CONFLICTO");
//                    System.out.println("Tengo " + confirmaciones_en_espera + " confirmaciones esperando habiendo " +
//                            agentesVivos() + " agentes disponibles");
//
//                    confirmaciones_en_espera--;
//
//                    System.out.println("Aún me quedan " + confirmaciones_en_espera);
//
//                    if (confirmaciones_en_espera == 0) {
//                        enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(direccion), ACLMessage.REQUEST, conversation_id, in_reply_to);
//                        fase_comando = 2;
//                    }
//                    System.out.println("Agente " + this.getName() + " finalizado case CONFIRMAR_NO_CONFLICTO");
//                    break;
//
//                case RESPUESTA_MOVIMIENTO_AGENTES:
//                    System.out.println("Agente " + this.getName() + " entrando a case RESPUESTA_MOVMIENTO_AGENTES");
//                    respuestaMovimientoAgentes();
//                    System.out.println("Agente " + this.getName() + " finalizado case RESPUESTA_MOVMIENTO_AGENTES");
//                    break;
//
//                case ERROR_COMUNICACION:
//                    System.out.println("Agente " + this.getName() + " entrando a case ERROR_COMUNICACION");
//                    enviarMensaje(AgentName.CONTROLADOR.toAgentId(), "", ACLMessage.CANCEL);
//                    System.out.println("Agente " + this.getName() + " finalizado case ERROR_COMUNICACION");
//                    break;
//
//                case CONFIRMACION_CANCEL:
//                    System.out.println("Agente " + this.getName() + " entrando a case CONFIRMACION_CANCEL");
//                    msg = recibirMensaje(); // Recepción del INFORM Trace del servidor
//                    bd.updateCasillaTerminar(posicion, percepcion.goal, capacidades.vuela);
//                    if (this.getName().equals(AgentName.MOVIL_1.toString())){
//                        VIVO1 = false;
//                    }
//                    else if (this.getName().equals(AgentName.MOVIL_2.toString())){
//                        VIVO2 = false;
//                    }
//                    else if (this.getName().equals(AgentName.MOVIL_3.toString())){
//                        VIVO3 = false;
//                    }
//                    else if (this.getName().equals(AgentName.MOVIL_4.toString())){
//                        VIVO4 = false;
//                    }
//                    System.out.println("Agente " + this.getName() + " va a morir, ahora hay " + agentesVivos() + " agentes disponibles");
//                    exit = true;
//
//                    System.out.println("Agente " + this.getName() + " finalizado case CONFIRMACION_CANCEL");
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    } catch (Exception e){
//        e.printStackTrace();
//        enviarMensajeCancel();
//    }
//    }
//
//    /**
//     * Método encargado de controlar la finalización del agente
//     *
//     * @author Daniel Soto del Ojo
//     */
//    @Override
//    public void finalize() {
//        imprimir(this.getName(), " ha finalizado");
//    }
//
//    /**
//     * Método encargado de *****
//     *
//     * @author Daniel Soto del Ojo: implementar esqueleto
//     */
//    public void onMessage() {
//
//    }
//
//    /**
//     * Método encargado de enviar al servidor el mensaje de subscripción y reenviar el conversation_id al resto de agentes
//     *
//     * @author Baltasar Ruiz Hernández
//     */
//    private void enviarMensajeSubscribe() {
//        // enviarMensajeCancel();
//
//        // Enviar mensaje de subscripción
//
//        enviarMensaje(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarSubscribe(map), ACLMessage.SUBSCRIBE);
//        msg = recibirMensaje();
//
//        // Repetir el envío de subscripción hasta que sea aceptada
//
//        while (msg.getPerformativeInt() != ACLMessage.INFORM){
//            enviarMensajeCancel();
//            msg = recibirMensaje();
//            enviarMensaje(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarSubscribe(map), ACLMessage.SUBSCRIBE);
//            msg = recibirMensaje();
//        }
//
//        // Recibir el conversation_id del servidor
//
//        conversation_id = msg.getConversationId();
//    }
//
//    /**
//     * Método encargado de enviar al servidor el mensaje con el comando CHECKIN de obtención de capacidades
//     *
//     * @author Baltasar Ruiz Hernández
//     */
//    private void enviarMensajeCheckin(){
//        enviarMensajeConversationID(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(Comando.CHECKIN), ACLMessage.REQUEST, conversation_id);
//        msg = recibirMensaje();
//
//        while(msg.getPerformativeInt() != ACLMessage.INFORM){
//            enviarMensajeConversationID(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(Comando.CHECKIN), ACLMessage.REQUEST, conversation_id);
//            msg = recibirMensaje();
//        }
//
//        in_reply_to = msg.getReplyWith();
//        capacidades = json_wrapper.interpretarMensajeCapacidades(msg.getContent());
//
//        imprimir(this.getName(), " las capacidades recibidas son [rango: " + capacidades.rango
//                + " | gasto: " + capacidades.gasto + " | vuela: " + capacidades.vuela + "]");
//
//        if (capacidades.vuela){
//            tipo_agente = TipoAgente.DRONE;
//        }
//
//        else{
//            if (capacidades.rango == 5){
//                tipo_agente = TipoAgente.COCHE;
//            }
//            else{
//                tipo_agente = TipoAgente.CAMION;
//            }
//        }
//    }
//
//    /**
//     * Método encargado de actualizar las variables internas del agente y la BD y comunicar dichas actualizaciones a los otros agentes
//     *
//     * @author Baltasar Ruiz Hernández
//     * @param mode evita que la actualización de la BD y el aviso de actualización lo realicen todos los agentes
//     */
//    private void realizarActualizaciones(int mode){
//        if (mode == 0)
//            this.actualizarMapaBaseDatos(percepcion);
//
//        this.actualizarMiMapaPases();
//
//        if (mode == 0)
//            this.comunicarMapaActualizado();
//
//        observable_mapa.setEstado(json_wrapper.generarJsonObservableMapa(this.getName(), posicion));
//    }
//
//    /**
//     * Método encargado de enviar los mensajes de comunicación pertinentes al agente Contable
//     *
//     * @author Baltasar Ruiz Hernández
//     */
//    private boolean comunicacionContable(){
//        System.out.println("Agente " + this.getName() + " realizando comprobaciones de comunicacion con Contable");
//        boolean continuar_traza_normal = true;
//
//        if (combustible < UMBRAL_BATERIA && percepcion.energy > 0){
//            boolean posibilidadrecarga = preguntarPosibilidadRecarga();
//
//            if (!posibilidadrecarga){
//                enviarMensaje(AgentName.CONTABLE.toAgentId(), json_wrapper.generarMensajeSolicitudAviso(combustible, percepcion.energy, 0), ACLMessage.REQUEST_WHEN);
//                msg = recibirMensaje(); // Recibir el AGREE al REQUEST_WHEN
//            }
//
//            else {
//                enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(Comando.RECARGAR), ACLMessage.REQUEST, conversation_id, in_reply_to);
//                msg = recibirMensaje();
//
//                while(msg.getPerformativeInt() != ACLMessage.INFORM){
//                    enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(Comando.RECARGAR), ACLMessage.REQUEST, conversation_id, in_reply_to);
//                    msg = recibirMensaje();
//                }
//
//                fase_comando = 1;
//            }
//
//            continuar_traza_normal = false;
//        }
//
//        else if (combustible < UMBRAL_BATERIA && percepcion.energy == 0){
//            enviarMensaje(AgentName.CONTROLADOR.toAgentId(), "", ACLMessage.CANCEL);
//            continuar_traza_normal = false;
//        }
//
//        return continuar_traza_normal;
//    }
//
//    /**
//     * Método encargado de preguntar al resto de agentes si nos podemos mover a una posición concreta
//     *
//     * @author Manuel Lafuente Aranda: implementación
//     * @author Baltasar Ruiz Hernández: encapsulación de la implementación en un método
//     */
//    private void consultaMovimientoAgentes(){
//        // Crear la cadena con la que se mandará la posición a la que nos queremos mover
//
//        Comando direccion = siguienteMovimiento();
//
//        PairInt pos_destino = calculaPosicionDestino(posicion, direccion);
//
//        String destino = "x: " + pos_destino.first + ", y: " + pos_destino.second;
//
//        // Mandar el mensaje al resto de agentes
//
//        preguntarPorMovimiento(destino);
//
//        // Esperar la respuesta de los agentes. En el caso de recibir un DISCONFIRM,
//        // dependiendo del modo en el que nos encontremos se actúa de una forma u otra
//
//        // int confirmaciones_en_espera = AGENTES_DISPONIBLES;
//
//        int confirmaciones_en_espera = 3;
//
//        boolean espera = false;
//
//        while(!espera && confirmaciones_en_espera != 0){
//            msg = recibirMensaje();
//
//            if(msg.getPerformativeInt() == ACLMessage.CONFIRM)
//                confirmaciones_en_espera--;
//
//            else if(msg.getPerformativeInt() == ACLMessage.DISCONFIRM)
//                espera = true;
//        }
//
//        if(espera){ // Se ha recibido un DISCONFIRM
//            int gasto_colisionante = preguntarGastoAgente(msg.getSender());
//
//            if(bd.porcentajeMapaExplorado() < PORCENTAJE_MINIMO){ // Modo de Exploración
//                if(preguntarSiVuelaAgente(msg.getSender())){
//                    esperar();
//                }
//
//                else{
//                    if(this.capacidades.gasto < gasto_colisionante){
//                        enviarMensaje(msg.getSender(), "MENOS_GASTO", ACLMessage.INFORM);
//                    }
//                    else if(this.capacidades.gasto > gasto_colisionante){
//                        enviarMensaje(msg.getSender(), "MAS_GASTO", ACLMessage.INFORM);
//                        esperar();
//                    }
//                    else{
//                        esperar();
//                    }
//                }
//            }
//
//            else{ // Modo de Avance
//                if(this.capacidades.gasto < gasto_colisionante){
//                    enviarMensaje(msg.getSender(), "MENOS_GASTO", ACLMessage.INFORM);
//                    enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(direccion), ACLMessage.REQUEST, conversation_id, in_reply_to);
//                    fase_comando = 2;
//                    enviarMensaje(msg.getSender(), "MOVIMIENTO_REALIZADO", ACLMessage.INFORM); // PROVISIONALMENTE AQUÍ, NO SÉ SI REALMENTE VA AQUÍ
//                }
//
//                else if(this.capacidades.gasto > gasto_colisionante){
//                    enviarMensaje(msg.getSender(), "MAS_GASTO", ACLMessage.INFORM);
//                    esperar();
//                }
//
//                else{
//                    esperar();
//                }
//            }
//        }
//
//        else{ // No se recibe DISCONFIRM
//            enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(direccion), ACLMessage.REQUEST, conversation_id, in_reply_to);
//            fase_comando = 2;
//            msg = recibirMensaje();
//        }
//    }
//
//    /**
//     * Método encargado de decidir la respuesta al agente que consulta si se puede mover a una casilla
//     *
//     * @author Manuel Lafuente Aranda: implementación
//     * @author Baltasar Ruiz Hernández: encapsulación de la implementación en un método
//     */
//    private void respuestaMovimientoAgentes(){
//        // Crear la cadena con la que compararemos, es decir, la que tendría nuestra decisión de movimiento
//
//        Comando direccion_destino = siguienteMovimiento();
//
//        PairInt pos_destino_decidido = calculaPosicionDestino(posicion, direccion_destino);
//
//        String destino_decidido = "x: " + pos_destino_decidido.first + ", y: " + pos_destino_decidido.second;
//
//        // Comparar las cadenas: si son iguales se responderá con un DISCONFIRM ya que sería
//        // la misma casilla, en caso contrario se responderá con un CONFIRM
//
//        if(destino_decidido.equals(msg.getContent())){
//            enviarMensaje(msg.getSender(), "MISMO_DESTINO", ACLMessage.DISCONFIRM);
//
//            msg = recibirMensaje();
//
//            if(msg.getPerformativeInt() == ACLMessage.INFORM && msg.getContent() == "MENOS_GASTO")
//                esperar();
//        } else {
//            enviarMensaje(msg.getSender(), "OK", ACLMessage.CONFIRM);
//        }
//    }
//
//    /**
//     * Método encargado de enviar al servidor el mensaje de cancelación y recibir las respuestas convenientes
//     *
//     * @author Baltasar Ruiz Hernández
//     */
//    private void enviarMensajeCancel() {
//        enviarMensaje(AgentName.CONTROLADOR.toAgentId(), "", ACLMessage.CANCEL);
//        msg = recibirMensaje();
//        msg = recibirMensaje();
//    }
//
//    /**
//     * Método encargado de preguntar al contable si se puede recargar
//     *
//     * @author Daniel Soto del Ojo: implementar esqueleto
//     * @author Baltasar Ruiz: implementación del método
//     * @return true si se puede recargar
//     * @return false si no se puede recargar
//     */
//    private boolean preguntarPosibilidadRecarga() {
//        boolean sepuederecargar = true;
//
//        enviarMensaje(AgentName.CONTABLE.toAgentId(), json_wrapper.generarMensajeRecarga(combustible, percepcion.energy, 0, tipo_agente, true), ACLMessage.QUERY_IF);
//        msg = recibirMensaje();
//
//        if (msg.getPerformativeInt() == ACLMessage.DISCONFIRM){
//            sepuederecargar = false;
//        }
//
//        return sepuederecargar;
//    }
//
//    /**
//     * Método ejecutado por el primer agente móvil creado, encargado de enviar el conversation_id del primer mensaje de respuesta del servidor al resto de agentes
//     *
//     * @author Daniel Soto del Ojo: implementar esqueleto
//     * @author Baltasar Ruiz Hernández: implementación método
//     */
//    private void divulgarConversationID() {
//        List<AgentName> moviles = AgentName.moviles();
//        String json = json_wrapper.generateConversationID(conversation_id);
//
//        for (AgentName n : moviles) {
//            if(!n.toAgentId().equals(this.getAid())) {
//                enviarMensaje(n.toAgentId(), json, ACLMessage.INFORM);
//            }
//        }
//
//        //  enviarMensaje(AgentName.MOVIL_2.toAgentId(), json_wrapper.generateConversationID(conversation_id), ACLMessage.INFORM);
//        //  enviarMensaje(AgentName.MOVIL_3.toAgentId(), json_wrapper.generateConversationID(conversation_id), ACLMessage.INFORM);
//        //  enviarMensaje(AgentName.MOVIL_4.toAgentId(), json_wrapper.generateConversationID(conversation_id), ACLMessage.INFORM);
//    }
//
//    /**
//     * Método encargado de despertar a los agentes durmientes durante la exploración
//     *
//     * @author Daniel Soto del Ojo: implementar el método
//     */
//    private void divulgarDespertar() {
//        List<AgentName> moviles = AgentName.moviles();
//
//        for (AgentName n : moviles) {
//            if(!n.toAgentId().equals(this.getAid())) {
//                enviarMensaje(n.toAgentId(), "fin_exploracion", ACLMessage.INFORM);
//            }
//        }
//
//        //  enviarMensaje(AgentName.MOVIL_2.toAgentId(), json_wrapper.generateConversationID(conversation_id), ACLMessage.INFORM);
//        //  enviarMensaje(AgentName.MOVIL_3.toAgentId(), json_wrapper.generateConversationID(conversation_id), ACLMessage.INFORM);
//        //  enviarMensaje(AgentName.MOVIL_4.toAgentId(), json_wrapper.generateConversationID(conversation_id), ACLMessage.INFORM);
//    }
//
//    /*
//    /**
//     * Método que comunica a todos los agentes que se va a morir para no tenerlo en cuenta a la hora de esperar una respuesta.
//     *
//     * @author Manuel Lafuente Aranda
//     */
//    /*private void divulgarMuerte() {
//        List<AgentName> moviles = AgentName.moviles();
//
//        for (AgentName n : moviles) {
//            if(!n.toAgentId().equals(this.getAid())) {
//                enviarMensaje(n.toAgentId(), "MUERTO", ACLMessage.INFORM);
//            }
//        }
//    }*/
//
//    /**
//     * Método encargado de preguntar por el siguiente movimiento
//     *
//     * @author Daniel Soto del Ojo
//     * @return mensaje que contiene el siguiente movimiento a realizar
//     */
//    private Comando siguienteMovimiento() {
//        Comando direccion = null;
//
//        this.mapa_explorado = (bd.porcentajeMapaExplorado() >= PORCENTAJE_MINIMO && bd.existeSolución());
//
//        if (!this.mapa_explorado) {
//            direccion = siguienteMovimientoExplorar();
//        }
//        else {
//            if(explorando){
//                explorando = false;
//                bd.calcularSuperficiesDeCostes();
//                mapa = bd.obtenerMapaCoste(capacidades.vuela);
//            }
//            if (capacidades.vuela && !avisar_fin_exploracion) {
//                avisar_fin_exploracion = true;
//                // mensaje al resto de agentes para que abandonen dormir
//                despertarAgentes();
//            }
//            direccion = siguienteMovimientoCalcular();
//        }
//
//        return direccion;
//    }
//
//    /**
//     * Método del dron encargado de despertar a los agentes que no exploran durante la fase de exploración
//     *
//     * @author Daniel Soto del Ojo
//     */
//    private void despertarAgentes() {
//        imprimir(this.getName(), "despertando al resto");
//        divulgarDespertar();
//    }
//
//    /**
//     * Método encargado de recibir y responder a peticiones y mensajes del resto de agentes cuando se ha recibido una respuesta de no confirmación para moverse a una casilla.
//     *
//     * @author Manuel Lafuente Aranda
//     */
//    private void esperar() {
//        boolean esperando = true;
//        while(esperando){
//            ACLMessage msg = recibirMensaje();
//
//            switch (msg.getPerformativeInt()){ // SI HAY QUE AÑADIR MÁS CASOS EN LA ESPERA, AÑADIR LOS CASE, HE PUESTO LOS DOS QUE YO CREÍA
//                case ACLMessage.QUERY_IF:
//                    String destino_decidido;
//                    Comando direccion_destino = siguienteMovimiento();
//
//                    PairInt pos_destino_decidido = calculaPosicionDestino(posicion, direccion_destino);
//
//                    destino_decidido = "x: " + pos_destino_decidido.first + ", y: " + pos_destino_decidido.second;
//
//                    if(destino_decidido.equals(msg.getContent())){
//                        enviarMensaje(msg.getSender(), "MISMO_DESTINO", ACLMessage.DISCONFIRM);
//                    }
//                    else{
//                        enviarMensaje(msg.getSender(), "OK", ACLMessage.CONFIRM);
//                    }
//                    break;
//
//                case ACLMessage.INFORM:
//                    if(msg.getContent() == "MOVIMIENTO_REALIZADO"){
//                        Comando direccion = siguienteMovimiento();
//                        esperando = false;
//                        enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(direccion), ACLMessage.REQUEST, conversation_id, in_reply_to);
//                        fase_comando = 2;
//                    }
//                    break;
//            }
//        }
//
//    }
//    /**
//     * Método encargado de poner al agente en fase de espera hasta que la exploración esté completada. Mientras tanto, recibirá
//     * mensajes que le soliciten cambiar su posición para permitir paso a otros agentes
//     *
//     * @author Daniel Soto del Ojo: implementación del método
//     * @author Manuel Lafuente Aranda: ayuda para la implementación del envío de movimiento
//     */
//    public void dormir() {
//        // Esperar solicitudes de mensajes pidiendo que nos apartemos
//        ACLMessage msg = recibirMensaje();
//
//        if (msg.getPerformativeInt() == ACLMessage.INFORM && msg.getContent().contains("exploracion")) {
//            String dir_mover = json_wrapper.interpretaCambioPosicion(msg.getContent());
//
//            // Calculamos nuestra nueva ubicación
//            Comando casilla = buscarCasillaLibre(dir_mover);
//            boolean exito = (casilla == null ? false:true);
//            if (exito) {
//                enviarMensajeConversationIDInReplyTo(AgentName.CONTROLADOR.toAgentId(), json_wrapper.generarComando(casilla), ACLMessage.REQUEST, conversation_id, in_reply_to);
//            }
//
//            // Notificamos al dron el éxito de nuestra operación
//            enviarMensaje(msg.getReceiver(), json_wrapper.generateRespuestaCambioPosicion(exito), ACLMessage.INFORM);
//        }
//        else if (msg.getPerformativeInt() == ACLMessage.INFORM && msg.getContent().contains("fin_exploracion")) {
//            imprimir(this.getName(), " fin de dormir");
//        }
//    }
//
//    /**
//     * Método encargado de preguntar por la siguiente acción a realizar para explorar
//     *
//     * @author Daniel Soto del Ojo: implementar esqueleto y método.
//     * @return mensaje que contiene la siguiente acción a realizar
//     */
//    private Comando siguienteMovimientoExplorar() {
//        // Importante que una vez se haya cargado el mapa de la base de datos, se llame al método de checkColsExploradas
//        Comando direccion = null;
//
//        // Si es el dron
//        if (capacidades.vuela) {
//            imprimir(this.getName(), "ANALIZANDO NORTE-SUR");
//            int analizarNorteSur = analizarNorteSur(posicion);
//
//            switch (analizarNorteSur) {
//                case 1: direccion = Comando.NORTE;
//                    break;
//                case 3: direccion = Comando.SUR;
//                    break;
//                default:
//                    imprimir(this.getName(), "ANALIZANDO COLUMNA EXPLORADA");
//                    // Si hemos detectado borde, comprobamos si ha sido explorada la columna
//                    boolean columna_explorada = checkExploracionColumna(posicion);
//
//                    // Si la columna ha sido explorada, nos movemos a los lados
//                    if (columna_explorada) {
//                        imprimir(this.getName(), "ANALIZANDO OESTE-ESTE");
//                        int analizarOesteEste = analizarOesteEste(posicion);
//
//                        if (analizarOesteEste == 1) {
//                            direccion = Comando.OESTE;
//                        }
//                        else if (analizarOesteEste == 2)
//                            direccion = Comando.ESTE;
//                        else { // RodearAgente
//                            imprimir(this.getName(), "ANALIZANDO NORTE-SUR RODEO");
//                            analizarNorteSur(posicion);
//                        }
//                    }
//                    else {
//                        // Si el borde se encontraba en el Norte, viajamos al Sur y viceversa
//                        direccion = (analizarNorteSur == 2?Comando.SUR:Comando.NORTE);
//                    }
//            }
//        }
//        else {
//            /* Si decidieramos que los agentes no drones pudieran explorar
//            //imprimir(this.getName(), "EXPLORANDO POR GREEDY");
//            // Explorar por el vecino más viejo
//            //direccion = explorarGreedy(posicion);
//            */
//            // El agente se queda a la espera de que el dron le solicite que se cambie de posición
//            dormir();
//        }
//
//        return direccion;
//    }
//
//    private Comando buscarCasillaLibre(String dir_mover) {
//        Comando resultado;
//        // Traducimos la dirección a la cual se quiere mover el dron
//        int x, y;
//        switch (dir_mover) {
//            case "norte": x=0; y=-1;
//                break;
//            case "sur": x=0; y=1;
//                break;
//            case "oeste": x=-1; y=0;
//                break;
//            case "este": x=1; y=0;
//                break;
//            default: x=0; y=0;
//        }
//
//        Comando direccion = null;
//        boolean salir = false;
//        PairInt tmp;
//        int fila, col;
//        fila = col = 0;
//
//        for (int i=-1; i<2 && !salir; i++) {
//
//            for (int j=-1; j<2 && !salir; j++) {
//                tmp = mapa.get(PairInt.valueOf(posicion.first + j, posicion.second + i));
//
//                if (tmp != null && (tmp.first == 0 || tmp.first == 3) && !(i == y && j == x)) {
//                    fila = j;
//                    col = i;
//                    salir = true;
//                }
//            }
//        }
//
//        direccion = traducirPosicionLocal(col, fila);
//        boolean encontrado = !(fila == 0 && col == 0);
//        if (encontrado) {
//            resultado = direccion;
//        }
//        else
//            resultado = null;
//        return resultado;
//    }
//
//    /**
//     * Método encargado de explorar cuando el agente no es un dron
//     * y buscar la casilla que menor número de pases tiene
//     *
//     * @author Daniel Soto del Ojo
//     * @param posicion coordenadas del mapa sobre las que se encuentra
//     * @return dirección a la que avanzar
//     */
//    private Comando explorarGreedy(PairInt posicion) {
//        Comando direccion = null;
//        boolean salir = false;
//        PairInt tmp;
//        int min_pases = Integer.MAX_VALUE;
//        int x, y;
//        x = y = 0;
//
//        for (int i=-1; i<2 && !salir; i++) {
//
//            for (int j=-1; j<2 && !salir; j++) {
//                tmp = mapa.get(PairInt.valueOf(posicion.first + i, posicion.second + j));
//
//                if (tmp != null && (tmp.first == 0 || tmp.first == 3) && tmp.second < min_pases && (i != 0 || j != 0)) {
//                    x = i;
//                    y = j;
//                    min_pases = tmp.second;
//                }
//            }
//        }
//
//        direccion = traducirPosicionLocal(x, y);
//
//        return direccion;
//    }
//
//    /**
//     * Método que permite traducir la posición de las casillas locales cercanas al vehículo a una dirección
//     * que permita alcanzarlas
//     *
//     * @author Daniel Soto del Ojo
//     * @param fila posición en el eje X
//     * @param col posición en el eje Y
//     * @return dirección hacia la que viajar
//     */
//    private Comando traducirPosicionLocal(int col, int fila) {
//        Comando direccion = null;
//
//        switch (fila) {
//            case -1:
//                if (col == -1)
//                    direccion = Comando.NOROESTE;
//                else if (col == 0)
//                    direccion = Comando.NORTE;
//                else
//                    direccion = Comando.NORESTE;
//
//                break;
//            case 0:
//                if (col == -1)
//                    direccion = Comando.OESTE;
//                else
//                    direccion = Comando.ESTE;
//
//                break;
//            case 1:
//                if (col == -1)
//                    direccion = Comando.SUROESTE;
//                else if (col == 0)
//                    direccion = Comando.SUR;
//                else
//                    direccion = Comando.SURESTE;
//
//                break;
//            default:
//        }
//
//        return direccion;
//    }
//
//    /**
//     * Método usado por el dron para saber si las casillas al este y al oeste pueden ser exploradas. En caso de que
//     * ambas hayan sido exploradas, se optará por la que menos veces ha sido pisada
//     *
//     * @author Daniel Soto del Ojo
//     * @param posicion coordenadas del mapa sobre las que se encuentra
//     * @return 1 si hay que viajar al oeste
//     * @return 2 si hay que viajar al este
//     * @return 3 si hay que viajar al norte-sur para evitar colisionar con un agente
//     */
//    private int analizarOesteEste(PairInt posicion) {
//        int resultado = 0;
//
//        // Obtener Oeste
//        PairInt valorW1 = mapa.get(PairInt.valueOf(posicion.first - 1, posicion.second));
//
//        boolean existeW2 = (mapa.containsKey(PairInt.valueOf(posicion.first - 2, posicion.second)));
//
//        PairInt valorW2 = null;
//        if (existeW2)
//            valorW1 = mapa.get(PairInt.valueOf(posicion.first - 2, posicion.second));
//
//
//        // Obtener Este
//        PairInt valorE1 = mapa.get(PairInt.valueOf(posicion.first + 1, posicion.second));
//
//        boolean existeE2 = (mapa.containsKey(PairInt.valueOf(posicion.first + 2, posicion.second)));
//
//        PairInt valorE2 = null;
//        if (existeE2) {
//            valorE2 = mapa.get(PairInt.valueOf(posicion.first + 2, posicion.second));
//        }
//
//        // Analizar Oeste
//        boolean viajarOeste = ((valorW1.first == 0 || valorW1.first == 3) && valorW1.second == 0) ||
//                (existeW2 ? ((valorW2.first == 0 || valorW2.first == 3) && valorW2.second == 0):false);
//
//        boolean bordeOeste = ((valorW1.first == 2));
//        boolean agenteOeste = ((valorW1.first == 4));
//
//        // Analizar Este
//        boolean viajarEste = ((valorE1.first == 0 || valorE1.first == 3) && valorE1.second == 0) ||
//                (existeE2 ? ((valorE2.first == 0 || valorE2.first == 3) && valorE2.second == 0):false);
//
//        boolean bordeEste = ((valorE1.first == 2));
//        boolean agenteEste = ((valorE1.first == 4));
//
//        // Tomar decisión
//        if (viajarOeste) {
//            if (agenteOeste) {
//                // Enviar mensaje para que se aparte
//                int exito = solicitarCambioPosicionExploracion(PairInt.valueOf(posicion.first - 1, posicion.second), "oeste");
//                if (exito == 2)
//                    resultado = 1;
//                else
//                    resultado = 3;
//
//            }
//            else
//                resultado = 1;
//        }
//        else if (viajarEste) {
//            if (agenteEste) {
//                // enviar mensaje para que se aparte
//                int exito = solicitarCambioPosicionExploracion(new PairInt(posicion.first + 1, posicion.second), "este");
//                if (exito == 2)
//                    resultado = 2;
//                else
//                    resultado = 3;
//
//            }
//            resultado = 2;
//        }
//        else if (!bordeOeste && valorW1.second <= valorE1.second) {
//            if (agenteOeste) {
//                // Enviar mensaje para que se aparte
//                int exito = solicitarCambioPosicionExploracion(new PairInt(posicion.first - 1, posicion.second), "oeste");
//                if (exito == 2)
//                    resultado = 1;
//                else
//                    resultado = 3;
//
//            } else
//                resultado = 1;
//        }
//        else if (!bordeEste && valorW1.second > valorE1.second) {
//            if (agenteEste) {
//                // enviar mensaje para que se aparte
//                int exito = solicitarCambioPosicionExploracion(new PairInt(posicion.first + 1, posicion.second), "este");
//                if (exito == 2)
//                    resultado = 2;
//                else
//                    resultado = 3;
//
//            }
//            resultado = 2;
//        }
//        else if (!bordeOeste && !agenteOeste)
//            resultado = 1;
//        else
//            resultado = 2;
//
//        return resultado;
//    }
//
//    /**
//     * Método usado por el dron para saber si la columna en la que se encuentra ha sido ya explorada o no
//     *
//     * @author Daniel Soto del Ojo
//     * @param posicion coordenadas del mapa sobre las que se encuentra
//     * @return true si la columna ya ha sido explorada
//     * @return false si la columna no ha sido explorada
//     */
//    private boolean checkExploracionColumna(PairInt posicion) {
//        boolean explorada = true;
//
//        // Buscamos en el array de columnas exploradas
//        boolean encontrado = false;
//        for (int i=0; i<columnas_exploradas.size() && !encontrado; i++) {
//            if (columnas_exploradas.get(i) == posicion.second)
//                encontrado = true;
//        }
//
//        // Buscamos en la columna en la cual nos encontramos
//        if (!encontrado) {
//            boolean salir = false;
//            int tmp = 1;
//            boolean mirarNorte = true;
//            boolean mirarSur = true;
//
//            while (!salir) {
//
//                if (mirarNorte) {
//
//                    if (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second - tmp))
//                            && mapa.get(PairInt.valueOf(posicion.first, posicion.second - tmp)).first != 2) {
//                        tmp++;
//
//                        if (mapa.get(PairInt.valueOf(posicion.first, posicion.second - tmp)).first == 2) {
//                            mirarNorte = false;
//                            tmp = 1;
//                        }
//                    } else {
//                        explorada = false;
//                        salir = true;
//                    }
//                } else if (mirarSur) {
//
//                    if (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second + tmp))
//                            && mapa.get(PairInt.valueOf(posicion.first, posicion.second + tmp)).first != 2) {
//                        tmp++;
//
//                        if (mapa.get(new PairInt(posicion.first, posicion.second + tmp)).first == 2) {
//                            mirarSur = false;
//                            tmp = 1;
//                        }
//                    } else {
//                        explorada = false;
//                        salir = true;
//                    }
//                } else {
//                    salir = true;
//
//                    // Añadimos la columna a la lista para no tener que volver a explorarla
//                    if (explorada)
//                        columnas_exploradas.add(posicion.first);
//                }
//
//            }
//        }
//
//        return explorada;
//    }
//
//    /**
//     * Método usado por el dron para evaluar lo que hay dos casillas al norte y al sur
//     *
//     * @author Daniel Soto del Ojo
//     * @param posicion coordenadas del mapa sobre las que se encuentra
//     * @return 1 si hay que viajar al norte
//     * @return 2 si la inmediatamente superior es borde de mapa
//     * @return 3 si hay que viajar al sur
//     * @return 4 si la inmediatamente inferior es borde de mapa
//     */
//    private int analizarNorteSur(PairInt posicion) {
//        int resultado = 0;
//
//        // Obtener Norte
//        PairInt valorN1 = mapa.get(PairInt.valueOf(posicion.first, posicion.second - 1));
//        if (valorN1 == null)
//            imprimir("CUIDADO", "VALORN1: " + posicion.first + ": " + (posicion.second-1) + " no existe");
//
//        boolean existeN2 = (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second - 2)));
//
//        PairInt valorN2 = null;
//        if (existeN2)
//            valorN2 = mapa.get(PairInt.valueOf(posicion.first, posicion.second - 2));
//
//
//        // Obtener Sur
//        PairInt valorS1 = mapa.get(PairInt.valueOf(posicion.first, posicion.second + 1));
//        if (valorS1 == null)
//            imprimir("CUIDADO", "VALORS1: " + posicion.first + ": " + (posicion.second+1) + " no existe");
//
//        boolean existeS2 = (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second + 2)));
//
//        PairInt valorS2 = null;
//        if (existeS2)
//            valorS2 = mapa.get(PairInt.valueOf(posicion.first, posicion.second + 2));
//
//        System.out.println("Valores de AnalizarNorteSur: valorN1: " + valorN1 + " | valorN2: " + valorN2
//        + " | valorS1: " + valorS1 + " | valorS2: " + valorS2);
//
//        // Analizar Norte
//        boolean viajarNorte = ((valorN1.first == 0 || valorN1.first == 3) && valorN1.second == 0) ||
//                (existeN2 ? (((valorN2.first == 0 || valorN2.first == 3) && valorN2.second == 0)):false);
//
//        boolean bordeNorte = ((valorN1.first == 2));
//        boolean agenteNorte = ((valorN1.first == 4));
//
//        // Analizar Sur
//        boolean viajarSur = ((valorS1.first == 0 || valorS1.first == 3) && valorS1.second == 0) ||
//                (existeS2 ? ((valorS2.first == 0 || valorS2.first == 3) && valorS2.second == 0):false);
//        boolean bordeSur = ((valorS1.first == 2));
//        boolean agenteSur = ((valorS1.first == 4));
//
//        if (viajarNorte) {
//            if (agenteNorte) {
//                // Enviar mensaje para que se aparte el otro agente
//                int exito = solicitarCambioPosicionExploracion(PairInt.valueOf(posicion.first, posicion.second - 1), "norte");
//                if (exito == 2)
//                    resultado = 1;
//                else                // Si no se ha movido el otro agente, nos movemos nosotros a los lados
//                    resultado = 2;
//                // falta considerar caso en que exito == 1
//            }
//            else
//                resultado = 1;
//        }
//        else if (viajarSur) {
//            if (agenteSur) {
//                // Enviar mensaje para que se aparte el otro agente
//                int exito = solicitarCambioPosicionExploracion(new PairInt(posicion.first, posicion.second + 1), "sur");
//                if (exito == 2)
//                    resultado = 3;
//                else
//                    resultado = 4;
//                // falta considerar caso en que exito == 1
//            }
//            else
//                resultado = 3;
//        }
//        else if (bordeNorte)
//            resultado = 2;
//        else if (bordeSur)
//            resultado = 4;
//        else                // Por defecto viajamos al norte cuando no hay casillas nuevas que explorar
//            resultado = 1;
//
//        return resultado;
//    }
//
//    /**
//     * Método encargado de rodear a un agente por el norte o el sur
//     * @param posicion posición en la que se encuentra nuestro agente
//     * @return 1 si viajar al norte
//     * @return 2 si viajar al sur
//     * @return 0 si error
//     */
//    private int analizarNorteSurRodeo(PairInt posicion) {
//        int resultado = 0;
//
//        // Obtener Norte
//        PairInt valorN1 = mapa.get(PairInt.valueOf(posicion.first, posicion.second - 1));
//
//        boolean existeN2 = (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second - 2)));
//
//        PairInt valorN2 = null;
//        if (existeN2)
//            valorN1 = mapa.get(PairInt.valueOf(posicion.first, posicion.second - 2));
//
//
//        // Obtener Sur
//        PairInt valorS1 = mapa.get(PairInt.valueOf(posicion.first, posicion.second + 1));
//
//        boolean existeS2 = (mapa.containsKey(PairInt.valueOf(posicion.first, posicion.second + 2)));
//
//        PairInt valorS2 = null;
//        if (existeS2)
//            valorS2 = mapa.get(PairInt.valueOf(posicion.first, posicion.second + 2));
//
//
//        // Analizar Norte
//        boolean viajarNorte = ((valorN1.first == 0 || valorN1.first == 3) && valorN1.second == 0) ||
//                (existeN2 ? ((valorN2.first == 0 || valorN2.first == 3) && valorN2.second == 0):false);
//
//        boolean bordeNorte = ((valorN1.first == 2));
//        boolean agenteNorte = ((valorN1.first == 4));
//
//        // Analizar Sur
//        boolean viajarSur = ((valorS1.first == 0 || valorS1.first == 3) && valorS1.second == 0) ||
//                (existeS2 ? ((valorS2.first == 0 || valorS2.first == 3) && valorS2.second == 0):false);
//        boolean bordeSur = ((valorS1.first == 2));
//        boolean agenteSur = ((valorS1.first == 4));
//
//        if (!agenteNorte && !bordeNorte) {
//            resultado = 1;
//        }
//        else if (!agenteSur && !bordeSur) {
//            resultado = 3;
//        }
//
//        return resultado;
//    }
//
//    /**
//     * Método encargado de solicitar, si se puede, a otro vehículo que se aparte.
//     *
//     * @author Daniel Soto del Ojo
//     * @param posicion coordenada en la que se encuentra el agente
//     * @return 0 si no se puede apartar por falta de espacio
//     * @return 1 si no se puede apartar por ser un dron
//     * @return 2 si se ha apartado con éxito
//     */
//    private int solicitarCambioPosicionExploracion(PairInt posicion, String direccion) {
//        AgentName agente = buscarAgente(posicion);
//        boolean vuela = preguntarSiVuelaAgente(agente.toAgentId());
//        int resultado;
//
//        if (!vuela) {
//            enviarMensaje(new AgentID(agente.toString()), json_wrapper.generateCambioPosicion(direccion), ACLMessage.INFORM);
//            ACLMessage tmp = recibirMensaje();
//            boolean exito = json_wrapper.interpretaRespuestaCambioPosicion(tmp.getContent());
//            resultado = (exito ? 2:0);
//        }
//        else {
//            resultado = 1;
//        }
//
//        return resultado;
//    }
//
//    /**
//     * Método encargado de averiguar si el agente es un dron
//     *
//     * @author Daniel Soto del Ojo: implementar esqueleto
//     * @author Baltasar Ruiz Hernández: implementación método
//     * @param agente identificador del agente al que se le realiza la consulta
//     * @return
//     */
//    private boolean preguntarSiVuelaAgente(AgentID agente) {
//        boolean resultado = false;
//
//        enviarMensaje(agente, "CAN_FLY", ACLMessage.QUERY_IF);
//        msg = recibirMensaje();
//
//        if (msg.getContent().equals("YES"))
//            resultado = true;
//
//        return resultado;
//    }
//
//    /**
//     * Método encargado de averiguar el gasto de un agente
//     *
//     * @author Baltasar Ruiz Hernández
//     * @param agente identificador del agente al que se le realiza la consulta
//     * @return
//     */
//    private int preguntarGastoAgente(AgentID agente) {
//        enviarMensaje(agente, "GASTO", ACLMessage.QUERY_REF);
//        msg = recibirMensaje();
//
//        return Integer.parseInt(msg.getContent());
//    }
//
//    private int agentesVivos(){
//        int vivos = 0;
//        if(VIVO1)
//            vivos++;
//        if(VIVO2)
//            vivos++;
//        if(VIVO3)
//            vivos++;
//        if(VIVO4)
//            vivos++;
//
//        return vivos == 4 ? 3 : vivos;
//    }
//
//    /**
//     * Método encargado de buscar a un agente dada una posición
//     *
//     * @author Daniel Soto del Ojo
//     * @param posicion coordenada en la que se encuentra el agente
//     * @return nombre del agente
//     */
//    private AgentName buscarAgente(PairInt posicion) {
//
//        return null;
//    }
//
//    /**
//     * Método encargado de preguntar por la siguiente acción a realizar para llegar al destino
//     *
//     * @author Daniel Soto del Ojo: implementar esqueleto
//     * @return mensaje que contiene la siguiente acción a realizar
//     */
//    private Comando siguienteMovimientoCalcular() {
//
//        List<PairInt> posiciones = Posiciones.posicionesAlrededor(posicion.first, posicion.second);
//        int coste = Integer.MAX_VALUE;
//        int nuevo_coste;
//        Comando siguiente = Comando.NOROESTE;
//        for (PairInt p: posiciones) {
//            nuevo_coste = mapa.get(p).second;
//            if(coste > nuevo_coste){
//                coste = nuevo_coste;
//                siguiente = Posiciones.toComando(posicion, p);
//            }
//        }
//        return siguiente;
//    }
//
//    /**
//     * Método encargado de actualizar el mapa local del agente
//     *
//     * @author Daniel Soto del Ojo: implementar esqueleto
//     * @author Baltasar Ruiz: implementación del método
//     */
//    private void actualizarMiMapaPases() {
//        mapa = bd.obtenerMapaPases();
//    }
//
//    /**
//     * Método encargado de actualizar el mapa de la base de datos
//     *
//     * @author Elías Méndez: implementación del método
//     */
//    private void actualizarMapaBaseDatos(Percepcion p) {
//        bd.actualizarMapaPases(p.mapa);
//        bd.actualizarPases(p.posicion.first, p.posicion.second);
//    }
//
//    /**
//     * Método encargado de informar al resto de agentes sobre la última actualización del mapa
//     *
//     * @author Daniel Soto del Ojo: implementar esqueleto
//     * @author Baltasar Ruiz: implementación del método
//     */
//    private void comunicarMapaActualizado() {
//        List<AgentName> moviles = AgentName.moviles();
//
//        for (AgentName agentname : moviles){
//            if (!agentname.toAgentId().equals(this.getAid()))
//                enviarMensaje(agentname.toAgentId(), json_wrapper.generateComunicarMapa(), ACLMessage.INFORM);
//        }
//    }
//
//    /**
//     * Método encargado de preguntar al resto de agentes si se puede mover al destino indicado
//     *
//     * @author Manuel Lafuente Aranda: creación e implementación del método
//     *
//     * @param destino destino al que se quiere mover el agente
//     */
//    private void preguntarPorMovimiento(String destino){
//        List<AgentName> moviles = AgentName.moviles();
//
//        for (AgentName agentname : moviles){
//            if (!agentname.toAgentId().equals(this.getAid()))
//                enviarMensaje(agentname.toAgentId(), destino, ACLMessage.QUERY_IF);
//        }
//    }
//
//    /**
//     * Método que calcula la posición de destino de un movimiento
//     *
//     * @author Manuel Lafuente Aranda
//     *
//     * @param posicion posición actual del agente
//     * @param direccion dirección en la que se moverá el agente
//     */
//    private PairInt calculaPosicionDestino(PairInt posicion, Comando direccion){
//        int x = posicion.first, y = posicion.second;
//        PairInt destino;
//
//        switch(direccion){
//            case NORTE: y --; break;
//            case NORESTE: x++; y --; break;
//            case ESTE: x++; break;
//            case SURESTE: x++; y++; break;
//            case SUR: y++; break;
//            case SUROESTE: x--; y++; break;
//            case OESTE: x--; break;
//            case NOROESTE: x--; y++; break;
//        }
//
//        destino = new PairInt(x, y);
//
//        return destino;
//    }
//
//    private enum Mensajes{
//        RECEPCION_INFORMACION, AVISO_ACTUALIZACION, CONSULTA_DRON, CONSULTA_GASTO, RECARGA_APROBADA,
//        COMANDO_APROBADO, CONFIRMAR_CONFLICTO, CONFIRMAR_NO_CONFLICTO, RESPUESTA_MOVIMIENTO_AGENTES,
//        PROCESAR_MUERTE, ERROR_COMUNICACION, CONFIRMACION_CANCEL;
//
//        public static Mensajes toEstado(ACLMessage msg){
//            String msg_content = msg.getContent();
//            int msg_performative = msg.getPerformativeInt();
//
//            if (msg_content.contains("battery") && msg_content.contains("energy")
//                    && msg_content.contains("goal") && msg_performative == ACLMessage.INFORM)
//                return Mensajes.RECEPCION_INFORMACION;
//
//            else if (msg_content.contains("ACTUALIZAR_MAPA") && msg_performative == ACLMessage.INFORM)
//                return Mensajes.AVISO_ACTUALIZACION;
//
//            else if (msg_content.contains("CAN_FLY") && msg_performative == ACLMessage.QUERY_IF)
//                return Mensajes.CONSULTA_DRON;
//
//            else if (msg_content.contains("GASTO") && msg_performative == ACLMessage.QUERY_REF)
//                return Mensajes.CONSULTA_GASTO;
//
//            else if (msg_content.contains("OK") && msg_performative == ACLMessage.INFORM)
//                return Mensajes.COMANDO_APROBADO;
//
//            else if (msg_content.contains("OK") && msg_performative == ACLMessage.CONFIRM)
//                return Mensajes.CONFIRMAR_NO_CONFLICTO;
//
//            else if (msg_content.contains("MISMO_DESTINO") && msg_performative == ACLMessage.DISCONFIRM)
//                return Mensajes.CONFIRMAR_CONFLICTO;
//
//            else if (!msg_content.contains("CAN_FLY") && msg_performative == ACLMessage.QUERY_IF)
//                return Mensajes.RESPUESTA_MOVIMIENTO_AGENTES;
//
//            else if (msg_content.contains("PERMISO_RECARGA") && msg_performative == ACLMessage.INFORM)
//                return Mensajes.RECARGA_APROBADA;
//
//            else if (msg_performative == ACLMessage.NOT_UNDERSTOOD || msg_performative == ACLMessage.FAILURE
//                    || msg_performative == ACLMessage.REFUSE)
//                return Mensajes.ERROR_COMUNICACION;
//
//            else if (msg_content.contains("OK") && msg_performative == ACLMessage.AGREE)
//                return Mensajes.CONFIRMACION_CANCEL;
//
//            else
//                return null;
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            AgentsConnection.connect("isg2.ugr.es", 6000, "Bellatrix", "Pitol", "Canmaior", false);
//            AgenteMovil m1 = new AgenteMovil(AgentName.MOVIL_1.toAgentId(), true, 90, 5, null, null, null, "map100");
//            AgenteMovil m2 = new AgenteMovil(AgentName.MOVIL_2.toAgentId(), false, 90, 5, null, null, null, "map100");
//            Contable c = new Contable(AgentName.CONTABLE.toAgentId());
//            c.start();
//            m1.start();
//            m2.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
