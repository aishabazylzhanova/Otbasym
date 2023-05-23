package com.abazy.otbasym.Constants;


public class Kinship {

    private String name;
    private final String definition;

    public Kinship(String name, String definition){

        this.name=name;
        this.definition=definition;

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDefinition() {
        return this.definition;
    }


}