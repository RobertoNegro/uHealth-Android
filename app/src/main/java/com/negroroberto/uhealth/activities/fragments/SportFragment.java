package com.negroroberto.uhealth.activities.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.GoogleSignInListener;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.fragments.abstracts.ModuleFragment;
import com.negroroberto.uhealth.databinding.FragmentSportBinding;
import com.negroroberto.uhealth.modules.sport.GoogleFitModule;
import com.negroroberto.uhealth.utils.Common;

public class SportFragment extends ModuleFragment {
    private FragmentSportBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sport, container, false);
        updateData();
        return mBinding.getRoot();
    }

    @Override
    public void updateData() {
        if(mBinding == null || getActivity() == null)
            return;

        float totalDuration =  ((UHealthApplication)getActivity().getApplication()).getGoals().getSportDuration();
        float totalDistance =  ((UHealthApplication)getActivity().getApplication()).getGoals().getSportDistance();
        float totalCalories =  ((UHealthApplication)getActivity().getApplication()).getGoals().getSportCalories();
        float totalSteps =  ((UHealthApplication)getActivity().getApplication()).getGoals().getSportSteps();

        if (totalDuration <= 0)
            mBinding.goalContainerDuration.setVisibility(View.INVISIBLE);
        else
            mBinding.goalContainerDuration.setVisibility(View.VISIBLE);
        mBinding.txtTotalDuration.setText(Common.DoubleStringify(totalDuration));
        mBinding.circularDuration.setTotalValue(totalDuration);

        if (totalDistance <= 0)
            mBinding.goalContainerDistance.setVisibility(View.INVISIBLE);
        else
            mBinding.goalContainerDistance.setVisibility(View.VISIBLE);
        mBinding.txtTotalDistance.setText(Common.DoubleStringify(totalDistance));
        mBinding.circularDistance.setTotalValue(totalDistance);

        if (totalCalories <= 0)
            mBinding.goalContainerCalories.setVisibility(View.INVISIBLE);
        else
            mBinding.goalContainerCalories.setVisibility(View.VISIBLE);
        mBinding.txtTotalCalories.setText(Common.DoubleStringify(totalCalories));
        mBinding.circularCalories.setTotalValue(totalCalories);

        if (totalSteps <= 0)
            mBinding.goalContainerSteps.setVisibility(View.INVISIBLE);
        else
            mBinding.goalContainerSteps.setVisibility(View.VISIBLE);
        mBinding.txtTotalSteps.setText(Common.DoubleStringify(totalSteps));
        mBinding.circularSteps.setTotalValue(totalSteps);

        UHealthActivity activity = null;
        if(getActivity() instanceof UHealthActivity)
            activity = (UHealthActivity)getActivity();
        if(activity != null) {
            activity.signInWithGoogle(new GoogleSignInListener() {
                @Override
                public void onSignIn(GoogleSignInAccount account) {
                    GoogleFitModule module = new GoogleFitModule((UHealthApplication)getActivity().getApplication(), account);

                    module.getDuration(getTimeStart(), getTimeEnd(), new GoogleFitModule.OnLongResultListener() {
                        @Override
                        public void onResult(long result) {
                            mBinding.circularDuration.setActualValue(result);
                            mBinding.txtActualDuration.setText(Common.DoubleStringify(result));
                        }
                    });
                    module.getDistance(getTimeStart(), getTimeEnd(), new GoogleFitModule.OnFloatResultListener() {
                        @Override
                        public void onResult(float result) {
                            result /= 1000f;
                            mBinding.circularDistance.setActualValue(result);
                            mBinding.txtActualDistance.setText(Common.DoubleStringify(result));
                        }
                    });
                    module.getCalories(getTimeStart(), getTimeEnd(), new GoogleFitModule.OnFloatResultListener() {
                        @Override
                        public void onResult(float result) {
                            mBinding.circularCalories.setActualValue(result);
                            mBinding.txtActualCalories.setText(Common.DoubleStringify(result));
                        }
                    });
                    module.getSteps(getTimeStart(), getTimeEnd(), new GoogleFitModule.OnLongResultListener() {
                        @Override
                        public void onResult(long result) {
                            mBinding.circularSteps.setActualValue(result);
                            mBinding.txtActualSteps.setText(Common.DoubleStringify(result));
                        }
                    });


                }
            });
        }

        mBinding.txtGfitWarn.setText(Html.fromHtml(getString(R.string.stats_sport_gfit_warn)));
    }

}
