
package com.nurby.overcomplicated_bees.library.apiary;

public interface IBeeModifier {
    default float modifyTemperature(float temperature) {
        return temperature;
    }

    default float modifyHumidity(float humidity) {
        return humidity;
    }

    default float modifyProductivity(float productivity) {
        return productivity;
    }

    default float modifyMutationRate(float mutationRate) {
        return mutationRate;
    }

    default float modifyLifespan(float lifespan) {
        return lifespan;
    }

    default int modifyTerritoryHorizontal(int radius) {
        return radius;
    }

    default int modifyTerritoryVertical(int radius) {
        return radius;
    }

    default Boolean modifySkyAccess(Boolean hasSkyAccess) {
        return null;
    }

    default Boolean modifyRainExposure(Boolean exposedToRain) {
        return null;
    }

    default Boolean modifyActiveTime(Boolean active) {
        return null;
    }
}