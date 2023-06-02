package fr.uge.succinctedge.shacl;

public class Rule {
    private final String filter;
    private final String client;
    private final String senseur;
    private final String timestamp;
    private final String result;

    public Rule(String filter, String client, String senseur, String timestamp, String result) {
        this.filter = filter;
        this.client = client;
        this.senseur = senseur;
        this.timestamp = timestamp;
        this.result = result;
    }

    public String getFilter() {
        return filter;
    }

    public String getClient() {
        return client;
    }

    public String getSenseur() {
        return senseur;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "filter='" + filter + '\'' +
                ", client='" + client + '\'' +
                ", senseur='" + senseur + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
