package com.negroroberto.uhealth.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class Json {
    private static Gson gson = null;

    public static Gson getGson() {
        if (gson != null)
            return gson;

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                    public JsonElement serialize(Double number, Type type, JsonSerializationContext context) {
                        return new JsonPrimitive(Double.valueOf(number));
                    }
                });
        gson = gsonBuilder.create();
        return gson;
    }
}
