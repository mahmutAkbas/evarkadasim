package com.mahmutakbas.evarkadasim.Management;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahmutakbas.evarkadasim.Models.User;
import com.mahmutakbas.evarkadasim.Other.Helper;
import com.mahmutakbas.evarkadasim.Other.ResultMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UserManagement {

    private static final String TABLE_USER = "users";

    private User user;

    public UserManagement(User user) {
        this.user = user;
    }

    public UserManagement() {

    }

    private HashMap convertToHashMap() {

        HashMap userMap = new HashMap();

        userMap.put("userId", Helper.IsNull(user.getUserId()));
        userMap.put("nameSurname", Helper.IsNull(user.getNameSurname()));
        userMap.put("department", Helper.IsNull(user.getDepartment()));
        userMap.put("userClass", Helper.IsNull(user.getClassNumber()));
        userMap.put("status", Helper.IsNull(user.getStatus()));
        userMap.put("time", user.getTimeStay());
        userMap.put("timeType", Helper.IsNull(user.getTimeType()));
        userMap.put("mailAddress", Helper.IsNull(user.getMailAddress()));
        userMap.put("phoneNumber", Helper.IsNull(user.getPhoneNumber()));
        userMap.put("photoUrl", Helper.IsNull(user.getPhotoUrl()));
        userMap.put("length", user.getLength());
        userMap.put("lengthType", Helper.IsNull(user.getLengthType()));
        return userMap;
    }

    public CompletableFuture<ResultMessage<User>> getUser(FirebaseFirestore fireStore, String userId) {
        CompletableFuture<ResultMessage<User>> result = new CompletableFuture<>();
        user = new User();
        fireStore.collection(TABLE_USER).document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        DocumentSnapshot itDocumentSnapshot = task.getResult();
                        user.setUserId(itDocumentSnapshot.getString("userId"));
                        user.setNameSurname(itDocumentSnapshot.getString("nameSurname"));
                        user.setDepartment(itDocumentSnapshot.getString("department"));
                        user.setClassNumber(itDocumentSnapshot.getString("userClass"));
                        user.setStatus(itDocumentSnapshot.getString("status"));
                        user.setTimeStay(itDocumentSnapshot.get("time", Integer.class));
                        user.setTimeType(itDocumentSnapshot.getString("timeType"));
                        user.setMailAddress(itDocumentSnapshot.getString("mailAddress"));
                        user.setPhoneNumber(itDocumentSnapshot.getString("phoneNumber"));
                        user.setPhotoUrl(itDocumentSnapshot.getString("photoUrl"));
                        user.setLength(itDocumentSnapshot.get("length", Integer.class));
                        user.setLengthType(itDocumentSnapshot.getString("lengthType"));

                        result.complete(new ResultMessage<User>(true, "Başarılı", user));
                    } else {

                        result.complete(new ResultMessage<User>(false, "User bulunamadı", null));
                    }
                } else {
                    result.completeExceptionally(task.getException());
                }

            }
        });

        return result;
    }

    public CompletableFuture<ResultMessage> setUser(FirebaseFirestore fireStore, String userId) {

        CompletableFuture<ResultMessage> result = new CompletableFuture<>();

        fireStore.collection(TABLE_USER).document(userId).set(convertToHashMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    result.complete(new ResultMessage(true, "Başarılı", null));

                } else {
                    result.complete(new ResultMessage(false, task.getException().getMessage(), null));
                }
            }
        });

        return result;
    }

    public CompletableFuture<ResultMessage<List<User>>> getUsers(FirebaseFirestore fireStore, String uId) {
        CompletableFuture<ResultMessage<List<User>>> result = new CompletableFuture<>();

        fireStore.collection(TABLE_USER).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user = new User();

                        user.setUserId(document.getString("userId"));
                        user.setNameSurname(document.getString("nameSurname"));
                        user.setDepartment(document.getString("department"));
                        user.setClassNumber(document.getString("userClass"));
                        user.setStatus(document.getString("status"));
                        user.setTimeStay(document.get("time", Integer.class));
                        user.setTimeType(document.getString("timeType"));
                        user.setMailAddress(document.getString("mailAddress"));
                        user.setPhoneNumber(document.getString("phoneNumber"));
                        user.setPhotoUrl(document.getString("photoUrl"));
                        user.setLength(document.get("length", Integer.class));
                        user.setLengthType(document.getString("lengthType"));

                        userList.add(user);
                    }

                    result.complete(new ResultMessage<>(true, "Başarılı", userList));
                } else {
                    result.completeExceptionally(task.getException());
                }
            }
        });

        return result;
    }
}
