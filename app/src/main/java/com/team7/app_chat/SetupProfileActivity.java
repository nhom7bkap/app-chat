package com.team7.app_chat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.components.ProgressButton;
import com.team7.app_chat.models.User;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SetupProfileActivity extends AppCompatActivity {

    public ImageView avatarImg;
    private ProgressButton progressButton;
    private Uri sourceUri;
    private Uri destinationUri;
    private ActivityResultLauncher<String[]> permissionContract;
    private ActivityResultLauncher<Uri> takePhotoContract;
    private ActivityResultLauncher<String> pickImageContract;
    private UserRepository userRepository;
    private User user;
    private ImageView imgAvatar;
    FirebaseAuth firebaseProfile;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        imgAvatar = findViewById(R.id.imgAvatar);
        firebaseProfile = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progressButton = new ProgressButton(SetupProfileActivity.this, findViewById(R.id.btnSetupProfile), "Setup Profile");
        userRepository = new UserRepository();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRepository.get(currentUser.getUid()).addOnSuccessListener(this, user -> {
            this.user = user;
            this.user.setId(currentUser.getUid());
        });
        initComponents();
        //load anh
        StorageReference profileRef = storageReference.child("users").child(firebaseProfile.getCurrentUser().getUid()).child("profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                Picasso.get().load(uri).fit().centerCrop().into(profileImage);
            Handler handler = new Handler(Looper.getMainLooper());
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.execute(() -> {
                try{
                    InputStream url = new URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(url);
                    handler.post(() -> ((ImageView) findViewById(R.id.imgAvatar)).setImageBitmap(bitmap));
                } catch(Exception e){

                }
            });
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });
    }

    private void initComponents() {
//        Toast.makeText(this, "Completed!", Toast.LENGTH_SHORT).show();
//        avatarImg = findViewById(R.id.imgAvatar);
//
//        takePhotoContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), status -> {
//            if (status) {
//                String destinationFileName = System.currentTimeMillis() + ".jpg";
//                destinationUri = Uri.fromFile(new File(getCacheDir(), destinationFileName));
//                UCrop.of(sourceUri, destinationUri)
//                        .withAspectRatio(1, 1)
//                        .withMaxResultSize(500, 500)
//                        .start(this);
//            }
//        });
//
//        permissionContract = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                AtomicBoolean status = new AtomicBoolean(true);
//                result.forEach((key, val) -> {
//                    if (!val) {
//                        status.set(false);
//                    }
//                });
//                if (!status.get()) {
//                    Toast.makeText(this, "Please allow permission to use this feature!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        pickImageContract = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
//            String destinationFileName = System.currentTimeMillis() + ".jpg";
//            destinationUri = Uri.fromFile(new File(getCacheDir(), destinationFileName));
//            sourceUri = uri;
//            UCrop.of(sourceUri, destinationUri)
//                    .withAspectRatio(1, 1)
//                    .withMaxResultSize(500, 500)
//                    .start(this);
//        });
//
//        ImageButton btnPickImg = findViewById(R.id.btnPickImg);
//        btnPickImg.setOnClickListener(view -> {
//            showDialog();
//        });
        findViewById(R.id.edtBirthday).setOnClickListener(view -> openDatePicker(view));


        findViewById(R.id.btnSkip).setOnClickListener(view -> goToHomePage());
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
        findViewById(R.id.btnSetupProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

//
//    private Uri createImageUri(){
//        Uri imageCollection;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//        } else{
//            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        }
//        String imageName = String.valueOf(System.currentTimeMillis());
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
//        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        Uri finalUri = getContentResolver().insert(imageCollection, contentValues);
//        return finalUri;
//    }
//    private void pickImageContract(){
//        permissionContract();
//        if(checkCameraPermission()){
//            pickImageContract.launch("image/*");
//        }
//
//    }
//    private void captureImageContract(){
//        permissionContract();
//        if(checkCameraPermission()){
//            sourceUri = createImageUri();
//            takePhotoContract.launch(sourceUri);
//        }
//    }
//    private void permissionContract() {
//        String permission[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        permissionContract.launch(permission);
//    }
//    private void loadImage(Uri uri){
//        avatarImg.setImageURI(uri);
//    }
//    private boolean checkCameraPermission(){
//        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
//        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//        return result && result1;
//    }
//
//    private void showDialog(){
//        try {
//            String imageItems[] = new String[]{"Take a picture", "Choose from gallery", "Cancel"};
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Select one");
//            builder.setItems(imageItems, (dialog, item) ->{
//                switch (imageItems[item]){
//                    case "Take a picture" :
//                        dialog.dismiss();
//                        captureImageContract();
//                        break;
//                    case "Choose from gallery":
//                        dialog.dismiss();
//                        pickImageContract();
//                        break;
//                    case "Cancel":
//                        dialog.dismiss();
//                        break;
//                }
//            });
//            builder.show();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//            final Uri resultUri = UCrop.getOutput(data);
//            loadImage(resultUri);
//        } else if (resultCode == UCrop.RESULT_ERROR) {
//            final Throwable cropError = UCrop.getError(data);
//        }
//    }


    private void submitForm() {
        progressButton.buttonActivated();
        String fullname = ((TextInputLayout) findViewById(R.id.editName)).getEditText().getText().toString();
        String date = ((EditText) findViewById(R.id.edtBirthday)).getText().toString();
        Date birthday = null;
        try {
            birthday = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setFullName(fullname);
        user.setDOB(birthday);
        user.setFirstTime(false);
        if (user.getAvatar() == null){
            user.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-app-4aa49.appspot.com/o/user-pngrepo-com.png?alt=media&token=e3dc0dd4-61d2-47e7-88f3-f727ed8b18e8");
        }
        RadioGroup radioButton = findViewById(R.id.radioGenderGroup);
        switch (radioButton.getCheckedRadioButtonId()) {
            case R.id.female_btn:
                user.setGender(1);
                break;
            case R.id.male_btn:
                user.setGender(2);
                break;
            default:
                user.setGender(0);
                break;
        }
        userRepository.update(user).addOnSuccessListener(unused -> {
            progressButton.buttonFinished();

             userRepository.get(user.getId()).addOnSuccessListener(this, user -> {
                 CurrentUser.user = user;
            });
            goToHomePage();
        }).addOnFailureListener(e -> {
            progressButton.buttonFailed();
        });
    }

    public void openDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        DatePickerDialog.OnDateSetListener listener = (datePicker, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            ((EditText) view).setText(fmt.format(calendar.getTime()));
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog pickerDialog = new DatePickerDialog(SetupProfileActivity.this, style, listener, year, month, day);
        pickerDialog.show();
    }

    private void goToHomePage() {
        Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                //profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);


            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        final StorageReference fileRef = storageReference.child("users/" + firebaseProfile.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imgAvatar);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}