package com.eliaschenker.recipegenerator;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ServiceTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.eliaschenker.recipegenerator.model.Recipe;
import com.eliaschenker.recipegenerator.service.RecipeAPIEventListener;
import com.eliaschenker.recipegenerator.service.RecipeAPIService;

import java.util.concurrent.TimeoutException;

/**
 * @author Elia Schenker
 * 08.07.2022
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RecipeAPIServiceInstrumentedTest {

    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();

    /**
     * Tests if the RecipeAPIService can be created in an android environment
     */
    @Test
    public void testWithBoundService() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(ApplicationProvider.getApplicationContext(),
                        RecipeAPIService.class);

        // Bind the service and grab a reference to the binder.
        IBinder binder = serviceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        RecipeAPIService service =
                ((RecipeAPIService.RecipeAPIBinder) binder).getService();

        // Verify that the service is working correctly.
        assertNotNull(service);
    }
}