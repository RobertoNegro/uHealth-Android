package com.negroroberto.uhealth.activities.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.FilePaths;

import java.util.ArrayList;
import java.util.List;

import static com.negroroberto.uhealth.utils.Common.DoubleStringify;

public class FoodsListAdapter extends ArrayAdapter<Food> implements Filterable {
    private ArrayList<Food> mFoods;
    private ArrayList<Food> mAllFoods;
    private int mResource;
    private LayoutInflater mInflater;
    private OnFoodSelectedClick mOnFoodSelectedClick;
    private OnFoodSettingsClick mOnFoodSettingsClick;
    private OnFoodTrashClick mOnFoodTrashClick;

    public FoodsListAdapter(Context context, ArrayList<Food> foods) {
        this(context, foods, R.layout.listitem_foods_list);
    }

    private FoodsListAdapter(Context context, ArrayList<Food> foods, int resource) {
        super(context, resource, foods);
        this.mFoods = new ArrayList<>(foods);
        this.mAllFoods = new ArrayList<>(foods);
        this.mResource = resource;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mFoods.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.btnSelect = convertView.findViewById(R.id.btnSelect);
            holder.btnSettings = convertView.findViewById(R.id.btnSettings);
            holder.btnTrash = convertView.findViewById(R.id.btnTrash);
            holder.imgBackgroundPhoto = convertView.findViewById(R.id.imgBackgroundPhoto);
            holder.txtNameAndBrand = convertView.findViewById(R.id.txtNameAndBrand);
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

        final Food f = mFoods.get(position);
        holder.imgBackgroundPhoto.setImageBitmap(Common.GetBitmapFromFile(getContext(), FilePaths.GetFoodImagePath(getContext()) + f.getPhoto()));

        holder.txtNameAndBrand.setText(Html.fromHtml("<font color='#252525'>" + f.getName() + "</font>" + " <font color='#5a5a5a'>(" + f.getBrand() + ")</font>"));
        holder.txtEnergy.setText(DoubleStringify(f.getEnergy()));
        holder.txtFat.setText(DoubleStringify(f.getFat()));
        holder.txtCarbs.setText(DoubleStringify(f.getCarbohydrates()));
        holder.txtFibers.setText(DoubleStringify(f.getFiber()));
        holder.txtProteins.setText(DoubleStringify(f.getProteins()));
        holder.txtSalt.setText(DoubleStringify(f.getSalt()));
        holder.txtSugars.setText(DoubleStringify(f.getSugars()));
        holder.txtSodium.setText(DoubleStringify(f.getSodium()));

        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFoodSelectedClick != null)
                    mOnFoodSelectedClick.onFoodSelectedClick(f);
            }
        });
        holder.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFoodSettingsClick != null)
                    mOnFoodSettingsClick.onFoodSettingsClick(f);
            }
        });
        holder.btnTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFoodTrashClick != null)
                    mOnFoodTrashClick.onFoodTrashClick(f);
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFoods = (ArrayList<Food>) results.values;
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();

                List<Food> allFoods = new ArrayList<>(mAllFoods);
                String constraintString = constraint != null ? constraint.toString().toLowerCase().trim() : "";
                if (constraintString.length() == 0) {
                    result.values = allFoods;
                    result.count = allFoods.size();
                } else {
                    ArrayList<Food> filteredList = new ArrayList<>();
                    String[] splitted = constraintString.split("\\s+");
                    for (Food f : allFoods) {
                        boolean contained = true;

                        for (String s : splitted)
                            if (!f.getName().toLowerCase().contains(s.trim()) && !f.getBrand().toLowerCase().contains(s.trim()))
                                contained = false;

                        if(contained)
                            filteredList.add(f);
                    }
                    result.values = filteredList;
                    result.count = filteredList.size();
                }
                return result;
            }
        };

        return filter;
    }

    private static class ViewHolder {
        ImageButton btnSelect;
        ImageButton btnSettings;
        ImageButton btnTrash;

        ImageView imgBackgroundPhoto;

        TextView txtNameAndBrand;
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
        void onFoodSettingsClick(Food f);
    }

    public void setOnFoodSettingsClick(OnFoodSettingsClick onFoodSettingsClick) {
        mOnFoodSettingsClick = onFoodSettingsClick;
    }

    public interface OnFoodTrashClick {
        void onFoodTrashClick(Food f);
    }

    public void setOnFoodTrashClick(OnFoodTrashClick onFoodTrashClick) {
        mOnFoodTrashClick = onFoodTrashClick;
    }

    public interface OnFoodSelectedClick {
        void onFoodSelectedClick(Food f);
    }

    public void setOnFoodSelectedClick(OnFoodSelectedClick onFoodSelectedClick) {
        mOnFoodSelectedClick = onFoodSelectedClick;
    }

}