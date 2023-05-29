package com.mahmutakbas.evarkadasim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.mahmutakbas.evarkadasim.Management.AuthManagement;
import com.mahmutakbas.evarkadasim.Other.ResultMessage;

import java.util.function.Consumer;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("HomeFriendSignIn", MODE_PRIVATE);

        String email =  sharedPreferences.getString("email", "");
        String pass =  sharedPreferences.getString("pass", "");

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!email.isEmpty() && !pass.isEmpty()){

                    AuthManagement.signIn(auth,email,pass).thenAcceptAsync(new Consumer<ResultMessage>() {
                        @Override
                        public void accept(ResultMessage message) {
                            if(message.isSuccess()){

                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();

                            }else{

                                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                                finish();
                            }
                        }
                    });

                }else{
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    finish();
                }
            }
        },2000);
    }
}