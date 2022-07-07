package com.eliaschenker.recipegenerator.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Recipe implements Serializable {
    private String name;
    private String category;
    private String area;
    private String instructions;
    private Ingredient[] ingredients;
    private URL thumbnailURL;
    private URL sourceURL;

    public Recipe() {

    }

    public Recipe(String name, String category, String area, String instructions, Ingredient[] ingredients, URL thumbnailURL, URL sourceURL) {
        this.name = name;
        this.category = category;
        this.area = area;
        this.ingredients = ingredients;
        this.thumbnailURL = thumbnailURL;
        this.sourceURL = sourceURL;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public URL getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        try {
            if (!Objects.equals(thumbnailURL, "")) {
                this.thumbnailURL = new URL(thumbnailURL);
            }
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public URL getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        try {
            if (!Objects.equals(sourceURL, "")) {
                this.sourceURL = new URL(sourceURL);
            }
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }
}