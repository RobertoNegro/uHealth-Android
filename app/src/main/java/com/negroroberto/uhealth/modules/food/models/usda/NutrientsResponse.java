package com.negroroberto.uhealth.modules.food.models.usda;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NutrientsResponse {
    @SerializedName("report")
    private Report report;

    public NutrientsResponse() {
    }

    public Report getReport() {
        return report;
    }

    public NutrientsResponse setReport(Report report) {
        this.report = report;
        return this;
    }

    public class Report {
        @SerializedName("food")
        private Food food;

        public Report() {
        }

        public Food getFood() {
            return food;
        }

        public Report setFood(Food food) {
            this.food = food;
            return this;
        }
    }

    public class Food {
        @SerializedName("nutrients")
        private ArrayList<Nutrient> nutrients;

        public Food() {
        }

        public ArrayList<Nutrient> getNutrients() {
            return nutrients;
        }

        public Food setNutrients(ArrayList<Nutrient> nutrients) {
            this.nutrients = nutrients;
            return this;
        }
    }

    public class Nutrient {
        @SerializedName("nutrient_id")
        private String id;

        @SerializedName("value")
        private double value;

        @SerializedName("measures")
        private ArrayList<Measure> measures;

        public Nutrient() {
        }

        public String getId() {
            return id;
        }

        public Nutrient setId(String id) {
            this.id = id;
            return this;
        }

        public double getValue() {
            return value;
        }

        public Nutrient setValue(double value) {
            this.value = value;
            return this;
        }

        public ArrayList<Measure> getMeasures() {
            return measures;
        }

        public Nutrient setMeasures(ArrayList<Measure> measures) {
            this.measures = measures;
            return this;
        }
    }

    public class Measure {
        @SerializedName("eqv")
        private double servingQuantity;

        public Measure() {
        }

        public double getServingQuantity() {
            return servingQuantity;
        }

        public Measure setServingQuantity(double servingQuantity) {
            this.servingQuantity = servingQuantity;
            return this;
        }
    }
}
