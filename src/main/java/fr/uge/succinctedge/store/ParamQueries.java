package fr.uge.succinctedge.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParamQueries {
    private final Map<String, ParamQuery> map = new HashMap<>();

    public void addQuery(ParamQuery query, String tag) {
        map.put(tag, query);
    }


    public Optional<ParamQuery> getByTag(String tag) {
        var value = map.getOrDefault(tag, null);
        return Optional.of(value);
    }
}
