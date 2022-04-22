package com.maxjacobi.genealogiesoftware;

import javafx.beans.property.SimpleStringProperty;

public class editPersonEvent {
    private final SimpleStringProperty type;
    private final SimpleStringProperty date;
    private final SimpleStringProperty name;
    private final SimpleStringProperty id;

    public editPersonEvent(String type, String date, String name, String id) {
        this.type = new SimpleStringProperty(type);
        this.date = new SimpleStringProperty(date);
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleStringProperty(id);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }
}
