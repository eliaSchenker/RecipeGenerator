package com.eliaschenker.recipegenerator.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.JsonReader;

import com.eliaschenker.recipegenerator.model.Ingredient;
import com.eliaschenker.recipegenerator.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Elia Schenker
 * 08.07.2022
 * The RecipeAPIService gives the ability to get a random recipe which is fetched from the API and
 * converted into the recipe model class
 */
public class RecipeAPIService extends Service {
    private final IBinder binder = new RecipeAPIBinder();
    private final String RANDOM_RECIPE_URL = "https://www.themealdb.com/api/json/v1/1/random.php";

    public class RecipeAPIBinder extends Binder {
        public RecipeAPIService getService() {
            // Return this instance of RecipeAPIService so clients can call public methods
            return RecipeAPIService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Returns a random recipe
     * @param recipeAPIEventListener The event listener, which is called when the execution is finished
     */
    public void getRandomRecipe(RecipeAPIEventListener recipeAPIEventListener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                URL url = new URL(RANDOM_RECIPE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());

                /*
                   Convert the inputStream to a string
                   Source: https://mkyong.com/java/how-to-convert-inputstream-to-string-in-java/
                 */
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                String jsonResult = result.toString("UTF-8");

                //Use the JSONObject class to read the json string
                JSONObject json = new JSONObject(jsonResult);

                Recipe recipe = new Recipe();
                ArrayList<Ingredient> ingredients = new ArrayList<>();

                //Fetch the recipe information out of the json
                JSONObject recipeTop = (JSONObject) json.getJSONArray("meals").get(0);
                recipe.setId(recipeTop.getString("idMeal"));
                recipe.setName(recipeTop.getString("strMeal"));
                recipe.setCategory(recipeTop.getString("strCategory"));
                recipe.setArea(recipeTop.getString("strArea"));
                recipe.setInstructions(recipeTop.getString("strInstructions"));
                recipe.setThumbnailURL(recipeTop.getString("strMealThumb"));
                if(recipeTop.has("strSource")) {
                    recipe.setSourceURL(recipeTop.getString("strSource"));
                }

                //Fetch the ingredients out of the json
                int ingredientIndex = 1;
                while(true) {
                    if(recipeTop.has("strIngredient" + ingredientIndex)) {
                        String ingredientName = recipeTop.getString("strIngredient" + ingredientIndex);
                        String ingredientAmount = recipeTop.getString("strMeasure" + ingredientIndex);
                        if (ingredientName.equals("")) {
                            break;
                        } else {
                            ingredients.add(new Ingredient(ingredientName, ingredientAmount));
                            ingredientIndex++;
                        }
                    }
                }
                recipe.setIngredients(ingredients.toArray(new Ingredient[0]));

                inputStream.close();

                //Call the onFinish event
                recipeAPIEventListener.onFinish(recipe);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                //Call the onFail event
                recipeAPIEventListener.onFail();
            }
        });
    }
}

