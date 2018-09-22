package com.negroroberto.uhealth.modules.food.models.usda;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchResponse {
    @SerializedName("list")
    private List list;

    public SearchResponse() {
    }

    public List getList() {
        return list;
    }

    public SearchResponse setList(List list) {
        this.list = list;
        return this;
    }

    public class List {
        @SerializedName("item")
        private ArrayList<Item> items;

        @SerializedName("q")
        private String code;

        public List() {
        }

        public ArrayList<Item> getItems() {
            return items;
        }

        public List setItems(ArrayList<Item> items) {
            this.items = items;
            return this;
        }

        public String getCode() {
            return code;
        }

        public List setCode(String code) {
            this.code = code;
            return this;
        }
    }

    public class Item {
        @SerializedName("ndbno")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("manu")
        private String brand;

        public Item() {
        }

        public String getId() {
            return id;
        }

        public Item setId(String id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public Item setName(String name) {
            this.name = name;
            return this;
        }

        public String getBrand() {
            return brand;
        }

        public Item setBrand(String brand) {
            this.brand = brand;
            return this;
        }
    }
}
