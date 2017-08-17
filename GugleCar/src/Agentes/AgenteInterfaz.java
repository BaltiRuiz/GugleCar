package Agentes;

import Agentes.Data.AgentName;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 * Clase encargada de realizar la gestión de la interfaz
 *
 * @author Daniel Soto del Ojo: implementar esqueleto de la clase
 */
public class AgenteInterfaz extends Agente {

    private int pasos;
    public static final int CONTINUAR = -1;
    public static final int SALIR = -2;

    /**
     * Método constructor del agente
     *
     * @param name nombre del agente
     * @throws Exception si aid no válido
     * @author Daniel Soto del Ojo
     */
    public AgenteInterfaz(AgentID name, int pasos) throws Exception {
        super(name);
        this.pasos = pasos;
    }

    /**
     * Método encargado de controlar la lógica del agente
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     */
    @Override
    public void execute() {
        ACLMessage outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(AgentName.CONTROLODAR_LOCAL.toAgentId());
        if(pasos == CONTINUAR) {
            outbox.setContent("CONTINUAR");
        } else if(pasos == SALIR) {
            outbox.setContent("SALIR");
        } else {
            outbox.setContent(Integer.toString(pasos));
        }

        this.send(outbox);
    }
}
