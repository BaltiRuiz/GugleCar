package Agentes.Old;

/**
 * Clase usada para transmisión de datos a la hora de contactar con el Contable para una recarga
 *
 * @deprecated
 * @author Manuel Lafuente Aranda
 * @author Baltasar Ruiz: modificaciones puntuales
 */
public class MensajeSolicitudAviso {
    public int bateria, distancia, gasto;
    public long energia;

    /**
     * Constructor por parámetros de una instancia de este tipo
     *
     * @author Manuel Lafuente Aranda
     *
     * @param ba Batería que tiene el agente emisor del mensaje
     * @param er Energía restante común a todos los agentes
     * @param d Distancia del agente emisor del mensaje hasta el objetivo
     */
    public MensajeSolicitudAviso(int ba, long er, int d, int g){
        bateria = ba;
        energia = er;
        distancia = d;
        gasto = g;
    }
}
