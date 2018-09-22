package com.negroroberto.uhealth.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.activities.fragments.FoodFragment;
import com.negroroberto.uhealth.activities.fragments.SportFragment;
import com.negroroberto.uhealth.activities.fragments.WaterFragment;
import com.negroroberto.uhealth.activities.fragments.abstracts.ModuleFragment;
import com.negroroberto.uhealth.databinding.ActivityStatsBinding;
import com.negroroberto.uhealth.unity.AppSocket;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Extras;
import com.negroroberto.uhealth.utils.RequestCodes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatsActivity extends UnityActivity implements UnityController.EventListener {
    public enum Module {
        NONE, FOOD, WATER, SPORT
    }

    private ActivityStatsBinding mBinding;
    private Calendar mSelectedDate;
    private CategoriesPagerAdapter mAdapter;
    private Module mForceShowModule;
    private ArrayList<Module> mModules;
    private ArrayList<ModuleFragment> mFragments;
    private boolean mHideDateArrows;

    private void analyzeIntent() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            long extraPeriod = args.getLong(Extras.EXTRA_STATS_PERIOD, -1);

            mSelectedDate = Calendar.getInstance();
            if (extraPeriod >= 0)
                mSelectedDate.setTime(new Date(extraPeriod));
            else
                mSelectedDate.setTime(new Date());

            mSelectedDate.set(Calendar.HOUR_OF_DAY, 0);
            mSelectedDate.set(Calendar.MINUTE, 0);
            mSelectedDate.set(Calendar.SECOND, 0);
            mSelectedDate.set(Calendar.MILLISECOND, 0);

            if (args.containsKey(Extras.EXTRA_STATS_FORCE_SHOW_MODULE))
                mForceShowModule = (Module) args.getSerializable(Extras.EXTRA_STATS_FORCE_SHOW_MODULE);
            else
                mForceShowModule = Module.NONE;

            if (args.containsKey(Extras.EXTRA_STATS_MODULES))
                mModules = (ArrayList<Module>) args.getSerializable(Extras.EXTRA_STATS_MODULES);

            if (mModules == null || mModules.size() == 0) {
                mModules = new ArrayList<>();
                mModules.add(Module.FOOD);
                mModules.add(Module.WATER);
                mModules.add(Module.SPORT);
            }

            mHideDateArrows = args.getBoolean(Extras.EXTRA_STATS_HIDE_DATE_ARROWS, false);
        } else {
            mSelectedDate = Calendar.getInstance();
            mSelectedDate.setTime(new Date());
            mSelectedDate.set(Calendar.HOUR_OF_DAY, 0);
            mSelectedDate.set(Calendar.MINUTE, 0);
            mSelectedDate.set(Calendar.SECOND, 0);
            mSelectedDate.set(Calendar.MILLISECOND, 0);

            mForceShowModule = Module.NONE;

            mModules = new ArrayList<>();
            mModules.add(Module.FOOD);
            mModules.add(Module.WATER);
            mModules.add(Module.SPORT);

            mHideDateArrows = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_stats);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFragments = new ArrayList<>();

        analyzeIntent();
        updateUi();

        int forceShowModuleIndex = 0;
        final ArrayList<TextView> txts = new ArrayList<>();

        for (int i = 0; i < mModules.size(); i++) {
            final Module m = mModules.get(i);

            if (m.equals(mForceShowModule))
                forceShowModuleIndex = i;

            switch (m) {
                case FOOD: {
                    TextView txtFood = new TextView(this);
                    txtFood.setFocusable(true);
                    txtFood.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    txtFood.setGravity(Gravity.CENTER);
                    txtFood.setText("Food");
                    txtFood.setTextColor(ResourcesCompat.getColor(getResources(), R.color.darkText, null));
                    final int index = mFragments.size();
                    txtFood.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBinding.viewPager.setCurrentItem(index);
                        }
                    });
                    mBinding.categoryBar.addView(txtFood);
                    txts.add(txtFood);

                    mFragments.add(ModuleFragment.newInstance(FoodFragment.class, getStartPeriod(), getEndPeriod()));
                }
                break;

                case WATER: {
                    TextView txtWater = new TextView(this);
                    txtWater.setFocusable(true);
                    txtWater.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    txtWater.setGravity(Gravity.CENTER);
                    txtWater.setText("Water");
                    txtWater.setTextColor(ResourcesCompat.getColor(getResources(), R.color.darkText, null));
                    final int index = mFragments.size();
                    txtWater.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBinding.viewPager.setCurrentItem(index);
                        }
                    });
                    mBinding.categoryBar.addView(txtWater);
                    txts.add(txtWater);

                    mFragments.add(ModuleFragment.newInstance(WaterFragment.class, getStartPeriod(), getEndPeriod()));
                }
                break;

                case SPORT: {
                    TextView txtSport = new TextView(this);
                    txtSport.setFocusable(true);
                    txtSport.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    txtSport.setGravity(Gravity.CENTER);
                    txtSport.setTextColor(ResourcesCompat.getColor(getResources(), R.color.darkText, null));
                    txtSport.setText("Sport");
                    final int index = mFragments.size();
                    txtSport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBinding.viewPager.setCurrentItem(index);
                        }
                    });
                    mBinding.categoryBar.addView(txtSport);
                    txts.add(txtSport);

                    mFragments.add(ModuleFragment.newInstance(SportFragment.class, getStartPeriod(), getEndPeriod()));
                }
                break;
            }
        }

        mAdapter = new CategoriesPagerAdapter(getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
        mBinding.viewPager.setOffscreenPageLimit(3);
        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.viewPager.setCurrentItem(forceShowModuleIndex);

        txts.get(mBinding.viewPager.getCurrentItem()).setTypeface(null, Typeface.BOLD);
        txts.get(mBinding.viewPager.getCurrentItem()).setBackground(getDrawable(R.drawable.background_border_bottom));

        txts.get(mBinding.viewPager.getCurrentItem()).requestFocus();
        txts.get(mBinding.viewPager.getCurrentItem()).requestFocusFromTouch();

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < txts.size(); i++) {
                    TextView t = txts.get(i);
                    if (i == position) {
                        t.setTypeface(null, Typeface.BOLD);
                        t.setBackground(getDrawable(R.drawable.background_border_bottom));
                    } else {
                        t.setTypeface(null, Typeface.NORMAL);
                        t.setBackground(null);
                    }
                }
            }
        });

        mBinding.btnPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedDate(-1);
            }
        });
        mBinding.btnPrevDay.setVisibility(mHideDateArrows ? View.GONE : View.VISIBLE);
        mBinding.btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedDate(1);
            }
        });
        mBinding.btnNextDay.setVisibility(mHideDateArrows ? View.GONE : View.VISIBLE);

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatsActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setUnitySettings() {
        super.setUnitySettings();

        ((UHealthApplication)getApplication()).getUnityController().setCameraTarget(UnityController.CameraTarget.HEAD);
        ((UHealthApplication)getApplication()).getUnityController().setCamera(180, 0.4f, 0.9f);
    }

    private boolean mFirstTime = true;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && mFirstTime) {
         mFirstTime = false;
            View parent = (View) mBinding.idUnity.getParent();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.idUnity.getLayoutParams();
            params.leftMargin = (int) parent.getWidth() - mBinding.idUnity.getWidth() - 30;
            params.topMargin = (int) parent.getHeight() - mBinding.idUnity.getHeight() - 30;
            mBinding.idUnity.setLayoutParams(params);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        closeActivity();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_goals: {
                Intent intent = new Intent(StatsActivity.this, GoalSettingsActivity.class);
                startActivityForResult(intent, RequestCodes.REQUEST_GOAL_SETTINGS);
            }
            break;
            case R.id.menu_summary: {
                Intent intent = new Intent(StatsActivity.this, SummaryActivity.class);
                startActivity(intent);
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.REQUEST_GOAL_SETTINGS: {
                if (resultCode == Activity.RESULT_OK) {
                    updatePeriod();
                }
            }

            break;
        }
    }


    private class CategoriesPagerAdapter extends FragmentPagerAdapter {
        CategoriesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position >= 0 && position < mFragments.size())
                return mFragments.get(position);
            else
                return null;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    private void updateUi() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        mBinding.txtDay.setText(sdf.format(mSelectedDate.getTime()));
        mBinding.txtDayDesc.setText(getDayDescriptionFromCalendar(mSelectedDate));
    }

    public void updatePeriod() {
        for (ModuleFragment fragment : mFragments) {
            fragment.setPeriod(getStartPeriod().getTimeInMillis(), getEndPeriod().getTimeInMillis());

        }
    }

    private String getDayDescriptionFromCalendar(Calendar cal) {
        Calendar actual = Calendar.getInstance();
        actual.setTime(new Date());

        long millisDiff = actual.getTimeInMillis() - cal.getTimeInMillis();
        long dayDiff = millisDiff / (24 * 60 * 60 * 1000);
        int weekDiff = actual.get(Calendar.WEEK_OF_YEAR) - cal.get(Calendar.WEEK_OF_YEAR);
        int monthDiff;
        if (actual.get(Calendar.MONTH) >= cal.get(Calendar.MONTH))
            monthDiff = actual.get(Calendar.MONTH) - cal.get(Calendar.MONTH);
        else
            monthDiff = (12 - actual.get(Calendar.MONTH)) + cal.get(Calendar.MONTH);

        if (dayDiff == 0)
            return "TODAY";
        if (dayDiff == 1)
            return "YESTERDAY";
        if (dayDiff < 7)
            return dayDiff + " DAYS AGO";

        if (weekDiff == 1)
            return "LAST WEEK";
        if (monthDiff <= 1)
            return weekDiff + " WEEKS AGO";

        return monthDiff + " MONTHS AGO";

    }

    private Calendar getStartPeriod() {
        Calendar start = (Calendar) mSelectedDate.clone();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        return start;
    }

    private Calendar getEndPeriod() {
        Calendar end = (Calendar) mSelectedDate.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return end;
    }

    private void changeSelectedDate(int amountOfDays) {
        Calendar actual = Calendar.getInstance();
        actual.setTime(new Date());

        mSelectedDate.add(Calendar.DAY_OF_YEAR, amountOfDays);
        if (actual.compareTo(mSelectedDate) < 0)
            mSelectedDate.add(Calendar.DAY_OF_YEAR, -amountOfDays);
        else {
            updatePeriod();
            updateUi();
        }
    }

}
