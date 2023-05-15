package com.abazy.otbasym;


import java.util.ArrayList;

public class Kinship {

    private String name; // название
    private String definition;

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

    public void setDefinition(String definition) {
        this.definition = definition;
    }


}