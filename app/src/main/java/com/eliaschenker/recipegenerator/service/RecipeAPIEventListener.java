package com.eliaschenker.recipegenerator.service;

import com.eliaschenker.recipegenerator.model.Recipe;

public interface RecipeAPIEventListener {
    void onFinish(Recipe recipe);
    void onFail();
}
