package com.example.greengiant.canopy2;

/**
 * Created by Justin on 2/22/2016.
 */
public class ThermostatSpinnerObject {
    String name;
    String id;

    public ThermostatSpinnerObject( String name, String id ) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return name;
    }
}
