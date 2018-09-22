package com.negroroberto.uhealth.modules.food.models.openfoodfacts;

import com.google.gson.annotations.SerializedName;

public class SearchResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("status")
    private Integer status; // 1 found, 0 not found

    @SerializedName("product")
    private Product product;

    public SearchResponse() {
    }

    public String getCode() {
        return code;
    }

    public SearchResponse setCode(String code) {
        this.code = code;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public SearchResponse setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public SearchResponse setProduct(Product product) {
        this.product = product;
        return this;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "code='" + code + '\'' +
                ", status=" + status +
                ", product=" + (product != null ? product.toString() : "null") +
                '}';
    }
}
