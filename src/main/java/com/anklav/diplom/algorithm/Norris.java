package com.anklav.diplom.algorithm;

import com.anklav.diplom.utils.TreeUtils;
import edu.stanford.nlp.trees.Tree;

import java.util.*;
import java.util.stream.Collectors;

public class Norris {
    private Map<Set<String>, Tree> resultList = new HashMap<>();
    private final Map<Set<String>, Tree> listOfNonClosableElements = new HashMap<>();

    public Map<Set<String>, Tree> getNorris(Map<Set<String>, Tree> matrix) {
        resultList.putAll(matrix);
        matrix.forEach((key1, value1) -> {
            matrix.forEach((key2, value2) -> {
                if (equalsMapa(Map.of(key1, value1), Map.of(key2, value2))) {
                    resultList.putAll(union(Map.of(key1, value1), Map.of(key2, value2)));
                    if (!(key1.size() == 1 || key2.size() == 1)) {
                        listOfNonClosableElements.putAll(Map.of(key2, value2));
                    }
                } else {
                    resultList.putAll(Map.of(key1, value1));
                    Map<Set<String>, Tree> intersection;
                    intersection = intersection(Map.of(key1, value1), Map.of(key2, value2));
                    intersection.putAll(intersection(resultList, Map.of(key2, value2)));
                    resultList.putAll(union(resultList, intersection));
                }
            });
        });

        resultList = union(resultList, intersection(resultList, resultList));
        resultList.keySet().removeAll(listOfNonClosableElements.keySet());

//        resultList = resultList
//                .entrySet()
//                .stream()
//                .sorted(Comparator.comparingInt(e -> e.getKey().size()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                        (e1, e2) -> e1, LinkedHashMap::new));
        return this.resultList;
    }

    private boolean equalsMapa(Map<Set<String>, Tree> val1, Map<Set<String>, Tree> val2) {
        for (Map.Entry<Set<String>, Tree> e : val1.entrySet()) {
            Tree v1 = e.getValue();
            for (Map.Entry<Set<String>, Tree> entry : val2.entrySet()) {
                Tree v2 = entry.getValue();
                return TreeUtils.equalsTrees(v1, v2);
            }
        }

        return Boolean.FALSE;
    }

    Map<Set<String>, Tree> union(Map<Set<String>, Tree> val1, Map<Set<String>, Tree> val2) {
        Map<Set<String>, Tree> result = new HashMap<>();

        val1.forEach((k1, v1) -> val2.forEach((k2, v2) -> {
            if (TreeUtils.equalsTrees(v1, v2)) {
                Set<String> set = new HashSet<>();
                set.addAll(k1);
                set.addAll(k2);
                result.put(set, v1);
                if (!k1.containsAll(k2))
                    listOfNonClosableElements.put(k1, v1);
            } else {
                result.put(k2, v2);
            }
        }));

        return result;
    }

    Map<Set<String>, Tree> intersection(Map<Set<String>, Tree> val1, Map<Set<String>, Tree> val2) {
        Map<Set<String>, Tree> result = new HashMap<>();
        TreeUtils treeUtils = new TreeUtils();

        val1.forEach((k1, v1) -> {
            val2.forEach((k2, v2) -> {
                Set<String> set = new HashSet<>();
                set.addAll(k1);
                set.addAll(k2);
                Tree resultTree;
                if (set.size() > 1) {
                    resultTree = treeUtils.treesIntersection(v1, v2);
                    result.put(set, resultTree);
                } else {
                    result.put(k1, v1);
                }
            });
        });

        return result;
    }
}
