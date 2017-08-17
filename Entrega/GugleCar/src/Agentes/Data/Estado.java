package Agentes.Data;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * Clase que contiene los distintos estados por los que pasa el controlador
 *
 * @author Elías Méndez García implementación
 * @author Baltasar Ruiz Hernández antigua implementación
 * @author Manuel Lafuente Aranda antigua implementación
 * @author Daniel Soto del Ojo: adición de varios estados
 */
public enum Estado {
    PEDIR_SENSORES("PEDIR_SENSORES"),
    ACTUALIZAR_BASE_DATOS("ACTUALIZAR_BASE_DATOS"),
    SIGUIENTE_COMANDO("SIGUIENTE_COMANDO"),
    COMPROBAR_RECARGA("COMPROBAR_RECARGA"),
    CONFIRMAR_COMANDO("CONFIRMAR_COMANDO"),
    RECARGAR("RECARGAR"),
    DORMIR("DORMIR"),
    MUERETE("MUERETE");

    private final String str;

    /**
     * Constructor de los valores del enumerado.
     * @param str cadena que queremos asignar al enumerado
     * @author Elías Méndez García
     */
    private Estado(String str) {
        this.str = str;
    }

    /**
     * Método que devuelve el string asociado al enumerado.
     * @return String asociado al enumerado.
     * @author Elías Méndez García
     */
    @Override
    public String toString() {
        return str;
    }

    /**
     * Método que obtiene un estado asimilado a un mensaje.
     * @author Daniel Soto del Ojo: adición de varios estados al método
     * @author Elías Méndez García implementación
     * @author Baltasar Ruiz Hernández antigua implementación
     * @author Manuel Lafuente Aranda antigua implementación
     * @param msg
     * @return
     */
    public static Estado toEstado(ACLMessage msg) {
        if (msg.getContent().equals("PEDIR_SENSORES") && msg.getPerformativeInt() == ACLMessage.REQUEST) {
            return Estado.PEDIR_SENSORES;
        } else if (msg.getContent().equals("ACTUALIZAR_BASE_DATOS") && msg.getPerformativeInt() == ACLMessage.REQUEST) {
            return Estado.ACTUALIZAR_BASE_DATOS;
        } else if (msg.getContent().equals("CONFIRMAR_COMANDO") && msg.getPerformativeInt() == ACLMessage.REQUEST) {
            return Estado.CONFIRMAR_COMANDO;
        } else if (msg.getContent().equals("SIGUIENTE_COMANDO") && msg.getPerformativeInt() == ACLMessage.REQUEST) {
            return Estado.SIGUIENTE_COMANDO;
        } else if (msg.getContent().equals("RECARGAR") && msg.getPerformativeInt() == ACLMessage.REQUEST) {
            return Estado.RECARGAR;
        } else if (msg.getContent().equals("DORMIR") && msg.getPerformativeInt() == ACLMessage.REQUEST) {
            return Estado.DORMIR;
        } else if (msg.getContent().equals("COMPROBAR_RECARGA") && msg.getPerformativeInt() == ACLMessage.QUERY_IF) {
            return Estado.COMPROBAR_RECARGA;
        } else if (msg.getContent().equals("MUERETE") && msg.getPerformativeInt() == ACLMessage.REQUEST) {
            return Estado.MUERETE;
        }

        return null;
    }
}
