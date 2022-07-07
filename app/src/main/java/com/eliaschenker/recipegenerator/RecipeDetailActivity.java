package com.eliaschenker.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliaschenker.recipegenerator.model.Ingredient;
import com.eliaschenker.recipegenerator.model.Recipe;
import com.eliaschenker.recipegenerator.service.FavoritesService;
import com.eliaschenker.recipegenerator.service.RecipeAPIService;
import com.eliaschenker.recipegenerator.service.SetImageToURLTask;

public class RecipeDetailActivity extends AppCompatActivity {

    public Recipe recipe;

    //UI References
    public Button backButton;
    public Button favoriteButton;
    public TextView recipeTitle;
    public TextView recipeDescription;
    public TextView recipeIngredients;
    public TextView instructionsText;
    public ImageView recipeDetailImage;

    //Favorite service
    public FavoritesService favoritesService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        recipe = (Recipe) intent.getExtras().get("recipe");

        //Assign UI References
        backButton = findViewById(R.id.backBtn);
        favoriteButton = findViewById(R.id.favoriteBtn);
        recipeTitle = findViewById(R.id.recipeDetailTitle);
        recipeDescription = findViewById(R.id.recipeDescription);
        recipeIngredients = findViewById(R.id.ingredientsList);
        instructionsText = findViewById(R.id.instructionsText);
        recipeDetailImage = findViewById(R.id.recipeDetailImage);

        initUI();
        initFavoritesService();
    }

    /**
     * Sets the UI by recipe
     */
    public void initUI() {
        recipeTitle.setText(recipe.getName());
        recipeDescription.setText(getString(R.string.recipe_detail_description,
                recipe.getCategory(),
                recipe.getArea()));
        StringBuilder ingredientsText = new StringBuilder();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredientsText.append("\t\t•");
            ingredientsText.append(ingredient.getName());
            ingredientsText.append(", ");
            ingredientsText.append(ingredient.getAmount());
            ingredientsText.append("\n");
        }
        recipeIngredients.setText(ingredientsText);
        instructionsText.setText(recipe.getInstructions());
        new SetImageToURLTask(recipeDetailImage).execute(recipe.getThumbnailURL().toString());

        //Create button events
        backButton.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
    }

    public void updateFavoriteButton() {
        if(!favoritesService.isFavorite(recipe.getId())) {
            favoriteButton.setText(getString(R.string.favorite_button));
            favoriteButton.setOnClickListener(view -> {
                favoritesService.addFavorite(recipe);
                updateFavoriteButton();
            });
        }else {
            favoriteButton.setText(getString(R.string.unfavorite_button));
            favoriteButton.setOnClickListener(view -> {
                favoritesService.removeFavorite(recipe.getId());
                updateFavoriteButton();
            });
        }
    }

    public void initFavoritesService() {
        Intent bindFavoritesServiceIntent = new Intent(this, FavoritesService.class);
        bindService(bindFavoritesServiceIntent, favoritesServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection favoritesServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //Get the instance of the BMIService
            FavoritesService.FavoritesBinder binder = (FavoritesService.FavoritesBinder) service;
            //References to the BMI Service
            favoritesService = binder.getService();
            updateFavoriteButton();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
}