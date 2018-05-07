package classifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class WriteArff {

    public static void writer(Hashtable bag, String file, List<List> sites) throws IOException {
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("@RELATION psico\n\n");

        //ATRIBUTOS
        Set<String> keys = bag.keySet();
        for (String key : keys) {
            bw.write("@ATTRIBUTE '" + key + "' NUMERIC\n");
            bw.flush();
        }
        bw.write("@ATTRIBUTE quality {true, false}\n\n");

        //DATAS
        bw.write("@DATA\n");
        for (List<String> site : sites) {
            Hashtable pagekeys = new Hashtable<String, Integer>();
            pagekeys.putAll(bag);
            for (int i = 0; i < site.size() - 1; i++) {
                String word = site.get(i);
                if (pagekeys.containsKey(word)) {
                    int num = 1;
                    //frequencia da palavra
                    num += (Integer) pagekeys.get(word);
                    pagekeys.put(word, num);
                }
            }
            keys = pagekeys.keySet();
            for (String key : keys) {
                int k = (int) pagekeys.get(key);
                bw.write(k + ",");
                bw.flush();
            }
            bw.write(site.get(site.size() - 1) + '\n');
        }
        bw.close();
        fw.close();
    }
}
