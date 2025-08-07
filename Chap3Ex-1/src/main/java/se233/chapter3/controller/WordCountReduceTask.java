package se233.chapter3.controller;

import se233.chapter3.model.FileFreq;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class WordCountReduceTask implements Callable<LinkedHashMap<String, List<FileFreq>>> {
    private Map<String, FileFreq>[] wordMap;
    private LinkedHashMap<String, List<FileFreq>> uniqueSets;

    public WordCountReduceTask(Map<String, FileFreq>[] wordMap) {
        this.wordMap = wordMap;
    }

    @Override
    public LinkedHashMap<String, List<FileFreq>> call() throws Exception {
        List<Map<String, FileFreq>> wordMapList = new ArrayList<>(Arrays.asList(wordMap));
        uniqueSets = wordMapList.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(
                        e -> e.getKey(),
                        Collector.of(
                                () -> new ArrayList<FileFreq>(),
                                (list, item) -> list.add(item.getValue()),
                                (current_list, new_items) -> {
                                    current_list.addAll(new_items);
                                    return current_list;
                                })
                ))
                .entrySet()
                .stream()
                // Sort by total frequency (descending) instead of alphabetically
                .sorted((e1, e2) -> {
                    int totalFreq1 = e1.getValue().stream().mapToInt(FileFreq::getFreq).sum();
                    int totalFreq2 = e2.getValue().stream().mapToInt(FileFreq::getFreq).sum();
                    return Integer.compare(totalFreq2, totalFreq1); // Descending order
                })
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> {
                            // Sort individual file frequencies in descending order for display
                            List<FileFreq> sortedList = e.getValue().stream()
                                    .sorted((f1, f2) -> Integer.compare(f2.getFreq(), f1.getFreq()))
                                    .collect(Collectors.toList());
                            return sortedList;
                        },
                        (v1, v2) -> v1,
                        () -> new LinkedHashMap<>()));
        return uniqueSets;
    }

    public LinkedHashMap<String, List<FileFreq>> getUniqueSets() {
        return uniqueSets;
    }

    public void setUniqueSets(LinkedHashMap<String, List<FileFreq>> uniqueSets) {
        this.uniqueSets = uniqueSets;
    }
}