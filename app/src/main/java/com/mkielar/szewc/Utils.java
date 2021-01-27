package com.mkielar.szewc;

public class Utils {
    static public float scaleToRange(float value, float inputRangeMin, float inputRangeMax, float outputRangeMin, float outputRangeMax) {
        return outputRangeMin + (outputRangeMax - outputRangeMin) * ((value - inputRangeMin) / (inputRangeMax - inputRangeMin));
    }
}
