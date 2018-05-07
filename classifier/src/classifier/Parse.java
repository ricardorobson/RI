package classifier;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parse {
    public static List parse(String file) throws IOException {

        String doc = Preprocessing.readFile(file);
        List listWords = new ArrayList<String>();
        listWords = Preprocessing.getTitle(doc);

        doc = doc.replaceAll("<[^>]*head[^>]*>(.*?)<[^>]*/[^>]*head[^>]*>", "");
        doc = doc.replaceAll(
                "<[\\s|\\t|\\n|\\r]*script[^<>]*>(.*?)<[\\s|\\t|\\n|\\r]*/[\\s|\\t|\\n|\\r]*script[^<>\\\"]*>", "");
        doc = doc.replaceAll(
                "<[\\s|\\t|\\n|\\r]*style[^<>]*>(.*?)<[\\s|\\t|\\n|\\r]*/[\\s|\\t|\\n|\\r]*style[^<>\\\"]*>", "");
        doc = doc.replaceAll("<(.*?)>", " ");
        doc = doc.replaceAll("[.] |, | ,|\\||\\!|\\?|:|'|;| & |\\(|\\)| - |=|#|&", " ");
        String sep = "-:-";
        doc = doc.replaceAll("\\s+| +", sep);
        doc = doc.replaceAll("^" + sep, "");
        String[] words = doc.split(sep);
        for (String w : words) {
            listWords.add(w);
        }
        return listWords;
    }

    public static List<String> parse1(String pathDir) throws IOException {
        String doc1 = Preprocessing.readFile(pathDir);
        List myList = new ArrayList<String>();
        myList = Preprocessing.getTitle(doc1);

        List<String> r = new ArrayList<String>();
            File input = new File(pathDir);
            Document doc = null;
            try {
                doc = Jsoup.parse(input, "UTF-8", "https://www.doctoralia.com.br/");
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;
            Elements td2 = doc.getElementsByTag("html");
            List words= new ArrayList<String>(Arrays.asList(td2.text().split(" ")));
        for (Object w: words) {
            myList.add(w + ",");
        }
        return myList;

    }
}
