/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author Eila
 */
public class PanelEstado extends javax.swing.JPanel implements Observador {

    /**
     * Creates new form PanelEstado
     */
    public PanelEstado() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label_GPS = new javax.swing.JLabel();
        estado_GPS = new javax.swing.JTextField();
        label_bateria = new javax.swing.JLabel();
        estado_bateria = new javax.swing.JTextField();

        label_GPS.setText("GPS:");

        estado_GPS.setEditable(false);
        estado_GPS.setBackground(new java.awt.Color(255, 255, 255));
        estado_GPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estado_GPSActionPerformed(evt);
            }
        });

        label_bateria.setText("BATERÍA:");

        estado_bateria.setEditable(false);
        estado_bateria.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_GPS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(estado_GPS, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(label_bateria)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(estado_bateria, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(estado_bateria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_bateria, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_GPS)
                        .addComponent(estado_GPS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void estado_GPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estado_GPSActionPerformed
    }//GEN-LAST:event_estado_GPSActionPerformed

    @Override
    public void manejarEvento(Object info) {
        String json = (String) info;

        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();

        estado_bateria.setText(obj.get("bateria").getAsString());

        String x = obj.get("x").getAsString();
        String y = obj.get("y").getAsString();
        estado_GPS.setText("x: " + x + ", y: " + y);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField estado_GPS;
    private javax.swing.JTextField estado_bateria;
    private javax.swing.JLabel label_GPS;
    private javax.swing.JLabel label_bateria;

    // End of variables declaration//GEN-END:variables
}