package com.eliaschenker.recipegenerator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * @author Elia Schenker
 * 08.07.2022
 * Implementation of App Widget functionality. The app widget enables the user to open the app and
 * generate a random recipe
 */
public class RecipeGeneratorWidget extends AppWidgetProvider {

    /**
     * Widget which starts the MainActivity
     * Source: https://developer.android.com/guide/topics/appwidgets
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        /* Perform this loop procedure for each widget that belongs to this
           Source: https://developer.android.com/guide/topics/appwidgets
           provider. */
        for (int i=0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    /* context = */ context,
                    /* requestCode = */ 0,
                    /* intent = */ intent,
                    /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Get the layout for the widget and attach an on-click listener
            // to the button.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_generator_widget);
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}