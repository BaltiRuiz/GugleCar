package Agentes.Data;

import Agentes.Old.TipoAgente;

/**
 * Clase encargada de almacenar las características que tiene un agente
 *
 * @author Daniel Soto del Ojo: implementar esqueleto de la clase
 */
public class Capacidades {
    public int rango;
    public int gasto;
    public boolean vuela;
    public TipoAgente tipo;

    /**
     * Método constructor de la clase
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @param rango amplitud de la percepción del radar
     * @param gasto cantidad de combustible que consume el agente al moverse
     * @param vuela indica si el agente es capaz de pasar a través de obstáculos
     */
    public Capacidades(int rango, int gasto, boolean vuela) {
        this.rango = rango;
        this.gasto = gasto;
        this.vuela = vuela;

        if (this.vuela){
            this.tipo = TipoAgente.DRONE;
        } else if(this.rango == 5) {
            this.tipo = TipoAgente.COCHE;
        } else {
            this.tipo = TipoAgente.CAMION;
        }

    }

    /**
     * Constructor
     *
     * @author Daniel Soto del Ojo
     */
    public Capacidades() {

    }
}
