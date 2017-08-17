package Agentes;

import Agentes.Data.*;
import Agentes.Utilities.JsonWrapper;
import Agentes.Utilities.SQLWrapper;
import GUI.Observables.Observable;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Claseque contiene la definición del agente que gestionará el envío de mensajes entre agentes y la toma de decisiones.
 *
 * @author Manuel Lafuente Aranda: código de versión anterior reutilizado en execute y método de impresión de traza
 */
public class Controlador extends Agente {

    // Otros agentes móviles a los que enviar mensajes.
    private List<AgentName> moviles;
    private List<ACLMessage> mensajes;
    private Queue<Solicitante> cola_recarga;
    private Set<PairInt> posiciones_actuales;
    private boolean exit;
    private ACLMessage msg;
    private List<PairInt> posiciones_siguientes;
    private JsonWrapper json_wrapper;
    private Estado estado;
    private long energia_restante;
    private final long UMBRAL_BATERIA;
    private String mapa;
    private int se_mueven;
    private SQLWrapper bd;
    private int n_moviles;
    private boolean explorando;
    private Observable ob_mensajes;
    private Observable ob_mapa;
    private Observable ob_botones;
    private final float PORCENTAJE_MINIMO;
    private int pasos;
    private boolean pause;

    /**
     * Método constructor del agente
     *
     * @param aid id del agente
     * @throws Exception si aid no válido
     * @author Daniel Soto del Ojo
     */
    public Controlador(AgentID aid, String mapa, long umbral_bateria, float porcentaje, Observable ob_mensajes, Observable ob_mapa, Observable ob_botones) throws Exception {
        super(aid);
        exit = false;
        json_wrapper = JsonWrapper.getInstance();
        mensajes = new ArrayList<>();
        cola_recarga = new PriorityQueue<>();
        moviles = AgentName.moviles();
        UMBRAL_BATERIA = umbral_bateria;
        posiciones_actuales = new TreeSet<>();
        posiciones_siguientes = new ArrayList<>();
        this.mapa = mapa;
        bd = new SQLWrapper(mapa);
        this.ob_mensajes = ob_mensajes;
        this.ob_mapa = ob_mapa;
        this.PORCENTAJE_MINIMO = porcentaje;
        explorando = true;
        this.ob_botones = ob_botones;
        pasos = 1;
        pause = false;
    }

    /**
     * Método encargado de ejecutar la lógica del agente
     *
     * @author Elías Méndez García: Implementación de estados y depuración
     * @author Eila Gómez Hidalgo: Implementación de estados y depuración.
     * @author Daniel Soto del Ojo:Implementación de estados y depuración.
     * @author Baltasar Ruiz: implementación comunicaciones base, implementación de comunicación con Contable. CODIGO REUTILIZADO
     * @author Manuel Lafuente Aranda: implementación de comunicación con Contable y entre los agentes a la hora de preguntar por un movimiento CODIGO REUTILIZADO
     */
    public void execute() {

        while(mensajes.size() != moviles.size()) {
            msg = recibirMensaje();
            mensajes.add(msg);
        }

        mensajes.clear();
        estado = Estado.PEDIR_SENSORES;

        while(!exit) {
            switch (estado) {
                case PEDIR_SENSORES:
                    divulgarMensaje(estado.toString(), ACLMessage.REQUEST);

                    n_moviles = moviles.size();
                    while(mensajes.size() != n_moviles) {
                        msg = recibirMensaje();
                        mensajes.add(msg);
                    }

                    ob_mapa.setEstado(null);

                    if(explorando) {
                        float porcentaje = bd.porcentajeMapaExplorado();
                        boolean explorado = porcentaje >= PORCENTAJE_MINIMO && bd.existeSolución();

                        if(explorado) {
                            bd.calcularSuperficiesDeCostes();
                            explorando = false;
                        }
                    }

                    // si quedan pasos por realizar reducir en uno.
                    if(pasos > 0) {
                        --pasos;
                        pause = (pasos == 0) ? true:false;
                    }

                    if (pause) {
                        ob_botones.setEstado(true);
                        waitContinue();
                    }

                    mensajes.clear();
                    estado = Estado.ACTUALIZAR_BASE_DATOS;
                    break;

                case ACTUALIZAR_BASE_DATOS:
                    divulgarMensaje(estado.toString(), ACLMessage.REQUEST);

                    n_moviles = moviles.size();
                    while(mensajes.size() != n_moviles) {
                        msg = recibirMensaje();
                        checkRemoveAgenteMuerto(msg);
                        mensajes.add(msg);
                    }

                    mensajes.clear();
                    estado = Estado.COMPROBAR_RECARGA;
                    break;

                case COMPROBAR_RECARGA:
                    divulgarMensaje(estado.toString(), ACLMessage.QUERY_IF);

                    while(mensajes.size() != moviles.size()) {
                        msg = recibirMensaje();
                        mensajes.add(msg);
                    }

                    MensajeRecarga recarga;
                    int recargas = 0;
                    for(ACLMessage m : mensajes) {
                        if(m.getPerformativeInt() == ACLMessage.CONFIRM) {
                            recarga = json_wrapper.interpretarMensajeRecarga(m.getContent());
                            energia_restante = recarga.energia_restante;

                            if (energia_restante == 0) {
                                enviarMensaje(m.getSender(), Estado.MUERETE.toString(), ACLMessage.REQUEST);
                            } else if (energia_restante > UMBRAL_BATERIA || recarga.exploracion) {
                                ++recargas;
                                enviarMensaje(m.getSender(), Estado.RECARGAR.toString(), ACLMessage.REQUEST);

                            } else {
                                cola_recarga.add(new Solicitante(m.getSender(), recarga.distancia, recarga.bateria_agente, recarga.gasto));
                            }

                        }
                    }

                    if(!cola_recarga.isEmpty()) {
                        Solicitante s = cola_recarga.poll();
                        if (s != null) {
                            ++recargas;
                            enviarMensaje(s.getID(), Estado.RECARGAR.toString(), ACLMessage.REQUEST);
                        }

                        s = cola_recarga.poll();
                        while (s != null) {
                            enviarMensaje(s.getID(), Estado.DORMIR.toString(), ACLMessage.REQUEST);
                        }
                    }

                    mensajes.clear();

                    while(mensajes.size() != recargas) {
                        msg = recibirMensaje();
                        checkRemoveAgenteMuerto(msg);
                        mensajes.add(msg);
                    }

                    mensajes.clear();
                    estado = Estado.SIGUIENTE_COMANDO;
                    break;

                case SIGUIENTE_COMANDO:
                    divulgarMensaje(estado.toString(), ACLMessage.REQUEST);

                    while(mensajes.size() != moviles.size()) {
                        msg = recibirMensaje();
                        posiciones_actuales.add(json_wrapper.interpretarSolicitudPosicionActual(msg.getContent()));
                        posiciones_siguientes.add(json_wrapper.interpretarSolicitudPosicionSiguiente(msg.getContent()));
                        mensajes.add(msg);
                    }

                    estado = Estado.CONFIRMAR_COMANDO;
                    break;

                case CONFIRMAR_COMANDO:

                    Map<PairInt, List<String>> conflictos_movimiento = new HashMap<>();
                    se_mueven = 0;

                    for(ACLMessage m : mensajes) {
                        PairInt mi_movimiento = json_wrapper.interpretarSolicitudPosicionSiguiente(m.getContent());
                        PairInt act_movimiento = json_wrapper.interpretarSolicitudPosicionActual(m.getContent());

                        if(!mi_movimiento.equals(act_movimiento)) {
                            int first_indice = posiciones_siguientes.indexOf(mi_movimiento);
                            int last_indice = posiciones_siguientes.lastIndexOf(mi_movimiento);

                            if (!posiciones_actuales.contains(mi_movimiento) && first_indice == last_indice) {
                                ++se_mueven;
                                enviarMensaje(m.getSender(), estado.toString(), ACLMessage.REQUEST);
                            } else if (!posiciones_actuales.contains(mi_movimiento) && first_indice != last_indice) {

                                if (!conflictos_movimiento.containsKey(mi_movimiento)) {
                                    List<String> agentes = new ArrayList<>();
                                    agentes.add(m.getSender().name);
                                    conflictos_movimiento.put(mi_movimiento, agentes);
                                } else {
                                    conflictos_movimiento.get(mi_movimiento).add(m.getSender().name);
                                }

                            }
                        }
                    }

                    if(!conflictos_movimiento.isEmpty()) {
                        Map<String, Integer> moviles_prioridad = bd.obtenerMovilesPrioridad();

                        conflictos_movimiento.forEach((posicion, agentes) -> {
                            String prioritario = agentes.get(0);
                            ++se_mueven;
                            for(String n : agentes) {
                                if(moviles_prioridad.get(prioritario) < moviles_prioridad.get(n)) {
                                    prioritario = n;
                                }
                            }

                            enviarMensaje(new AgentID(prioritario), estado.toString(), ACLMessage.REQUEST);

                        });
                    }

                    mensajes.clear();

                    while(mensajes.size() != se_mueven) {
                        msg = recibirMensaje();
                        checkRemoveAgenteMuerto(msg);
                        mensajes.add(msg);
                    }

                    mensajes.clear();
                    posiciones_actuales.clear();
                    posiciones_siguientes.clear();
                    mensajes.clear();
                    estado = Estado.PEDIR_SENSORES;
                    break;
            }

            if(moviles.size() == 0) {
                exit = true;

                enviarMensaje(AgentName.CONTROLADOR.toAgentId(), "", ACLMessage.CANCEL);
                msg = recibirMensaje();
                msg = recibirMensaje();

                bd.dropMoviles();
                imprimeTraza(msg);
                ob_botones.setEstado(true);
                this.ob_mensajes.setEstado("<span style=\"color:red\">TODOS LOS VEHICULOS HAN TERMINADO</span>: " + this.getName());
            }
        }
    }

    /**
     * Método para esperar los comándos de la interfaz gráfica.
     * @author Elías Méndez García
     */
    private void waitContinue() {
        msg = recibirMensaje();
        if(msg.getContent().equals("CONTINUAR")) {
            pause = false;
        } else if(msg.getContent().equals("SALIR")) {
            divulgarMensaje(Estado.MUERETE.toString(), ACLMessage.REQUEST);
        } else {
            pasos = Integer.valueOf(msg.getContent());
            pause = false;
        }
    }

    /**
     * Método encargado de imprimir la traza.
     *
     * @author Manuel Lafuente Aranda
     * @author Elías Méndez García: implementación en la práctica anterior
     *
     * @param inbox ACLMessage que contiene la traza a guardar
     */
    private void imprimeTraza(ACLMessage inbox) {
        byte[] img = json_wrapper.interpretarTraza(inbox.getContent());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mapa + ".png");
            fos.write(img);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        ob_mensajes.setEstado("Traza guardada.");
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
     * Método para comprobar si un mensaje recibido significa que un agente ha muero
     * @param m mensaje recibido
     * @author Elías Méndez García
     */
    private void checkRemoveAgenteMuerto(ACLMessage m) {
        String content = m.getContent();
        if(content.equals("SOLUCION") || content.equals("MUERTE")) {

            for(int i = 0; i < moviles.size();) {
                if(moviles.get(i).toString().equals(m.getSender().name)) {
                    moviles.remove(i);
                } else {
                    ++i;
                }
            }
        }
    }
}
