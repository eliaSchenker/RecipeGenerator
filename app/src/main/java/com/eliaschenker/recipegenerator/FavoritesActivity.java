package com.eliaschenker.recipegenerator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eliaschenker.recipegenerator.model.Recipe;
import com.eliaschenker.recipegenerator.service.FavoritesService;

/**
 * @author Elia Schenker
 * 08.07.2022
 * The FavoritesAcitivty displays the users favorite recipes. The recipes can be opened and read or
 * deleted.
 */
public class FavoritesActivity extends AppCompatActivity {

    //UI References
    public Button backBtn;
    public LinearLayout favoritesList;
    public CardView favoritesTemplate;

    //Favorites service
    public FavoritesService favoritesService;
    public boolean favoritesServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_favorites);
        getSupportActionBar().hide();

        //Get UI References
        backBtn = findViewById(R.id.favoriteBackBtn);
        favoritesList = findViewById(R.id.favoritesList);
        favoritesTemplate = findViewById(R.id.favoriteTemplate);

        //Create event listener
        backBtn.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        initFavoritesService();
    }

    /**
     * Loads the favorite list using the favorite service
     */
    public void loadFavoritesList() {
        if(favoritesServiceBound) {
            //Clear the favorites list
            favoritesList.removeAllViews();
            favoritesList.invalidate();

            //Iterate through the favorites
            for (Recipe favorite : favoritesService.getFavorites()) {
                //Create a clone of the template layout
                CardView cardView = (CardView) LayoutInflater.from(this).inflate(R.layout.favorites_template, null);

                cardView.setOnClickListener(view -> {
                    Intent recipeDetailActivity = new Intent(this, RecipeDetailActivity.class);
                    recipeDetailActivity.putExtra("recipe", favorite);
                    startActivity(recipeDetailActivity);
                });

                //Set the values of the template
                ((TextView) cardView.findViewById(R.id.favoriteTitle)).setText(favorite.getName());
                ((TextView) cardView.findViewById(R.id.favoriteDescription)).setText(getString(
                        R.string.recipe_card_description,
                        favorite.getCategory(),
                        favorite.getArea(),
                        favorite.getIngredients().length));
                cardView.findViewById(R.id.favoriteDeleteBtn)
                        .setOnClickListener(view -> {
                            new AlertDialog.Builder(this)
                                    .setTitle("Confirm deletion")
                                    .setMessage("Do you really want to delete the recipe " +
                                            favorite.getName() + "?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                                        favoritesService.removeFavorite(favorite.getId(), true);
                                        loadFavoritesList();
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        });
                favoritesList.addView(cardView); //Add the template to the list
            }
        }
    }

    /**
     * Called when the activity becomes active again (if the user returns from the RecipeDetailActivity)
     * Reloads the favorites list to detect any chances made to the favorites
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        loadFavoritesList();
    }

    /**
     * Initializes and binds the FavoritesService
     */
    public void initFavoritesService() {
        Intent bindFavoritesServiceIntent = new Intent(this, FavoritesService.class);
        bindService(bindFavoritesServiceIntent, favoritesServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection favoritesServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //Get the instance of the FavoritesBinder
            FavoritesService.FavoritesBinder binder = (FavoritesService.FavoritesBinder) service;
            //Get reference to the FavoritesService
            favoritesService = binder.getService();
            favoritesServiceBound = true;
            //Load the favorites
            loadFavoritesList();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            favoritesServiceBound = false;
        }
    };
}