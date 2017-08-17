/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Representacion3D;

import Representacion3D.PrimitiveShape3D.SphereShape3D;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;

/**
 * Clase que forma el fondo de la escena.
 * @author Elías Méndez García implementación para otra asignatura anterior.
 * @author Eila Gómez Hidalgo implementación para otra asignatura anterior.
 */
public class Fondo extends BranchGroup {
    /**
     * Constructor que carga la imagen del fondo y la añade al branchgroup del objeto.
     * @author Elías Méndez García implementación para otra asignatura anterior.
     * @author Eila Gómez Hidalgo implementación para otra asignatura anterior.
     */
    public Fondo() {
        // Se crea el objeto para el fondo y
        //     se le asigna un área de influencia
        Background background = new Background ();
        background.setApplicationBounds (Limites.getInstance());

        // Se crea un aspecto basado en la textura a mostrar
        Appearance app = new Appearance ();
        Texture texture = new TextureLoader("imgs/back.jpg", null).getTexture();
        app.setTexture (texture);

        // Se hace la esfera con un determinado radio indicándole:
        //    - Que genere coordenadas de textura
        //    - Que genere las normales hacia adentro
        //    - Que tenga el aspecto creado

        Shape3D sphere = new SphereShape3D(new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD, app));

        // Se crea la rama para la geometría del fondo,
        BranchGroup bgGeometry = new BranchGroup ();
        // Se le añade la esfera
        bgGeometry.addChild (sphere);
        // Y se establece como geometría del objeto background
        background.setGeometry (bgGeometry);

        // Finalmente, se cuelga el fondo creado
        this.addChild (background);
    }
}
