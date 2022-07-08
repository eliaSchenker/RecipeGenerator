package com.eliaschenker.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliaschenker.recipegenerator.model.Ingredient;
import com.eliaschenker.recipegenerator.model.Recipe;
import com.eliaschenker.recipegenerator.service.FavoritesService;
import com.eliaschenker.recipegenerator.service.RecipeAPIService;
import com.eliaschenker.recipegenerator.service.SetImageToURLTask;

import java.net.URL;

/**
 * @author Elia Schenker
 * 08.07.2022
 * RecipeDetailActivity displays a single recipe's information, ingredients and instructions.
 * It also allows the user to share the recpie and view it's source.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    //The Recipe of which the details are displayed
    public Recipe recipe;

    //UI References
    public Button backButton;
    public Button favoriteButton;
    public Button shareButton;
    public Button openRecipeSourceButton;
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_recipe_detail);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        recipe = (Recipe) intent.getExtras().get("recipe"); //Get the recipe stored in the intent

        //Assign UI References
        backButton = findViewById(R.id.backBtn);
        favoriteButton = findViewById(R.id.favoriteBtn);
        shareButton = findViewById(R.id.shareRecipeBtn);
        openRecipeSourceButton = findViewById(R.id.openRecipeSourceBtn);
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
        String recipeDesc = getString(R.string.recipe_detail_description,
                recipe.getCategory(),
                recipe.getArea());
        recipeDescription.setText(recipeDesc);

        //Build the ingredients text
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

        shareButton.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            //Generate the text for the message
            String shareText =
                    recipe.getName() + "\n" +
                            recipeDesc + "\n\nIngredients:\n" +
                            ingredientsText + "\n" +
                            recipe.getInstructions() +
                            "\n\nThis recipe was generated using the Recipe Generator, download now!";

            //Open the share screen
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

            //Vibrate the device when sharing
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            v.vibrate(400);
        });

        openRecipeSourceButton.setOnClickListener(view -> {
            URL sourceURL = recipe.getSourceURL();
            //Check if the source URL exists
            if(sourceURL != null && !sourceURL.toString().equals("")) {
                //If it exists, open it in the browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sourceURL.toString()));
                startActivity(intent);
            }else {
                //Show error message if it doesnt exist.
                new AlertDialog.Builder(this).setTitle("No source link").
                        setMessage("Sorry, this recipe does not have a source link!")
                        .setNeutralButton(R.string.dialog_ok, (dialogInterface, i) -> {});
            }

        });
    }

    /**
     * Updates the text and event of the favorite button (according to if the recipe is already favorited)
     */
    public void updateFavoriteButton() {
        if(!favoritesService.isFavorite(recipe.getId())) {
            favoriteButton.setText(getString(R.string.favorite_button));
            favoriteButton.setOnClickListener(view -> {
                favoritesService.addFavorite(recipe, true);
                updateFavoriteButton();
            });
        }else {
            favoriteButton.setText(getString(R.string.unfavorite_button));
            favoriteButton.setOnClickListener(view -> {
                favoritesService.removeFavorite(recipe.getId(), true);
                updateFavoriteButton();
            });
        }
    }

    /**
     * Initialize and bind the favorites service
     */
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