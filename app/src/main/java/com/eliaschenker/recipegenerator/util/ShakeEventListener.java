package com.eliaschenker.recipegenerator.util;

/**
 * @author Elia Schenker
 * 08.07.2022
 * ShakeEventListener interface used by the ShakeDetector.
 * Contains the method onShakeStart, which is called when the user starts shaking the device
 * and onShakeStop, which is called when the user stops shaking thhe device
 */
public interface ShakeEventListener {
    void onShakeStart();
    void onShakeStop();
}