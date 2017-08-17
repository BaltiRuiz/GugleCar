package Agentes.Old;

/**
 * Enumerado que contiene los tipos de agente en los que puede convertirse el AgenteMovil
 *
 * @deprecated
 * @author Daniel Soto del Ojo: implementar esqueleto de la clase
 */
public enum TipoAgente {
    DRONE("Drone"),
    COCHE("Coche"),
    CAMION("Camion");

    private final String TIPO;

    /**
     * Método constructor privado para construir el String asignado a cada enum
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @param tipo contiene el tipo de agente que va a ser el agente
     */
    TipoAgente(String tipo) {
        this.TIPO = tipo;
    }

    /**
     * Método para obtener el String asignado al valor de un enum
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @return String que contiene el tipo del agente
     */
    @Override
    public String toString() {
        return this.TIPO;
    }
}
