package fr.uge.succinctedge.store;

import org.apache.jena.sparql.exec.http.Params;

import java.util.Objects;

public class ParamQuery {
    private final String query;

    public ParamQuery(String s) {
        Objects.requireNonNull(s);
        query = s;
    }

    public String getQuery() {
        return query;
    }
}
