package com.mahmutakbas.evarkadasim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.Help;
import com.mahmutakbas.evarkadasim.Management.AuthManagement;
import com.mahmutakbas.evarkadasim.Management.UserManagement;
import com.mahmutakbas.evarkadasim.Models.User;
import com.mahmutakbas.evarkadasim.Other.Helper;
import com.mahmutakbas.evarkadasim.Other.ResultMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private final int REQUEST_CAMERA_PERMISSION_CODE = 1;
    private final int REQUEST_IMAGE_CAPTURE = 2;
    private final int REQUEST_GALLERY_PERMISSION_CODE = 1000;
    private TextInputLayout emailSignUp, passSignUp, passAgainSignUp, fullName;
    private CircleImageView profileImage;
    private Button signUpBtn;
    private TextView signINText;
    private FirebaseAuth auth;
    private FirebaseFirestore fireStore;
    private StorageReference storageReference;
    private String Uid = "";
    private Uri mImageUri = null;
    private boolean isPhotoSelected = false;
    private User user;
    private UserManagement userManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        fullName = findViewById(R.id.sing_up_fullName);
        emailSignUp = findViewById(R.id.sing_up_email);
        passSignUp = findViewById(R.id.sing_up_password);
        passAgainSignUp = findViewById(R.id.sing_up_password_again);
        signUpBtn = findViewById(R.id.sing_up_btn);
        signINText = findViewById(R.id.sign_in_text);
        profileImage = findViewById(R.id.sign_up_circleImageView);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });

        signINText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {

        ProgressDialog dialog = Helper.showProgressDialog(SignUpActivity.this, "Lütfen bekleyiniz...");
        dialog.show();

        String nameSurname = fullName.getEditText().getText().toString();
        String email = emailSignUp.getEditText().getText().toString();
        String passOne = passSignUp.getEditText().getText().toString();
        String passTwo = passAgainSignUp.getEditText().getText().toString();


        if (!nameSurname.isEmpty() && !email.isEmpty() && !passOne.isEmpty() && !passTwo.isEmpty()) {

            if (email.contains("std.yildiz.edu.tr")) {

                if (passOne.equals(passTwo)) {

                    AuthManagement authManagement = new AuthManagement();
                    CompletableFuture<ResultMessage> resultAuth = authManagement.signUp(auth, email, passOne);
                    resultAuth.thenAccept(authMessage -> {
                        if (authMessage.isSuccess()) {

                            if (isPhotoSelected) {

                                CompletableFuture<ResultMessage> resultImage = authManagement.putFile(storageReference, mImageUri, authMessage.getMessage());
                                resultImage.thenAccept(imageMessage -> {
                                    if (imageMessage.isSuccess()) {

                                        user = new User(nameSurname, "", "", "", "metre", "", email, "",Uid + ".jpg",Uid, 0, 0);

                                        userManagement = new UserManagement(user);
                                        userManagement.setUser(fireStore, authMessage.getMessage());

                                        FirebaseUser cUser = auth.getCurrentUser();

                                        CompletableFuture<ResultMessage> resultUser = userManagement.setUser(fireStore, authMessage.getMessage());
                                        resultUser.thenAccept(userMessage -> {
                                            if (userMessage.isSuccess()) {

                                                cUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Helper.showToast(SignUpActivity.this, "Emailinize doğrulama linki gönderildi.\n Linke tıklayarak giriş doğrulama yapabilirsiniz.");
                                                            dialog.dismiss();

                                                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));

                                                        } else {
                                                            Helper.showToast(SignUpActivity.this, task.getException().getMessage());
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                });
                                            } else {

                                                cUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("USER_DELETE", "User deleted.");
                                                            dialog.dismiss();

                                                        } else {
                                                            Log.w("USER_DELETE", "User not deleted.");
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                });
                                                Helper.showToast(SignUpActivity.this, userMessage.getMessage());
                                            }
                                        });
                                    } else {
                                        Helper.showToast(SignUpActivity.this, imageMessage.getMessage());

                                        dialog.dismiss();
                                    }
                                });
                            } else {

                                FirebaseUser cUser = auth.getCurrentUser();
                                user = new User(nameSurname, "", "", "", "metre", "", email, "",Uid + ".jpg",Uid, 0, 0);

                                userManagement = new UserManagement(user);

                                userManagement.setUser(fireStore, authMessage.getMessage());

                                CompletableFuture<ResultMessage> resultUser = userManagement.setUser(fireStore, authMessage.getMessage());
                                resultUser.thenAccept(userMessage -> {
                                    if (userMessage.isSuccess()) {

                                        cUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Helper.showToast(SignUpActivity.this, "Doğrulama Gönderildi");
                                                    dialog.dismiss();

                                                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));

                                                } else {
                                                    Helper.showToast(SignUpActivity.this, task.getException().getMessage());
                                                    dialog.dismiss();
                                                }
                                            }
                                        });
                                    } else {

                                        cUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("USER_DELETE", "User deleted.");
                                                    dialog.dismiss();

                                                } else {
                                                    Log.w("USER_DELETE", "User not deleted.");
                                                    dialog.dismiss();
                                                }
                                            }
                                        });
                                        Helper.showToast(SignUpActivity.this, userMessage.getMessage());
                                    }
                                });
                            }
                        } else {
                            Helper.showToast(SignUpActivity.this, authMessage.getMessage());
                            dialog.dismiss();
                        }
                    });
                } else {
                    Helper.showToast(SignUpActivity.this, "Şifreler uyuşmuyor!");
                    dialog.dismiss();
                }
            } else {
                Helper.showToast(SignUpActivity.this, "Sadece : std.yildiz.edu.tr mail ile kayıt olabilir siniz.");
                dialog.dismiss();
            }
        } else {
            Helper.showToast(SignUpActivity.this, "Boş alanları doldurun!");
            dialog.dismiss();
        }
    }

    private void selectImage(View view) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_PERMISSION_CODE);
            return;
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            return;
        }

        final CharSequence[] options = {"Camera", "Galeriden Fotoğraf Seç", "İptal"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);

        builder.setTitle("Fotoğraf Ekle");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        break;
                    case 1:
                        Intent iGallery = new Intent(Intent.ACTION_PICK);
                        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(iGallery, REQUEST_GALLERY_PERMISSION_CODE);
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_GALLERY_PERMISSION_CODE:

                    Glide.with(SignUpActivity.this).load(data.getData()).into(profileImage);

                    mImageUri = data.getData();
                    //profileImage.setImageURI(data.getData());
                    isPhotoSelected = true;
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    Glide.with(SignUpActivity.this).load(imageBitmap).into(profileImage);

                    mImageUri = saveImageToGalery(imageBitmap);
                    //profileImage.setImageBitmap(imageBitmap);
                    isPhotoSelected = true;
                    break;
            }
        }
    }

    private Uri saveImageToGalery(Bitmap imageBitmap) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG" + timestamp + ".jpg";
        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream outputstream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
            outputstream.flush();
            outputstream.close();

            Intent mediaScanlntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            Uri uri = Uri.fromFile(imageFile);
            mediaScanlntent.setData(uri);

            sendBroadcast(mediaScanlntent);
            //Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_LONG).show();
            return uri;

        } catch (Exception e) {

        }
        return null;
    }
}