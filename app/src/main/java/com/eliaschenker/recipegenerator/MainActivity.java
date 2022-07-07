package com.eliaschenker.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eliaschenker.recipegenerator.model.Recipe;
import com.eliaschenker.recipegenerator.service.RecipeAPIEventListener;
import com.eliaschenker.recipegenerator.service.RecipeAPIService;
import com.eliaschenker.recipegenerator.service.SetImageToURLTask;
import com.eliaschenker.recipegenerator.util.ShakeDetector;
import com.eliaschenker.recipegenerator.util.ShakeEventListener;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    //UI References
    public ImageButton showFavoriteBtn;
    public Button generateRecipeBtn;

    //Recipe Card
    public CardView recipeCard;
    public ImageView recipeImage;
    public TextView recipeTitle;
    public TextView recipeText;
    public Button showRecipeBtn;


    //Shake Detector
    public ShakeDetector shakeDetector;

    //Recipe API Connection
    public RecipeAPIService recipeAPIService;
    public boolean recipeAPIServiceConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create UI references
        showFavoriteBtn = findViewById(R.id.showFavoritesBtn);
        generateRecipeBtn = findViewById(R.id.generateRecipeBtn);

        recipeCard = findViewById(R.id.recipeCard);
        recipeTitle = findViewById(R.id.recipeTitle);
        recipeText = findViewById(R.id.recipeText);
        showRecipeBtn = findViewById(R.id.showRecipeBtn);
        recipeImage = findViewById(R.id.recipeImage);

        //Register OnClick Events
        generateRecipeBtn.setOnClickListener(v -> fetchAndSetRandomRecipe());

        //Register Shake Detector
        shakeDetector = new ShakeDetector(this, new ShakeEventListener() {
            @Override
            public void onShakeStart() {
                fetchAndSetRandomRecipe();
            }

            @Override
            public void onShakeStop() {
                //Not implemented
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        initRecipeAPIService();
    }

    /**
     * Create and bind the RecipeAPIService
     */
    public void initRecipeAPIService() {
        Intent bindRecipeAPIServiceIntent = new Intent(this, RecipeAPIService.class);
        bindService(bindRecipeAPIServiceIntent, recipeAPIServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Display a recipe on the Recipe Card
     * @param recipe A recipe object
     */
    public void showRecipeCard(Recipe recipe) {
        runOnUiThread(() -> {
            recipeCard.setVisibility(View.VISIBLE);
            recipeTitle.setText(recipe.getName());
            recipeText.setText(getString(
                    R.string.recipe_card_description,
                    recipe.getCategory(),
                    recipe.getArea(),
                    recipe.getIngredients().length));
            new SetImageToURLTask(recipeImage).execute(recipe.getThumbnailURL().toString());
            showRecipeBtn.setOnClickListener(view -> {
                Intent recipeDetailActivity = new Intent(this, RecipeDetailActivity.class);
                recipeDetailActivity.putExtra("recipe", recipe);
                startActivity(recipeDetailActivity);
            });
        });

    }

    /**
     * Downloads a recipe from the API and sets it to the UI
     */
    public void fetchAndSetRandomRecipe() {
        if(recipeAPIServiceConnected) {
            Activity context = this;
            this.recipeAPIService.getRandomRecipe(new RecipeAPIEventListener() {
                @Override
                public void onFinish(Recipe recipe) {
                   showRecipeCard(recipe);
                }

                @Override
                public void onFail() {
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(context)
                                .setTitle("No connection")
                                .setMessage("Generation of random recipe failed, not connected!")
                                .setNeutralButton(R.string.dialog_ok, (dialogInterface, i) -> {
                                })
                                .show();
                    });
                }
            });
        }
    }

    private final ServiceConnection recipeAPIServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //Get the instance of the BMIService
            RecipeAPIService.RecipeAPIBinder binder = (RecipeAPIService.RecipeAPIBinder) service;
            //References to the BMI Service
            recipeAPIService = binder.getService();
            recipeAPIServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            recipeAPIServiceConnected = false;
        }
    };
}