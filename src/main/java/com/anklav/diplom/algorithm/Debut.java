package com.anklav.diplom.algorithm;

import edu.stanford.nlp.trees.Tree;
import lombok.Data;

import java.util.Set;

@Data
public class Debut {
    private Set<String> s;
    private Tree tS;
    private boolean isTest = Boolean.TRUE;
    private Set<String> generalization;
    private Set<String> q;
    private Set<String> sTest;
    private Set<String> notS;
    private Set<String> v;
    private Set<String> candidate;
    private Set<String> select;
    private Set<String> ext;
}
