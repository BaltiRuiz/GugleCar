package Agentes.Utilities;

import Agentes.Data.Capacidades;
import Agentes.Data.DatosMovil;
import Agentes.Data.PairInt;

import java.sql.*;
import java.util.*;

/**
 * Clase encargada de las comunicaciones con la base de datos
 *
 * @author Elías Méndez García: Implementar mayoría de la clase y depuración
 * @author Daniel Soto del Ojo: Implementación de algunos métodos y depuración
 * @author Eila Gómez Hidalgo: Implementación de algunos métodos y depuración
 * @author Baltasar Ruiz Hernández: Implementación de uno de los métodos
 */
public class SQLWrapper {

    private String url;
    public static final int CASILLA_SOLUCION = 3;
    public static final int CASILLA_MOVIL = 4;
    public static final int CASILLA_LIBRE = 0;
    public static final int BLOQUED = -1;
    public static final int WORKING = 0;

    /**
     * Constructor para generar la base de datos del mapa actual.
     * @param map mapa actual
     * @author Elías Méndez García
     */
    public SQLWrapper(String map) {
        String db = "db/" + map + ".db";

        this.url = "jdbc:sqlite:" + db;

        this.createMapa();
        this.createMoviles();
    }

    /**
     * Método para borrar el número de pasos de un mapa.
     * @author Elías Méndez García
     */
    public void limpiarPases() {
        String sql_moviles = "UPDATE mapa SET n_pases=0";

        try {
            Connection connection = this.connect();

            if(connection != null) {
                Statement statement = connection.createStatement();
                statement.execute(sql_moviles);

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para crear la tabla de los vehículos.
     * @author Elías Méndez García
     */
    public void createMoviles() {
        String sql_moviles = "CREATE TABLE IF NOT EXISTS moviles (\n"
                + "nombre VARCHAR(80) PRIMARY KEY,\n"
                + "x INTEGER NOT NULL DEFAULT 0,\n"
                + "y INTEGER NOT NULL DEFAULT 0,\n"
                + "gasto INTEGER,\n"
                + "bateria INTEGER DEFAULT 0,\n"
                + "estado INTEGER DEFAULT 0\n"
                + ");";

        try {
            Connection connection = this.connect();

            if(connection != null) {
                Statement statement = connection.createStatement();
                statement.execute(sql_moviles);

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para crear la tabla del mapa.
     * @author Elías Méndez García
     */
    public void createMapa() {
        String sql_mapa = "CREATE TABLE IF NOT EXISTS mapa (\n"
                + "x INTEGER NOT NULL,\n"
                + "y INTEGER NOT NULL,\n"
                + "tipo_casilla INTEGER NOT NULL,\n"
                + "n_pases INTEGER NOT NULL DEFAULT 0,\n"
                + "coste_suelo INTEGER NOT NULL DEFAULT " + Integer.MAX_VALUE + ",\n"
                + "coste_aire INTEGER NOT NULL DEFAULT  " + Integer.MAX_VALUE + ",\n"
                + "PRIMARY KEY(x, y)\n"
                + ");";

        try {
            Connection connection = this.connect();

            if(connection != null) {
                Statement statement = connection.createStatement();
                statement.execute(sql_mapa);

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para alterar el estádo de un móvil
     * @param nombre nombre del movil
     * @param estado nuevo estado del movil
     * @author Elías Méndez García
     */
    public void setEstadoMovil(String nombre, int estado) {

        String sql = "UPDATE moviles SET estado=? WHERE nombre=?";

        boolean intentar = true;
        while(intentar) {
            try {
                Connection connection = this.connect();

                if (connection != null) {
                    PreparedStatement statement = connection.prepareStatement(sql);

                    statement.setInt(1, estado);
                    statement.setString(2, nombre);

                    statement.executeUpdate();

                    intentar = false;
                    statement.close();
                    connection.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para obtener las posiciones donde se encuentran vehículos bloqueados.
     * @return lista de las posiciones donde se encuentran vehículos bloqueados.
     * @author Elías Méndez García
     */
    public List<PairInt> getPosicionesBloqueadas(){
        String sql = "SELECT x, y FROM moviles WHERE estado = ?";
        List<PairInt> lista = new ArrayList<>();

        try {
            Connection connection = this.connect();

            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setInt(1, BLOQUED);

                ResultSet result = statement.executeQuery();

                while(result.next()) {
                    int x = result.getInt("x");
                    int y = result.getInt("y");
                    PairInt pos = PairInt.valueOf(x,y);
                    lista.add(pos);
                }

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Método para insertar un móvil dentro de la base de datos
     * @param name nombre del móvil
     * @param gasto gasto del móvil
     * @author Elías Méndez García
     */
    public void insertMovil(String name, int gasto) {
        String sql = "INSERT INTO moviles(nombre, gasto) VALUES(?,?)";

        try {
            Connection connection = this.connect();

            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setString(1, name);
                statement.setInt(2, gasto);

                statement.executeUpdate();

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para obtener todos los móviles. Se usa para moverlos dentro de la interfaz 3d
     * @return Lista con los datos de los móviles.
     * @author Elías Méndez García
     */
    public List<DatosMovil> obtenerMoviles(){
        List<DatosMovil> moviles = new ArrayList<>();

        String sql = "SELECT * FROM moviles";

        try {
            Connection connection = this.connect();

            if(connection != null) {
                Statement statement = connection.createStatement();

                ResultSet result = statement.executeQuery(sql);

                DatosMovil datos;
                while (result.next()) {
                    int x = result.getInt("x");
                    int y = result.getInt("y");
                    String nombre = result.getString("nombre");
                    PairInt pos = PairInt.valueOf(x,y);
                    datos = new DatosMovil(nombre, pos);
                    moviles.add(datos);
                }

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return moviles;
    }

    /**
     * Método para obtener los móviles ordenados por priosidad según su gasto
     * @return mapa con los móviles y su prioridad.
     * @author Eila Gómez Hidalgo.
     */
    public Map<String, Integer> obtenerMovilesPrioridad(){
        Map<String, Integer> moviles = new HashMap<>();

        String sql = "SELECT * FROM moviles ORDER BY gasto ASC, nombre ASC";

        try {
            Connection connection = this.connect();

            if(connection != null) {
                Statement statement = connection.createStatement();

                ResultSet result = statement.executeQuery(sql);

                DatosMovil datos = null;
                String nombre = null;
                int i = 0;

                while (result.next()) {
                    nombre = result.getString("nombre");
                    moviles.put(nombre, i);
                    ++i;
                }

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return moviles;
    }

    /**
     * Método para obtener el mapa de pases de la base de datos.
     * @return mapa con todas las casillas
     * @author Elías Méndez García
     */
    public Map<PairInt, PairInt> obtenerMapaPases() {

        Map<PairInt, PairInt> mapa = new HashMap<>();

        String sql_mapa = "SELECT * FROM mapa";
        String sql_moviles = "SELECT x, y FROM moviles";

        try {
            Connection connection = this.connect();

            if(connection != null) {
                Statement statement_mapa = connection.createStatement();
                Statement statement_moviles = connection.createStatement();

                ResultSet result_mapa = statement_mapa.executeQuery(sql_mapa);
                ResultSet result_moviles = statement_moviles.executeQuery(sql_moviles);


                while (result_mapa.next()) {
                    int x = result_mapa.getInt("x");
                    int y = result_mapa.getInt("y");
                    int tipo_casilla = result_mapa.getInt("tipo_casilla");
                    int n_pases = result_mapa.getInt("n_pases");

                    mapa.put(PairInt.valueOf(x, y), PairInt.valueOf(tipo_casilla, n_pases));
                }

                while (result_moviles.next()) {
                    int x = result_moviles.getInt("x");
                    int y = result_moviles.getInt("y");
                    PairInt p = mapa.get(PairInt.valueOf(x, y));
                    if(p != null)
                        mapa.get(PairInt.valueOf(x, y)).first = CASILLA_MOVIL;
                    else
                        mapa.put(PairInt.valueOf(x, y), PairInt.valueOf(CASILLA_MOVIL, 1));
                }

                statement_mapa.close();
                statement_moviles.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapa;
    }

    /**
     * Método para obtener el mápa con las superficies de coste calculadas.
     * @param vuela si el movil puede volar
     * @return mapa con las casillas
     * @author Elías Méndez García
     */
    public Map<PairInt, PairInt> obtenerMapaCoste(boolean vuela) {

        Map<PairInt, PairInt> mapa = new HashMap<>();
        String campo = (vuela) ? "coste_aire":"coste_suelo";


        String sql = "SELECT * FROM mapa";

        try {
            Connection connection = this.connect();

            if(connection != null) {
                Statement statement = connection.createStatement();

                ResultSet result = statement.executeQuery(sql);


                while (result.next()) {
                    int x = result.getInt("x");
                    int y = result.getInt("y");
                    int tipo_casilla = result.getInt("tipo_casilla");
                    int n_pases = result.getInt(campo);

                    mapa.put(PairInt.valueOf(x, y), PairInt.valueOf(tipo_casilla, n_pases));
                }

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapa;
    }

    /**
     * Método para actualizar el n_pases de una casilla
     * @param x
     * @param y
     * @author Elías Méndez García
     */
    public void actualizarPases(int x, int y) {

        String sql = "UPDATE mapa SET n_pases=n_pases+1 WHERE x=? AND y=?";
        boolean intentar = true;
        while (intentar) {
            try {
                Connection connection = this.connect();

                if (connection != null) {
                    PreparedStatement statement = connection.prepareStatement(sql);

                    statement.setInt(1, x);
                    statement.setInt(2, y);

                    statement.executeUpdate();

                    intentar = false;
                    statement.close();
                    connection.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    /**
     * Método para actualizar el mapa de la base de datos.
     * @param mapa mapa con la nueva información a introducir.
     * @author Elías Méndez García
     */
    public void actualizarMapaPases(Map<PairInt, PairInt> mapa) {
        String sql = "INSERT OR IGNORE INTO mapa(x,y,tipo_casilla) VALUES(?,?,?)";

        try {
            Connection connection = this.connect();

            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(sql);
                mapa.forEach((p, c) -> {
                    if (c.first != CASILLA_MOVIL) {
                        boolean intentar = true;
                        while(intentar) {
                            try {

                                statement.setInt(1, p.first);
                                statement.setInt(2, p.second);
                                statement.setInt(3, c.first);

                                statement.executeUpdate();
                                intentar = false;

                            } catch (SQLException e) {
                            }
                        }
                    }
                });

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
        }

    }

    /**
     * Método que calcula el porcentaje actual de mapa explorado
     *
     * @author Daniel Soto del Ojo
     * @return porcentaje de mapa explorado si se han encontrado los extremos del mapa
     * @return 0 si no se han encontrado los extremos del mapa
     */
    public float porcentajeMapaExplorado() {

        String sql_max_x = "SELECT DISTINCT m.x AS x FROM mapa m WHERE (m.tipo_casilla=0 OR m.tipo_casilla=1 OR  m.tipo_casilla=3) AND EXISTS (SELECT m2.x FROM mapa m2 WHERE m2.x=(m.x+1) AND m2.y=m.y AND m2.tipo_casilla=2)";
        String sql_max_y = "SELECT DISTINCT m.y AS y FROM mapa m WHERE (m.tipo_casilla=0 OR m.tipo_casilla=1 OR  m.tipo_casilla=3) AND EXISTS (SELECT m2.y FROM mapa m2 WHERE m2.y=(m.y+1) AND m2.x=m.x AND m2.tipo_casilla=2)";
        float porcentaje = 0.0f;
        boolean encontrado_x, encontrado_y;
        encontrado_x = encontrado_y = false;

        int x, y;
        x = y = 0;

        try {
            Connection connection = this.connect();

            if (connection != null) {
                // Obtener X
                PreparedStatement statement = connection.prepareStatement(sql_max_x);
                ResultSet result_x = statement.executeQuery();

                if (result_x.next()) {
                      encontrado_x = true;
                      x = result_x.getInt("x") + 1;
                }

                // Obtener Y
                statement = connection.prepareStatement(sql_max_y);
                ResultSet result_y = statement.executeQuery();

                if (result_y.next()) {
                      encontrado_y = true;
                      y = result_y.getInt("y") + 1;
                }

                // Calcular porcentaje
                if (encontrado_x && encontrado_y) {
                    int total = x * y;
                    String sql_total = "SELECT COUNT(x) AS Total FROM mapa WHERE tipo_casilla<>2";
                    statement = connection.prepareStatement(sql_total);
                    ResultSet result_total = statement.executeQuery();

                    if (result_total.next())
                        porcentaje = (result_total.getFloat("Total") / total * 1.0f) * 100.0f;
                    else
                        porcentaje = 0;
                }
                statement.close();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return porcentaje;
    }

    /**
     * Método para comprobar si hay solución en la base de datos.
     * @return true si existe, false en otro caso
     * @author Elías Méndez García
     */
    public boolean existeSolución() {

        boolean b_solution = false;
        String sql = "SELECT * FROM mapa WHERE `tipo_casilla`=?";

        try {
            Connection connection = this.connect();

            if(connection != null) {
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setInt(1, CASILLA_SOLUCION);

                ResultSet result = statement.executeQuery();

                if(result.next()) {
                    b_solution = true;
                }

                statement.close();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return b_solution;
    }

    /**
     * Método para obtener un lista con las casillas de solución.
     * @return lista con las casillas de solución
     * @author Eila Gómez Hidalgo
     */
    public List<PairInt> obtenerCasillasSolucion() {
        List<PairInt> casillas_solucion = new ArrayList<>();

        String sql = "SELECT x, y FROM mapa WHERE tipo_casilla=" + CASILLA_SOLUCION;

        try {
            Connection connection = this.connect();

            if(connection != null) {
                Statement statement = connection.createStatement();

                ResultSet result = statement.executeQuery(sql);


                while (result.next()) {
                    int x = result.getInt("x");
                    int y = result.getInt("y");
                    casillas_solucion.add(PairInt.valueOf(x, y));
                }

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return casillas_solucion;
    }

    /**
     * Método para saber si se han calculado las superficies de coste.
     * @author Elías Méndez García
     * @return true si se han calculado, false en otro caso.
     */
    private boolean superficieCalculada() {
        String sql = "SELECT * FROM mapa WHERE coste_suelo<>?";
        boolean calculada = false;
        try {
            Connection connection = this.connect();

            if(connection != null) {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.MAX_VALUE);

                ResultSet result = statement.executeQuery();

                if(result.next()) {
                    calculada = true;
                }

                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return calculada;
    }

    /**
     * Método para calcular las superficies de coste.
     * @author Elías Méndez García
     */
    public void calcularSuperficiesDeCostes(){
        if(!this.superficieCalculada()) {
            this.calcularSuperficiesDeCostesSiempre();
        }

    }

    /**
     * Método para calcular las superficies de coste.
     * @author Elías Méndez García multithreading
     * @author Eila Gómez Hidalgo lógica
     */
    private  void calcularSuperficiesDeCostesSiempre() {
        Map<PairInt, PairInt> mapa = this.obtenerMapaPases();
        List<PairInt> soluciones = this.obtenerCasillasSolucion();
        List<CalcularCostes> hebrasTerrestres = new ArrayList<>();
        List<CalcularCostes> hebrasAereas = new ArrayList<>();

        for (PairInt solucion : soluciones) {
            CalcularCostes costesTerrestre = new CalcularCostes(mapa, solucion, false);
            CalcularCostes costesAereo = new CalcularCostes(mapa, solucion, true);

            costesAereo.start();
            costesTerrestre.start();

            hebrasAereas.add(costesAereo);
            hebrasTerrestres.add(costesTerrestre);
        }

        Thread mergeTerrestres = mergeUpdateCostes(hebrasTerrestres, false);
        Thread mergeAereas = mergeUpdateCostes(hebrasAereas, true);


        try {
            mergeTerrestres.start();
            mergeTerrestres.join();
            mergeAereas.start();
            mergeAereas.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para juntar los resultados de todas las hebras de calculo
     * @param hebras lista de hebras a juntar sus resultados.
     * @param vuela si la solución es para voladores.
     * @return una hebra lista para ser ejecutada.
     * @author Elías Méndez García
     */
    private Thread mergeUpdateCostes(List<CalcularCostes> hebras, boolean vuela) {
        Thread merge = new Thread() {
            @Override
            public void run() {
                List<Map<PairInt, Integer>> mapas_coste = new ArrayList<>();
                for(CalcularCostes hebra : hebras) {
                    try {
                        hebra.join();
                        mapas_coste.add(hebra.getMapaCoste());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Map<PairInt, Integer> mapa_coste = mapas_coste.get(0);
                mapas_coste.remove(0);

                if(mapas_coste.size() != 0) {
                    mapa_coste.forEach((k, v) -> {
                        Integer value = v;
                        for (Map<PairInt, Integer> mapa_c : mapas_coste) {
                            int new_value = mapa_c.get(k);
                            if (value > new_value) {
                                mapa_coste.put(k, new_value);
                                value = new_value;
                            }
                        }
                    });
                }

                updateCasillasCoste(mapa_coste, vuela);
            }
        };

        return merge;
    }

    /**
     * Método para actualizar el estado de un movil en la base de datos,
     * @param nombre nombre del movil
     * @param posicion posición en la que se encuentra
     * @param bateria bateria con la que cuenta
     * @author Baltasar Ruiz Hernández
     */
    public void updateMovil(String nombre, PairInt posicion, int bateria){
        String sql_update = "UPDATE moviles SET x=?, y=?, bateria=? WHERE nombre=?";

        boolean intentar = true;
        while(intentar) {
            try {
                Connection connection = this.connect();
                if (connection != null) {
                    PreparedStatement statement = null;
                    statement = connection.prepareStatement(sql_update);
                    statement.setInt(1, posicion.first);
                    statement.setInt(2, posicion.second);
                    statement.setInt(3, bateria);
                    statement.setString(4, nombre);

                    statement.executeUpdate();

                    intentar = false;
                    statement.close();
                    connection.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    /**
     * Método para actualizar el coste de una casilla
     * @param mapa mapa con la infromación
     * @param vuela si el movil vuela o no
     * @author Elías Méndez García
     */
    public void updateCasillasCoste(Map<PairInt, Integer> mapa, boolean vuela) {
        String campo = (vuela) ? "coste_aire":"coste_suelo";
        String sql = "UPDATE mapa SET " + campo + "=? WHERE x=? AND y=?";

        try {
            Connection connection = this.connect();

            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(sql);
                mapa.forEach((k, v) -> {
                    boolean intentar = true;
                    while(intentar) {
                        try {

                            statement.setInt(1, v);
                            statement.setInt(2, k.first);
                            statement.setInt(3, k.second);

                            statement.executeUpdate();
                            intentar = false;
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para eliminar todos los óviles de la base de datos.
     * @author Elías Méndez García
     */
    public void dropMoviles() {
        String sql = "DROP TABLE moviles";

        try {
            Connection connection = this.connect();
            if (connection != null) {
                Statement statement = connection.createStatement();

                statement.execute(sql);
                statement.close();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Método para devolver una conexión a la base de datos.
     * @return la conexión a la base de datos
     * @throws SQLException si no se puede conectar
     * @author Elías Méndez García
     */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(this.url);
    }

//    // Main para probar la funcionalidad de la base de datos.
//    public static void main(String args[]) {
////        List<String>mapas = new ArrayList<>();
////        mapas.add("map1");
////        mapas.add("map3");
////        mapas.add("map4");
////        mapas.add("map5");
////        mapas.add("map6");
////        mapas.add("map7");
////        mapas.add("map8");
////        mapas.add("map9");
////
////        for(String s  : mapas) {
////            System.out.println(s);
////            SQLWrapper sql = new SQLWrapper(s);
////            sql.calcularSuperficiesDeCostesSiempre();
////        }
//
//
//    }

}
