package fr.uge.succinctedge.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.uge.succinctedge.io.QueryReader;
import fr.uge.succinctedge.mqtt.Receiver;
import fr.uge.succinctedge.mqtt.Sender;
import fr.uge.succinctedge.shacl.Rule;
import fr.uge.succinctedge.shacl.ShaclReader;
import fr.uge.succinctedge.signal.ReturnSignal;
import fr.uge.succinctedge.signal.SignalMap;
import fr.uge.succinctedge.store.JenaStore;
import fr.uge.succinctedge.store.ParamQueries;
import org.apache.jena.base.Sys;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.sparql.expr.nodevalue.NodeValueInteger;
import org.apache.jena.system.progress.ProgressMonitorBasic;
import org.apache.jena.system.progress.ProgressMonitorFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;

import static org.apache.jena.sparql.util.NodeFactoryExtra.intToNode;

public class Main {
    public static void main(String[] args) throws MqttException, FileNotFoundException, JsonProcessingException {
        Map<ReturnSignal,Model> temporaryGraphs = new HashMap<>();
        SignalMap signals = new SignalMap();
        ParamQueries param = new ParamQueries();



        ArgsParser.Settings settings = ArgsParser.parseArgs(args);
        System.out.println("SETTINGS " + settings);

        JenaStore.load(settings.dataPath);
        var graph = JenaStore.getGraph();
        JenaStore.getModel().read(new FileInputStream("data/test_data/sitsol.owl"),null,"TTL"); //Faut intégrer ça dans les données de base

        var rules = ShaclReader.read(settings.shaclPath, graph);
        System.out.println(rules);
        List<String> queries = new QueryReader(settings.queryPath).read();

        Sender querySender = new Sender();

        for(Rule rule: rules) {
            querySender.sendMessage("query", "__start__");
            for (String query : queries) {
                querySender.sendMessage("query", query);
            }
            //querySender.sendMessage("query", rule.getFilter()); à incoporer dans la query
            querySender.sendMessage("query", "__signal__ (" + signals.addSignal(rule) + ")");
            querySender.sendMessage("query", "__end__");
        }

        Receiver r = new Receiver();
        while(true) {
            Model dataModel = JenaUtil.createDefaultModel();
            Model shapeModel = JenaUtil.createDefaultModel();
            shapeModel.read(settings.shaclPath);
            Model inferenceModel = JenaUtil.createDefaultModel();
            ReturnSignal signal = r.getSignal();
            Rule rule = signals.getQueryBySignal(signal.getSignal());
            Triple failTriple = new Triple(NodeFactory.createURI(rule.getResult()), NodeFactory.createURI("http://qudt.org/schema/qudt#numericValue"), intToNode(signal.getValue())); //creating the fail triple

            //Rule rule = signals.getQueryBySignal(0);
            //Triple failTriple = new Triple(NodeFactory.createURI(rule.getResult()), NodeFactory.createURI("http://qudt.org/schema/qudt#numericValue"), dataModel.createTypedLiteral(Integer.valueOf(25)).asNode()); //creating the fail triple
            //System.out.println(failTriple);
            dataModel.add(JenaStore.getModel());
            var baseGraph = JenaStore.getGraph();
            baseGraph.add(failTriple);
            System.out.println(ShaclValidator.get().validate(shapeModel.getGraph(), baseGraph).getGraph());
            var start = System.currentTimeMillis();
            inferenceModel = RuleUtil.executeRules(dataModel, shapeModel, inferenceModel, null); // Contains the inferred triples
            System.out.println(System.currentTimeMillis() - start);
            //System.out.println(dataModel);
            //System.out.println(shapeModel);
            //System.out.println(inferenceModel);
            baseGraph.remove(failTriple.getSubject(), failTriple.getPredicate(), failTriple.getObject());
            var temporaryGraph = ModelFactory.createDefaultModel();
            temporaryGraph.add(inferenceModel);
            System.out.println(temporaryGraph);
            System.out.println(baseGraph);
            temporaryGraphs.put(signal, temporaryGraph);

            return;

        }
    }
}

