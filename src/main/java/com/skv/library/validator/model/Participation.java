package com.skv.library.validator.model;

import java.util.List;

public class Participation {
    private List<Integer> all;

    public List<Integer> getAll() {
        return all;
    }

    public void setAll(List<Integer> all) {
        this.all = all;
    }

    public List<Integer> getOwner() {
        return owner;
    }

    public void setOwner(List<Integer> owner) {
        this.owner = owner;
    }

    private List<Integer> owner;

    // getters and setters
}
