package fr.uge.succinctedge.signal;

import fr.uge.succinctedge.shacl.Rule;

import java.util.HashMap;
import java.util.Map;

public class SignalMap {
    private final Map<Integer, Rule> map = new HashMap<>();
    private int maxSignalCount = 0;

    public int addSignal(Rule query){
        map.put(maxSignalCount, query);
        return maxSignalCount++;
    }

    public Rule getQueryBySignal(int signal){
        return map.getOrDefault(signal, null);
    }

}
