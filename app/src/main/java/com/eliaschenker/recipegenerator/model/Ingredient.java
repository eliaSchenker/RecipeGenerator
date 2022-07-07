package com.eliaschenker.recipegenerator.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private String name;
    private String amount;

    public Ingredient() {

    }

    public Ingredient(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
