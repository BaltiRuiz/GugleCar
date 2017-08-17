package Agentes.Utilities;

import Agentes.Data.*;
import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;

import Agentes.Data.MensajeRecarga;
import Agentes.Old.MensajeSolicitudAviso;

/**
 * Clase singleton para tratar con todos los mensajes json de los agentes.
 *
 * @author Daniel Soto del Ojo: implementar esqueleto
 * @author Elías Méndez García
 * @author Baltasar Ruiz Hernández
 */
public class JsonWrapper {
    private static JsonWrapper instance = new JsonWrapper();
    private JsonParser parser;

    /**
     * Método constructor de la clase
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     */
    private JsonWrapper() {
        parser = new JsonParser();
    }

    /**
     * Método para obtener la instancia de la clase
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @return JsonWrapper instancia de la clase
     */
    public static JsonWrapper getInstance() {
        return instance;
    }

    /**
     * Método para generar el mensaje que divulga el nombre del primer agente creado
     *
     * @author Baltasar Ruiz Hernández: Implementación del método.
     * @param name Nombre del primer agente, a incluir en el mensaje.
     *
     */
    public String generateFirstAgentName(String name) {
        JsonObject obj = new JsonObject();
        obj.addProperty("agente_first", name);

        return obj.toString();
    }

    /**
     * Método para interpretar la divulgación del nombre del primer agente creado
     *
     * @author Baltasar Ruiz Hernández: Implementación del método.
     * @param json Mensaje recibido con el nombre del primer agente.
     */
    public String interpretarFirstAgentName(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();
        return obj.get("agente_first").getAsString();
    }

    /**
     * Método para generar el mensaje que divulga la muerte de un agente
     *
     * @author Baltasar Ruiz Hernández: Implementación del método.
     * @param name Nombre del agente que muere, a incluir en el mensaje.
     *
     */
    public String generateMensajeMuerte(String name) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("command", "MUERTO");

        return obj.toString();
    }

    /**
     * Método para interpretar la divulgación de la muerte de un agente
     *
     * @author Baltasar Ruiz Hernández: Implementación del método.
     * @param json Mensaje recibido con el nombre del agente muerto.
     */
    public String interpretarMensajeMuerte(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();
        return obj.get("name").getAsString();
    }

    /**
     * Método para generar el mensaje que divulga el desplazamiento de un agente a una casilla concreta
     *
     * @author Baltasar Ruiz Hernández: Implementación del método.
     * @param name Nombre del agente que muere, a incluir en el mensaje.
     *
     */
    public String generateDesplazamiento(String name, PairInt p) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("command", "COMPROBAR_CONFLICTO_MOVIMIENTO");
        obj.addProperty("x", p.first);
        obj.addProperty("y", p.second);

        return obj.toString();
    }

    /**
     * Método para interpretar la divulgación del desplazamiento de un agente a una casilla concreta
     *
     * @author Baltasar Ruiz Hernández: Implementación del método.
     * @param json Mensaje recibido con el nombre del agente muerto.
     */
    public PairInt interpretarDesplazamiento(String json) {
        PairInt pair_desplazamiento = new PairInt();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        pair_desplazamiento.first = obj.get("x").getAsInt();
        pair_desplazamiento.first = obj.get("y").getAsInt();

        return pair_desplazamiento;
    }

    /**
     * Método para generar el mensaje que divulge el conversationID
     *
     * @author Elías Méndez García Implementación del metodo.
     * @param id Conversation ID a incluir en el mensaje.
     *
     */
    public String generateConversationID(String id) {
        JsonObject obj = new JsonObject();
        obj.addProperty("conversation_id", id);

        return obj.toString();
    }

    /**
     * Método para interpretar la divulgación del conversationID
     * @author Elías Méndez García implementación inicial.
     * @param json Mensaje recibido con el conversation ID.
     */
    public String interpretarConversationID(String json) {

        JsonObject obj = parser.parse(json).getAsJsonObject();
        return obj.get("conversation_id").getAsString();

    }

    /**
     * Método para generar un mensaje que pueda ser interpretado por la interfaz gráfica para representar el estado.
     * @param bateria batería del agente.
     * @param posicion posición del agente.
     * @return String con el json.
     * @author Elías Méndez García
     */
    public String generarJsonObservablePercecpcion(int bateria, PairInt posicion) {
        JsonObject obj = new JsonObject();
        obj.addProperty("bateria", bateria);
        obj.addProperty("x", posicion.first);
        obj.addProperty("y", posicion.second);

        return obj.toString();
    }

    /**
     * Método para generar un mensaje que pueda ser interpretado por la interfaz gráfica para representar el estado en el 3d.
     * @param id id del agente.
     * @param posicion posición del agente.
     * @return String con el json.
     * @author Elías Méndez García
     */
    public String generarJsonObservableMapa(String id, PairInt posicion) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);
        obj.addProperty("x", posicion.first);
        obj.addProperty("y", posicion.second);

        return obj.toString();
    }

    /**
     * Método encargado de generar la suscripción en el servidor
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @param map nombre del mapa al que se suscribe
     * @return insert_info_here
     */
    public String generarSubscribe(String map) {
        JsonObject obj = new JsonObject();
        obj.addProperty("world", map);
        return obj.toString();
    }

    /**
     * Método encargado de generar un String formateado en json que contiene
     * la siguiente acción a realizar
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @param comando mensaje en json que contiene la respuesta al login
     * @return devuelve un mensaje en json con el comando seleccionado y la key
     */
    public String generarComando(Comando comando) {
        JsonObject obj = new JsonObject();
        obj.addProperty("command", comando.toString());
        return obj.toString();
    }

    /**
     * Método encargado de interpretar la percepción recibida por el agente
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @param json cadena que contieneel mensaje
     * @return percepción interpretada
     */
    public Percepcion interpretPercepcion(String json) {
        //System.out.println("JsonWrapper interpretando percepcion del mensaje " + json);

        Percepcion p = new Percepcion();
        JsonObject obj = parser.parse(json).getAsJsonObject().get("result").getAsJsonObject();

        p.combustible = obj.get("battery").getAsInt();
        p.posicion.first = obj.get("x").getAsInt();
        p.posicion.second = obj.get("y").getAsInt();

        JsonArray sensor  = obj.get("sensor").getAsJsonArray();

        int matrix_size = (int)Math.sqrt(sensor.size());
        int half_matrix = (matrix_size / 2);

        int x = p.posicion.first - half_matrix;
        int y = p.posicion.second - half_matrix;
        int i_row = 0;

        Map<PairInt, PairInt> m_sensor = new HashMap<>();

        for(JsonElement e  : sensor) {
            m_sensor.put(PairInt.valueOf(x, y), PairInt.valueOf(e.getAsInt(), 0));

            ++i_row;
            ++x;

            if(i_row % matrix_size == 0) {
                i_row = 0;
                x = p.posicion.first - half_matrix;
                ++y;
            }
        }

        p.mapa = m_sensor;
        p.energy = obj.get("energy").getAsLong();
        p.goal = obj.get("goal").getAsBoolean();

        return p;
    }

    /**
     * Método encargado de generar el mensaje de contacto con el contable para una recarga
     *
     * @author Baltasar Ruiz Hernández
     * @return devuelve un json que el contable solicitará interpretar posteriormente
     */
    public String generarMensajeRecarga(int localenergy, long globalenergy, int distance, int gasto, boolean mode){
        JsonObject obj = new JsonObject();
        obj.addProperty("localenergy", localenergy);
        obj.addProperty("globalenergy", globalenergy);
        obj.addProperty("distance", distance);
        obj.addProperty("gasto", gasto);
        obj.addProperty("mode", mode);
        return obj.toString();
    }

    /**
     * Método encargado de generar el mensaje de contacto con el contable para una recarga
     *
     * @author Manuel Lafuente Aranda: implementación del esqueleto
     * @author Baltasar Ruiz Hernández
     * @return devuelve un objeto de la clase MensajeRecarga con los datos que el contable necesita
     */
    public MensajeRecarga interpretarMensajeRecarga(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();

        int combustible = obj.get("localenergy").getAsInt();
        long energiaglobal = obj.get("globalenergy").getAsLong();
        int distancia = obj.get("distance").getAsInt();
        int gasto = obj.get("gasto").getAsInt();
        boolean modo = obj.get("mode").getAsBoolean();

        MensajeRecarga mr = new MensajeRecarga(combustible, energiaglobal, gasto, distancia, modo);

        return mr;
    }

    /**
     * Método encargado de generar el mensaje de contacto de un agente encolado con el contable para una recarga
     *
     * @author Baltasar Ruiz Hernández
     * @return devuelve un json que el contable solicitará interpretar posteriormente
     */
    public String generarMensajeSolicitudAviso(int localenergy, long globalenergy, int distance, int gasto){
        JsonObject obj = new JsonObject();
        obj.addProperty("localenergy", localenergy);
        obj.addProperty("globalenergy", globalenergy);
        obj.addProperty("distance", distance);
        obj.addProperty("gasto", gasto);

        return obj.toString();
    }

    /**
     * Método encargado de generar el mensaje de contacto de un agente encolado con el contable para una recarga
     *
     * @author Manuel Lafuente Aranda: implementación del esqueleto
     * @author Baltasar Ruiz Hernández
     * @return devuelve un objeto de la clase MensajeSolicitudAviso con los datos que el contable necesita
     */
    public MensajeSolicitudAviso interpretarMensajeSolicitudAviso(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();

        int combustible = obj.get("localenergy").getAsInt();
        long energiaglobal = obj.get("globalenergy").getAsLong();
        int distancia = obj.get("distance").getAsInt();
        int gasto = obj.get("gasto").getAsInt();

        MensajeSolicitudAviso msa = new MensajeSolicitudAviso(combustible, energiaglobal, distancia, gasto);

        return msa;
    }

    public String generarMensajeCapacidades(Capacidades capacidades) {

        return null;
    }

    /**
     * Método encargado de interpretar las capacidades del agente
     *
     * @author Daniel Soto del Ojo: implementar esqueleto
     * @param json cadena que contiene el mensaje
     * @return capacidades del agente
     */
    public Capacidades interpretarMensajeCapacidades(String json) {

        JsonObject obj = parser.parse(json).getAsJsonObject().get("capabilities").getAsJsonObject();
        int gasto = obj.get("fuelrate").getAsInt();
        boolean vuela = obj.get("fly").getAsBoolean();
        int rango = obj.get("range").getAsInt();

        Capacidades c = new Capacidades(rango, gasto, vuela);

        return c;
    }

    /**
     * Método para generar el mensaje de aviso de actualización del mapa
     *
     * @author Baltasar Ruiz Implementación del metodo.
     *
     */
    public String generateComunicarMapa() {
        JsonObject obj = new JsonObject();
        obj.addProperty("command", "ACTUALIZAR_MAPA");

        return obj.toString();
    }

    /**
     * Método para interpretar el mensaje de aviso de actualización del mapa
     *
     * @author Baltasar Ruiz implementación inicial.
     * @param json Mensaje recibido con el mensaje de aviso.
     */
    public String interpretarComunicarMapa(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();
        return obj.get("command").getAsString();
    }

    /**
     * Método para generar el mensaje para solicitar un cambio de posición durante la exploración
     *
     * @author Daniel Soto del Ojo
     * @param direccion a la que deseo moverme
     * @return objeto json
     */
    public String generateCambioPosicion(String direccion) {
        JsonObject obj = new JsonObject();
        obj.addProperty("exploracion", "exploracion");
        obj.addProperty("direccion", direccion);

        return obj.toString();
    }

    /**
     * Método encargado de generar la respuesta frente a una solicitud de cambio de posición
     * @param exito mensaje que informa sobre si ha podido realizarse la solicitud
     * @return si tuvo éxito la solicitud o no
     */
    public String generateRespuestaCambioPosicion(boolean exito) {
        JsonObject obj = new JsonObject();
        obj.addProperty("exito", exito);

        return obj.toString();
    }

    /**
     * Método encargado de traducir la respuesta por el mensaje recibido tras solicitar un cambio de posición
     * @param json mensaje que se ha recibido
     * @return si tuvo éxito la solicitud o no
     */
    public boolean interpretaRespuestaCambioPosicion(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();
        return obj.get("exito").getAsBoolean();
    }

    /**
     * Método encargado de interpretar el mensaje recibido por un dron de exploración que solicita cambiar nuestra posición
     * @param json mensaje que se ha recibido
     * @return si tuvo éxito la solicitud o no
     */
    public String interpretaCambioPosicion(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String direccion = obj.get("direccion").getAsString();
        return direccion;
    }

    /**
     * Método para interpretar la información de la traza.
     *
     * @author Elías Méndez García
     * @param msg mensaje en json que contiene la información de la traza.
     * @return un vector de bytes con la imagen.
     */
    public byte[] interpretarTraza(String msg) {
        JsonArray array = parser.parse(msg).getAsJsonObject().get("trace").getAsJsonArray();
        byte[] img =  (new Gson()).fromJson(array, byte[].class);

        return (img);
    }

    /**
     * Método encargado de comunicar al controlador local la posición a la cual se quiere mover un vehículo
     *
     * @author Daniel Soto del Ojo
     * @param posicion en la que está el vehículo
     * @return objeto json
     */
    public String generarSolicitudPosicion(PairInt posicion, PairInt siguiente_posicion) {
        JsonObject obj = new JsonObject();

        obj.addProperty("SOLICITUD_POSICION", "SOLICITUD_POSICION");
        obj.addProperty("x", posicion.first);
        obj.addProperty("y", posicion.second);
        obj.addProperty("new_x", siguiente_posicion.first);
        obj.addProperty("new_y", siguiente_posicion.second);

        return obj.toString();
    }

    /**
     * Método encargado de interpretar la posición actual en la que se encuentra el vehículo cuando solicita moverse.
     *
     * @param json mensaje que se ha recibido
     * @return posición actual del vehículo
     */
    public PairInt interpretarSolicitudPosicionActual(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();
        int x = obj.get("x").getAsInt();
        int y = obj.get("y").getAsInt();
        
        return PairInt.valueOf(x, y);
    }

    /**
     * Método encargado de interpretar la posición a la cual quiere moverse el vehículo cuando solicita moverse
     * .
     * @param json mensaje que se ha recibido
     * @return posición futura del vehículo
     */
    public PairInt interpretarSolicitudPosicionSiguiente(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();
        int x = obj.get("new_x").getAsInt();
        int y = obj.get("new_y").getAsInt();

        return PairInt.valueOf(x, y);
    }

    /**
     * Método encargado de interpretar al controlador local la posición a la cual se quiere mover un vehículo
     *
     * @author Daniel Soto del Ojo
     * @param json mensaje que se ha recibido
     * @return mensaje interpretado
     */
    public PairInt interpretarSolicitudPosicionPos(String json) {
        PairInt pos = new PairInt();

        JsonObject obj = parser.parse(json).getAsJsonObject();

        pos.first = obj.get("x").getAsInt();
        pos.second = obj.get("y").getAsInt();

        return pos;
    }

    /**
     * Método encargado de interpretar al controlador local la posición a la cual se quiere mover un vehículo
     *
     * @author Daniel Soto del Ojo
     * @param json mensaje que se ha recibido
     * @return mensaje interpretado
     */
    public Comando interpretarSolicitudPosicionDir(String json) {
        JsonObject obj = parser.parse(json).getAsJsonObject();

        Comando c = Comando.getComando(obj.get("direccion").getAsString());

        return c;
    }
}
