package com.mahmutakbas.evarkadasim;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mahmutakbas.evarkadasim.Management.AuthManagement;
import com.mahmutakbas.evarkadasim.Other.Helper;
import com.mahmutakbas.evarkadasim.Other.ResultMessage;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ProfilePasswordResetActivity extends AppCompatActivity {

    private TextInputLayout passOld, passNew, passNewAgain;
    private Button btnEdit;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_password_reset);


        passOld = findViewById(R.id.update_old_password);
        passNew = findViewById(R.id.update_new_password);
        passNewAgain = findViewById(R.id.update_new_password_again);

        btnEdit = findViewById(R.id.update_btn);

        auth = FirebaseAuth.getInstance();

        ProgressDialog dialog = Helper.showProgressDialog(ProfilePasswordResetActivity.this, "Şfreniz Güncelleniyor.\nLütfen bekleyiniz...");


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String oldPass = passOld.getEditText().getText().toString();
                String newPass = passNew.getEditText().getText().toString();
                String newAgainPass = passNewAgain.getEditText().getText().toString();

                if (!oldPass.isEmpty() && !newPass.isEmpty() && !newAgainPass.isEmpty()) {

                    if (newPass.equals(newAgainPass)) {
                        CompletableFuture<ResultMessage> resultChangedPassword = AuthManagement.passwordChange(auth, oldPass, newPass);
                        resultChangedPassword.thenAccept(new Consumer<ResultMessage>() {
                            @Override
                            public void accept(ResultMessage resultMessage) {
                                if (resultMessage.isSuccess()) {
                                    Helper.showToast(ProfilePasswordResetActivity.this, resultMessage.getMessage());
                                    dialog.dismiss();
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Helper.showToast(ProfilePasswordResetActivity.this, resultMessage.getMessage());
                                    dialog.dismiss();
                                }
                            }
                        });
                    } else {
                        Helper.showToast(ProfilePasswordResetActivity.this, "Yeni şifreler aynı değil.");
                        dialog.dismiss();
                    }
                } else {
                    Helper.showToast(ProfilePasswordResetActivity.this, "Lütfen boş alanları doldurunuz!");
                    dialog.dismiss();
                }
            }
        });
    }
}