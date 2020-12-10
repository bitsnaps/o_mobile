package com.odoo.odoorx.core.data.util;

public class SaveState {

    private Status status;
    private String message;

    public enum Status {
        SAVED, ERROR, NO_DATA,
    }

    public static SaveState getSavedState() {
        SaveState saveState = new SaveState();
        saveState.status = Status.SAVED;
        return saveState;
    }

    public static SaveState getErrorState() {
        SaveState saveState = new SaveState();
        saveState.status = Status.ERROR;
        return saveState;
    }

    public static SaveState getErrorState(String message) {
        SaveState saveState = new SaveState();
        saveState.status = Status.ERROR;
        saveState.message = message;
        return saveState;
    }

    public static SaveState getEmptyDataState() {
        SaveState saveState = new SaveState();
        saveState.status = Status.NO_DATA;
        saveState.message = "No Data Changed yet to save";
        return saveState;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
