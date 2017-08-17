/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Representacion3D;

import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Clase que contiene todos los tamaños para generar los objetos así como limitar  la región activa de dibujo.
 * @author Daniel Soto del Ojo implementación del esqueleto.
 * @author Elías Méndez García implementación.
 * @author Eila Gómez Hidalgo correción de los valores.
 */
public class Limites extends BoundingSphere {
    private static Limites instance = new Limites();
    private float alto_libre, alto_ocupado, ancho, largo, radio, alto_libre_half, alto_ocupado_half;
    private int divisiones;
    private Vector3d vUp;

    /**
     * Constructor privado(patrón singleton) para generar los límites con los valores correctos.
     * @author Elías Méndez García primera implementación.
     * @author Eila Gómez Hidalgo correción de tamaños y otros valores.
     */
    private Limites() {
        super(new Point3d(0.0f, 0.0f, 0.0f), 500.0f);
        ancho = largo = 0.5f;
        alto_ocupado = 0.5f;
        alto_libre = 0.1f;
        radio = 0.5f;
        divisiones = 30;
        alto_libre_half = alto_libre * 0.5f;
        alto_ocupado_half = alto_ocupado * 0.5f;
        vUp = new Vector3d(-1,0,0);
    }
    
    /**
     * Método que devuelve la única instancia de la clase Limites.
     * @author Daniel Soto del Ojo
     * @return instancia de la clase Limites para poder usarla
     */
    public static Limites getInstance() {
        return instance;
    }


    /**
     * Método que devuelve el ancho que deben tener los objetos del mundo cuadriculado.
     * @author Daniel Soto del Ojo
     * @return devuelve el ancho del lado
     */
    public float getAncho() {
        return ancho;
    }

    /**
     * Método que devuelve el alto que ocupan los objetos que tienen altura.
     * @author Elías Méndez García
     * @return devuelve el alto de los objetos que ocupan espacio.
     */
    public float getAltoOcupado() {
        return alto_ocupado;
    }

    /**
     * Método que devuelve el alto que ocupan los objetos que tienen altura plana.
     * @author Elías Méndez García
     * @return devuelve el alto de los objetos que son planos.
     */
    public float getAltoLibre() {
        return alto_libre;
    }

    /**
     * Método que obtiene el largo de los objetos.
     * @author Elías Méndez García
     * @return devuelve el largo de los objetos del mundo cuadriculado.
     */
    public float getLargo() {
        return largo;
    }

    /**
     * Método que devuelve el radio de los objetos esféricos.
     * @author Elías Méndez García
     * @return radio de los objetos circulares.
     */
    public float getRadio() {
        return radio;
    }

    /**
     * Método que devuelve el número de divisiones que tiene los objetos esféricos ensu creación.
     * @author Elías Méndez García
     * @return devuelve el número de divisiones.
     */
    public int getDivisiones() {
        return divisiones;
    }

    /**
     * Método que devuelve el vector vUp.
     * @author Elías Méndez García
     * @return vUp para la cámara.
     */
    public Vector3d getvUp() {
        return vUp;
    }

    /**
     * Método que devuelve la mitad de la altura de los objetos planos.
     * @author Elías Méndez García
     * @return mitad de la altura de los objetos planos.
     */
    public float getAltoLibreHalf() {
        return alto_libre_half;
    }

    /**
     * Método que devuelve la mitad de la altura de los objetos con altura.
     * @author Elías Méndez García
     * @return mitad de la altura de los objetos.
     */
    public float getAltoOcupadoHalf() {
        return alto_ocupado_half;
    }
}
