
package com.nurby.overcomplicated_bees.library.apiary;

import net.minecraft.network.chat.Component;

import java.util.List;

public final class BeeProductionState {
    public enum WorkingState {
        NOT_WORKING,
        COMFORTABLE,
        ECSTATIC
    }

    private final WorkingState workingState;
    private final List<Component> failureReasons;

    public BeeProductionState(
            WorkingState workingState,
            List<? extends Component> failureReasons
    ) {
        this.workingState = workingState;
        this.failureReasons = List.copyOf(failureReasons);
    }

    public WorkingState getWorkingState() {
        return workingState;
    }

    public List<Component> getFailureReasons() {
        return failureReasons;
    }

    public boolean isWorking() {
        return workingState != WorkingState.NOT_WORKING;
    }

    public boolean isComfortable() {
        return workingState == WorkingState.COMFORTABLE;
    }

    public boolean isEcstatic() {
        return workingState == WorkingState.ECSTATIC;
    }
}