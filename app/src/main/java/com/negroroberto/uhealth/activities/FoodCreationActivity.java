package com.negroroberto.uhealth.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.PopupMenu;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.negroroberto.uhealth.R;
import com.negroroberto.uhealth.UHealthApplication;
import com.negroroberto.uhealth.activities.abstracts.UHealthActivity;
import com.negroroberto.uhealth.activities.abstracts.UnityActivity;
import com.negroroberto.uhealth.databinding.ActivityFoodCreationBinding;
import com.negroroberto.uhealth.modules.food.interfaces.FoodResultListener;
import com.negroroberto.uhealth.modules.food.models.Food;
import com.negroroberto.uhealth.modules.food.utils.FoodGetter;
import com.negroroberto.uhealth.unity.UnityController;
import com.negroroberto.uhealth.utils.Common;
import com.negroroberto.uhealth.utils.Debug;
import com.negroroberto.uhealth.utils.Extras;
import com.negroroberto.uhealth.utils.FilePaths;
import com.negroroberto.uhealth.utils.RequestCodes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FoodCreationActivity extends UnityActivity {
    private ActivityFoodCreationBinding mBinding;
    private FoodGetter mFoodGetter;

    private Food mEditingFood;

    private Bitmap mPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_food_creation);
        setResult(RESULT_CANCELED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mEditingFood = getIntent().getParcelableExtra(Extras.EXTRA_FOOD);

        mBinding.btnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        mBinding.btnGenerateFromBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFromCode();
            }
        });

        mBinding.btnCancelGeneration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSearch();
            }
        });

        mBinding.btnPhotoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(FoodCreationActivity.this, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_take_photo:
                                takePhoto();
                                break;
                            case R.id.menu_gallery:
                                selectFromGallery();
                                break;
                            case R.id.menu_remove:
                                removePhoto();
                                break;
                        }
                        return false;
                    }
                });

                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_food_creation_img_settings, popup.getMenu());
                popup.show();
            }
        });

        if (mEditingFood != null) {
            updateInfo(mEditingFood, BitmapFactory.decodeFile(FilePaths.GetFoodImagePath(FoodCreationActivity.this) + mEditingFood.getPhoto()));
            mBinding.btnCreate.setText("Update");
            Debug.Log(this, mEditingFood.toString());
        }

        mBinding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClicked();
            }
        });

        if(getIntent().getBooleanExtra(Extras.EXTRA_FOOD_CREATION_BARCODE, false))
            startScan();

        mBinding.idUnity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodCreationActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });

        if(getIntent().getBooleanExtra(Extras.EXTRA_FIRST_RUN, false))
            sendMessageToUser("From here, you can enter the nutritional values of all your types of food. And don't worry: you'll have to do it only once per type of food. You can scan the barcode number of your product and try to get all the nutritional values automatically if they're available. I suggest you to add a photo of your product too!", null);
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


    private void createClicked() {
        boolean result = createFood(new OnFoodCreatedListener() {
            @Override
            public void onFoodCreated(Food f) {
                Intent result = new Intent();
                result.putExtra(Extras.EXTRA_FOOD, (Parcelable) f);
                setResult(RESULT_OK, result);
                closeActivity();
            }
        });

        if (!result)
            Toast.makeText(FoodCreationActivity.this, "Something went wrong. Please, check the fields.", Toast.LENGTH_SHORT).show();
    }

    private void startScan() {
        new IntentIntegrator(FoodCreationActivity.this).setOrientationLocked(false).setBeepEnabled(false).initiateScan();
    }

    private void takePhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RequestCodes.REQUEST_CAMERA_PERMISSION);
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, RequestCodes.REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestCodes.REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, RequestCodes.REQUEST_TAKE_PHOTO);
                } else
                    Toast.makeText(this, "Camera permission required. Please, try again.", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private void selectFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, RequestCodes.REQUEST_OPEN_GALLERY);
    }

    private void removePhoto() {
        mPhoto = null;
        updateInfo(null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE: {
                if (result.getContents() != null) {
                    String barcode = result.getContents();
                    mBinding.txtCode.setText(barcode);
                    searchFromCode();
                }
            }
            break;
            case RequestCodes.REQUEST_TAKE_PHOTO: {
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    updateInfo(null, (Bitmap) data.getExtras().get("data"));
                }
            }
            case RequestCodes.REQUEST_OPEN_GALLERY: {
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap bitmap = null;

                    final Uri uri = data.getData();
                    if (uri != null) {
                        InputStream inputStream = null;
                        try {
                            inputStream = getContentResolver().openInputStream(uri);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                        } catch (FileNotFoundException ignore) {
                        } finally {
                            try {
                                if (inputStream != null)
                                    inputStream.close();
                            } catch (IOException ignore) {
                            }
                        }
                    }

                    updateInfo(null, bitmap);
                }
            }
            break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_food_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_create: {
                createClicked();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        closeActivity();
        return true;
    }

    private void updateInfo(final Food food, final Bitmap image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (image != null)
                    mPhoto = image;

                if (mPhoto == null)
                    mBinding.imgPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_photo_default));
                else
                    mBinding.imgPhoto.setImageBitmap(mPhoto);

                if (food != null) {
                    mBinding.txtCode.setText(food.getCode());
                    mBinding.txtName.setText(food.getName());
                    mBinding.txtBrand.setText(food.getBrand());
                    mBinding.txtServingQuantity.setText(Common.DoubleStringify(food.getServingQuantity()));
                    mBinding.txtEnergy.setText(Common.DoubleStringify(food.getEnergy()));
                    mBinding.txtFat.setText(Common.DoubleStringify(food.getFat()));
                    mBinding.txtCarbs.setText(Common.DoubleStringify(food.getCarbohydrates()));
                    mBinding.txtFiber.setText(Common.DoubleStringify(food.getFiber()));
                    mBinding.txtProteins.setText(Common.DoubleStringify(food.getProteins()));
                    mBinding.txtSalt.setText(Common.DoubleStringify(food.getSalt()));
                    mBinding.txtSugars.setText(Common.DoubleStringify(food.getSugars()));
                    mBinding.txtSodium.setText(Common.DoubleStringify(food.getSodium()));
                }
            }
        });
    }

    private void stopSearch() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.btnGenerateFromBarcode.setVisibility(View.VISIBLE);
                mBinding.layoutGeneratingProgress.setVisibility(View.GONE);
                mBinding.txtCode.setEnabled(true);
                mBinding.txtCode.setKeyListener((KeyListener) mBinding.txtCode.getTag());
            }
        });

        if (mFoodGetter != null)
            mFoodGetter.setResultListener(null);
    }

    private void searchFromCode() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.btnGenerateFromBarcode.setVisibility(View.GONE);
                mBinding.layoutGeneratingProgress.setVisibility(View.VISIBLE);
                mBinding.txtCode.setEnabled(false);
                mBinding.txtCode.setTag(mBinding.txtCode.getKeyListener());
                mBinding.txtCode.setKeyListener(null);
            }
        });
        mFoodGetter = new FoodGetter(getApplicationContext());

        mFoodGetter.setResultListener(new FoodResultListener() {
            @Override
            public void onResult(Food food, Bitmap image) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.btnGenerateFromBarcode.setVisibility(View.VISIBLE);
                        mBinding.layoutGeneratingProgress.setVisibility(View.GONE);
                        mBinding.txtCode.setEnabled(true);
                        mBinding.txtCode.setKeyListener((KeyListener) mBinding.txtCode.getTag());
                    }
                });

                if (food != null)
                    updateInfo(food, image);
                else
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FoodCreationActivity.this, "We're sorry, this code is not in our databases.", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        mFoodGetter.startSearch(mBinding.txtCode.getText().toString());

    }

    private interface OnFoodCreatedListener {
        void onFoodCreated(Food f);
    }

    private boolean createFood(final OnFoodCreatedListener listener) {
        final Food f = new Food();

        if (mEditingFood != null)
            f.setId(mEditingFood.getId());

        try {
            f.setCode(mBinding.txtCode.getText().toString());
            f.setName(mBinding.txtName.getText().toString());
            f.setBrand(mBinding.txtBrand.getText().toString());
            f.setServingQuantity(Common.ParseDouble(mBinding.txtServingQuantity.getText().toString()));
            f.setEnergy(Common.ParseDouble(mBinding.txtEnergy.getText().toString()));
            f.setFat(Common.ParseDouble(mBinding.txtFat.getText().toString()));
            f.setCarbohydrates(Common.ParseDouble(mBinding.txtCarbs.getText().toString()));
            f.setFiber(Common.ParseDouble(mBinding.txtFiber.getText().toString()));
            f.setProteins(Common.ParseDouble(mBinding.txtProteins.getText().toString()));
            f.setSalt(Common.ParseDouble(mBinding.txtSalt.getText().toString()));
            f.setSugars(Common.ParseDouble(mBinding.txtSugars.getText().toString()));
            f.setSodium(Common.ParseDouble(mBinding.txtSodium.getText().toString()));
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }

        if (mPhoto != null) {
            final String filename = (f.getCode() + f.getName()).replaceAll("\\W+", "").replaceAll("\\s+", "") + ".jpg";
            SavePhotoTask task = new SavePhotoTask(FilePaths.GetFoodImagePath(FoodCreationActivity.this), filename, mPhoto, new OnSavePhotoFinishListener() {
                @Override
                public void onFinish(boolean valid) {
                    if (valid) {
                        f.setPhoto(filename);
                        listener.onFoodCreated(f);
                    }
                }
            });
            task.execute();
        } else
            listener.onFoodCreated(f);
        return true;
    }

    private interface OnSavePhotoFinishListener {
        void onFinish(boolean valid);
    }

    private static class SavePhotoTask extends AsyncTask<Void, Void, Void> {
        private String mPath;
        private String mFilename;
        private Bitmap mBitmap;
        private OnSavePhotoFinishListener mListener;

        public SavePhotoTask(String path, String filename, Bitmap bitmap, OnSavePhotoFinishListener listener) {
            mPath = path;
            mFilename = filename;
            mBitmap = bitmap;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File filePhoto = new File(mPath, mFilename);
            boolean valid = false;

            filePhoto.getParentFile().mkdirs();

            if (filePhoto.exists())
                filePhoto.delete();

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(filePhoto.getPath());
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
                valid = true;
            } catch (IOException e) {
                Debug.Err(this, "Error saving photo file: " + e.toString());
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException ignore) {
                }
            }

            if (!valid && filePhoto.exists())
                filePhoto.delete();

            if (mListener != null)
                mListener.onFinish(valid);

            return null;
        }
    }
}
