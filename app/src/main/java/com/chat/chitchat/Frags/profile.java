package com.chat.chitchat.Frags;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.ResourceLoader;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class profile extends Fragment {
    View view;

    FirebaseAuth auth;
    String userUid, userName, userEmail, userImgUrl;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Button update;
    EditText name;

    ProgressDialog dialog;

    Uri imgUri;
    ActivityResultLauncher<String> imgContent;
    ImageView setImage;
    CircleImageView avatar,avatar2;
    boolean isChanging = false;

    Bitmap imgBitmap = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        update = view.findViewById(R.id.update);
        name = view.findViewById(R.id.name);
        avatar = view.findViewById(R.id.avatar);
        avatar2 = view.findViewById(R.id.avatar2);
        setImage = view.findViewById(R.id.setImage);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("loading...");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        userUid = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        getUserInfo();


        imgContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                //avatar2.setImageURI(result);
                imgUri = result;
                update.setText("wait...");
                Glide.with(getContext())
                        .asBitmap().load(imgUri).override(200,200)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                imgBitmap = resource;
                                update.setText("UPLOAD");
                                avatar2.setImageBitmap(imgBitmap);
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
                getImage();
            }
        });
        avatar2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getImage();
            }
        });
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });






        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()){
                    dialog.setMessage("updating...");
                    dialog.setCancelable(false);
                    dialog.show();
                    uploadData();
                }
            }
        });





        return view;
    }
    public boolean validate(){
        String str = name.getText().toString();
        if (str.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }

    private void getImage() {
        avatar.setVisibility(View.GONE);
        avatar2.setVisibility(View.VISIBLE);
        imgContent.launch("image/*");
    }


    private void getUserInfo() {
        dialog.show();
        database.getReference().child("users")
                .child(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            userName = snapshot.child("name").getValue().toString();
                            userEmail = snapshot.child("email").getValue().toString();
                            userImgUrl = snapshot.child("image").getValue().toString();

                        }
                        else {
                            userName = "No User Found!";
                            userEmail = "Not Found";
                            userImgUrl = "Not Found";
                        }
                        setUser();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void updateImage(String link){
        Glide.with(getContext()).load(link)
                .placeholder(R.drawable.avatar)
                .into(avatar);
    }

    private void setUser() {
        name.setText(userName);
        updateImage(userImgUrl);
    }


    private void uploadData() {
        if(imgUri != null){

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
            byte[] bytes = baos.toByteArray();


            StorageReference storageReference = storage.getReference().child("Profiles")
                    .child(userUid);
            storageReference.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                userImgUrl = uri.toString();
                                updateImage(userImgUrl);
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
        String username = name.getText().toString();
        String email = userEmail;
        User user = new User(uid,username,email,userImgUrl);
        database.getReference().child("users")
                .child(uid)
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Profile Updated !", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
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



}