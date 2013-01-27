package com.mediaplatform.model;

/**
 * User: timur
 * Date: 1/26/13
 * Time: 2:55 AM
 */
public enum Gender {
    UNDEFINED("Не задан"),MALE("Мужской"),FEMALE("Женский");
    private String label;

    Gender(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
