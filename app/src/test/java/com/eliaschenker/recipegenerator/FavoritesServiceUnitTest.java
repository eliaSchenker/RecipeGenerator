package com.eliaschenker.recipegenerator;

import android.content.Context;

import com.eliaschenker.recipegenerator.model.Ingredient;
import com.eliaschenker.recipegenerator.model.Recipe;
import com.eliaschenker.recipegenerator.service.FavoritesService;
import com.eliaschenker.recipegenerator.service.RecipeAPIEventListener;
import com.eliaschenker.recipegenerator.service.RecipeAPIService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class FavoritesServiceUnitTest {
    public FavoritesService favoritesService;

    @Before
    public void initializeService() throws MalformedURLException {
        favoritesService = new FavoritesService();
        favoritesService.addFavorite(new Recipe(
                "12435",
                "Recipe",
                "Category",
                "Area",
                "Instructions",
                new Ingredient[0],
                new URL("https://eliaschenker.com"),
                new URL("https://eliaschenker.com")), false);
    }


    @Test()
    public void getFavorites() {
        ArrayList<Recipe> favorites = favoritesService.getFavorites();
        Assert.assertEquals("Recipe", favorites.get(favorites.size() - 1).getName());
    }

    @Test()
    public void addFavorite() throws MalformedURLException {
        favoritesService.addFavorite(new Recipe(
                "12435",
                "Recipe2",
                "Category2",
                "Area2",
                "Instructions",
                new Ingredient[0],
                new URL("https://eliaschenker.com"),
                new URL("https://eliaschenker.com")), false);
        ArrayList<Recipe> favorites = favoritesService.getFavorites();
        Assert.assertEquals("Recipe2", favorites.get(favorites.size() - 1).getName());
    }

    @Test()
    public void deleteFavorite() {
        ArrayList<Recipe> favorites = favoritesService.getFavorites();
        int favoritesSize = favorites.size();
        favoritesService.removeFavorite(favorites.get(favoritesSize - 1).getId(), false);

        Assert.assertEquals(favoritesSize - 1, favoritesService.getFavorites().size());
    }
}
