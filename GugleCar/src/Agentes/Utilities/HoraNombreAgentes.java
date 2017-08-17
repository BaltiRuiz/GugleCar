package Agentes.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Clase para generar nombres de agentes que no den conflictos.
 * @auhtor Daniel Soto del Ojo
 * @author Elías Méndez García
 */
public class HoraNombreAgentes {
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH:mm:ss";

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
}
