package fr.uge.succinctedge.main;

import static java.lang.System.exit;

public class ArgsParser {
    public static class Settings {
        String dataPath;
        String queryPath;
        String shaclPath;
        String paramQueriesPath;
        boolean strReason;
        boolean constraint;

        @Override
        public String toString() {
            return "./SEServer " + dataPath + " " + queryPath + " " + shaclPath + " " + paramQueriesPath + " " + strReason + " " + constraint;
        }
    }


    public static Settings parseArgs(String[] args) {
        Settings s = new Settings();

        if (args.length < 6) {
            System.out.println("usage : [./SEServer] dataPath queryPath shaclPath paramQueriesPath strReason constraintQuery");
            exit(0);
        }

        s.dataPath = args[0];
        s.queryPath = args[1];
        s.shaclPath = args[2];
        s.paramQueriesPath = args[3];
        s.strReason = args[4].equals("true");
        s.constraint = args[5].equals("true");
        return s;
    }
}
