package com.eliaschenker.recipegenerator.service;

import com.eliaschenker.recipegenerator.model.Recipe;

/**
 * @author Elia Schenker
 * 08.07.2022
 * The RecipeAPIEventListener is used in the RecipeAPIService to indicate when a call to the API is
 * finished or has failed.
 */
public interface RecipeAPIEventListener {
    void onFinish(Recipe recipe);
    void onFail();
}
