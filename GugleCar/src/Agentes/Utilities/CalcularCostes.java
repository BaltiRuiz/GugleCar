package Agentes.Utilities;

import Agentes.Data.PairInt;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Clase para crear una hebra que calcule el coste de un mapa a raiz de una casilla de solución.
 * @author Elías Méndez García
 * @author Eila Gómez Hidalgo
 */
public class CalcularCostes extends Thread {
    private Map<PairInt, PairInt> mapa;
    private Map<PairInt, Integer> mapa_coste;
    private PairInt solucion;
    private boolean vuela;

    /**
     * Constructor de la hebra
     * @param mapa mapa sobre el que queremos calcular
     * @param solucion casilla donde se encuentra la solución
     * @param vuela si se debe calcular con vuelo o no
     * @author Elías Méndez García
     */
    public CalcularCostes(Map<PairInt, PairInt> mapa, PairInt solucion, boolean vuela) {
        this.mapa = mapa;
        this.mapa_coste = new HashMap<>();
        this.solucion = solucion;
        this.vuela = vuela;
    }

    /**
     * Método para calcular los costes desde la casilla de solución dada.
     * @author Eila Gómez Hidalgo
     */
    @Override
    public void run() {
        int distancia = 0;

        Queue<PairInt> cola = new LinkedBlockingDeque<>();
        cola.add(solucion);
        mapa_coste.put(solucion, distancia);

        while(!cola.isEmpty()){
            PairInt siguiente = cola.poll();
            List<PairInt> adyacentes = Posiciones.posicionesAlrededor(siguiente.first, siguiente.second);
            distancia = mapa_coste.get(siguiente) + 1;
            for(PairInt casilla : adyacentes){

                if(mapa.containsKey(casilla) &&
                        casillaValida(mapa.get(casilla).first) &&
                        !mapa_coste.containsKey(casilla)){

                    cola.add(casilla);
                    mapa_coste.put(casilla, distancia);

                }
            }
        }
    }

    /**
     * Método que nos dice si podemos pasar por una casilla o no.
     * @param tipo_casilla tipo de la casilla
     * @return true si podemos pasar por ella, false en otro caso.
     * @author Eila Gómez Hidalgo
     */
    private boolean casillaValida(int tipo_casilla) {
        if(vuela){
            return tipo_casilla != 2;
        }else {
            return tipo_casilla != 2 && tipo_casilla != 1;
        }
    }

    /**
     * Método para obtener el mapa de costes calculado por la hebra.
     * @author Elías Méndez García
     * @return Mapa con los costes calculados.
     */
    public Map<PairInt, Integer> getMapaCoste() {
        return this.mapa_coste;
    }
}