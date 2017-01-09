package com.example.android.pets.models;

/**
 * Created by Jan on 06.01.2017.
 */

public class Pet {

    private int id;
    private String name;
    private String breed;
    private int gender;
    private int weight;

    public Pet(int id, String name, String breed, int gender, int weight) {
        this.id = id;
        this.breed = breed;
        this.gender = gender;
        this.name = name;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "breed='" + breed + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", weight=" + weight +
                '}';
    }
}
