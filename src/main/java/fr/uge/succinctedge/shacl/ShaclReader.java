package fr.uge.succinctedge.shacl;

import fr.uge.succinctedge.store.JenaStore;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.query.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.engine.constraint.ValueRangeConstraint;
import org.apache.jena.shacl.parser.Constraint;
import org.apache.jena.shacl.parser.Shape;
import org.apache.jena.shacl.validation.VLib;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.eval.PathEval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShaclReader {
    public static Shapes getShapes(String path){
        String SHAPES = "data/test_shacl/shacl_test.ttl";
        var shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        return Shapes.parse(shapesGraph);
    }

    public static Collection<Rule> read(String path, Graph data) {
        var rules = new ArrayList<Rule>();
        var clist = new ArrayList<Constraint>();
        var shapesGraph = RDFDataMgr.loadGraph(path);
        var shapes = Shapes.parse(shapesGraph);
        var map = shapes.getShapeMap();
        map.forEach((node, shape) -> {
            for (Constraint constr : shape.getConstraints()) {
                if (ValueRangeConstraint.class.isAssignableFrom(constr.getClass())) clist.add(constr);
            }
        });

        var filter = clist.get(0).toString();
        switch (filter.split("\\[")[0]) {
            case "minInclusive":
                filter = filter.replaceAll("minInclusive", "FILTER (?v >= ");
                break;
            case "minExclusive":
                filter = filter.replaceAll("minExclusive", "FILTER (?v > ");
                break;
            case "maxInclusive":
                filter = filter.replaceAll("maxInclusive", "FILTER (?v <= ");
                break;
            case "maxExclusive":
                filter = filter.replaceAll("maxExclusive", "FILTER (?v < ");
                break;
            default:
                throw new IllegalArgumentException("Invalid shacl constraints");
        }
        filter = filter.replace("[", "");
        filter = filter.replace("]", ")");
        for(Shape s : shapes){
            Collection<Node> focusNodes = VLib.focusNodes(data, s);
            if(!focusNodes.isEmpty())
                rules.addAll(resolve(s,data, filter));
        }

        return rules;
    }
    private static List<Rule> resolve(Shape s, Graph data, String filter){
        List<Path> path = new ArrayList<>();
        List<Rule> rules = new ArrayList<>();
        resolvePath(s,path);
        VLib.focusNodes(data, s)
                .forEach(node -> PathEval.eval1(data,node,path.get(0))
                .forEachRemaining(n -> rules.addAll(resolve(n,data,path,1,filter))));

        return rules;
    }

    private static List<Rule> resolve(Node node, Graph data, List<Path> path, int index, String filter){
        List<Rule> rules = new ArrayList<>();
        if(path.size() > index+1)
            PathEval.eval1(data,node,path.get(index)).forEachRemaining(n -> rules.addAll(resolve(n,data,path,index+1,filter)));
        else
            rules.add(resolveRule(node, data, filter));
        return rules;
    }

    private static Rule resolveRule(Node value, Graph data, String filter){
        String sparql = "SELECT ?Timestamp ?Sensor ?Client ?Res WHERE {\n" +
                "    ?Obs <http://www.w3.org/ns/sosa#hasResult> ?Res .\n" +
                "    ?Obs <http://qudt.org/schema/qudt#resultTime> ?Timestamp .\n" +
                "    ?Sensor <http://www.w3.org/ns/sosa#observes> ?Obs .\n" +
                "    ?Client <http://www.w3.org/ns/sosa#hosts> ?Sensor .\n" +
                "}";

        Query qry = QueryFactory.create(sparql);
        QueryExecution qe = QueryExecutionFactory.create(qry, JenaStore.getModel());
        ResultSet rs = qe.execSelect();

        QuerySolution sol = rs.nextSolution();
        var timestamp = sol.get("Timestamp");
        var sensor = sol.get("Sensor");
        var client = sol.get("Client");
        var result = sol.get("Res");

        return new Rule(filter, client.toString(), sensor.toString(), timestamp.toString(), result.toString());
    }

    private static void resolvePath(Shape s, List<Path> path){
        s.getPropertyShapes().forEach(propertyShape -> {
            path.add(propertyShape.getPath());
            resolvePath(propertyShape,path);
        });
    }
}
