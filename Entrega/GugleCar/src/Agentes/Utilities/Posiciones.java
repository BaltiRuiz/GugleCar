package Agentes.Utilities;

import Agentes.Data.Comando;
import Agentes.Data.PairInt;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase con métodos para generar las posiciones al rededor de una posicion
 * @author Elías Méndez García
 * @author Eila Gómez Hidalgo
 */
public abstract class Posiciones {

    /**
     * Método para generar las casillas al rededor de una posición
     * @param x
     * @param y
     * @return Lista de las casillas colindantes.
     * @author Eila Gómez Hidalgo
     */
    public static List<PairInt> posicionesAlrededor(int x, int y) {
        PairInt norOeste = PairInt.valueOf(x - 1, y - 1);
        PairInt norte = PairInt.valueOf(x, y - 1);
        PairInt norEste = PairInt.valueOf(x + 1, y - 1);
        PairInt este = PairInt.valueOf(x + 1, y);
        PairInt surEste = PairInt.valueOf(x + 1, y + 1);
        PairInt sur = PairInt.valueOf(x, y + 1);
        PairInt surOeste = PairInt.valueOf(x - 1, y + 1);
        PairInt oeste = PairInt.valueOf(x - 1, y);

        List<PairInt> l = new ArrayList<>();

        l.add(norte);
        l.add(norEste);
        l.add(este);
        l.add(surEste);
        l.add(sur);
        l.add(surOeste);
        l.add(oeste);
        l.add(norOeste);

        return l;
    }

    /**
     * Método para generar un comando para ir de una posición a otra.
     * @param now posicion actual
     * @param next siguiente posición
     * @return comando para ir de una posición a otra
     * @author Elías Méndez García
     */
    public static Comando toComando(PairInt now, PairInt next) {

        int compare_x = now.first - next.first;
        int compare_y = now.second - next.second;

        if(compare_x == 0 && compare_y == 0) {
            return Comando.ERROR;
        } else if(compare_x > 0 && compare_y > 0) {
            return Comando.NOROESTE;
        } else if(compare_x == 0 && compare_y > 0) {
            return Comando.NORTE;
        } else if(compare_x < 0 && compare_y > 0) {
            return Comando.NORESTE;
        } else if(compare_x < 0 && compare_y == 0) {
            return Comando.ESTE;
        } else if(compare_x < 0 && compare_y < 0) {
            return Comando.SURESTE;
        } else if(compare_x == 0 && compare_y < 0) {
            return Comando.SUR;
        } else if(compare_x > 0 && compare_y < 0) {
            return Comando.SUROESTE;
        } else {
            return Comando.OESTE;
        }
    }
}
