/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Representacion3D;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Texture;
import javax.vecmath.Color3f;

/**
 * Enumerado que contiene las apariencias de todos los tipos de objetos que vamos a dibujar.
 * @author Elías Méndez García implementación del esqueleto.
 * @author Eila Gómez Hidalgo implementación de los materiales.
 */
public enum Apariencias {
    Coche(Internal.Coche),
    Borde(Internal.Borde),
    Bloqueado(Internal.Bloqueado),
    Libre(Internal.Libre),
    Camino(Internal.Camino), 
    Objetivo(Internal.Objetivo);
    
    private enum Internal { Coche, Borde, Bloqueado, Libre, Camino, Objetivo; };
    
    private final Appearance ap;

    /**
     * Constructor que asigna una apariencia a cada elemento del enumerado.
     * @author Eila Gómez Hidalgo
     * @param internal valor del enumerado en cuestión.
     */
    private Apariencias(Internal internal) {
        ap = new Appearance();


        switch (internal) {
            case Coche:
                ap.setMaterial(new Material (
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color ambiental
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color emisivo
                        new Color3f (0.1f, 0.0f, 0.9f),   // Color difuso
                        new Color3f (0.2f, 0.2f, 0.5f),   // Color especular
                        0.25f ));
                break;
            case Bloqueado:
                ap.setMaterial(new Material (
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color ambiental
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color emisivo
                        new Color3f (0.01f, 0.01f, 0.01f),   // Color difuso
                        new Color3f (0.5f, 0.5f, 0.5f),   // Color especular
                        0.25f ));
                break;
            case Borde:
                ap.setMaterial(new Material (
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color ambiental
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color emisivo
                        new Color3f (0.5f, 0.5f, 0.0f),   // Color difuso
                        new Color3f (0.6f, 0.6f, 0.5f),   // Color especular
                        0.25f ));
                break;
            case Libre:
                ap.setMaterial(new Material (
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color ambiental
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color emisivo
                        new Color3f (0.55f, 0.55f, 0.55f),   // Color difuso
                        new Color3f (0.7f, 0.7f, 0.7f),   // Color especular
                        0.078f ));
                break;
            case Camino:
                ap.setMaterial(new Material (
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color ambiental
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color emisivo
                        new Color3f (0.1f, 0.35f, 0.1f),   // Color difuso
                        new Color3f (0.45f, 0.55f, 0.45f),   // Color especular
                        0.25f ));
                break;
            case Objetivo:
                ap.setMaterial(new Material (
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color ambiental
                        new Color3f (0.0f, 0.0f, 0.0f),   // Color emisivo
                        new Color3f (0.5f, 0.0f, 0.0f),   // Color difuso
                        new Color3f (0.7f, 0.6f, 0.6f),   // Color especular
                        0.25f ));
                break;
        }

    }

    /**
     * Método para obtener la apariencia de un valor concreto.
     * @author Eila Gómez Hidalgo
     * @return Appearance del tipo de objeto.
     */
    public Appearance getAppearance() {
        return ap;
    }
}
