/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Representacion3D;

import Agentes.Data.AgentName;
import Agentes.Data.DatosMovil;
import Agentes.Utilities.SQLWrapper;
import GUI.Inicio;
import GUI.Observador;
import GUI.VentanaControl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que controla toda la escena de la respresentación 3D así como manejar los eventos cuando se le manda
 * nueva información.
 *
 * @author Daniel Soto del Ojo implementación del esqueleto.
 * @author Elías Méndez García implementación e interpretación de eventos.
 * @author Eila Gómez Hidalgo implementación y lógica del moviento de la cámara.
 */
public class GestorEscena implements Observador {
    private VirtualUniverse universo;
    private Canvas3D canvas;
    private Locale locale;
    private Fondo fondo;
    private Map<String, Coche> vehiculos;
    private Mapa mapa;
    private TransformGroup transformCamara;
    private Limites limites;

    private SQLWrapper wrapper;

    /**
     * Contructor que genera el canvas, universo, luces, mapa y coche.
     * @author Elías Méndez García implementación.
     * @author Eila Gómez Hidalgo implementación y cambio del tamaño del canvas.
     */
    public GestorEscena(String map) {
        wrapper = new SQLWrapper(map);
        limites = Limites.getInstance();
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        vehiculos = new HashMap<>();
        canvas.setSize(900, 900);

        crearUniverso();

        AmbientLight ambiental = new AmbientLight();
        ambiental.setInfluencingBounds (limites);
        ambiental.setEnable(true);

        BranchGroup root = new BranchGroup();

        root.addChild(ambiental);

        // creamos el fondo y lo añadimos a la raiz.
        fondo = new Fondo();
        root.addChild(fondo);

        //root.addChild(new Box(1.0f, 0.0f, 1.0f, Apariencias.Libre.getAppearance()));

        // creamos el mapa y lo añadimos a la raiz
        mapa = new Mapa(map);
        root.addChild(mapa);

        this.generarMapa();
        this.moverCamara();

        // creamos el coche y lo añadimos a la raiz.
        Coche vehiculo = new Coche();
        vehiculos.put(AgentName.MOVIL_1.toString(), vehiculo);
        root.addChild(vehiculo);

        vehiculo = new Coche();
        vehiculos.put(AgentName.MOVIL_2.toString(), vehiculo);
        root.addChild(vehiculo);

        vehiculo = new Coche();
        vehiculos.put(AgentName.MOVIL_3.toString(), vehiculo);
        root.addChild(vehiculo);

        vehiculo = new Coche();
        vehiculos.put(AgentName.MOVIL_4.toString(), vehiculo);
        root.addChild(vehiculo);


        // Se optimiza la escena y se cuelga del universo
        root.compile();
        locale.addBranchGraph(root);
    }

    /**
     * Comunica al coche que tiene que desplazarse.
     *
     * @author Eila Gómez Hidalgo
     * @param fila fila del mundo cuadriculado donde se encuentra el coche.
     * @param columna columna del mundo cuadriculado donde se encuentra el coche.
     */
    private void moverCoche(String id, int fila, int columna) {
        vehiculos.get(id).mover(fila, columna);
    }

    /**
     * Comunica al mapa que ha llegado nueva información del radar para añadir los elementos nuevos.
     * @author Elías Méndez García
     */
    private void generarMapa() {
       // BoxShape3D box = mapa.createBox(1);
        //mapa.addPosicion(box, 98, 1);
        mapa.nuevoMovimiento();
    }

    /**
     * Método que posiciona la cámara dependiendo del tamaño del mapa.
     * @author Eila Gómez Hidalgo
     */
    private void moverCamara() {
        int tam_x = mapa.getMaxX() + mapa.getMinX();
        int tam_z = mapa.getMaxZ() + mapa.getMinZ();

        int dist_x = mapa.getMaxX() - mapa.getMinX();
        int dist_z = mapa.getMaxZ() - mapa.getMinZ();
        double altura = Math.sqrt(dist_x * dist_x + dist_z * dist_z) / 2.0f + limites.getAltoOcupado() * 2.5f;
        float centro_x = tam_x * 0.5f;
        float cantro_z = tam_z * 0.5f;

        Transform3D transform = new Transform3D();

        // obtenemos la transformacion.
        transformCamara.getTransform(transform);

        transform.lookAt(new Point3d(centro_x, -altura, cantro_z), new Point3d(centro_x, 0.0f, cantro_z), limites.getvUp());
        transform.invert();
        // establecemos la nueva transformacion.
        transformCamara.setTransform(transform);


    }

    /**
     * Método para crear el universo de donde van a colgar todos los objetos de la representación.
     * @author Elías Méndez García
     */
    private void crearUniverso() {
        this.universo = new VirtualUniverse();
        this.locale = new Locale(this.universo);

        // creamos la camara.
        BranchGroup vistas = new BranchGroup();
        crearCamara(new Point3d(99,5,0), new Point3d(99,0,0), limites.getvUp());

        vistas.addChild(transformCamara);

        locale.addBranchGraph(vistas);
    }

    /**
     * Método para crear la cámara y engancharla al view.
     *
     * @author Elías Méndez García implementación.
     * @author Eila Gómez Hidalgo implementación.
     * @param pos posición de la camara.
     * @param dir dirección hacia donde mira la cámara.
     * @param vUp vector de dirección para indicar cual es el arriba de la cámara.
     */
    private void crearCamara(Point3d pos, Point3d dir, Vector3d vUp) {

        ViewPlatform camara = new ViewPlatform();
        camara.setActivationRadius(1000.0f);

        // La transformación de vista, dónde se está, a dónde se mira, Vup
        transformCamara = new TransformGroup();

        transformCamara.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformCamara.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D viewTransform3D = new Transform3D();
        viewTransform3D.lookAt (pos, dir, vUp);
        viewTransform3D.invert();
        transformCamara.setTransform (viewTransform3D);

        transformCamara.addChild(camara);

        PointLight puntualSol = new PointLight();
        puntualSol.setInfluencingBounds (limites);
        puntualSol.setEnable(true);

        transformCamara.addChild(puntualSol);

        //Tipo de camara
        View viewPlanta = new View();
        viewPlanta.setPhysicalBody(new PhysicalBody());
        viewPlanta.setPhysicalEnvironment(new PhysicalEnvironment());
        viewPlanta.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
        viewPlanta.setFieldOfView(Math.toRadians(90));
        viewPlanta.setBackClipDistance(1000.0f);

        viewPlanta.addCanvas3D(canvas);
        viewPlanta.attachViewPlatform(camara);
    }


    /**
     * Método para obtener el canvas del gestor.
     * @author Daniel Soto del Ojo
     * @return canvas instancia del objeto canvas
     */
    public Canvas3D getCanvas() {
        return canvas;
    }

    /**
     * Método que se ejecuta al recibir un evento con nueva información para generar el mapa, mover el coche
     * y mover la cámara para que se vea al completo el mapa en la ventana.
     * @author Elías Méndez García
     * @param info información actualizada con el evento.
     */
    @Override
    public void manejarEvento(Object info) {

        List<DatosMovil> moviles = wrapper.obtenerMoviles();

        for(DatosMovil d : moviles) {
            moverCoche(d.name, d.posicion.second, d.posicion.first);
        }

        generarMapa();
        moverCamara();

//        Thread actualizar_coche = new Thread() {
//            @Override
//            public void run() {
//
//            }
//        };

        //actualizar_coche.start();
    }
    
}
