package com.eliaschenker.recipegenerator.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;


import com.eliaschenker.recipegenerator.model.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * @author Elia Schenker
 * 08.07.2022
 * The FavoritesService manages a list of favorite recipes which can be added, removed and are
 * saved in the SharedPreferences.
 */
public class FavoritesService extends Service {
    private final IBinder binder = new FavoritesBinder();

    public class FavoritesBinder extends Binder {
        public FavoritesService getService() {
            // Return this instance of FavoritesService so clients can call public methods
            return FavoritesService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        loadFavorites();
        return binder;
    }

    private final String sharedPreferenceName = "RecipeGenerator";
    private final String sharedPreferenceFavoriteKey = "favorites";

    private ArrayList<Recipe> favorites = new ArrayList<>();

    /**
     * Saves the favorites to the shared preferences
     */
    public void saveFavorites() {
        SharedPreferences prefs = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(favorites);

        editor.putString(sharedPreferenceFavoriteKey, json);
        editor.apply();
    }

    /**
     * Loades the favorites from the shared preferences
     */
    public void loadFavorites() {
        SharedPreferences prefs = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String json = prefs.getString(sharedPreferenceFavoriteKey, "");
        if(!json.equals("")) {
            Gson gson = new Gson();
            favorites = gson.fromJson(json, new TypeToken<ArrayList<Recipe>>(){}.getType());
        }

        editor.apply();
    }

    /**
     * Adds a new favorite
     * @param recipe The new favorite recipe
     * @param doSave Should the favorites be saved to the sharedpreferences
     */
    public void addFavorite(Recipe recipe, boolean doSave) {
        favorites.add(recipe);
        if(doSave) {
            saveFavorites();
        }
    }

    /**
     * Removes a favorite
     * @param id The id of a recipe to be removed
     * @param doSave Should the favorites be saved to the sharedpreferences
     */
    public void removeFavorite(String id, boolean doSave) {
        Recipe recipeToRemove = null;
        for(Recipe recipe: favorites) {
            if(recipe.getId().equals(id)) {
                recipeToRemove = recipe;
                break;
            }
        }
        if(recipeToRemove != null) {
            favorites.remove(recipeToRemove);
        }
        if(doSave) {
            saveFavorites();
        }
    }

    /**
     * Checks if a recipe is already a favorite
     * @param id The id of a recipe to check
     * @return Is the recipe already a favorite
     */
    public boolean isFavorite(String id) {
        for(Recipe recipe: favorites) {
            if(recipe.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the favorites
     * @return Recipe ArrayList
     */
    public ArrayList<Recipe> getFavorites() {
        return favorites;
    }
}