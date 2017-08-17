package Agentes.Old;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jeste on 16/01/2017.
 * @deprecated
 */
public class Conflictos {
    private Map<String, List<String>> conflictos;

    public Conflictos() {
        this.conflictos = new TreeMap<>();
    }

    public void add(String name, String conflicto) {
        if(!this.conflictos.containsKey(conflicto)) {
            List<String> lista = new ArrayList<>();
            lista.add(name);

            this.conflictos.put(conflicto, lista);
        } else {
            this.conflictos.get(conflicto).add(name);
        }
    }

    public boolean contains(String conflicto) {
        return this.conflictos.containsKey(conflicto);
    }

    public boolean only(String conflicto) {
        return this.conflictos.containsKey(conflicto) && this.conflictos.size() == 1;
    }

    public List<String> get(String conflicto) {
        return this.conflictos.get(conflicto);
    }

    public void clear() {
        this.conflictos.clear();
    }
}
