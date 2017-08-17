/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Representacion3D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.j3d.*;
import javax.vecmath.Vector3f;

import Agentes.Data.DatosMovil;
import Agentes.Data.PairInt;
import Agentes.Utilities.SQLWrapper;
import Representacion3D.PrimitiveShape3D.BoxShape3D;
import com.sun.j3d.utils.geometry.Box;

/**
 * Clase que contiene el mundo cuadriculado del mapa.
 *
 * @author Daniel Soto del Ojo implementación del esqueleto.
 * @author Elías Méndez García implementación.
 * @author Eila Gómez Hidalgo implementación de las dimensiones del mapa.
 */
public class Mapa extends BranchGroup {

    private Map<Integer, Map<Integer, BoxShape3D> > mapa;
    private final int LIBRE = 0, BORDE = 2, BLOQUEADO = 1, SOLUCION = 3,  VEHICULO = 4;
    private int x_min, z_min, x_max, z_max;
    private Limites limites;
    private SQLWrapper wrapper;

    /**
     * Contructor del mapa.
     * @author Elías Méndez García
     */
    public Mapa(String map) {

        this.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        mapa = new HashMap<>();
        wrapper = new SQLWrapper(map);
        limites = Limites.getInstance();
        x_min = z_min = Integer.MAX_VALUE;
        x_max = z_max = Integer.MIN_VALUE;
    }

    /**
     * Método que añade casillas al mundo cuadriculado cuando ya no están en la respresentación.
     * @author Elías Méndez García implementación de añadir casillas.
     * @author Eila Gómez Hidalgo añadir la actualización de las dimensiones del mapa.
     */
    synchronized public void nuevoMovimiento() {

        Map<PairInt, PairInt> map = wrapper.obtenerMapaCoste(false);
        List<DatosMovil> moviles = wrapper.obtenerMoviles();

        map.forEach((p, c) -> {
            int fila_mundo = p.second;
            int columna_mundo = p.first;
            updateMaxMin(fila_mundo, columna_mundo);

            if(!mapa.containsKey(fila_mundo)) {
                // cramos la caja de la posicion actual.
                BoxShape3D box = createBox(c.first);

                // crear la columna.
                Map<Integer, BoxShape3D> mapa_columna = new HashMap<>();
                // añadimos la la caja a lacolumna actual.
                mapa_columna.put(columna_mundo, box);

                // añadir la nueva caja a la visualización y al mapa
                mapa.put(fila_mundo, mapa_columna);

                addPosicion(box,c.first, fila_mundo, columna_mundo);

            } else if(!mapa.get(fila_mundo).containsKey(columna_mundo)) {
                // cramos la caja de la posicion actual.
                BoxShape3D box = createBox(c.first);

                // añadir la nueva caja a la visualización y al mapa
                mapa.get(fila_mundo).put(columna_mundo, box);

                addPosicion(box, c.first, fila_mundo, columna_mundo);

            }

        });

        for(DatosMovil d : moviles) {
            // cambiamos la apariencia de la caja en la que nos encontramos para indicar que ya  hemos pasado.
            Map<Integer, BoxShape3D> fila = mapa.get(d.posicion.second);
            if(fila != null) {
                BoxShape3D box = fila.get(d.posicion.first);
                if (box != null)
                    box.setAppearance(Apariencias.Camino.getAppearance());
            }
        }

    }

    /**
     * Método para añadir una nueva casilla a la representación 3D.
     * @author Elías Méndez García
     * @param box objeto 3D a añadir.
     * @param posicion tipo de casilla.
     * @param fila fila en el mundo cuadriculado.
     * @param columna columna en el mundo cuadriculado.
     */
    public void addPosicion(BoxShape3D box, int posicion, int fila, int columna) {
        // transfomación de movimiento de la caja
        Transform3D poner_posicion = new Transform3D();
        if(posicion == BLOQUEADO || posicion == BORDE) {
            poner_posicion.set(new Vector3f(fila, -limites.getAltoOcupadoHalf(), columna));
        } else {
            poner_posicion.set(new Vector3f(fila, -limites.getAltoLibreHalf(), columna));
        }


        TransformGroup posicionCaja = new TransformGroup();
        posicionCaja.setTransform(poner_posicion);

        // añadimos la caja al movimento.
        posicionCaja.addChild(box);
        
        // añadimos la caja a la visualizacion
        BranchGroup caja = new BranchGroup();
        caja.addChild(posicionCaja);
        this.addChild(caja);
    }

    /**
     * Método para crear una caja como objeto 3D para representar la casilla.
     * @author Elías Méndez García
     * @param posicion tipo de casilla.
     * @return BoxShape3D con la apariencia del tipo de casilla.
     */
    public BoxShape3D createBox(int posicion) {

        BoxShape3D box = null;


        switch (posicion) {
            case LIBRE:
                box = new BoxShape3D(new Box(limites.getAncho(), limites.getAltoLibre(), limites.getLargo(), Apariencias.Libre.getAppearance()));
                break;

            case BLOQUEADO:
                box = new BoxShape3D(new Box(limites.getAncho(), limites.getAltoOcupado(), limites.getLargo(), Apariencias.Bloqueado.getAppearance()));
                break;

            case SOLUCION:
                box = new BoxShape3D(new Box(limites.getAncho(), limites.getAltoLibre(), limites.getLargo(), Apariencias.Objetivo.getAppearance()));
                break;

            case VEHICULO:
                box = new BoxShape3D(new Box(limites.getAncho(), limites.getAltoLibre(), limites.getLargo(), Apariencias.Camino.getAppearance()));
                break;

            case BORDE:
                box = new BoxShape3D(new Box(limites.getAncho(), limites.getAltoOcupado(), limites.getLargo(), Apariencias.Borde.getAppearance()));
                break;
        }

        // permitir que se le cambie la apariencia.
        box.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        box.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);


        return box;
    }

    /**
     * Método para actualizar el tamaño del mapa.
     * @author Eila Gómez Hidalgo
     * @param fila fila en el mundo cuadriculado.
     * @param columna columna en el mundo cuadriculado.
     */
    private void updateMaxMin(int fila, int columna) {
        
        if(fila < x_min)
            x_min = fila;

        if(fila > x_max)
            x_max = fila;

        if(columna < z_min)
            z_min = columna;

        if(columna > z_max)
            z_max = columna;

    }

    /**
     * Método para obtener el máximo tamaño en las x.
     * @author Elías Méndez García
     * @return tamaño máximo de las x.
     */
    public int getMaxX() {
        return x_max;
    }

    /**
     * Método para obtener el mínimo tamaño en las x.
     * @author Elías Méndez García
     * @return tamaño mínimo de las x.
     */
    public int getMinX() {
        return x_min;
    }

    /**
     * Método para obtener el máximo tamaño en las z.
     * @author Elías Méndez García
     * @return tamaño máximo de las z.
     */
    public int getMaxZ() {
        return z_max;
    }

    /**
     * Método para obtener el mínimo tamaño en las z.
     * @author Elías Méndez García
     * @return tamaño mínimo de las z.
     */
    public int getMinZ() {
        return z_min;
    }
}
