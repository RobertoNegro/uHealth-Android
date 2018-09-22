package com.negroroberto.uhealth.activities.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.modules.food.FoodModule;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.models.FoodEaten;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.FilePaths;

import java.util.ArrayList;

import static com.negroroberto.uhealth.utils.Common.DoubleStringify;

public class FoodsOnMealAdapter extends ArrayAdapter<FoodEaten> {
    private ArrayList<FoodEaten> mFoods;
    private int mResource;
    private LayoutInflater mInflater;
    private OnFoodSettingsClick mOnFoodSettingsClick;
    private OnFoodTrashClick mOnFoodTrashClick;
    private FoodModule mFoodModule;
    private UHealthActivity mActivity;
    private UHealthApplication mApplication;

    public FoodsOnMealAdapter(UHealthApplication application, UHealthActivity activity, ArrayList<FoodEaten> foods) {
        this(application, activity, foods, R.layout.listitem_foods_on_meal);
    }

    private FoodsOnMealAdapter(UHealthApplication application, UHealthActivity activity, ArrayList<FoodEaten> foods, int resource) {
        super(activity, resource, foods);
        this.mFoods = foods;
        this.mResource = resource;
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mFoodModule = new FoodModule(application);

        this.mApplication = application;
        this.mActivity = activity;
    }

    public OnFoodSettingsClick getOnFoodSettingsClick() {
        return mOnFoodSettingsClick;
    }

    public void setOnFoodSettingsClick(OnFoodSettingsClick onFoodSettingsClick) {
        mOnFoodSettingsClick = onFoodSettingsClick;
    }

    public OnFoodTrashClick getOnFoodTrashClick() {
        return mOnFoodTrashClick;
    }

    public void setOnFoodTrashClick(OnFoodTrashClick onFoodTrashClick) {
        mOnFoodTrashClick = onFoodTrashClick;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.btnSettings = convertView.findViewById(R.id.btnSettings);
            holder.btnTrash = convertView.findViewById(R.id.btnTrash);
            holder.imgBackgroundPhoto = convertView.findViewById(R.id.imgBackgroundPhoto);
            holder.txtNameAndBrand = convertView.findViewById(R.id.txtNameAndBrand);
            holder.txtGrams = convertView.findViewById(R.id.txtGrams);
            holder.txtPortions = convertView.findViewById(R.id.txtPortions);
            holder.txtEnergy = convertView.findViewById(R.id.txtEnergy);
            holder.txtFat = convertView.findViewById(R.id.txtFat);
            holder.txtCarbs = convertView.findViewById(R.id.txtCarbs);
            holder.txtFibers = convertView.findViewById(R.id.txtFibers);
            holder.txtProteins = convertView.findViewById(R.id.txtProteins);
            holder.txtSalt = convertView.findViewById(R.id.txtSalt);
            holder.txtSugars = convertView.findViewById(R.id.txtSugars);
            holder.txtSodium = convertView.findViewById(R.id.txtSodium);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final FoodEaten fe = mFoods.get(position);
        holder.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnFoodSettingsClick != null)
                    mOnFoodSettingsClick.onFoodSettingsClick(fe);
            }
        });

        holder.btnTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnFoodTrashClick != null)
                    mOnFoodTrashClick.onFoodTrashClick(fe);
            }
        });


        mFoodModule.getFoodsById(new FoodModule.OnFoodsListResultListener() {
            @Override
            public void onResult(ArrayList<Food> result) {
                if(result.size() > 0) {
                    final Food f = result.get(0);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.imgBackgroundPhoto.setImageBitmap(Common.GetBitmapFromFile(getContext(),FilePaths.GetFoodImagePath(getContext()) + f.getPhoto()));
                            holder.txtNameAndBrand.setText(Html.fromHtml("<font color='#252525'>" + f.getName() + "</font>" + " <font color='#5a5a5a'>(" + f.getBrand() + ")</font>"));
                            holder.txtGrams.setText(DoubleStringify(fe.getQuantity()));
                            holder.txtPortions.setText(DoubleStringify(fe.getQuantity() / f.getServingQuantity()));
                            holder.txtEnergy.setText(DoubleStringify(f.getEnergy() / 100d * fe.getQuantity()));
                            holder.txtFat.setText(DoubleStringify(f.getFat() / 100d * fe.getQuantity()));
                            holder.txtCarbs.setText(DoubleStringify(f.getCarbohydrates() / 100d * fe.getQuantity()));
                            holder.txtFibers.setText(DoubleStringify(f.getFiber() / 100d * fe.getQuantity()));
                            holder.txtProteins.setText(DoubleStringify(f.getProteins() / 100d * fe.getQuantity()));
                            holder.txtSalt.setText(DoubleStringify(f.getSalt() / 100d * fe.getQuantity()));
                            holder.txtSugars.setText(DoubleStringify(f.getSugars() / 100d * fe.getQuantity()));
                            holder.txtSodium.setText(DoubleStringify(f.getSodium() / 100d * fe.getQuantity()));
                        }
                    });
                }
            }
        }, fe.getFoodId());

        return convertView;
    }

    private static class ViewHolder {
        ImageButton btnSettings;
        ImageButton btnTrash;

        ImageView imgBackgroundPhoto;

        TextView txtNameAndBrand;
        TextView txtGrams;
        TextView txtPortions;
        TextView txtEnergy;
        TextView txtFat;
        TextView txtCarbs;
        TextView txtFibers;
        TextView txtProteins;
        TextView txtSalt;
        TextView txtSugars;
        TextView txtSodium;
    }

    public interface OnFoodSettingsClick {
        void onFoodSettingsClick(FoodEaten f);
    }
    public interface OnFoodTrashClick {
        void onFoodTrashClick(FoodEaten f);
    }
}