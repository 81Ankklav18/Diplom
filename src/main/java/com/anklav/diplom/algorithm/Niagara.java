package com.anklav.diplom.algorithm;

import com.anklav.diplom.utils.TreeUtils;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.Sets;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Niagara {
    List<Debut> fDebuts = new ArrayList<>();
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
        Set<Set<String>> debutSet = debut(n1, n2);
        List<Debut> selectList = select(debutSet);
        Set<Set<String>> exts = extensions(selectList, n1);
        Map<Set<String>, Tree> result = new HashMap<>();
        Map<String, Tree> temp = new HashMap<>();
        exts.forEach((e) -> {
            e.forEach((ee) -> {
                for (NiagaraObject niagaraObject : n1) {
                    if (ee.equals(niagaraObject.getId().toString().substring(1, niagaraObject.getId().toString().length() - 1))) {
                        temp.put(ee, niagaraObject.getTree());
                    }
                }
            });
        });

        Map<Set<String>, Tree> temp1 = new HashMap<>();
        temp.forEach((k1, v1) -> {
            temp.forEach((k2, v2) -> {
                if (!k1.equals(k2)) {
                    temp1.put(Set.of(k1, k2), treeUtils.treesIntersection(v1, v2));
                }
            });
        });

        Map<Set<String>, Tree> temp2 = new HashMap<>();
        temp1.forEach((k1, v1) -> {
            temp1.forEach((k2, v2) -> {
                temp2.put(Sets.union(k1, k2), treeUtils.treesIntersection(v1, v2));
            });
        });

        exts.forEach((e) -> {
            result.put(e, temp2.get(e));
        });

        return result;
    }

    private Set<Set<String>> debut(List<NiagaraObject> n1, List<NiagaraObject> n2) {
        Set<Set<String>> stest = new HashSet<>();
        TreeUtils treeUtils = new TreeUtils();

        for (int i = 0; i < n1.size(); i++) {
            for (int j = i + 1; j < n1.size(); j++) {
                Debut debut = new Debut();

                Tree tree = treeUtils.treesIntersection(n1.get(i).getTree(), n1.get(j).getTree());
                Set<String> temp = new HashSet<>();
                temp.addAll(n1.get(i).getId());
                temp.addAll(n1.get(j).getId());
                debut.setS(temp);
                debut.setGeneralization(temp);
                debut.setTS(tree);
                debut.setTest(Boolean.TRUE);
                for (int k = 0; k < n1.size(); k++) {
                    if (k != i && k != j) {
                        if (TreeUtils.equalsTrees(tree, treeUtils.treesIntersection(tree, n1.get(k).getTree()))) {
                            Set<String> tempOfGeneralization = new HashSet<>();
                            tempOfGeneralization.addAll(temp);
                            tempOfGeneralization.addAll(n1.get(k).getId());
                            debut.setGeneralization(tempOfGeneralization);
                        }
                    }
                }
                debut.setSTest(debut.getGeneralization());
                stest.add(debut.getGeneralization());
                fDebuts.add(debut);
            }
        }

        return stest;
    }

    private List<Debut> select(Set<Set<String>> debuts) {
        List<Debut> debutList = new ArrayList<>();
        Set<String> elements = new HashSet<>();

        debuts.forEach(elements::addAll);

        debuts.forEach((e) -> {
            Debut debut = new Debut();
            debut.setS(e);
            debut.setNotS(com.google.common.collect.Sets.difference(elements, e));
            debuts.forEach((e2) -> {
                if (e2.containsAll(e)) {
                    if (! e2.equals(e)) {
                        debut.setV(e2);
                    }
                }
            });
            if (debut.getV() == null){
                debut.setCandidate(debut.getNotS());
                debut.setSelect(debut.getNotS());
            } else {
                debut.setCandidate(com.google.common.collect.Sets.difference(debut.getNotS(), debut.getV()));
                debut.setSelect(com.google.common.collect.Sets.difference(debut.getNotS(), debut.getV()));
            }
            debutList.add(debut);
        });

        return debutList;
    }

    private Set<Set<String>> extensions(List<Debut> debuts, List<NiagaraObject> n1) {
        Set<Debut> extList = new HashSet<>();
        Set<Set<String>> ext = new HashSet<>();
        TreeUtils treeUtils = new TreeUtils();
        for (int i = 0; i < debuts.size(); i++) {
            Debut debut = new Debut();
            int finalI = i;
            debuts.get(i).getSelect().forEach((e) -> {
                Set<String> temp = new HashSet<>();
                temp.addAll(debuts.get(finalI).getS());
                temp.add(e);
                debut.setS(temp);
                debut.setExt(temp);
                for (NiagaraObject niagaraObject : n1) {
                    if (niagaraObject.getId().toString().substring(1, niagaraObject.getId().toString().length() - 1).equals(e)) {
                        for (int k = 0; k < fDebuts.size(); k++) {
                            if (fDebuts.get(k).getS().equals(debuts.get(k).getS())) {
                                debut.setTS(treeUtils.treesIntersection(niagaraObject.getTree(), fDebuts.get(k).getTS()));
                            }
                        }
                    }
                }

                extList.add(debut);
                ext.add(temp);
            });
        }

        fDebuts.addAll(extList);
        return ext;
    }
}
