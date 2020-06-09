package com.anklav.diplom.dto;

import edu.stanford.nlp.trees.Tree;

import java.util.Set;

//TODO: возвращать отсортированный список DTO-х
public class SemiLatticeViewDTO {
    String title;
    Set<String> ids;
    String tree;

    public SemiLatticeViewDTO(String title, Set<String> ids, String tree) {
        this.title = title;
        this.ids = ids;
        this.tree = tree;
    }
}
