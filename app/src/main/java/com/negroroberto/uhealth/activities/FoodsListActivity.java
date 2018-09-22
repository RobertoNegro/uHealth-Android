package com.negroroberto.uhealth.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.activities.adapters.FoodsListAdapter;
import com.negroroberto.uhealth.databinding.ActivityFoodsListBinding;
import com.negroroberto.uhealth.modules.food.FoodModule;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Extras;
import com.negroroberto.uhealth.utils.RequestCodes;

import java.util.ArrayList;

public class FoodsListActivity extends UnityActivity implements FoodsListAdapter.OnFoodSelectedClick, FoodsListAdapter.OnFoodSettingsClick, FoodsListAdapter.OnFoodTrashClick{
    private ActivityFoodsListBinding mBinding;
    private FoodModule mFoodModule;
    private ArrayList<Food> mFoods;
    private FoodsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_foods_list);
        setResult(RESULT_CANCELED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFoodModule = new FoodModule((UHealthApplication)getApplication());

        mBinding.txtNameFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if(mAdapter != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.getFilter().filter(s);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ViewGroup footer = (ViewGroup) getLayoutInflater().inflate(R.layout.listitem_plus_footer, mBinding.listFoods, false);
        mBinding.listFoods.addFooterView(footer, null, true);
        footer.findViewById(R.id.btnFooterPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFood();
            }
        });

        updateList();

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodsListActivity.this, SceneActivity.class);
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


    private void createFood() {
        Intent intent = new Intent(FoodsListActivity.this, FoodCreationActivity.class);
        startActivityForResult(intent, RequestCodes.REQUEST_FOOD_CREATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case RequestCodes.REQUEST_FOOD_CREATION: {
                if(resultCode == RESULT_OK) {
                    Food f = data.getParcelableExtra(Extras.EXTRA_FOOD);
                    mFoodModule.createFood(new FoodModule.OnLongResultListener() {
                        @Override
                        public void onResult(long result) {
                            updateList();
                        }
                    }, f);
                }
            } break;
            case RequestCodes.REQUEST_FOOD_UPDATE: {
                if(resultCode == RESULT_OK) {
                    Food f = data.getParcelableExtra(Extras.EXTRA_FOOD);
                    mFoodModule.updateFood(new FoodModule.OnVoidResultListener() {
                        @Override
                        public void onResult() {
                            updateList();
                        }
                    }, f);
                    setResult(RESULT_OK);
                }
            } break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_foods_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_create_food) {
            createFood();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        closeActivity();
        return true;
    }

    private void updateList() {
        mFoodModule.getAllFoodsList(new FoodModule.OnFoodsListResultListener() {
            @Override
            public void onResult(ArrayList<Food> result) {
                mFoods = result;

                mAdapter = new FoodsListAdapter(FoodsListActivity.this, mFoods);
                mAdapter.setOnFoodSelectedClick(FoodsListActivity.this);
                mAdapter.setOnFoodSettingsClick(FoodsListActivity.this);
                mAdapter.setOnFoodTrashClick(FoodsListActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.getFilter().filter(mBinding.txtNameFilter.getText());


                        mBinding.listFoods.setAdapter(mAdapter);
                    }
                });


            }
        });
    }

    @Override
    public void onFoodSettingsClick(Food f) {
        Intent intent = new Intent(FoodsListActivity.this, FoodCreationActivity.class);
        intent.putExtra(Extras.EXTRA_FOOD, (Parcelable)f);
        startActivityForResult(intent, RequestCodes.REQUEST_FOOD_UPDATE);
    }

    @Override
    public void onFoodTrashClick(final Food f) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodsListActivity.this);
        builder
                .setTitle("Are you sure?")
                .setMessage("You're going to delete your food \"" + f.getName() + "\" and all its own data, and it will be removed from all your meals in which it was present too. This action cannot be undone.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mFoodModule.removeFood(new FoodModule.OnVoidResultListener() {
                            @Override
                            public void onResult() {
                                updateList();
                                setResult(RESULT_OK);
                            }
                        }, f);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onFoodSelectedClick(Food f) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Extras.EXTRA_FOOD_ID, f.getId());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
