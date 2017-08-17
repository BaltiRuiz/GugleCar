/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Representacion3D.PrimitiveShape3D;


import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;

/**
 * Clase que convierte un objeto del tipo Sphere a Shape3D
 * @author Elías Méndez García implementación para otra asignatura anterior.
 * @author Eila Gómez Hidalgo implementación para otra asignatura anterior.
 */
public class SphereShape3D extends Shape3D {

    /**
     * Constructor que coge una Sphere, extrae la información y la añade a nuestro Shape3D.
     * @author Elías Méndez García implementación para otra asignatura anterior.
     * @author Eila Gómez Hidalgo implementación para otra asignatura anterior.
     * @param s esfera para construir el Shape3D
     */
    public SphereShape3D(Sphere s) {
        Shape3D esfera = s.getShape();
        Appearance ap = s.getAppearance();

        this.setGeometry(esfera.getGeometry());
        this.setAppearance(ap);
    }

}
