/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Representacion3D.PrimitiveShape3D;

import com.sun.j3d.utils.geometry.Box;
import javax.media.j3d.Shape3D;

/**
 * Clase que convierte un objeto del tipo Box a Shape3D
 * @author Elías Méndez García implementación para otra asignatura anterior.
 * @author Eila Gómez Hidalgo implementación para otra asignatura anterior.
 */
public class BoxShape3D extends Shape3D {

    /**
     * Constructor que coge una box, extrae la información y la añade a nuestro Shape3D.
     * @author Elías Méndez García implementación para otra asignatura anterior.
     * @author Eila Gómez Hidalgo implementación para otra asignatura anterior.
     * @param b caja para construir el Shape3D
     */
    public BoxShape3D(Box b) {
        Shape3D lado = b.getShape(Box.FRONT);
        this.addGeometry(lado.getGeometry());

        lado = b.getShape(Box.TOP);
        this.addGeometry(lado.getGeometry());

        lado = b.getShape(Box.BOTTOM);
        this.addGeometry(lado.getGeometry());

        lado = b.getShape(Box.RIGHT);
        this.addGeometry(lado.getGeometry());

        lado = b.getShape(Box.LEFT);
        this.addGeometry(lado.getGeometry());

        lado = b.getShape(Box.BACK);
        this.addGeometry(lado.getGeometry());

        this.setAppearance(b.getAppearance());
    }

}
