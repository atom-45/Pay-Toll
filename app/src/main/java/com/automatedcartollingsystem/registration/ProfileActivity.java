package com.automatedcartollingsystem.registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.automatedcartollingsystem.database.ActsDatabaseHelper;
import com.automatedcartollingsystem.models.Constants;
import com.automatedcartollingsystem.models.ImageModel;
import com.example.automatedcartollingsystem.R;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    public static String user_email;
    public static String user_id;
    private static final int REQUEST_PERMISSION_CODE = 3;
    private static final int REQUEST_ACTIVITY_CODE = 1;
    private Uri contentUri;
    private List<ImageModel> imageModels;
    private SQLiteDatabase db;
    private SQLiteOpenHelper actsDatabaseHelper;
    private ImageView profilePicture;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        actsDatabaseHelper = new ActsDatabaseHelper(this);


        if ((ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED)
        && (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        }
        getContentUri();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        profilePicture = findViewById(R.id.profilePicture);
        setPreviousProfilePic();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //RequestQueue requestQueue1 = Volley.newRequestQueue(this);
        getUserData(requestQueue);

        findViewById(R.id.changeProfilePicture).setOnClickListener(
                v -> {

                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    Log.e("File Loc", String.valueOf(intent.getData()));
                    startActivityForResult(intent,REQUEST_ACTIVITY_CODE);

                }
        );
        findViewById(R.id.edit_button).setOnClickListener(v -> {
                    startActivity(new Intent(this, EditProfileActivity.class));
                    finish();
        });
    }

    @Override
    protected void onDestroy() {
        actsDatabaseHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri imageSelected = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getApplicationContext().getContentResolver()
                        .query(imageSelected, filePathColumn,
                                null, null, null);

                cursor.moveToFirst();
                //int indexColumn = cursor.getColumnIndex(filePathColumn[0]);
                //String filePath = cursor.getString(indexColumn);
                cursor.close();

                String readOnlyMode = "r";
                ContentResolver resolver = getApplicationContext().getContentResolver();

                try (ParcelFileDescriptor parcelFileDescriptor = resolver.openFileDescriptor(imageSelected,readOnlyMode))
                { //I have to edit my BitMap issue.
                    db = actsDatabaseHelper.getReadableDatabase();
                    Bitmap image = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor());


                    //profilePicture.setImageBitmap(encodeImage(image));
                    profilePicture.setImageURI(imageSelected);

                    ContentValues contentValue = new ContentValues();
                    contentValue.put("EMAIL_ADDRESS",user_email);
                    contentValue.put("BITMAP_RESOURCE_ID", imageSelected.toString());
                    db.insert("USERS",null,contentValue);

                    db.close();

                    Log.e("Bitmap",image.toString());
                } catch (IOException|SQLiteFullException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions,
                                           @NonNull @NotNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permissions", "Permission granted");
            }
        }
    }

    private void getContentUri() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else{
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
    }

    private void setPreviousProfilePic(){
        List<String> itemIds = new ArrayList<>();
        SQLiteDatabase database = actsDatabaseHelper.getReadableDatabase();
        String[] columns = new String[]{"BITMAP_RESOURCE_ID"};
        
        try (Cursor cursor = database.query("USERS", columns, null,
                null, null, null, null)) {

            itemIds.size();
            while (cursor.moveToNext()) {
                itemIds.add(cursor.getString(cursor.getColumnIndexOrThrow("BITMAP_RESOURCE_ID")));
                profilePicture.setImageURI(Uri.parse(itemIds.get(itemIds.size()-1)));
            }
        }

        Log.e("BitMaps array",itemIds.toString());

    }
    private Bitmap encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);
        //byte[] bytes = byteArrayOutputStream.toByteArray();
        // rreturn Base64.encodeToString(bytes,Base64.DEFAULT);
        return previewBitmap;
    }

    private void getUserData(RequestQueue requestQueue){
        TextView nameText = findViewById(R.id.nameTextView);
        TextView emailText = findViewById(R.id.emaiTextView);
        TextView mobileNumberText = findViewById(R.id.mobileNumberTextView);
        TextView accountIDText = findViewById(R.id.accountTextView);

        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                Constants.URL_USER_STRING, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                user_id = jsonObject.getString("user_id");
                String name = jsonObject.getString("name");
                String mobileNumber = jsonObject.getString("mobile_number");
                String email = jsonObject.getString("email");
                String account_id = Integer.toString(jsonObject.getInt("account_id"));

                nameText.setText(name);
                emailText.setText(email);
                mobileNumberText.setText(mobileNumber);
                accountIDText.setText(account_id);

                getCarData(user_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, Throwable::printStackTrace)
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> mParameters = new HashMap<>();
                mParameters.put("email",user_email);
                mParameters.put("type","getUser");
                return mParameters;
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        requestQueue.add(stringRequest1);
    }


    private void getCarData(String user_id){
        // I have to set car data onto the car textView.
        TextView carTextView = findViewById(R.id.carsTextView);
        RequestQueue requestQueue1 = Volley.newRequestQueue(this);

        StringRequest  stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_CAR_STRING, response -> {
                    try {
                        if(response!=null){
                            JSONObject jsonObject = new JSONObject(response);
                            String carModel = jsonObject.getString("model");
                            String carBrand = jsonObject.getString("brand");
                            String carReg = jsonObject.getString("registration");

                            carTextView.setText(
                                    String.format("Brand: %s\nModel: %s\nReg: %s",carBrand,carModel,carReg)
                            );
                        } else {
                            carTextView.setText(
                                    String.format("Brand: %s\nModel: %s\nReg: %s",
                                            "Unknown","Unknown","Unknown"
                                    )
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace)

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> parameters = new HashMap<>();
                parameters.put("user_id",user_id);
                parameters.put("type","getCars2");
                return  parameters;
            }

            @Override
            public Priority getPriority() {
                return Priority.NORMAL;
            }
        };
        requestQueue1.add(stringRequest);
    }

    private void storeImage(){
        ImageModel imageModel = new ImageModel();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};

        String selection = MediaStore.Images.Media.SIZE +" <= ?";
        String[] selectionArgs = new String[] {String.valueOf(40)};
        String sortOrder = MediaStore.Images.Media.DISPLAY_NAME+" ASC";

        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                contentUri,
                projection,selection,selectionArgs,sortOrder)){
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

            while(cursor.moveToNext()){
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int size = cursor.getInt(sizeColumn);
                Uri contentUri_1 = ContentUris
                        .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);

                imageModels.add(new ImageModel(name,id,size,contentUri_1));
            }
        }
    }
}