package Agentes.Data;

import Agentes.Utilities.HoraNombreAgentes;
import es.upv.dsic.gti_ia.core.AgentID;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que contiene el nombre de los diferentes agentes
 *
 * @author Daniel Soto del Ojo: implementar esqueleto de la clase
 * @author Elías Méndez García implementación.
 */
public enum AgentName {
    MOVIL_1("movil1-" + HoraNombreAgentes.now()),
    MOVIL_2("movil2-" + HoraNombreAgentes.now()),
    MOVIL_3("movil3-" + HoraNombreAgentes.now()),
    MOVIL_4("movil4-" + HoraNombreAgentes.now()),
    CONTROLODAR_LOCAL("controllador-" + HoraNombreAgentes.now()),
    CONTABLE("Contable" + HoraNombreAgentes.now()),
    CONTROLADOR("Bellatrix"),
    AGENTE_INTERFAZ("AgenteInterfaz");

    private final AgentID ID;
    private final String name;
    public static boolean explorando = true;

    /**
     * Método constructor de la clase
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @author Elías Méndez García implementación
     * @param nombre nombre del agente
     */
    private AgentName(String nombre) {
        this.ID = new AgentID(nombre);
        this.name = nombre;
    }

    /**
     * Método que devuelve el agentid de un enumerado
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @author Elías Méndez García implementación
     * @return AgentID del enumerado.
     */
    public AgentID toAgentId() {
        return this.ID;
    }

    /**
     * Método que devuelve el nombre de un agente enumerado.
     * @author Elías Méndez García implementación
     * @return String con el nombre del agente
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Método que devuelve una lista con todas las ids de los agentes.
     * @author Elías Méndez García implementación
     * @return lista de valores del enumerado.
     */
    public static List<AgentName> moviles() {
        List<AgentName> n = new ArrayList<>();

        n.add(AgentName.MOVIL_1);
        if(!explorando) {
            n.add(AgentName.MOVIL_2);
            n.add(AgentName.MOVIL_3);
            n.add(AgentName.MOVIL_4);
        }


        return n;
    }

    /**
     * Método que devuelve una lista con todas las ids de los agentes excepto el que lo llama.
     * @author Elías Méndez García implementación
     * @return lista de valores del enumerado.
     */
    public static List<AgentName> moviles(String movil) {
        List<AgentName> n = new ArrayList<>();

        if(!AgentName.MOVIL_1.toString().equals(movil)) {
            n.add(AgentName.MOVIL_1);
        }

        if(!explorando) {
            if (!AgentName.MOVIL_2.toString().equals(movil)) {
                n.add(AgentName.MOVIL_2);
            }

            if (!AgentName.MOVIL_3.toString().equals(movil)) {
                n.add(AgentName.MOVIL_3);
            }

            if (!AgentName.MOVIL_4.toString().equals(movil)) {
                n.add(AgentName.MOVIL_4);
            }
        }

        return n;
    }
}
