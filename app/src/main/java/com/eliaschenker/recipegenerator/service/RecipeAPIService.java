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

    public void getRandomRecipe(RecipeAPIEventListener recipeAPIEventListener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.myLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(RANDOM_RECIPE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());

                /* Convert the inputStream to a string
                   Source: https://mkyong.com/java/how-to-convert-inputstream-to-string-in-java/
                 */
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                String jsonResult = result.toString("UTF-8");
                JSONObject json = new JSONObject(jsonResult);

                Recipe recipe = new Recipe();
                ArrayList<Ingredient> ingredients = new ArrayList<>();

                JSONObject recipeTop = (JSONObject) json.getJSONArray("meals").get(0);
                recipe.setName(recipeTop.getString("strMeal"));
                recipe.setCategory(recipeTop.getString("strCategory"));
                recipe.setArea(recipeTop.getString("strArea"));
                recipe.setInstructions(recipeTop.getString("strInstructions"));
                recipe.setThumbnailURL(recipeTop.getString("strMealThumb"));
                recipe.setSourceURL(recipeTop.getString("strSource"));

                int ingredientIndex = 1;
                while(true) {
                    String ingredientName = recipeTop.getString("strIngredient" + ingredientIndex);
                    String ingredientAmount = recipeTop.getString("strMeasure" + ingredientIndex);
                    if(ingredientName.equals("")) {
                        break;
                    }else {
                        ingredients.add(new Ingredient(ingredientName, ingredientAmount));
                        ingredientIndex++;
                    }
                }
                recipe.setIngredients(ingredients.toArray(new Ingredient[0]));

                inputStream.close();

                recipeAPIEventListener.onFinish(recipe);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
    }
}

