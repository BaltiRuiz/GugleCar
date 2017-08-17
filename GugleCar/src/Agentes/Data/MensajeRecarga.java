package Agentes.Data;

/**
 * Clase usada para transmisión de datos a la hora de contactar con el Ccontable para una recarga
 *
 * @author Manuel Lafuente Aranda: implementación inicial
 * @author Baltasar Ruiz: modificaciones puntuales
 */
public class MensajeRecarga {
    public int bateria_agente, distancia;
    public long energia_restante;
    public int gasto;
    public boolean exploracion;

    /**
     * Constructor por parámetros de una instancia de este tipo
     *
     * @author Manuel Lafuente Aranda: implementación inicial
     *
     * @param ba Batería que tiene el agente emisor del mensaje
     * @param er Energía restante común a todos los agentes
     * @param g gasto del vehiculo por movimiento
     * @param d Distancia del agente emisor del mensaje hasta el objetivo
     * @param m Indicador de si se está explorando o no
     */
    public MensajeRecarga(int ba, long er, int g, int d, boolean m){
        bateria_agente = ba;
        energia_restante = er;
        gasto = g;
        distancia = d;
        exploracion = m;
    }
}
