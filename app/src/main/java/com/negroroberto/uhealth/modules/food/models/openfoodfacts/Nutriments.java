package com.negroroberto.uhealth.modules.food.models.openfoodfacts;

import com.google.gson.annotations.SerializedName;

public class Nutriments {
    @SerializedName("energy_100g")
    private Double energy;
    @SerializedName("fat_100g")
    private Double fat;
    @SerializedName("carbohydrates_100g")
    private Double carbohydrates;
    @SerializedName("fiber_100g")
    private Double fiber;
    @SerializedName("proteins_100g")
    private Double proteins;
    @SerializedName("salt_100g")
    private Double salt;
    @SerializedName("sugars_100g")
    private Double sugars;
    @SerializedName("sodium_100g")
    private Double sodium;

    public Nutriments() {
    }

    public Double getEnergy() {
        return energy;
    }

    public Nutriments setEnergy(Double energy) {
        this.energy = energy;
        return this;
    }

    public Double getFat() {
        return fat;
    }

    public Nutriments setFat(Double fat) {
        this.fat = fat;
        return this;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public Nutriments setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
        return this;
    }

    public Double getFiber() {
        return fiber;
    }

    public Nutriments setFiber(Double fiber) {
        this.fiber = fiber;
        return this;
    }

    public Double getProteins() {
        return proteins;
    }

    public Nutriments setProteins(Double proteins) {
        this.proteins = proteins;
        return this;
    }

    public Double getSalt() {
        return salt;
    }

    public Nutriments setSalt(Double salt) {
        this.salt = salt;
        return this;
    }

    public Double getSugars() {
        return sugars;
    }

    public Nutriments setSugars(Double sugars) {
        this.sugars = sugars;
        return this;
    }

    public Double getSodium() {
        return sodium;
    }

    public Nutriments setSodium(Double sodium) {
        this.sodium = sodium;
        return this;
    }

    @Override
    public String toString() {
        return "Nutriments{" +
                "fat='" + fat + '\'' +
                ", carbohydrates='" + carbohydrates + '\'' +
                ", fiber='" + fiber + '\'' +
                ", proteins='" + proteins + '\'' +
                ", salt='" + salt + '\'' +
                ", sugars='" + sugars + '\'' +
                ", energy='" + energy + '\'' +
                ", sodium='" + sodium + '\'' +
                '}';
    }
}
