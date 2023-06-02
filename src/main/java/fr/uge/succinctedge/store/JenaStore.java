package fr.uge.succinctedge.store;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class JenaStore {
    private static final Model model = ModelFactory.createDefaultModel();

    public static void load(String path) {
        model.read(path);
        System.out.println("Successfully loaded " + path);
    }

    public static Model getModel() {
        return model;
    }

    public static Graph getGraph() {
        return model.getGraph();
    }

}
