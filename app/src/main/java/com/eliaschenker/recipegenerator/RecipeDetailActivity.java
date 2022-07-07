package com.eliaschenker.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliaschenker.recipegenerator.model.Ingredient;
import com.eliaschenker.recipegenerator.model.Recipe;
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
            ingredientsText.append("\t\t&#8226;");
            ingredientsText.append(ingredient.getName());
            ingredientsText.append(" - ");
            ingredientsText.append(ingredient.getAmount());
            ingredientsText.append("<br/>");
        }
        recipeIngredients.setText(ingredientsText);
        instructionsText.setText(recipe.getInstructions());
        new SetImageToURLTask(recipeDetailImage).execute(recipe.getThumbnailURL().toString());
    }


}