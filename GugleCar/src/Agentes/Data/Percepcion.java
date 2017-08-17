package Agentes.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase encargada de almacenar la información asociada con el entorno y el vehículo
 *
 * @author Daniel Soto del Ojo: implementar esqueleto de la clase
 */
public class Percepcion {
    public int combustible;
    public Map<PairInt, PairInt> mapa;
    public PairInt posicion;
    public long energy;
    public boolean goal;

    /**
     * Constructor de la clase
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     */
    public Percepcion() {
        mapa = new HashMap<>();
        posicion = new PairInt();
    }
}
