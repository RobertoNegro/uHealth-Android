package com.negroroberto.uhealth.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.WaterCreationActivity;
import com.negroroberto.uhealth.activities.adapters.WaterDrunkAdapter;
import com.negroroberto.uhealth.activities.fragments.abstracts.ModuleFragment;
import com.negroroberto.uhealth.databinding.FragmentWaterBinding;
import com.negroroberto.uhealth.modules.water.WaterModule;
import com.negroroberto.uhealth.modules.water.models.WaterDrunk;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Extras;
import com.negroroberto.uhealth.utils.RequestCodes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WaterFragment extends ModuleFragment {
    private FragmentWaterBinding mBinding;
    private WaterModule mWaterModule;
    private WaterDrunkAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getActivity() == null)
            return null;

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_water, container, false);
        mWaterModule = new WaterModule((UHealthApplication)getActivity().getApplication());

        updateData();

        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.listitem_plus_footer, mBinding.listWaterDrunk, false);
        mBinding.listWaterDrunk.addFooterView(footer, null, true);
        footer.findViewById(R.id.btnFooterPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWaterDrunk();
            }
        });


        return mBinding.getRoot();
    }

    private void updateWaterDrunk(WaterDrunk w) {
        Intent intent = new Intent(getActivity(), WaterCreationActivity.class);
        intent.putExtra(Extras.EXTRA_WATER, (Parcelable)w);
        startActivityForResult(intent, RequestCodes.REQUEST_WATER_UPDATE);
    }

    private void deleteWaterDrunk(WaterDrunk w) {
        mWaterModule.removeWater(new WaterModule.OnVoidResultListener() {
            @Override
            public void onResult() {
                updateData();
            }
        }, w);
    }

    private void createWaterDrunk() {
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(new Date());
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);

        Calendar actCal = Calendar.getInstance();
        actCal.setTime(new Date());
        actCal.set(Calendar.SECOND, 0);
        actCal.set(Calendar.MILLISECOND, 0);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(getTimeStart()));
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.compareTo(todayCal) > 0)
            cal = actCal;

        Intent intent = new Intent(getActivity(), WaterCreationActivity.class);
        intent.putExtra(Extras.EXTRA_CALENDAR, cal);
        startActivityForResult(intent, RequestCodes.REQUEST_WATER_CREATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodes.REQUEST_WATER_CREATION: {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    WaterDrunk water = data.getParcelableExtra(Extras.EXTRA_WATER);
                    if(water != null) {
                        mWaterModule.createWater(new WaterModule.OnLongResultListener() {
                            @Override
                            public void onResult(long result) {
                                updateData();
                            }
                        }, water);
                    }
                }
            } break;
            case RequestCodes.REQUEST_WATER_UPDATE: {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    WaterDrunk water = data.getParcelableExtra(Extras.EXTRA_WATER);
                    if(water != null) {
                        mWaterModule.updateWater(new WaterModule.OnVoidResultListener() {
                            @Override
                            public void onResult() {
                                updateData();
                            }
                        }, water);
                    }
                }
            } break;
        }
    }

    @Override
    public void updateData() {
        if (getActivity() == null || mBinding == null)
            return;

        final double totalWaterDrunk = ((UHealthApplication)getActivity().getApplication()).getGoals().getWaterQuantity();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (totalWaterDrunk <= 0)
                    mBinding.goalContainerQuantity.setVisibility(View.INVISIBLE);
                else
                    mBinding.goalContainerQuantity.setVisibility(View.VISIBLE);
                mBinding.txtTotalWaterDrunk.setText(Common.DoubleStringify(totalWaterDrunk));
                mBinding.circularWaterDrunk.setTotalValue(totalWaterDrunk);
            }
        });

        mWaterModule.getWaterByPeriod(getTimeStart(), getTimeEnd(), new WaterModule.OnWaterDrunkListResultListener() {
            @Override
            public void onResult(final ArrayList<WaterDrunk> result) {
                long sum = 0;
                for (WaterDrunk w : result)
                    sum += w.getQuantity();

                final double actualWaterDrunk = sum / 1000d;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new WaterDrunkAdapter(getActivity(), result);
                        mAdapter.setWaterDrunkEditClickListener(new WaterDrunkAdapter.OnWaterDrunkClickListener() {
                            @Override
                            public void onWaterDrunkClick(WaterDrunk w) {
                                updateWaterDrunk(w);
                            }
                        });
                        mAdapter.setWaterDrunkDeleteClickListener(new WaterDrunkAdapter.OnWaterDrunkClickListener() {
                            @Override
                            public void onWaterDrunkClick(WaterDrunk w) {
                                deleteWaterDrunk(w);
                            }
                        });

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.txtActualWaterDrunk.setText(Common.DoubleStringify(actualWaterDrunk));
                                mBinding.circularWaterDrunk.setActualValue(actualWaterDrunk);
                                mBinding.listWaterDrunk.setAdapter(mAdapter);
                            }
                        });
                    }
                });

            }
        });
    }
}
