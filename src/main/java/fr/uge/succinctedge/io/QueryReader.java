package fr.uge.succinctedge.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryReader {
    private final String path;

    public QueryReader(String p) {
        this.path = p;
    }
    public List<String> read() throws FileNotFoundException {
        List<String> res = new ArrayList<String>();
        File f = new File(path);
        Scanner scan = new Scanner(f);
        while (scan.hasNextLine()) {
            res.add(scan.nextLine());
        }
        scan.close();
        return res;
    }

}
