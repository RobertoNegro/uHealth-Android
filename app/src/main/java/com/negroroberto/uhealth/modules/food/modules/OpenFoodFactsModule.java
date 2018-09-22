package com.negroroberto.uhealth.modules.food.modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.negroroberto.uhealth.modules.food.interfaces.FoodModuleInterface;
import com.negroroberto.uhealth.modules.food.interfaces.FoodResultListener;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.models.openfoodfacts.Nutriments;
import com.negroroberto.uhealth.modules.food.models.openfoodfacts.Product;
import com.negroroberto.uhealth.modules.food.models.openfoodfacts.SearchResponse;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Json;
import com.negroroberto.uhealth.utils.Networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import okhttp3.Response;

import static com.negroroberto.uhealth.utils.Networking.HasEmptyBody;
import static com.negroroberto.uhealth.utils.Networking.IsSuccessful;

public class OpenFoodFactsModule extends FoodModuleInterface {
    public OpenFoodFactsModule(Context context) {
        super(context);
    }

    @Override
    public void getByBarcode(final String barcode, final FoodResultListener listener) {
        final Food f = new Food();

        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
        Debug.Log(this, "Getting from url: " + url);
        Networking.Get(url, new Networking.ResponseListener() {
            @Override
            public void onResponse(Response response) {
                if (IsSuccessful(response) && !HasEmptyBody(response)) {
                    final SearchResponse r = Json.getGson().fromJson(new BufferedReader(new InputStreamReader(response.body().byteStream())), SearchResponse.class);



                    if (r.getCode() == null || !r.getCode().equals(barcode)) {
                        Debug.Err(this, "Invalid barcode returned");
                        listener.onResult(null, null);
                        return;
                    }

                    if (r.getStatus() == null || !r.getStatus().equals(1)) {
                        Debug.Err(this, "Invalid status");
                        listener.onResult(null, null);
                        return;
                    }

                    if (r.getProduct() == null || r.getProduct() == null) {
                        Debug.Err(this, "Missing product data");
                        listener.onResult(null, null);
                        return;
                    }

                    final Product p = r.getProduct();

                    if (p.getNutriments() == null) {
                        Debug.Err(this, "Missing nutriments data");
                        listener.onResult(null, null);
                        return;
                    }

                    final Nutriments n = p.getNutriments();

                    f.setCode(r.getCode());
                    f.setBrand(p.getBrands());
                    f.setName(p.getProductName());
                    f.setServingQuantity(p.getServingQuantity());
                    if(n.getEnergy() != null)
                        f.setEnergy(n.getEnergy() * 0.239006);
                    else
                        f.setEnergy(0d);
                    f.setFat(n.getFat());
                    f.setCarbohydrates(n.getCarbohydrates());
                    f.setFiber(n.getFiber());
                    f.setProteins(n.getProteins());
                    f.setSalt(n.getSalt());
                    f.setSugars(n.getSugars());
                    f.setSodium(n.getSodium());

                    if (p.getImageUrl() != null && p.getImageUrl().trim().length() > 0) {
                        Debug.Log(this, "Getting photo from url: " + p.getImageUrl());
                        Networking.Get(p.getImageUrl(), new Networking.ResponseListener() {
                            @Override
                            public void onResponse(Response response) {
                                if (IsSuccessful(response) && !HasEmptyBody(response)) {
                                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                    listener.onResult(f, bitmap);
                                    return;
                                } else {
                                    Debug.Warn(this, "Error downloading the photo");
                                    listener.onResult(f, null);
                                    return;
                                }
                            }
                        });
                    } else {
                        Debug.Warn(this, "No photo found");
                        listener.onResult(f, null);
                        return;
                    }
                } else {
                    Debug.Err(this, "No response from server");
                    listener.onResult(null, null);
                    return;
                }
            }
        });
    }
}
