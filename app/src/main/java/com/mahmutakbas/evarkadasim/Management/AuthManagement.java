package com.mahmutakbas.evarkadasim.Management;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.data.StreamAssetPathFetcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mahmutakbas.evarkadasim.Models.User;
import com.mahmutakbas.evarkadasim.Other.ResultMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class AuthManagement {

    final static String PROFILE_PIC = "Profile_pics";

    public static CompletableFuture<ResultMessage> signIn(FirebaseAuth firebaseAuth, String email, String pass) {

        CompletableFuture<ResultMessage> result = new CompletableFuture<>();

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    result.complete(new ResultMessage(true, firebaseAuth.getCurrentUser().getUid()));
                } else {
                    result.complete(new ResultMessage(false, task.getException().getMessage()));
                }
            }
        });
        return result;
    }

    public static CompletableFuture<ResultMessage> signUp(FirebaseAuth firebaseAuth, String email, String pass) {

        CompletableFuture<ResultMessage> result = new CompletableFuture<>();

        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    result.complete(new ResultMessage(true, firebaseAuth.getCurrentUser().getUid()));

                } else {

                    result.complete(new ResultMessage(false, task.getException().getMessage()));

                }

            }
        });
        return result;
    }

    public static CompletableFuture<ResultMessage> passwordChange(FirebaseAuth firebaseAuth, String oldPass, String newPass) {

        CompletableFuture<ResultMessage> result = new CompletableFuture<>();

        String email = firebaseAuth.getCurrentUser().getEmail();

        CompletableFuture<ResultMessage> resultLogin = signIn(firebaseAuth, email, oldPass);
        resultLogin.thenAccept(new Consumer<ResultMessage>() {
            @Override
            public void accept(ResultMessage resultMessage) {

                if (resultMessage.isSuccess()) {

                    firebaseAuth.getCurrentUser().updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                result.complete(new ResultMessage(true, "Şifre güncellendi."));
                            } else {
                                result.complete(new ResultMessage(false, task.getException().getMessage()));
                            }
                        }
                    });
                }else{
                    result.complete(new ResultMessage(false, "Yanlış şifre girdiniz"));
                }
            }
        });

        return result;
    }

    public static CompletableFuture<ResultMessage> putFile(StorageReference storageReference, Uri image, String userId) {

        CompletableFuture<ResultMessage> result = new CompletableFuture<>();

        StorageReference imageRef = storageReference.child(PROFILE_PIC).child(userId + ".jpg");

        imageRef.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    result.complete(new ResultMessage(true, "Resim başarıyla kayıt edildi."));
                } else {
                    result.complete(new ResultMessage(false, task.getException().getMessage()));
                }
            }
        });
        return result;
    }

    public static CompletableFuture<ResultMessage<Uri>> getFile(StorageReference storageReference, String userId) {

        CompletableFuture<ResultMessage<Uri>> result = new CompletableFuture<>();

        StorageReference imageRef = storageReference.child(PROFILE_PIC).child(userId + ".jpg");

        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    result.complete(new ResultMessage<Uri>(true, "Başarılı", task.getResult()));
                } else {
                    result.complete(new ResultMessage<Uri>(false, task.getException().getMessage(), null));
                }
            }
        });

        return result;
    }
}
