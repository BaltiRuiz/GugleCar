package GUI;

import javax.swing.*;

/**
 * Clase para poder extender los JButtons de Java como observadores.
 * @author Elías Méndez García
 */
public class BotonObservador extends JButton implements Observador {

    /**
     * Método para activar el botón cuando se recibe un evento.
     * @author Elías Méndez García
     * @param info evento para interpretar por el observador.
     */
    @Override
    public void manejarEvento(Object info) {
        this.setEnabled(true);
    }
}
