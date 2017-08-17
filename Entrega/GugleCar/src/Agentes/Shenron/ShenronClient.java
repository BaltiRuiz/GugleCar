/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agentes.Shenron;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author Luis Castillo
 */
public class ShenronClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String controller = "Bellatrix",
                user="Pitol",
                password="Canmaior";

        System.out.println("Conectando ...");
        AgentsConnection.connect("isg2.ugr.es",6000, "test", "guest", "guest", false);

        try {
            System.out.println("Lanzando a Mutenroshi "
                    + "...");
            Mutenroshi m = new Mutenroshi(new AgentID("Mutenroshi"), controller, user, password);
            m.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
