package com.anklav.diplom.algorithm;

import com.anklav.diplom.utils.TreeUtils;
import edu.stanford.nlp.trees.Tree;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class CloseByOne {
    private ConcurrentMap<Set<String>, Tree> resultList = new ConcurrentHashMap<>();
    private final ConcurrentMap<Set<String>, Tree> listOfNonClosableElements = new ConcurrentHashMap<>();
    int count;

    public CloseByOne(ConcurrentMap<Set<String>, Tree> matrix, int count) {
        //TODO: протестировать
        matrix.forEach((k, v) -> {
            resultList.putIfAbsent(k, v);
        });
        this.count = count;
    }

    public Map<Set<String>, Tree> recursiveCbO(ConcurrentMap<Set<String>, Tree> matrix) {
        matrix.forEach((key1, value1) -> {
            matrix.forEach((key2, value2) -> {
                if (!key1.equals(key2)) {
                    Map<Set<String>, Tree> resultOfIntersection = intersection(Map.of(key1, value1), Map.of(key2, value2));

                    resultList.forEach((k1, v1) ->
                            resultOfIntersection.forEach((k2, v2) -> {
                                if (TreeUtils.equalsTrees(v1, v2) && !k1.equals(k2) && k1.size() < k2.size()) {
                                    listOfNonClosableElements.put(k1, v1);
                                }
                            }));
                    resultList.keySet().removeAll(listOfNonClosableElements.keySet());
                    resultList.putAll(resultOfIntersection);
                }
            });
        });

        resultList = resultList
                .entrySet()
                .stream()
                .filter(x -> !(x.getValue().depth() == 0 && x.getKey().size() != count))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, ConcurrentHashMap::new));

        while (resultList.keySet().stream().noneMatch(x -> x.size() == count)) {
            recursiveCbO(resultList);
        }

//        resultList = resultList
//                .entrySet()
//                .stream()
//                .sorted(Comparator.comparingInt(e -> e.getKey().size()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                        (e1, e2) -> e1, ConcurrentHashMap::new));

//        System.out.println("///////CLOSE BY ONE///////");
//        List<Tree> treesVal = resultList.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
//
//        for (int i = 0; i < treesVal.size(); i++) {
//            treesVal.get(i).pennPrint();
//            System.out.println("----------");
//        }
//        System.out.println("///////END CLOSE BY ONE///////");
        return this.resultList;
    }

    boolean equalsSetsOfString(Set<String> s1, Set<String> s2) {
        List<String> l1 = new ArrayList<>(s1);
        List<String> l2 = new ArrayList<>(s2);

        if (l1.size() != l2.size()) {
            return Boolean.FALSE;
        } else {
            for (int i = 0; i < l1.size(); i++) {
                if (l1.get(i) != l2.get(i)) {
                    return Boolean.FALSE;
                }
            }
        }

        return Boolean.TRUE;
    }

    List<Map<Set<String>, Tree>> convertMapToListOfMap(Map<Set<String>, Tree> stage) {
        List<Map<Set<String>, Tree>> convertedList = new ArrayList<>();
        stage.forEach((k, v) -> {
            Map<Set<String>, Tree> temp = new HashMap<>();
            temp.put(k, v);
            convertedList.add(new HashMap<Set<String>, Tree>(temp));
        });

        return convertedList;
    }

    Map<Set<String>, Tree> intersection(Map<Set<String>, Tree> val1, Map<Set<String>, Tree> val2) {
        Map<Set<String>, Tree> result = new HashMap<>();
        TreeUtils treeUtils = new TreeUtils();

        val1.forEach((k1, v1) -> {
            val2.forEach((k2, v2) -> {
                Set<String> set = new HashSet();
                set.addAll(k1);
                set.addAll(k2);
                Tree resultTree = treeUtils.treesIntersection(v1, v2);
                result.put(set, resultTree);
            });
        });

        return result;
    }

    boolean equalsMapa(Map<Set<String>, Tree> val1, Map<Set<String>, Tree> val2) {
        boolean result = Boolean.FALSE;
        for (Map.Entry<Set<String>, Tree> e : val1.entrySet()) {
            Tree v1 = e.getValue();
            for (Map.Entry<Set<String>, Tree> entry : val2.entrySet()) {
                Tree v2 = entry.getValue();
                result = TreeUtils.equalsTrees(v1, v2);
            }
        }

        return result;
    }
}
