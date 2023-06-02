package fr.uge.succinctedge.signal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jena.graph.Node;

import java.util.HashMap;
import java.util.Map;

public class ReturnSignal {
    Map<String, String> signal_map;

    public ReturnSignal(Map<String,String> map) {
        this.signal_map = map;
    }

    public String getSensor() {
        return signal_map.get("sensor");
    }

    public String getClient() {
        return signal_map.get("client");
    }

    public int getSignal() {
        return Integer.parseInt(signal_map.get("signal"));
    }

    public int getValue() {
        return Integer.parseInt(signal_map.get("value"));
    }

    public String getObservation() {
        return signal_map.get("observation");
    }

    static public ReturnSignal readSignal(String jsonReturn) throws JsonProcessingException {
        return new ReturnSignal(new ObjectMapper().readValue(jsonReturn, HashMap.class));
    }

}
