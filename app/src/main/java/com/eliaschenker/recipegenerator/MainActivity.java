package com.eliaschenker.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //UI References
    public ImageButton showFavoriteBtn;
    public Button generateRecipeBtn;

    //Recipe Card
    public CardView recipeCard;
    public TextView recipeTitle;
    public TextView recipeText;
    public Button showRecipeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();

        //Create UI references
        showFavoriteBtn = findViewById(R.id.showFavoritesBtn);
        generateRecipeBtn = findViewById(R.id.generateRecipeBtn);

        recipeCard = findViewById(R.id.recipeCard);
        recipeTitle = findViewById(R.id.recipeTitle);
        recipeText = findViewById(R.id.recipeText);
        showRecipeBtn = findViewById(R.id.showRecipeBtn);

        //Register OnClick Events
        generateRecipeBtn.setOnClickListener(v -> getRandomRecipe());

        //Register Shake Handler
    }

    public void getRandomRecipe() {

    }
}