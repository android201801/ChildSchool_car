package com.bikomobile.multipartsample;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bikomobile.multipart.Multipart;
import com.bikomobile.multipart.Utils.BytesUtils;
import com.bikomobile.multipart.Utils.SplitBytes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class useradd extends AppCompatActivity {

    private static final String UPLOAD_URL = "http://222.122.30.53:8000/lpr";
    String auth_key = "YWFhYWE6YmJiYmI";

    private Uri mImageUri = null;

    private final int REQ_CODE_SELECT_IMAGE = 1001;

    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useradd);

        final EditText editText = (EditText) findViewById(R.id.et_name);
        View btnSelectImage = findViewById(R.id.btn_select_image);
        if (btnSelectImage != null) {
            btnSelectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchSelectedImage();
                }
            });
        }


        View btnUploadImage = findViewById(R.id.btn_upload);
        if (btnUploadImage != null) {
            btnUploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = editText.getText().toString().trim();

                    if (name.isEmpty()) {
                        name = "default.png";
                    }

                    if (mImageUri != null) {
                        uploadPhoto(name, mImageUri);
                    }
                }
            });
        }

    }

    private void launchSelectedImage() {getGallery();}


    private void getGallery() {
        Intent intent = null;

        // 안드로이드 KitKat(level 19)부터는 ACTION_PICK 이용
        if (Build.VERSION.SDK_INT >= 19) {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }


    private void uploadPhoto(String name, Uri imageUri) {

        final Context context = getApplicationContext();
        name = auth_key;

        loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);


        Multipart multipart = new Multipart(context);

        multipart.addParam("auth_key", name);
        multipart.addFile("image/jpeg", "myFile", name, imageUri);

        multipart.launchRequest(UPLOAD_URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                loading.dismiss();
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showImages(List<Uri> images) {

        if (images != null && images.size() == 1) {

            Uri image = images.get(0);

            ImageView imageView = (ImageView) findViewById(R.id.img_selected);
            if (imageView != null) {
                mImageUri = image;
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(getBitmap(image));
            }
        }
    }


    /**
     * Convert the uri image to Bitmap.
     * This method requires permission READ_EXTERNAL_STORAGE
     *
     * @param uri uri from image
     * @return bitmap with the image
     */


    private Bitmap getBitmap(Uri uri) {
        InputStream imageStream = null;

        try {
            imageStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(imageStream);
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


/////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case REQ_CODE_SELECT_IMAGE :
                if (resultCode == RESULT_OK) {
                    ArrayList<Uri> images = new ArrayList<>();
                    if (intent.getData() != null) { // Single image
                        images.add(intent.getData());
                    } else { // Multiple images
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            for (int i = 0; i < intent.getClipData().getItemCount(); i++) {
                                images.add(intent.getClipData().getItemAt(i).getUri());
                            }
                        }
                    }

                    showImages(images);
                }
                break;
        }
    }

}
