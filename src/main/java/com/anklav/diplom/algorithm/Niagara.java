package com.anklav.diplom.algorithm;

import com.anklav.diplom.utils.TreeUtils;
import edu.stanford.nlp.trees.Tree;

import java.util.*;

public class Niagara {
    private final List<Debut> debutList;
    private final Set<String> stgood;
    private final Set<String> notS;
    private final Map<String, Integer> countOfIndexes;

    public Niagara() {
        this.debutList = new ArrayList<>();
        this.stgood = new HashSet<>();
        this.notS = new HashSet<>();
        this.countOfIndexes = new HashMap<>();
    }

    public Map<Set<String>, Tree> startNiagara(List<NiagaraObject> n1, List<NiagaraObject> n2) {
        TreeUtils treeUtils = new TreeUtils();

        for (int i = 0; i < n1.size() - 3; i++) {
            for (int j = i + 1; j < n1.size() - 2; j++) {
                Debut debut = new Debut();
                for (NiagaraObject niagaraObject : n2) {
                    if (TreeUtils.equalsTrees(
                            treeUtils.treesIntersection(n1.get(i).getTree(), n1.get(j).getTree()),
                            niagaraObject.getTree())) {
                        Set<String> temp = new HashSet<>();
                        temp.addAll(n1.get(i).getId());
                        temp.addAll(n1.get(j).getId());

                        debut.setQ(temp);
                        debut.setTest(Boolean.FALSE);
                    }
                }

                if (debut.isTest() != Boolean.FALSE)
                    debut.setTest(Boolean.TRUE);
                Set<String> temp = new HashSet<>();
                temp.addAll(n1.get(i).getId());
                temp.addAll(n1.get(j).getId());
                debut.setS(temp);
                this.notS.addAll(temp);
                if (n1.get(i).getTree() != null && n1.get(j).getTree() != null)
                    debut.setTS(treeUtils.treesIntersection(n1.get(i).getTree(), n1.get(j).getTree()));
                if (debut.getQ() == null) {
                    if (this.debutList.size() > 0) {
                        for (Debut value : debutList) {
                            if (TreeUtils.equalsTrees(value.getTS(), debut.getTS())) {
                                value.setGeneralization(debut.getS());
                                value.setSTest(debut.getS());
                                debut.setGeneralization(debut.getS());
                                debut.setSTest(debut.getS());
                            }
                        }
                    }
                }
                debut.getS().forEach((s) -> {
                    if (countOfIndexes.containsKey(s)) {
                        countOfIndexes.put(s, countOfIndexes.get(s) + 1);
                    } else {
                        countOfIndexes.put(s, 1);
                    }
                });
                debutList.add(debut);
            }
        }

        for (int i = 2; i < n1.size(); i++) {
            List<Debut> tempList = new ArrayList<>();
            for (int j = 0; j < this.debutList.size(); j++) {
                for (int k = j + 1; k < n1.size(); k++) {
                    Debut debut = new Debut();

                    debut.setTest(Boolean.TRUE);
                    Set<String> temp = new HashSet<>();
                    temp.addAll(this.debutList.get(j).getS());
                    temp.addAll(n1.get(k).getId());
                    debut.setS(temp);
                    debut.setTS(treeUtils.treesIntersection(this.debutList.get(j).getTS(), n1.get(j).getTree()));
                    if (debut.getQ() == null) {
                        for (Debut value : this.debutList) {
                            if (TreeUtils.equalsTrees(value.getTS(), debut.getTS())) {
                                value.setGeneralization(debut.getS());
                                value.setSTest(debut.getS());
//                                debut.setGeneralization(debut.getS());
                                debut.setSTest(debut.getS());
                            }
                        }
                    }
                    tempList.add(debut);
                }
            }
            this.debutList.addAll(tempList);
        }
        List<Debut> listOfGoods = new ArrayList<>();
        countOfIndexes.forEach((k, v) -> {
            if (v == 1) {
                for (int i = 0; i < debutList.size(); i++) {
                    int finalI = i;
                    debutList.get(i).getS().forEach((s) -> {
                        if (s.equals(k)) {
                            this.stgood.addAll(debutList.get(finalI).getSTest());
                            listOfGoods.add(debutList.get(finalI));
                        }
                    });
                }
            }
        });
        debutList.removeAll(listOfGoods);

        Map<Set<String>, Tree> result = new HashMap<>();

        for (Debut debut : debutList) {
            result.put(debut.getS(), debut.getTS());
        }

        return result;
    }
}
