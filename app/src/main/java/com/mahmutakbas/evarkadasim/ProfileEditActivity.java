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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileEditActivity extends AppCompatActivity {
    private final int REQUEST_CAMERA_PERMISSION_CODE = 1;
    private final int REQUEST_IMAGE_CAPTURE = 2;
    private final int REQUEST_GALLERY_PERMISSION_CODE = 1000;
    private TextInputLayout emailSignUp, fullName, phoneNumber, time, length;
    private CircleImageView profileImage;
    private Spinner spinTimeType, spinLengthType, spinClassType, spinStatus, spinDepartment;
    private Button saveBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore fireStore;
    private StorageReference storageReference;
    private String Uid = "";
    private Uri mImageUri = null;
    private boolean isPhotoSelected = false;
    private User user;
    private UserManagement userManagement;

    String[] departments;
    String[] statuses;
    String[] classes;
    String[] times;
    String[] distances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        profileImage = findViewById(R.id.person_edit_circleImageView);
        fullName = findViewById(R.id.person_edit_fullName);
        emailSignUp = findViewById(R.id.person_edit_email);
        phoneNumber = findViewById(R.id.person_edit_phone_number);
        time = findViewById(R.id.person_edit_time);
        length = findViewById(R.id.person_edit_length);
        spinDepartment = findViewById(R.id.person_edit_department);
        spinTimeType = findViewById(R.id.person_edit_timeType);
        spinLengthType = findViewById(R.id.person_edit_lengthType);
        spinClassType = findViewById(R.id.person_edit_class);
        spinStatus = findViewById(R.id.person_edit_status);
        saveBtn = findViewById(R.id.person_edit_btn);

        emailSignUp.getEditText().setEnabled(false);



        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        fireStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        user = new User();

        userManagement = new UserManagement(user);

        ProgressDialog dialog = Helper.showProgressDialog(ProfileEditActivity.this, "Profiliniz Yükleniyor...");

        dialog.show();

        CompletableFuture<ResultMessage<User>> resultUser = userManagement.getUser(fireStore, Uid);
        resultUser.thenAccept(userResultMessage -> {
            if (userResultMessage.isSuccess()) {

                loadSpinner();
                user = userResultMessage.getData();
                loadView(user);

                CompletableFuture<ResultMessage<Uri>> resultImage = AuthManagement.getFile(storageReference, Uid);
                resultImage.thenAccept(new Consumer<ResultMessage<Uri>>() {
                    @Override
                    public void accept(ResultMessage<Uri> uriResultMessage) {
                        if (uriResultMessage.isSuccess()) {

                            Glide.with(ProfileEditActivity.this).load(uriResultMessage.getData()).into(profileImage);
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            Helper.showToast(ProfileEditActivity.this, uriResultMessage.getMessage());
                        }
                    }
                });
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();



            }
        });
    }

    private void saveUser() {

        ProgressDialog dialog = Helper.showProgressDialog(ProfileEditActivity.this, "Lütfen bekleyiniz...");
        dialog.show();

        String nameSurname = fullName.getEditText().getText().toString();
        String email = emailSignUp.getEditText().getText().toString();
        String phone = phoneNumber.getEditText().getText().toString();

        Helper.showToast(ProfileEditActivity.this, spinStatus.getSelectedItem().toString());
        if (!nameSurname.isEmpty()) {
            if (isPhotoSelected) {

                CompletableFuture<ResultMessage> resultImage = AuthManagement.putFile(storageReference, mImageUri, Uid);
                resultImage.thenAcceptAsync(imageMessage -> {
                    if (imageMessage.isSuccess()) {

                        user = new User(nameSurname, spinDepartment.getSelectedItem().toString(), spinClassType.getSelectedItem().toString(), spinStatus.getSelectedItem().toString(), spinLengthType.getSelectedItem().toString(), spinTimeType.getSelectedItem().toString(), email, phone, Uid + ".jpg",Uid, Integer.parseInt(length.getEditText().getText().toString()), Integer.parseInt(time.getEditText().getText().toString()));

                        userManagement = new UserManagement(user);
                        userManagement.setUser(fireStore, Uid);

                        CompletableFuture<ResultMessage> resultUser = userManagement.setUser(fireStore, Uid);
                        resultUser.thenAccept(userMessage -> {
                            if (userMessage.isSuccess()) {

                                Helper.showToast(ProfileEditActivity.this, "Güncelleme Başarılı");
                                dialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            } else {


                                Helper.showToast(ProfileEditActivity.this, userMessage.getMessage());
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Helper.showToast(ProfileEditActivity.this, imageMessage.getMessage());

                        dialog.dismiss();
                    }
                });
            } else {

                user = new User(nameSurname, spinDepartment.getSelectedItem().toString(), spinClassType.getSelectedItem().toString(), spinStatus.getSelectedItem().toString(), spinLengthType.getSelectedItem().toString(), spinTimeType.getSelectedItem().toString(), email, phone, Uid + ".jpg",Uid, Integer.parseInt(length.getEditText().getText().toString()), Integer.parseInt(time.getEditText().getText().toString()));

                userManagement = new UserManagement(user);

                userManagement.setUser(fireStore, Uid);

                CompletableFuture<ResultMessage> resultUser = userManagement.setUser(fireStore, Uid);
                resultUser.thenAccept(userMessage -> {
                    if (userMessage.isSuccess()) {
                        Helper.showToast(ProfileEditActivity.this, "Güncelleme Başarılı");
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Helper.showToast(ProfileEditActivity.this, userMessage.getMessage());
                        dialog.dismiss();
                    }
                });
            }
        } else {
            Helper.showToast(ProfileEditActivity.this, "Boş alanları doldurun!");
            dialog.dismiss();
        }
    }

    private void loadSpinner() {

        distances = new String[]{"Metre", "Kilometre"};
        times = new String[]{"Gün", "Ay", "Yıl"};
        classes = new String[]{"Sınıf Seçiniz", "Hazırlık", "1.Sınıf", "2.Sınıf", "3.Sınıf", "4.Sınıf", "5.Sınıf"};
        statuses = new String[]{"Durum Seçiniz", "Kalacak Ev Arıyor", "Kalacak Oda Arıyor", "Ev Arkadaşı Arıyor", "Oda Arkadaşı Arıyor", "Aramıyor"};
        departments = new String[]{"Bölüm Seçiniz", "Bilgisayar Mühendisliği", "Bilgisayar ve Öğretim Teknolojileri Öğretmenliği", "Biyomedikal Mühendisliği", "Biyomühendislik", "Elektrik Mühendisliği", "Elektronik ve Haberleşme Mühendisliği", "Endüstri Mühendisliği", "Fen Bilgisi Öğretmenliği", "Fizik", "Fotoğraf ve Video", "Fransızca Mütercim ve Tercümanlık", "Gemi Makineleri İşletme Mühendisliği", "Gemi İnşaatı ve Gemi Makineleri Mühendisliği"
                , "Gıda Mühendisliği", "Harita Mühendisliği", "Havacılık Elektrik ve Elektroniği", "Kimya", "Kimya Mühendisliği", "Kontrol ve Otomasyon Mühendisliği", "Kültür Varlıklarını Koruma ve Onarım", "Makine Mühendisliği", "Matematik", "Matematik Mühendisliği", "Mekatronik Mühendisliği", "Metalurji ve Malzeme Mühendisliği"
                , "Mimarlık", "Moleküler Biyoloji ve Genetik", "Okul Öncesi Öğretmenliği", "Rehberlik ve Psikolojik Danışmanlık", "Sanat ve Kültür Yönetimi", "Siyaset Bilimi ve Uluslararası İlişkiler", "Sosyal Bilgiler Öğretmenliği", "Sınıf Öğretmenliği", "Türk Dili ve Edebiyatı", "Türkçe Öğretmenliği", "Çevre Mühendisliği", "İktisat", "İletişim ve Tasarımı", "İlköğretim Matematik Öğretmenliği", "İngilizce Öğretmenliği"
                , "İnşaat Mühendisliği", "İstatistik", "İşletme", "Şehir ve Bölge Planlama"};


        ArrayAdapter _distances = new ArrayAdapter(this, android.R.layout.simple_spinner_item, distances);
        ArrayAdapter _times = new ArrayAdapter(this, android.R.layout.simple_spinner_item, times);
        ArrayAdapter _classes = new ArrayAdapter(this, android.R.layout.simple_spinner_item, classes);
        ArrayAdapter _statuses = new ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses);
        ArrayAdapter _departments = new ArrayAdapter(this, android.R.layout.simple_spinner_item, departments);

        _distances.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _times.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _classes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _statuses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _departments.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinStatus.setAdapter(_statuses);
        spinClassType.setAdapter(_classes);
        spinLengthType.setAdapter(_distances);
        spinTimeType.setAdapter(_times);
        spinDepartment.setAdapter(_departments);
    }

    private void loadView(User _user) {



        spinDepartment.setSelection(Arrays.binarySearch(departments, _user.getDepartment()));
        spinClassType.setSelection(Arrays.binarySearch(classes, _user.getClassNumber()));
        spinLengthType.setSelection(Arrays.binarySearch(distances, _user.getLengthType()));
        spinTimeType.setSelection(Arrays.binarySearch(times, _user.getTimeType()));
        spinStatus.setSelection(Arrays.binarySearch(statuses, _user.getStatus()));
        emailSignUp.getEditText().setText(Helper.IsNull(_user.getMailAddress()));
        phoneNumber.getEditText().setText(Helper.IsNull(_user.getPhoneNumber()));
        time.getEditText().setText(_user.getTimeStay() + "");
        length.getEditText().setText(_user.getLength() + "");
        fullName.getEditText().setText(Helper.IsNull(_user.getNameSurname()));




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

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);

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

                    Glide.with(ProfileEditActivity.this).load(data.getData()).into(profileImage);

                    mImageUri = data.getData();
                    //profileImage.setImageURI(data.getData());
                    isPhotoSelected = true;
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    Glide.with(ProfileEditActivity.this).load(imageBitmap).into(profileImage);

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