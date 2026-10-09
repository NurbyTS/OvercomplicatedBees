
package com.nurby.overcomplicated_bees.library.apiary;

import net.minecraft.network.chat.Component;

import java.util.List;

public record BeeProductionState(WorkingState workingState, List<Component> failureReasons) {
    public enum WorkingState {
        NOT_WORKING, COMFORTABLE, ECSTATIC
    }

    public boolean isWorking() {
        return workingState != WorkingState.NOT_WORKING;
    }

    public boolean isEcstatic() {
        return workingState == WorkingState.ECSTATIC;
    }
}