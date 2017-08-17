package Agentes.Data;

/**
 * Enumeración con todos los posibles comandos que podemos realizar con el
 * servidor, excepto las acciones realizadas con el login
 *
 * @author Daniel Soto del Ojo: implementar esqueleto de la clase
 */
public enum Comando {
    CHECKIN("checkin"),
    NORTE("moveN"),
    SUR("moveS"),
    ESTE("moveE"),
    OESTE("moveW"),
    NORESTE("moveNE"),
    SURESTE("moveSE"),
    SUROESTE("moveSW"),
    NOROESTE("moveNW"),
    RECARGAR("refuel"),
    ERROR("error"),
    DORMIR("dormir");

    private final String COMANDO;

    /**
     * Método constructor de la clase que construye el String asignado a cada enum.
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @param comando mensaje que contiene la acción a realizar
     */
    private Comando(String comando) {
        this.COMANDO = comando;
    }

    /**
     * Método para obtener el String asignado al valor de un enum.
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @return String asignado a ese valor del enum.
     */
    @Override
    public String toString() {
        return COMANDO;
    }

    /**
     * Método encargado de obtener un comando a partir de un string
     *
     * @author Daniel Soto del Ojo
     * @param comando string que contiene el comando
     * @return objeto de la clase Comando
     */
    public static Comando getComando(String comando) {

        switch (comando) {
            case "moveN":
                return Comando.NORTE;
            case "moveS":
                return Comando.SUR;
            case "moveW":
                return Comando.OESTE;
            case "moveE":
                return Comando.ESTE;
            case "moveNW":
                return Comando.NOROESTE;
            case "moveNE":
                return Comando.NORESTE;
            case "moveSW":
                return Comando.SUROESTE;
            case "moveSE":
                return Comando.SURESTE;
            default:
                return null;
        }
    }

}
