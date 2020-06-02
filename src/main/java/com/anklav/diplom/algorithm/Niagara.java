package com.anklav.diplom.algorithm;

import com.anklav.diplom.utils.TreeUtils;
import com.google.common.collect.Sets;
import edu.stanford.nlp.trees.Tree;

import java.util.*;

public class Niagara {
    private List<Debut> debutList;
    private Set<String> stgood;
    private Set<String> notS;
    private Set<Integer> nts;
    private Map<String, Integer> countOfIndexes;

    public Niagara() {
        this.debutList = new ArrayList<>();
        this.stgood = new HashSet<>();
        this.notS = new HashSet<>();
        this.nts = new HashSet<>();
        this.countOfIndexes = new HashMap<>();
    }

    public void startNiagara(List<NiagaraObject> n1, List<NiagaraObject> n2) {
        debut(n1, n2);
        select();
        extension();
        analisysOfExtension();
    }

    public Map<Set<String>, Tree> debut(List<NiagaraObject> n1, List<NiagaraObject> n2) {
        TreeUtils treeUtils = new TreeUtils();

        for (int i = 0; i < n1.size() - 3; i++) {
            for (int j = i + 1; j < n1.size() - 2; j++) {
                Debut debut = new Debut();
                for (int k = 0; k < n2.size(); k++) {
                    if (TreeUtils.equalsTrees(
                            treeUtils.treesIntersection(n1.get(i).getTree(), n1.get(j).getTree()),
                            n2.get(k).getTree())) {
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
                        for (int l = 0; l < debutList.size(); l++) {
                            if (TreeUtils.equalsTrees(debutList.get(l).getTS(), debut.getTS())) {
                                debutList.get(l).setGeneralization(debut.getS());
                                debutList.get(l).setSTest(debut.getS());
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
                        for (int l = 0; l < this.debutList.size(); l++) {
                            if (TreeUtils.equalsTrees(this.debutList.get(l).getTS(), debut.getTS())) {
                                this.debutList.get(l).setGeneralization(debut.getS());
                                this.debutList.get(l).setSTest(debut.getS());
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

        for (int i = 0; i < debutList.size(); i++) {
            result.put(debutList.get(i).getS(), debutList.get(i).getTS());
        }

        return result;
    }

    void select() {
        for (int i = 0; i < this.debutList.size(); i++) {
            debutList.get(i).setNotS(com.google.common.collect.Sets.difference(
                    debutList.get(i).getNotS(),
                    debutList.get(i).getS()));

            if (!debutList.get(i).getGeneralization().isEmpty()) {
                debutList.get(i).setV(debutList.get(i).getGeneralization());
            }

            if (!debutList.get(i).getV().isEmpty()) {
                if (!debutList.get(i).getNotS().isEmpty()) {
                    debutList.get(i).setCandidate(Sets.difference(
                            debutList.get(i).getNotS(),
                            debutList.get(i).getV()));
                }
            }
        }
    }

    void extension() {

    }

    void analisysOfExtension() {

    }
}
