package com.ri.wse.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ranker {
    private InvertedIndex invertedIndex;
    private Map<String, Integer> termOrder;

    private Map<Integer, double[]> documentTfVectors;
    private Map<Integer, Double> documentTfVectorMagnitudes;

    private Map<Integer, double[]> documentTfIdfVectors;
    private Map<Integer, Double> documentTfIdfVectorMagnitudes;

    public Ranker(InvertedIndex invertedIndex) {
        this.invertedIndex = invertedIndex;
        termOrder = calculateTermOrder(invertedIndex.termoFrequencia.keySet());
        Pair<Map<Integer, double[]>, Map<Integer, double[]>> documentVectors = calculateDocumentVectors(invertedIndex.arquivo, invertedIndex.termoFrequencia,termOrder);
        documentTfVectors = documentVectors.getKey();
        documentTfIdfVectors = documentVectors.getValue();
        documentTfVectorMagnitudes = new HashMap<>();
        documentTfIdfVectorMagnitudes = new HashMap<>();
        for (Map.Entry<Integer, double[]> docEntry : documentTfVectors.entrySet()) {
            documentTfVectorMagnitudes.put(docEntry.getKey(), calculateVectorMagnitude(docEntry.getValue()));
        }
        for (Map.Entry<Integer, double[]> docEntry : documentTfIdfVectors.entrySet()) {
            documentTfIdfVectorMagnitudes.put(docEntry.getKey(), calculateVectorMagnitude(docEntry.getValue()));
        }
    }

    private Map<String, Integer> calculateTermOrder(Set<String> terms) {
        int termIndex = 0;
        Map<String, Integer> termOrder = new HashMap<>();
        for (String term : terms) {
            termOrder.put(term, termIndex++);
        }
        return termOrder;
    }

    private Pair<Map<Integer, double[]>, Map<Integer, double[]>> calculateDocumentVectors(Map<String, Map<Integer, Double>> termDocs, Map<String, Integer> termFreqs, Map<String, Integer> termOrder) {
        Set<Integer> docIndices = new HashSet<>();

        for (Map<Integer, Double> docIdIdf : termDocs.values()) {
            docIndices.addAll(docIdIdf.keySet());
        }

        Map<Integer, double[]> documentTfVectors = new HashMap<>();
        Map<Integer, double[]> documentTfIdfVectors = new HashMap<>();
        for (Integer docId : docIndices) {
            double[] docTfVector = calculateDocumentTfIdfVector(docId, true, termDocs, termFreqs,termOrder);
            double[] docTfIdfVector = calculateDocumentTfIdfVector(docId, false, termDocs, termFreqs,termOrder);
            documentTfVectors.put(docId, docTfVector);
            documentTfIdfVectors.put(docId, docTfIdfVector);
        }

        return new Pair<>(documentTfVectors, documentTfIdfVectors);
    }

    private double[] calculateDocumentTfIdfVector(int documentIndex, boolean tfOnly, Map<String, Map<Integer, Double>> termDocs, Map<String, Integer> termFreqs,Map<String, Integer> termOrder) {
        Map<Integer, Double> indicesTdIdf = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Double>> entry : termDocs.entrySet()) {
            int termTfIdfArrayIndex = termOrder.get(entry.getKey());
            double termFrequencyInDocument = entry.getValue().getOrDefault(documentIndex, 0.0);
            double tf = tfOnly ? (termFrequencyInDocument > 0 ? 1 : 0) : (termFrequencyInDocument == 0 ? 0 : 1 + Math.log(termFrequencyInDocument));
            double idf = tfOnly ? 1 : Math.log(termDocs.size() / (double) termFreqs.get(entry.getKey()));

            indicesTdIdf.put(termTfIdfArrayIndex, tf * idf);
        }
        return indicesTdIdf.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .mapToDouble(boxedDouble -> boxedDouble)
                .toArray();
    }

    private double calculateVectorMagnitude(double[] doc) {
        double acc = 0;
        for (double value : doc) {
            acc += Math.pow(value, 2);
        }

        return Math.sqrt(acc);
    }

    public List<String> rank(List<String> query, boolean tfOnly) {
        double[] queryVector = calculateQueryTfIdfVector(query, tfOnly,invertedIndex.arquivo,invertedIndex.termoFrequencia,termOrder);

        Map<Integer, Double> docRank = new HashMap<>();
        for(Map.Entry<Integer,double[]> docVector: (tfOnly ? documentTfVectors: documentTfIdfVectors).entrySet()){
            double mag = (tfOnly ? documentTfVectorMagnitudes: documentTfIdfVectorMagnitudes).get(docVector.getKey());
            docRank.put(docVector.getKey(), calculateCossineBetweenVectors(queryVector, 1, docVector.getValue(), mag));
        }

        return docRank.entrySet().stream().sorted((p1, p2) -> (int) Math.signum(p1.getValue() - p2.getValue()))
                .map(Map.Entry::getKey)
                .map(id -> invertedIndex.documentIndice.get(id))
                .collect(Collectors.toList());
    }

    private double[] calculateQueryTfIdfVector(List<String> query, boolean tfOnly, Map<String, Map<Integer, Double>> termDocs, Map<String, Integer> termFreqs,Map<String, Integer> termOrder) {

        Map<Integer, Double> indicesTdIdf = new HashMap<>();

        for (Map.Entry<String, Map<Integer, Double>>  termDocEntry:termDocs.entrySet()) {
            int termTfIdfArrayIndex = termOrder.get(termDocEntry.getKey());
            boolean queryContainsTerm = query.stream()
                    .anyMatch(queryTerm -> queryTerm.toLowerCase().contains(termDocEntry.getKey().toLowerCase())
                            || termDocEntry.getKey().toLowerCase().contains(queryTerm.toLowerCase()));
            double idf = !queryContainsTerm
                    ? 0
                    : tfOnly
                    ? 1
                    : Math.log(termDocs.size() / (double) termFreqs.get(termDocEntry.getKey()));
            indicesTdIdf.put(termTfIdfArrayIndex, idf);
        }
        return indicesTdIdf.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .mapToDouble(boxedDouble -> boxedDouble)
                .toArray();
    }

    private double calculateCossineBetweenVectors(double[] vector1, double vector1Magnitude, double[] vector2, double vector2Magnitude) {
        return dotProduct(vector1, vector2) / vector1Magnitude * vector2Magnitude;
    }

    private double dotProduct(double[] vector1, double[] vector2) {
        return IntStream.range(0, vector1.length)
                .mapToDouble(index -> vector1[index] * vector2[index])
                .sum();
    }
}