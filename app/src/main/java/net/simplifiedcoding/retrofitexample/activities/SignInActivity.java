package net.simplifiedcoding.retrofitexample.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import net.simplifiedcoding.retrofitexample.R;
import net.simplifiedcoding.retrofitexample.api.APIService;
import net.simplifiedcoding.retrofitexample.api.APIUrl;
import net.simplifiedcoding.retrofitexample.helper.SharedPrefManager;
import net.simplifiedcoding.retrofitexample.models.Result;
import net.simplifiedcoding.retrofitexample.models.User;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword;
    private Button buttonSignIn,Upload_img;
    ImageView postimage;

    private String userChoosenTask;
    public static final int REQUEST_CAMERA = 0;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    public static final String GALLERY_DIRECTORY_NAME = "Hello Camera";
    public static final String IMAGE_EXTENSION = "jpg";
    private static String imageStoragePath;
    private Bundle savedInstanceState;
    private Boolean upflag = false;
    int Gallery_view = 2;
     String email1;
    String id1;
    Map<String, RequestBody> map = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        Upload_img = (Button) findViewById(R.id.img_upload);
        postimage=(ImageView)findViewById(R.id.img_view);

        buttonSignIn.setOnClickListener(this);

        Upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(SignInActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        if (CameraUtils.checkPermissions(getApplicationContext())) {
                            captureImage();
                            restoreFromBundle(savedInstanceState);
                        } else {
                           // requestCameraPermission(MEDIA_TYPE_IMAGE);
                        }
                    //captureImage();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                upflag = true;
                // Refreshing the gallery
                previewCapturedImage();

                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // successfully captured the image
                // display it in image view
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        if (requestCode == Gallery_view && data != null) {
            Uri pickedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            imageStoragePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            postimage.setImageBitmap(BitmapFactory.decodeFile(imageStoragePath));
            upflag = true;
            cursor.close();
        }

    }



    private void galleryIntent() {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(i, Gallery_view);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    /**
     * Restoring image path from saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }
    //--------------------------------------------------------------------

    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + IMAGE_EXTENSION)) {
                        previewCapturedImage();
                    }
                }
            }
        }
    }

    private void previewCapturedImage() {

        try {
            // hide video preview
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            new ImageCompression().execute(imageStoragePath);

            postimage.setImageBitmap(bitmap);
            File imgFile = new File(imageStoragePath);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(SignInActivity.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    //***********************************************
    private void userSignIn() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);

         email1 = editTextEmail.getText().toString();
        //String password = editTextPassword.getText().toString();
         id1 = "2";
        File imgFile = new File(imageStoragePath);
        RequestBody   fileBody = RequestBody.create(MediaType.parse("image/*"), imgFile);
        RequestBody   fileBody1 = RequestBody.create(MediaType.parse("image/*"), imgFile);

        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_image", imgFile.getName(),fileBody);
        MultipartBody.Part body1 = MultipartBody.Part.createFormData("sign_image", imgFile.getName(),fileBody1);

        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), email1);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "rrr");
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), "7867867871");
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), "jajaja");
        RequestBody passout_year = RequestBody.create(MediaType.parse("text/plain"), "2018");
        RequestBody collage_name = RequestBody.create(MediaType.parse("text/plain"), "beta");
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), "indori");
       // RequestBody id = RequestBody.create(MediaType.parse("text/plain"), id1);


        Call<Result> call = service.userLogin(email,name,mobile,password,passout_year,collage_name,address,body,body1);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                if (response.body().getResponce().equals("true")) {
                   // finish();
                    Toast.makeText(SignInActivity.this, "tt "+response.body().getResponce(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignInActivity.this, "msg "+response.body().getMassage(), Toast.LENGTH_SHORT).show();
                   // startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "ff "+response.body().getResponce(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "ff "+response.body().getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
            userSignIn();
        }
    }

    public class ImageCompression extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length == 0 || strings[0] == null)
                return null;

            return CommonUtils.compressImage(strings[0]);
        }

        protected void onPostExecute(String imagePath) {
            // imagePath is path of new compressed image.
//            mivImage.setImageBitmap(BitmapFactory.decodeFile(new File(imagePath).getAbsolutePath()));
            if (imagePath != null) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                   // new ImageUploadTask(imgFile).execute();
                }
            } else {
                //AlertDialogCreate();
            }

            }
        }

}
