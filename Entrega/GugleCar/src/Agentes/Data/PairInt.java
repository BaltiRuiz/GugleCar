package Agentes.Data;

/**
 * Clase para almacenar una pareja de ints
 * @author Elías Méndez García
 * @author Eila Gómez Hidalgo
 */
public class PairInt implements Comparable<PairInt> {
    public int first;
    public int second;

    /**
     * Constructor por defecto con valores mínimos.
     * @author Elías Méndez García
     * @author Eila Gómez Hidalgo
     */
    public PairInt() {
        this.first = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
    }

    /**
     * Constructor con dos valores
     * @param x primer valor
     * @param y segundo valor
     * @author Eila Gómez Hidalgo
     */
    public PairInt(int x, int y) {
        this.first = x;
        this.second = y;
    }

    /**
     * Método para comparar dos pair ints
     * @param other objeto con el que se quiere comparar
     * @return true si son igualeslosos enteros, false en otro caso.
     * @author Elías Méndez García
     */
    @Override
    public boolean equals(Object other) {
        PairInt o = (PairInt) other;

        return (o.first == this.first && o.second == this.second);
    }


    /**
     * Método para calcular el hash de los dos enteros y poder ser usado en hashmaps.
     * @author Elías Méndez García
     * @return valor del hash de los dos enteros
     */
    @Override
    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + first;
//        result = prime * result + second;
        if(first < 0 || second < 0)
            return -1;

        int sum = first + second;
        int result = sum * (int)((sum + 1) * 0.5f) + first;
        return result;
    }

    /**
     * Método para generar un PairInt en función de dos valores.
     * @author Elías Méndez García
     * @param x primer valor
     * @param y segundo valor
     * @return PairInt con los dos valores proporcionados.
     */
    public static PairInt valueOf(int x, int y) {
        return new PairInt(x, y);
    }

    /**
     * Método para comparar dos pairint
     * @param pairInt otro objeto con el que comparar
     * @return 0 si son iguales, 1 en otro caso.
     */
    @Override
    public int compareTo(PairInt pairInt) {
        return (this.first == pairInt.first && this.second == pairInt.second) ? 0:1;
    }
}
