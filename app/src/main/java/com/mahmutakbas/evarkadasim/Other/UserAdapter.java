package com.mahmutakbas.evarkadasim.Other;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mahmutakbas.evarkadasim.ListStudentFragment;
import com.mahmutakbas.evarkadasim.Management.AuthManagement;
import com.mahmutakbas.evarkadasim.Models.User;
import com.mahmutakbas.evarkadasim.R;
import com.mahmutakbas.evarkadasim.SignUpActivity;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private OnItemClickListener onItemClickListener;

    private StorageReference storageReference;

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);

        return new UserViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.classs.setText(user.getClassNumber());
        holder.status.setText(user.getStatus());
        holder.department.setText(user.getDepartment());
        holder.length.setText(user.getLength()+ " "+ user.getLengthType());
        holder.time.setText(user.getTimeStay()+ " "+ user.getTimeType());
        holder.nameSurname.setText(user.getNameSurname());


        CompletableFuture<ResultMessage<Uri>> resultImage = AuthManagement.getFile(storageReference, user.getUserId());

        resultImage.thenAccept(new Consumer<ResultMessage<Uri>>() {
            @Override
            public void accept(ResultMessage<Uri> uriResultMessage) {
                if (uriResultMessage.isSuccess()) {
                    holder.bind(uriResultMessage.getData());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameSurname, status, department, classs, length,time;
        CircleImageView circleImageView;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameSurname = itemView.findViewById(R.id.recyclerItem_nameSurname);
            status = itemView.findViewById(R.id.recyclerItem_status);
            department = itemView.findViewById(R.id.recyclerItem_department);
            classs = itemView.findViewById(R.id.recyclerItem_class);
            time = itemView.findViewById(R.id.recyclerItem_time);
            length = itemView.findViewById(R.id.recyclerItem_length);

            circleImageView = itemView.findViewById(R.id.recyclerItem_circleImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
        }

        public void bind(Uri imageUrl) {
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .into(circleImageView);
        }
    }

}
