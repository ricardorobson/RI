package com.ri.wse.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class InvertedIndex {
    public Hashtable<String, Map<Integer, Double>> arquivo = new Hashtable<>();
    public Map<String, Integer> termoFrequencia = new HashMap<>();
    public Set<String> bagOfWord = new HashSet<>();
    Map<Integer,String> documentIndice=new HashMap<>();

    public InvertedIndex() throws IOException {
        List<String> lines = Files.readAllLines(new File(getClass().getResource("/file/data.csv").getPath()).toPath());
        /*
        Path wiki_path = Paths.get("C:/", "data.csv");

        Charset charset = Charset.forName("ISO-8859-1");
        List<String> lines = Files.readAllLines(wiki_path, charset);
        */
        List<String> attrs = Arrays.asList(lines.get(0).split(","));
        lines = lines.subList(1,lines.size());

        //URL,nome,telefone,CRP,preço


        Map<Integer, Map<String, String[]>> documentTerms=new HashMap<>();
        int i=0;
        for(String line : lines) {
            String[] values = line.split(",");
            Map<String, String[]> documentMap = new HashMap<>();
            for (int j= 0; j < attrs.size(); j++) {
                final int jj = j;
                documentMap.put(attrs.get(j), values[j].split(" +"));
                bagOfWord.addAll(
                        Arrays.stream(documentMap.get(attrs.get(j)))
                                .map(word -> attrs.get(jj) + "." + word)
                                .collect(Collectors.toList()));
            }

            documentIndice.put(i,documentMap.get("URL")[0]);
            documentMap.remove("URL");
            documentTerms.put(i,documentMap);
            i++;
        }

        for(String term : bagOfWord){
            int n=0;
            for(Map.Entry<Integer, Map<String, String[]>> documentTerm:documentTerms.entrySet()) {
                System.out.println(Arrays.toString(documentTerm.getValue().getOrDefault(term, new String[0])));
                Map<Integer, Double> documenIdf = arquivo.getOrDefault(term, new HashMap<>());
                for(Map.Entry<String, String[]> termAux:documentTerm.getValue().entrySet()){
                    for(String termStringAux:termAux.getValue()) {
                        if (!termStringAux.equalsIgnoreCase("")){
                            if (term.contains(termStringAux) || termStringAux.contains(term)) {
                                n++;
                            }
                        }
                    }
                }
                if(n==0){
                    documenIdf.put(documentTerm.getKey(), Math.log(documentTerms.size() / 0.000000001));
                }else{
                    documenIdf.put(documentTerm.getKey(), Math.log(documentTerms.size() / n));
                }
                n=0;
                arquivo.put(term, documenIdf);
            }
        }


        arquivo.entrySet().stream()
                .forEach(entry -> termoFrequencia.put(
                        entry.getKey(),
                        entry.getValue()
                                .values()
                                .stream()
                                .mapToInt(elem -> elem>0? 1:0)
                                .sum()
                        )
                );
        System.out.println("teste");
    }


}


