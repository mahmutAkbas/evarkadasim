package com.mahmutakbas.evarkadasim;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahmutakbas.evarkadasim.Management.UserManagement;
import com.mahmutakbas.evarkadasim.Models.User;
import com.mahmutakbas.evarkadasim.Other.Helper;
import com.mahmutakbas.evarkadasim.Other.ResultMessage;
import com.mahmutakbas.evarkadasim.Other.UserAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ListStudentFragment extends Fragment implements UserAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private List<User> userList;
    private UserAdapter userAdapter;
    private FirebaseFirestore fireStore;
    private FirebaseAuth auth;

    public ListStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_student, container, false);

        recyclerView = view.findViewById(R.id.recyclerUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();

        fireStore = FirebaseFirestore.getInstance();

        UserManagement userManagement = new UserManagement();
        CompletableFuture<ResultMessage<List<User>>> result = userManagement.getUsers(fireStore, auth.getCurrentUser().getUid());

        result.thenAccept(new Consumer<ResultMessage<List<User>>>() {
            @Override
            public void accept(ResultMessage<List<User>> listResultMessage) {

                userList = listResultMessage.getData();
                userAdapter = new UserAdapter(userList);
                userAdapter.setOnItemClickListener(ListStudentFragment.this);

                recyclerView.setAdapter(userAdapter);
            }
        });
        return view;
    }

    @Override
    public void onItemClick(int position) {
        Helper.showToast(getContext(), userList.get(position).getNameSurname());
    }
}
