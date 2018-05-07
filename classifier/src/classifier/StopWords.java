package classifier;

import java.io.IOException;
import java.util.Hashtable;

public class StopWords {
    public static void mStopWords(String stopwords, Hashtable sw) throws IOException {
        if (stopwords != null) {
            sw = new Hashtable<String, Integer>();
            String[] sws = Preprocessing.readFile(stopwords).split(" ");
            for (String s : sws) {
                if (s.length() > 0){
                    sw.put(s, 0);
                }
            }
        }
    }
}
