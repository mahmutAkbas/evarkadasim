package com.mahmutakbas.evarkadasim;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mahmutakbas.evarkadasim.Management.AuthManagement;
import com.mahmutakbas.evarkadasim.Management.UserManagement;
import com.mahmutakbas.evarkadasim.Models.User;
import com.mahmutakbas.evarkadasim.Other.Helper;
import com.mahmutakbas.evarkadasim.Other.ResultMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonSettingFragment extends Fragment {


    private TextView emailSignUp, fullName, phoneNumber, time, length, classType, status, department;
    private CircleImageView profileImage;

    private Button editBtn, passwordResetBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore fireStore;
    private StorageReference storageReference;
    private String Uid = "";
    private Uri mImageUri = null;
    private User user;
    private UserManagement userManagement;
    private static final int REQUEST_CODE = 1;

    public PersonSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_setting, container, false);

        department = view.findViewById(R.id.person_setting_department);
        classType = view.findViewById(R.id.person_setting_class);
        status = view.findViewById(R.id.person_setting_status);
        emailSignUp = view.findViewById(R.id.person_setting_email);
        phoneNumber = view.findViewById(R.id.person_setting_phoneNumber);
        time = view.findViewById(R.id.person_setting_time);
        length = view.findViewById(R.id.person_setting_length);
        fullName = view.findViewById(R.id.person_setting_nameSurname);
        profileImage=view.findViewById(R.id.person_setting_circleImageView);

        editBtn=view.findViewById(R.id.person_setting_btnEdit);
        passwordResetBtn=view.findViewById(R.id.person_setting_btnPasswordReset);

        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        fireStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        user = new User();

        userManagement = new UserManagement(user);

        loadData();

        passwordResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), ProfilePasswordResetActivity.class), REQUEST_CODE);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), ProfileEditActivity.class), REQUEST_CODE);
            }
        });

        return view;
    }

    private void loadView(User _user) {
        department.setText( _user.getDepartment());
        classType.setText(_user.getClassNumber());
        status.setText(_user.getStatus());
        emailSignUp.setText(Helper.IsNull(_user.getMailAddress()));
        phoneNumber.setText(Helper.IsNull(_user.getPhoneNumber()));
        time.setText(_user.getTimeStay() + " "+_user.getTimeType());
        length.setText(_user.getLength() +" "+ _user.getLengthType());
        fullName.setText(Helper.IsNull(_user.getNameSurname()));
    }

    private  void  loadData(){
        ProgressDialog dialog = Helper.showProgressDialog(getContext(), "Profiliniz Yükleniyor...");

        dialog.show();

        CompletableFuture<ResultMessage<User>> resultUser = userManagement.getUser(fireStore, Uid);
        resultUser.thenAccept(userResultMessage -> {
            if (userResultMessage.isSuccess()) {

                user = userResultMessage.getData();
                loadView(user);

                CompletableFuture<ResultMessage<Uri>> resultImage = AuthManagement.getFile(storageReference, Uid);
                resultImage.thenAccept(new Consumer<ResultMessage<Uri>>() {
                    @Override
                    public void accept(ResultMessage<Uri> uriResultMessage) {
                        if (uriResultMessage.isSuccess()) {

                            Glide.with(getActivity()).load(uriResultMessage.getData()).into(profileImage);
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            Helper.showToast(getActivity(), uriResultMessage.getMessage());
                        }
                    }
                });
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {

            if (resultCode == getActivity().RESULT_OK) {
                loadData();
            }
        }
    }
}