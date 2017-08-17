/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Agentes.AgenteInterfaz;
import Agentes.Controlador;
import Agentes.AgenteMovil;
import Agentes.Old.Contable;
import Agentes.Data.AgentName;
import Agentes.Utilities.SQLWrapper;
import GUI.Observables.Observable;
import GUI.Observables.ObservableBotones;
import GUI.Observables.ObservableInfo;
import GUI.Observables.ObservableMapa;
import Representacion3D.GestorEscena;

import javax.swing.UIManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.lang.System.exit;

/**
 *
 * @author Eila Gómez Hidalgo
 */
public class VentanaControl extends javax.swing.JFrame {

    private AgenteMovil movil_1;
    private AgenteMovil movil_2;
    private AgenteMovil movil_3;
    private AgenteMovil movil_4;
    private Controlador controlador;
    private Contable contable;
    private String map;
    private float porcentaje;
    private GestorEscena gestor_escena;

    /**
     * Creates new form VentanaControl
     */
    public VentanaControl() {
        initComponents();
        this.setTitle("Ventana de control");
       
        panelEstado_1.setBorder(javax.swing.BorderFactory.createTitledBorder("Movil 1"));
        panelEstado_2.setBorder(javax.swing.BorderFactory.createTitledBorder("Movil 2"));
        panelEstado_3.setBorder(javax.swing.BorderFactory.createTitledBorder("Movil 3"));
        panelEstado_4.setBorder(javax.swing.BorderFactory.createTitledBorder("Movil 4"));

        this.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent evt ) {
               /**/
                cerrar();
                exitForm(evt);
            }
        } );

    }

    /**
     * Método que avisa al vehículo de debe terminar cuando se pulsa el botón correspondiente.
     * @author Eila Gómez Hidalgo
     * @param evt acción que dispara la acción.
     */
    private void exitForm(java.awt.event.WindowEvent evt) {
        exit(0);
    }

    /**
     * Método para finalizar la ejecución del programa
     * @author Eila Gómez Hidalgo
     */
    private void cerrar() {
        AgenteInterfaz interfaz = null;
        try {
            interfaz = new AgenteInterfaz(AgentName.AGENTE_INTERFAZ.toAgentId(), AgenteInterfaz.SALIR);
            interfaz.start();
        } catch (Exception e) {
        }
    }
    /**
     * Método para crear y lanzar los agentes.
     * Al crear los agentes se les incluye los observadores a los que tienen que actualizar la información.
     * @author Eila Gómez Hidalgo implementación inicial.
     *
     */
    public void lanzarAgentes() throws Exception {

        // Crear la base de datos al principio para evitar locks.
        SQLWrapper sql = new SQLWrapper(map);
        sql.limpiarPases();

        boolean buscar = (sql.porcentajeMapaExplorado() >= porcentaje && sql.existeSolución());
        AgentName.explorando = !buscar;

        Observable ob_info_m1 = new ObservableInfo();
        ob_info_m1.incluirObservador(panelEstado_1);

        Observable ob_mensajes_m1 = new ObservableInfo();
        ob_mensajes_m1.incluirObservador(panel_mensajes);

        Observable ob_info_m2 = new ObservableInfo();
        ob_info_m2.incluirObservador(panelEstado_2);

        Observable ob_mensajes_m2 = new ObservableInfo();
        ob_mensajes_m2.incluirObservador(panel_mensajes);

        Observable ob_info_m3 = new ObservableInfo();
        ob_info_m3.incluirObservador(panelEstado_3);

        Observable ob_mensajes_m3 = new ObservableInfo();
        ob_mensajes_m3.incluirObservador(panel_mensajes);

        Observable ob_info_m4 = new ObservableInfo();
        ob_info_m4.incluirObservador(panelEstado_4);

        Observable ob_mensajes_m4 = new ObservableInfo();
        ob_mensajes_m4.incluirObservador(panel_mensajes);

        Observable ob_mensajes_controlador = new ObservableInfo();
        ob_mensajes_controlador.incluirObservador(panel_mensajes);

        Observable ob_mapa = new ObservableMapa();
        ob_mapa.incluirObservador(gestor_escena);

        ObservableBotones ob_botones = new ObservableBotones();
        ob_botones.incluirObservador(button_final);
        ob_botones.incluirObservador(button_npasos);
        ob_botones.incluirObservador(button_paso);
        ob_botones.incluirObservador(button_salir);


        controlador = new Controlador(AgentName.CONTROLODAR_LOCAL.toAgentId(), map, 400, this.porcentaje, ob_mensajes_controlador, ob_mapa, ob_botones);
        movil_1 = new AgenteMovil(AgentName.MOVIL_1.toAgentId(), true, this.porcentaje, 5, ob_mensajes_m1, ob_info_m1, this.map);
        movil_2 = new AgenteMovil(AgentName.MOVIL_2.toAgentId(), false, this.porcentaje, 5, ob_mensajes_m2, ob_info_m2, this.map);
        movil_3 = new AgenteMovil(AgentName.MOVIL_3.toAgentId(), false, this.porcentaje, 5, ob_mensajes_m3, ob_info_m3, this.map);
        movil_4 = new AgenteMovil(AgentName.MOVIL_4.toAgentId(), false, this.porcentaje, 5, ob_mensajes_m4, ob_info_m4,  this.map);

        controlador.start();
        if(buscar) {
            movil_4.start();
            movil_3.start();
            movil_2.start();
            ob_mensajes_controlador.setEstado("<b>COMENZANDO EN MODO BUSCAR SOLUCION</b>");
        } else {
            ob_mensajes_controlador.setEstado("<b>COMENZANDO EN MODO EXPLORACION</b>");
        }
        movil_1.start();


    }

    /**
     * Metodo para establecer el porcentaje al cual deben de dejar de explorar los agentes.
     * @author Elías Méndez García implementacion inicial.
     * @param porcentaje porcentaje que hay que explorar antes de finalizar.
     */
    public void setPorcentaje(float porcentaje) {
        this.porcentaje = porcentaje;
    }

    /**
     * Metodo para establecer el mapa que se va a explorar durante la ejecución.
     * @author Elías Méndez García implementacion inicial.
     * @param map mapa sobre el que se realiza la ejecución.
     */
    public void setMapa(String map) {
        this.map = map;
        gestor_escena = new GestorEscena(map);
    }

    /**
     * Método para mostrar esta ventana y la de la visualización 3D una vez están los datos creados.
     * @author Elías Méndez García Implementación inicial
     * @author Eila Gómez Hidalgo Adaptación a la práctica 3
     */
    public void showWindow() {
        try {
            VentanaRecorrido visualization = new VentanaRecorrido (this, false, gestor_escena.getCanvas());
            //visualization.setLocation(0, 0);
            visualization.setLocation(this.getWidth() + 100, 100);
            visualization.setVisible(true);
        } catch (Exception e) {
        }

        this.setVisible(true);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_botones = new javax.swing.JPanel();
        button_paso = new BotonObservador();
        button_final = new BotonObservador();
        jLabel2 = new javax.swing.JLabel();
        input_pasos = new javax.swing.JTextField();
        button_npasos = new BotonObservador();
        button_salir = new BotonObservador();
        jSeparator2 = new javax.swing.JSeparator();
        panel_mensajes = new GUI.PanelMensajes();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        panelEstado_2 = new GUI.PanelEstado();
        panelEstado_3 = new GUI.PanelEstado();
        panelEstado_4 = new GUI.PanelEstado();
        panelEstado_1 = new GUI.PanelEstado();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(242, 242, 242));

        panel_botones.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        button_paso.setText("AVANZAR UN PASO");
        button_paso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pasoActionPerformed(evt);
            }
        });

        button_final.setText("AVANZAR HASTA EL FINAL");
        button_final.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_finalActionPerformed(evt);
            }
        });

        jLabel2.setText("Introduce un nº de pasos:");

        input_pasos.setText("0");

        button_npasos.setText("AVANZAR");
        button_npasos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_npasosActionPerformed(evt);
            }
        });

        button_salir.setText("SALIR");
        button_salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_salirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_botonesLayout = new javax.swing.GroupLayout(panel_botones);
        panel_botones.setLayout(panel_botonesLayout);
        panel_botonesLayout.setHorizontalGroup(
            panel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_botonesLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(panel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addComponent(button_final, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button_paso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel_botonesLayout.createSequentialGroup()
                        .addComponent(input_pasos, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button_npasos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(button_salir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        panel_botonesLayout.setVerticalGroup(
            panel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_botonesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_paso)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_final)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(input_pasos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_npasos))
                .addGap(40, 40, 40)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(button_salir)
                .addGap(23, 23, 23))
        );

        jLabel1.setText("Información de los agentes");

        panelEstado_2.setBorder(javax.swing.BorderFactory.createTitledBorder("Hola"));

        panelEstado_3.setBorder(javax.swing.BorderFactory.createTitledBorder("Hola"));

        panelEstado_4.setBorder(javax.swing.BorderFactory.createTitledBorder("Hola"));

        panelEstado_1.setBorder(javax.swing.BorderFactory.createTitledBorder("Hola"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(601, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panelEstado_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelEstado_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jSeparator1)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(panel_mensajes, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(22, 22, 22)
                                    .addComponent(panel_botones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelEstado_3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addComponent(panelEstado_4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(panel_botones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel_mensajes, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                        .addGap(18, 18, 18)))
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(panelEstado_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelEstado_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(panelEstado_3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelEstado_4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Método que avisa a los vehículos de que deben realizar un paso cuando se pulsa el botón correspondiente.
     * @author Eila Gómez Hidalgo implementación.
     * @param evt evento que dispara la acción.
     */
    private void button_pasoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pasoActionPerformed
        AgenteInterfaz interfaz = null;
        try {
            interfaz = new AgenteInterfaz(AgentName.AGENTE_INTERFAZ.toAgentId(), 1);
            disbleButtons();
            interfaz.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_pasoActionPerformed

    /**
     * Método que avisa a los vehículos de que debes buscar la solución sin esperar cuando se pulsa el botón correspondiente.
     * @author Eila Gómez Hidalgo implementación.
     * @param evt acción que dispara la acción.
     */
    private void button_finalActionPerformed(java.awt.event.ActionEvent evt) {                                             
        AgenteInterfaz interfaz = null;
        try {
            interfaz = new AgenteInterfaz(AgentName.AGENTE_INTERFAZ.toAgentId(), AgenteInterfaz.CONTINUAR);
            disbleButtons();
            interfaz.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para desactivar los botones de la interfaz.
     * @author Elías Méndez García
     */
    private void disbleButtons() {
        button_final.setEnabled(false);
        button_npasos.setEnabled(false);
        button_paso.setEnabled(false);
        button_salir.setEnabled(false);
    }

    /**
     * Método que avisa a los vehículos de que deben ejecutar n pasos cuando se pulsa el botón correspondiente.
     * @author Eila Gómez Hidalgo implementación.
     * @param evt acción que dispara la acción.
     */
    private void button_npasosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_npasosActionPerformed
        int pasos = Integer.valueOf( input_pasos.getText());
        if(pasos > 0) {
            AgenteInterfaz interfaz = null;
            try {
                interfaz = new AgenteInterfaz(AgentName.AGENTE_INTERFAZ.toAgentId(), pasos);
                disbleButtons();
                interfaz.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_button_npasosActionPerformed

    /**
     * Método que avisa a los vehículos de deben terminar cuando se pulsa el botón correspondiente.
     * @author Eila Gómez Hidalgo
     * @param evt acción que dispara la acción.
            */
    private void button_salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_salirActionPerformed
        cerrar();
        this.dispose();
        exit(0);
    }//GEN-LAST:event_button_salirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BotonObservador button_final;
    private BotonObservador button_npasos;
    private BotonObservador button_paso;
    private BotonObservador button_salir;
    private javax.swing.JTextField input_pasos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private GUI.PanelEstado panelEstado_1;
    private GUI.PanelEstado panelEstado_2;
    private GUI.PanelEstado panelEstado_3;
    private GUI.PanelEstado panelEstado_4;
    private javax.swing.JPanel panel_botones;
    private GUI.PanelMensajes panel_mensajes;
    // End of variables declaration//GEN-END:variables
}
