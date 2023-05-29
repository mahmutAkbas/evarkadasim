package com.mahmutakbas.evarkadasim;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.rpc.Help;
import com.mahmutakbas.evarkadasim.Management.AuthManagement;
import com.mahmutakbas.evarkadasim.Other.Helper;
import com.mahmutakbas.evarkadasim.Other.ResultMessage;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SignInActivity extends AppCompatActivity {

    private TextInputLayout emailSignIn, passSignIn;
    private Button signInBtn;
    private TextView signINText;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailSignIn = findViewById(R.id.sing_in_email);
        passSignIn = findViewById(R.id.sing_in_password);
        signInBtn = findViewById(R.id.sing_in_btn);
        signINText = findViewById(R.id.sign_in_text);

        sharedPreferences = getSharedPreferences("HomeFriendSignIn", MODE_PRIVATE);

        AuthManagement authManagement = new AuthManagement();
        auth = FirebaseAuth.getInstance();

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog dialog = Helper.showProgressDialog(SignInActivity.this, "Lütfen bekleyiniz...");
                dialog.show();

                String email = emailSignIn.getEditText().getText().toString();
                String pass = passSignIn.getEditText().getText().toString();

                if (!pass.isEmpty() && !email.isEmpty()) {

                    CompletableFuture<ResultMessage> login = authManagement.signIn(auth, email, pass);

                    login.thenAccept(new Consumer<ResultMessage>() {
                        @Override
                        public void accept(ResultMessage message) {
                            if (message.isSuccess()) {

                                FirebaseUser currentUSer = auth.getCurrentUser();
                                if (currentUSer.isEmailVerified()) {

                                    SharedPreferences.Editor myedit = sharedPreferences.edit();

                                    myedit.putString("email", email);
                                    myedit.putString("pass", pass);
                                    myedit.commit();

                                    Helper.showToast(SignInActivity.this, "Giriş Yapılıyor.");

                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));

                                    dialog.dismiss();

                                    finish();
                                } else {
                                    Helper.showToast(SignInActivity.this, "Lütfen e-postanıza gönderdiğimiz doğrulamayı yapın.");

                                    dialog.dismiss();
                                }
                            } else {
                                Helper.showToast(SignInActivity.this, message.getMessage());

                                dialog.dismiss();
                            }
                        }
                    });

                } else {
                    Helper.showToast(SignInActivity.this, "Email ve Şifre giriniz.");

                    dialog.dismiss();
                }
            }
        });

        signINText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }
}