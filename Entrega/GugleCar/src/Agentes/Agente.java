package Agentes;

//import com.sun.javafx.runtime.SystemProperties;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 * Clase base de la que heredarán nuestros agentes
 *
 * @author Daniel Soto del Ojo: Implementar esqueleto de la clase
 * @author Baltasar Ruiz Hernández: Implementación de algunos de los métodos
 */
public abstract class Agente extends SingleAgent {

    protected boolean salir;

    /**
     * Método constructor del agente
     *
     * @author Daniel Soto del Ojo
     * @param aid id del agente
     * @throws Exception si aid no válido
     */
    public Agente(AgentID aid) throws Exception {
        super(aid);
    }

    /**
     * Método encargado de enviar un mensaje a otro agente
     *
     * @author Daniel Soto del Ojo: implementación base
     * @author Baltasar Ruiz: adición de mensajes por consola
     * @author Manuel Lafuente Aranda: implementación en la práctica anterior
     * @param receiver Agente que recibirá el mensaje
     * @param content contenido del mensaje
     * @param performative performativa del mensaje
     */
    protected void enviarMensaje(AgentID receiver, String content, int performative) {
        ACLMessage outbox = new ACLMessage();
        outbox.setPerformative(performative);
        outbox.setSender(this.getAid());
        outbox.setReceiver(receiver);
        outbox.setContent(content);

        //System.out.println("\nEnviando mensaje de " + this.getName() + " a " + receiver.getLocalName() +
        //        " con la performativa " + ACLMessage.getPerformative(performative) + " y el contenido " + content);
        this.send(outbox);
    }

    /**
     * Método encargado de enviar un mensaje a otro agente, con un conversation_id
     *
     * @author Baltasar Ruiz Hernández: implementación del método
     * @param receiver Agente que recibirá el mensaje
     * @param content contenido del mensaje
     * @param performative performativa del mensaje
     * @param conversationid conversation_id del mensaj
     */
    protected void enviarMensajeConversationID(AgentID receiver, String content, int performative, String conversationid) {
        ACLMessage outbox = new ACLMessage();
        outbox.setPerformative(performative);
        outbox.setSender(this.getAid());
        outbox.setReceiver(receiver);
        outbox.setContent(content);
        outbox.setConversationId(conversationid);

        imprimir("\nEnviando mensaje de " + this.getName() + " a " + receiver.getLocalName() +
                " con la performativa " + ACLMessage.getPerformative(performative) +
                ", el conversation_id " + conversationid + " y el contenido " + content);

        this.send(outbox);
    }

    /**
     * Método encargado de enviar un mensaje a otro agente, con un conversation_id y un in_reply_to
     *
     * @author Baltasar Ruiz Hernández: implementación del método
     * @param receiver Agente que recibirá el mensaje
     * @param content contenido del mensaje
     * @param performative performativa del mensaje
     * @param conversationid conversation_id del mensaj
     */
    protected void enviarMensajeConversationIDInReplyTo(AgentID receiver, String content, int performative, String conversationid, String inreplyto) {
        ACLMessage outbox = new ACLMessage();
        outbox.setPerformative(performative);
        outbox.setSender(this.getAid());
        outbox.setReceiver(receiver);
        outbox.setContent(content);
        outbox.setConversationId(conversationid);
        outbox.setInReplyTo(inreplyto);

        imprimir("\nEnviando mensaje de " + this.getName() + " a " + receiver.getLocalName() +
                " con la performativa " + ACLMessage.getPerformative(performative) +
                ", el conversation_id " + conversationid + ", el in_reply_to " + inreplyto + " y el contenido " + content);
        this.send(outbox);
    }

    /**
     * Método encargado de recibir un mensaje
     *
     * @author Daniel Soto del Ojo
     * @author Baltasar Ruiz Hernández: adición de mensajes por consola
     * @author Manuel Lafuente Aranda: implementación en la práctica anterior
     * @return ACLMessage que contiene el mensaje recibido
     */
    protected ACLMessage recibirMensaje() {
        ACLMessage msg = null;
        try {
            msg = receiveACLMessage();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

//        System.out.println("\nRecibido mensaje por parte de " + this.getName() + ", procedente de "
//                + msg.getSender().getLocalName() + ", con la performativa " + msg.getPerformative()
//                + ", el conversation_id " + msg.getConversationId() + ", el in_reply_to " + msg.getReplyWith()
//                + " y con el contenido: " + msg.getContent());
        return msg;
    }

    /**
     * Método encargado de imprimir un mensaje
     *
     * @author Daniel Soto del Ojo
     * @param nombre variable que contiene el nombre el agente que imprime el mensaje
     * @param mensaje variable que contiene el mensaje que va a ser impreso
     */
    public void imprimir(String nombre, String mensaje) {
        //System.out.println(nombre + ": " + mensaje);
    }

    public void imprimir(String msg) {
        //System.out.println(msg);
    }
}
