package com.anklav.diplom.algorithm;

import edu.stanford.nlp.trees.Tree;
import lombok.Data;

import java.util.Set;

@Data
public class NiagaraObject {
    private Set<String> id;
    private Tree tree;

    public NiagaraObject(Set<String> id, Tree tree) {
        this.id = id;
        this.tree = tree;
    }
}
