package Agentes.Data;

/**
 * Clase para almacenar los datos necesarios para actualizar la posición de los vehículos en la interfaz 3d.
 * @author Elías Méndez García
 */
public class DatosMovil {
    public String name;
    public PairInt posicion;

    /**
     * Constructor por defecto
     * @author Elías Méndez García
     */
    public DatosMovil() {}

    /**
     * Constructor con valores.
     * @author Elías Méndez García
     * @param name nombre del agente
     * @param posicion posición en la que se encuentra
     */
    public DatosMovil(String name, PairInt posicion) {
        this.name = name;
        this.posicion = posicion;
    }
}
