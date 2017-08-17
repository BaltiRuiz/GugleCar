/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Representacion3D;

import Representacion3D.PrimitiveShape3D.SphereShape3D;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Clase que contiene el objeto del coche y lo mueve en el espacio.
 * @author Daniel Soto del Ojo implementación del esqueleto.
 * @author Elías Méndez García implementación del esqueleto y la funcionalidad.
 * @author Eila Gómez Hidalgo implementación del esqueleto y la funcionalidad.
 */
public class Coche extends BranchGroup {
    private TransformGroup posicion_actual;

    /**
     * Constructor que crea el objeto que se usa como coche y lo añade como hijo a este objeto.
     * @author Elías Méndez García implementación del esqueleto y la funcionalidad.
     * @author Eila Gómez Hidalgo implementación del esqueleto y la funcionalidad.
     */
    public Coche() {
        // ponemos una transformacion que no haga nada
        Transform3D transform = new Transform3D();
        transform.set(new Vector3d(0.0f, 0.0f, 0.0f));

        posicion_actual = new TransformGroup(transform);
        posicion_actual.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        posicion_actual.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        // creamos la esfera
        Limites limites = Limites.getInstance();

        int flagsSphere = Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS;
        SphereShape3D sphere = new SphereShape3D(new Sphere(limites.getRadio(),
                flagsSphere, limites.getDivisiones(), Apariencias.Coche.getAppearance()));

        // añadimos la esfera al coche
        posicion_actual.addChild(sphere);
        this.addChild(posicion_actual);

    }

    /**
     * Método para trasladar el coche de posición.
     *
     * @author Elías Méndez García implementación del esqueleto y la funcionalidad.
     * @author Eila Gómez Hidalgo implementación del esqueleto y la funcionalidad.
     * @param fila fila del mundo cuadriculado donde se encuentra el coche.
     * @param columna columna del mundo cuadriculado donde se encuentra el coche.
     */
    public void mover(int fila, int columna) {
        Transform3D transform = new Transform3D();

        // obtenemos la transformacion.
        posicion_actual.getTransform(transform);

        transform.set(new Vector3f(fila, 0.0f, columna));

        // establecemos la nueva transformacion.
        posicion_actual.setTransform(transform);

    }
}
