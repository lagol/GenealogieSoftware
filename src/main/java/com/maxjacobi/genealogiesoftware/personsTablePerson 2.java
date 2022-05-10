package com.maxjacobi.genealogiesoftware;

import javafx.beans.property.SimpleStringProperty;


public class personsTablePerson {
    private final SimpleStringProperty name;
    private final SimpleStringProperty sex;
    private final SimpleStringProperty id;
    private final SimpleStringProperty birthDate;
    private final SimpleStringProperty deathDate;

    public personsTablePerson(String name, String sex, String id, String birthDate, String deathDate) {
        this.name = new SimpleStringProperty(name);
        this.sex = new SimpleStringProperty(sex);
        this.id = new SimpleStringProperty(id);
        this.birthDate = new SimpleStringProperty(birthDate);
        this.deathDate = new SimpleStringProperty(deathDate);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSex() {
        return sex.get();
    }

    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getBirthDate() {
        return birthDate.get();
    }

    public void setBirthDate(String birthDate) {
        this.birthDate.set(birthDate);
    }

    public String getDeathDate() {
        return deathDate.get();
    }

    public void setDeathDate(String deathDate) {
        this.deathDate.set(deathDate);
    }


}
