package com.chat.chitchat.Frags;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chat.chitchat.Adaptor.GroupAdaptor;
import com.chat.chitchat.Adaptor.usersAdaptor;
import com.chat.chitchat.Models.Group;
import com.chat.chitchat.Models.User;
import com.chat.chitchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class groups_chat extends Fragment {
    View view;
    ArrayList<Group> groups;
    GroupAdaptor groupAdaptor;
    RecyclerView recyclerView;
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String userUid, userName;
    String groupImgUrl = "No Url";

    ImageView btnAdd;
    ConstraintLayout groupBox;
    TextView create, cancel;
    EditText input;

    Uri imgUri;
    ActivityResultLauncher<String> imgContent;
    Bitmap imgBitmap = null;
    CircleImageView avatar,avatar2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_groups_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        btnAdd = view.findViewById(R.id.btnADD);
        groupBox = view.findViewById(R.id.groupBox);
        create = view.findViewById(R.id.create);
        cancel = view.findViewById(R.id.cancel);
        avatar = view.findViewById(R.id.imgGroup);
        input = view.findViewById(R.id.input);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("showing... groups");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        userUid = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        groups = new ArrayList<>();
        groupAdaptor = new GroupAdaptor(getContext(), groups);
        recyclerView.setAdapter(groupAdaptor);
        dialog.show();

        getUserName();

        database.getReference().child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groups.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Group group = snapshot1.getValue(Group.class);
                    groups.add(group);

                }
                groupAdaptor.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        imgContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                //avatar2.setImageURI(result);
                imgUri = result;
                create.setText("wait...");
                Glide.with(getContext())
                        .asBitmap().load(imgUri).override(200,200)
                        .centerCrop()
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                imgBitmap = resource;
                                create.setText("CREATE");
                                avatar.setImageBitmap(imgBitmap);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });


            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgContent.launch("image/*");
            }
        });











        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupBox.setVisibility(View.VISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             groupBox.setVisibility(View.GONE);
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()){
                    dialog.setMessage("creating...");
                    dialog.setCancelable(false);
                    dialog.show();
                    groupBox.setVisibility(View.GONE);
                    uploadData();
                }
            }
        });






        return view;
    }

    private void getUserName() {
        dialog.show();
        database.getReference().child("users")
                .child(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            userName = snapshot.child("name").getValue().toString();

                        }
                        else {
                            userName = "No User Found!";
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public boolean validate(){
        String str = input.getText().toString();
        if (str.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }


    private void uploadData() {
        if(imgUri != null){

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
            byte[] bytes = baos.toByteArray();


            String time = System.currentTimeMillis() + "";
            StorageReference storageReference = storage.getReference().child("Groups")
                    .child(time);
            storageReference.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                groupImgUrl = uri.toString();
                                uploadDataBase();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        Toast.makeText(getActivity(), "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            uploadDataBase();
        }
    }








    private void uploadDataBase() {
        String uid = userUid;
        String name = input.getText().toString();
        Group group = new Group(name,groupImgUrl, userName, System.currentTimeMillis()+"");
        database.getReference().child("groups")
                .push()
                .setValue(group)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Group Created !", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            resetGroup();
                        }
                        else {
                            Toast.makeText(getActivity(), "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void resetGroup() {
        avatar.setImageResource(R.drawable.avatar);
        input.setText("");
    }
}