package com.negroroberto.uhealth.modules.food.models.openfoodfacts;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("brands")
    private String brands;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("serving_quantity")
    private Double servingQuantity;

    @SerializedName("nutriments")
    private Nutriments nutriments;


    public Product() {
    }

    public String getProductName() {
        return productName;
    }

    public Product setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Product setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Nutriments getNutriments() {
        return nutriments;
    }

    public Product setNutriments(Nutriments nutriments) {
        this.nutriments = nutriments;
        return this;
    }

    public Double getServingQuantity() {
        return servingQuantity;
    }

    public Product setServingQuantity(Double servingQuantity) {
        this.servingQuantity = servingQuantity;
        return this;
    }

    public String getBrands() {
        return brands;
    }

    public Product setBrands(String brands) {
        this.brands = brands;
        return this;
    }

    @Override
    public String toString() {
        return "Product{" +
                "brands='" + brands + '\'' +
                ", productName='" + productName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", servingQuantity=" + servingQuantity +
                ", nutriments=" + (nutriments != null ? nutriments.toString() : "null") +
                '}';
    }
}
