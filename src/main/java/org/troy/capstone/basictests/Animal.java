package org.troy.capstone.basictests;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Animal {
    private StringProperty name;
    public StringProperty nameProperty() {
        if( name == null )
            name = new SimpleStringProperty(this, "name");
        return name;
    }
    public String getName() {
        return nameProperty().get();
    }
    public void setName(String name) {
        nameProperty().set(name);
    }

    private DoubleProperty weight;
    public DoubleProperty weightProperty() {
        if( weight == null )
            weight = new SimpleDoubleProperty(this, "weight");
        return weight;
    }
    public double getWeight() {
        return weightProperty().get();
    }
    public void setWeight(double weight) {
        weightProperty().set(weight);
    }

    public Animal(String name, double weight) {
        setName(name);
        setWeight(weight);
    }
}