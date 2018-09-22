package com.negroroberto.uhealth.activities.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.modules.water.models.WaterDrunk;
import com.negroroberto.uhealth.utils.Common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class WaterDrunkAdapter extends ArrayAdapter<WaterDrunk> {
    private ArrayList<WaterDrunk> mWaterDrunk;
    private int mResource;
    private LayoutInflater mInflater;

    public WaterDrunkAdapter(Activity activity, ArrayList<WaterDrunk> waterDrunk) {
        this(activity, waterDrunk, R.layout.listitem_waterdrunk);
    }

    private WaterDrunkAdapter(Activity activity, ArrayList<WaterDrunk> waterDrunk, int resource) {
        super(activity, resource, waterDrunk);
        this.mWaterDrunk = waterDrunk;
        this.mResource = resource;
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private interface OnUpdateCalsFinish {
        void onFinish(double value);
    }

    private OnWaterDrunkClickListener mWaterDrunkEditClickListener;
    private OnWaterDrunkClickListener mWaterDrunkDeleteClickListener;
    public interface OnWaterDrunkClickListener {
        void onWaterDrunkClick(WaterDrunk w);
    }

    public void setWaterDrunkEditClickListener(OnWaterDrunkClickListener waterDrunkEditClickListener) {
        mWaterDrunkEditClickListener = waterDrunkEditClickListener;
    }
    public void setWaterDrunkDeleteClickListener(OnWaterDrunkClickListener waterDrunkDeleteClickListener) {
        mWaterDrunkDeleteClickListener = waterDrunkDeleteClickListener;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.btnSettings = convertView.findViewById(R.id.btnSettings);
            holder.txtTime = convertView.findViewById(R.id.txtTime);
            holder.txtLiters = convertView.findViewById(R.id.txtLiters);
            holder.verticalBarTop = convertView.findViewById(R.id.verticalBarTop);
            holder.verticalBarBottom = convertView.findViewById(R.id.verticalBarBottom);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final WaterDrunk waterDrunk = mWaterDrunk.get(position);

        holder.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                if (mWaterDrunkEditClickListener != null)
                                    mWaterDrunkEditClickListener.onWaterDrunkClick(waterDrunk);
                                break;
                            case R.id.menu_delete:
                                if(mWaterDrunkDeleteClickListener != null)
                                    mWaterDrunkDeleteClickListener.onWaterDrunkClick(waterDrunk);

                                break;

                        }
                        return false;
                    }
                });

                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_water_list_settings, popup.getMenu());
                popup.show();
            }
        });


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.txtTime.setText(sdf.format(waterDrunk.getTime().getTime()));

        holder.txtLiters.setText(Common.DoubleStringify(waterDrunk.getQuantity() / 1000d));

        if (position == 0)
            holder.verticalBarTop.setVisibility(View.INVISIBLE);
        else
            holder.verticalBarTop.setVisibility(View.VISIBLE);

        if (position == mWaterDrunk.size() - 1)
            holder.verticalBarBottom.setVisibility(View.INVISIBLE);
        else
            holder.verticalBarBottom.setVisibility(View.VISIBLE);

        return convertView;
    }

    private static class ViewHolder {
        ImageButton btnSettings;
        TextView txtTime;
        TextView txtLiters;
        View verticalBarTop;
        View verticalBarBottom;
    }
}