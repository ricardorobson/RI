package com.ri.wse.utils;


import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SpearmanCR {

    public static double evaluate(List<URL> rank1, List<URL> rank2) {
        AtomicInteger urlIndex = new AtomicInteger(0);
        Map<URL, Integer> rank1Map = rank1.stream()
                .collect(Collectors.toMap(Function.identity(), url -> urlIndex.getAndIncrement()));
        urlIndex.set(0);
        Map<URL, Integer> rank2Map = rank2.stream()
                .collect(Collectors.toMap(Function.identity(), url -> urlIndex.getAndIncrement()));

        int squareDistancesSum = rank1.stream()
                .mapToInt(url -> rank1Map.get(url) - rank2Map.get(url))
                .map(difference -> difference * difference)
                .sum();

        return 1 - ((6 * squareDistancesSum) / (double) (rank1.size() * (rank1.size() * rank1.size() - 1)));
    }
}