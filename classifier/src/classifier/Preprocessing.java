package classifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preprocessing {
    private String stopwords;
    private Hashtable sw = null;

    public Preprocessing(String truePages, String falsePages, String arff, String stopwords) throws IOException {
        this.stopwords = stopwords;
        List<List> allPages = new ArrayList<List>();

        falsePages(falsePages, allPages);
        truePages(truePages, allPages);
        bagOfWords(allPages, arff);
    }

    public static List<List> falsePages(String falsePages, List<List> allPages) throws IOException { //FALSE
        List<String> sitesFalse = getFiles(falsePages);
        for (String site : sitesFalse) {
            List l = Parse.parse(site);
            l.add("false");
            allPages.add(l);
        }
        return allPages;

    }

    public static List<List> truePages(String truePages, List<List> allPages) throws IOException {
        //TRUE
        List<String> sitesTrue = getFiles(truePages);
        for (String site : sitesTrue) {
            List l = Parse.parse(site);
            l.add("true");
            allPages.add(l);
        }
        return allPages;

    }

    public static List<String> getFiles(String path) {
        List<String> r = new ArrayList<String>();
        File dir = new File(path);
        File[] files = dir.listFiles();
        for (File f : files) {
            String p = f.getAbsolutePath();
            if (f.isDirectory()) {
                r.addAll(getFiles(p));
            } else {
                r.add(p);
            }
        }
        return r;
    }

    public static String readFile(String path) throws IOException {
        String r = new String(Files.readAllBytes(Paths.get(path)));
        return r.replaceAll("\\n|\\r", " ");
    }


    public void bagOfWords(List<List> sites, String file) throws IOException {
        //inicializar stopwords
        StopWords.mStopWords(stopwords, sw);

        Hashtable bag = new Hashtable<String, Integer>();
        /* contando as palavras e colocando numa hash */
        for (List<String> site : sites) {
            for (int i = 0; i < site.size() - 1; i++) {
                String word = site.get(i);
                if (!isStopWord(sw, word)){
                    bag.put(word, 0);
                }
            }
        }

        WriteFile.writer( bag,  file, sites);
    }

    public static boolean isStopWord(Hashtable stopWords, String word){
        boolean result = false;
        if (stopWords != null){
            if(stopWords.containsKey(word)){
                result = true;
            }
        }
        return result;
    }


    public static List getTitle(String doc) {
        String[] sub = doc.split("<");
        List<String> r = new ArrayList<String>();
        Pattern pattern = Pattern.compile("^[\\s]*title[^>]*>(.*)");
        for (String l : sub) {
            Matcher matcher = pattern.matcher(l);
            if (matcher.find()) {
                String contentTitle = matcher.group(1);
                String[] wordsTitle = contentTitle.split(" ");
                for (String w : wordsTitle) {
                    if (!w.equals("")) {
                        w = w.replaceAll("'", "");
                        r.add("title." + w);
                    }
                }
                break;
            }
        }
        return r;
    }
}