package com.negroroberto.uhealth.modules.food.modules;

import android.content.Context;

import com.negroroberto.uhealth.modules.food.interfaces.FoodModuleInterface;
import com.negroroberto.uhealth.modules.food.interfaces.FoodResultListener;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.models.usda.NutrientsResponse;
import com.negroroberto.uhealth.modules.food.models.usda.SearchResponse;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Json;
import com.negroroberto.uhealth.utils.Networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import okhttp3.Response;

import static com.negroroberto.uhealth.utils.Networking.HasEmptyBody;
import static com.negroroberto.uhealth.utils.Networking.IsSuccessful;

public class USDAModule extends FoodModuleInterface {
    public USDAModule(Context context) {
        super(context);
    }

    @Override
    public void getByBarcode(final String barcode, final FoodResultListener listener) {
        final Food f = new Food();

        String url = "https://api.nal.usda.gov/ndb/search/?max=1&api_key=RDTWPzOQTGrNah7NhOUIXXDyt3AQVQ65zDZmCswx&q=" + barcode;
        Debug.Log(this, "Getting from url: " + url);
        Networking.Get(url, new Networking.ResponseListener() {
            @Override
            public void onResponse(Response response) {
                if (IsSuccessful(response) && !HasEmptyBody(response)) {
                    final SearchResponse r = Json.getGson().fromJson(new BufferedReader(new InputStreamReader(response.body().byteStream())), SearchResponse.class);

                    if (r.getList() == null) {
                        Debug.Err(this, "No list returned");
                        listener.onResult(null, null);
                        return;
                    }

                    if (r.getList().getCode() == null || !r.getList().getCode().equals(barcode)) {
                        Debug.Err(this, "Invalid barcode returned");
                        listener.onResult(null, null);
                        return;
                    }

                    if (r.getList().getItems() == null || r.getList().getItems().isEmpty()) {
                        Debug.Err(this, "No item found");
                        listener.onResult(null, null);
                        return;
                    }

                    if (r.getList().getItems().get(0) == null) {
                        Debug.Err(this, "Missing product data");
                        listener.onResult(null, null);
                        return;
                    }

                    f.setCode(r.getList().getCode());
                    f.setBrand(r.getList().getItems().get(0).getBrand());
                    f.setName(r.getList().getItems().get(0).getName());

                    // 208, 204, 205, 291, 203, salt, 269, 307

                    String url = "https://api.nal.usda.gov/ndb/reports/?api_key=RDTWPzOQTGrNah7NhOUIXXDyt3AQVQ65zDZmCswx&ndbno=" + r.getList().getItems().get(0).getId();
                    Debug.Log(this, "Getting from url: " + url);
                    Networking.Get(url, new Networking.ResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            if (IsSuccessful(response) && !HasEmptyBody(response)) {
                                final NutrientsResponse r = Json.getGson().fromJson(new BufferedReader(new InputStreamReader(response.body().byteStream())), NutrientsResponse.class);

                                if (r.getReport() == null || r.getReport().getFood() == null || r.getReport().getFood().getNutrients() == null) {
                                    Debug.Err(USDAModule.this, "Missing nutrients data");
                                    listener.onResult(null, null);
                                    return;
                                }

                                if (r.getReport().getFood().getNutrients().size() > 0 && r.getReport().getFood().getNutrients().get(0).getMeasures().size() > 0)
                                    f.setServingQuantity(r.getReport().getFood().getNutrients().get(0).getMeasures().get(0).getServingQuantity());


                                for (NutrientsResponse.Nutrient n : r.getReport().getFood().getNutrients()) {
                                    switch (n.getId().trim().toLowerCase()) {
                                        case "208":
                                            f.setEnergy(n.getValue());
                                            break;
                                        case "204":
                                            f.setFat(n.getValue());
                                            break;
                                        case "205":
                                            f.setCarbohydrates(n.getValue());
                                            break;
                                        case "291":
                                            f.setFiber(n.getValue());
                                            break;
                                        case "203":
                                            f.setProteins(n.getValue());
                                            break;
                                        case "269":
                                            f.setSugars(n.getValue());
                                            break;
                                        case "307":
                                            f.setSodium(n.getValue());
                                            break;
                                    }
                                }

                                listener.onResult(f, null);
                                return;
                            }
                        }
                    });
                } else {
                    Debug.Err(this, "No response from server");
                    listener.onResult(null, null);
                    return;
                }
            }
        });
    }
}
