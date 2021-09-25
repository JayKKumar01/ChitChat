package com.chat.chitchat.Frags;

import android.app.ProgressDialog;
import android.content.Intent;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chat.chitchat.Activities.MainActivity;
import com.chat.chitchat.R;
import com.chat.chitchat.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class signup extends Fragment {

    private EditText email, name, pass;
    private Button signup;
    View view;
    private FirebaseAuth auth;
    private DatabaseReference ref;
    private DatabaseReference dRef;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    String stremail, strname, strpass,strimgUrl;

    ProgressDialog dialog;
    Bitmap imgBitmap = null;

    float v = 0;
    ConstraintLayout progressBar;

    CircleImageView avatar;
    Uri imgUri;
    ActivityResultLauncher<String> imgContent;


    private void openMain() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_signup, container, false);
        startViews();
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Updating... Please wait!");
        dialog.setCancelable(false);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        signup.setTranslationY(300);
        signup.setAlpha(v);
        signup.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        imgContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imgUri = result;
                signup.setText("wait...");
                Glide.with(getContext())
                        .asBitmap().load(imgUri).override(200,200)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                imgBitmap = resource;
                                signup.setText("SIGNUP");
                                avatar.setImageBitmap(imgBitmap);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        });

        return view;
    }



    private void getImage() {
        imgContent.launch("image/*");
    }

    private void signUp() {
        if(validate()){
            dialog.show();
            createUser();
        }

    }

    private void createUser() {
        auth.createUserWithEmailAndPassword(stremail,strpass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            uploadData();
                        }
                        else{
                            Toast.makeText(getActivity(), "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
    }

    private void uploadData() {
        if(imgUri != null){

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
            byte[] bytes = baos.toByteArray();

            StorageReference storageReference = storage.getReference().child("Profiles")
                    .child(auth.getUid());
            storageReference.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                strimgUrl = uri.toString();
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
            strimgUrl = "No Image";
            uploadDataBase();
        }
    }

    private void uploadDataBase() {
        String uid = auth.getUid();
        String name = strname;
        String email = stremail;
        User user = new User(uid,name,email,strimgUrl);
        database.getReference().child("users")
                .child(uid)
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Welcome "+strname, Toast.LENGTH_SHORT).show();
                            openMain();
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

    private boolean validate(){
        stremail = email.getText().toString();
        strname = name.getText().toString();
        strpass = pass.getText().toString();
        if(stremail.isEmpty() || strname.isEmpty() || strpass.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }


    public void startViews(){
        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
        pass = view.findViewById(R.id.pass);
        signup = view.findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        progressBar = view.findViewById(R.id.progress_bar);
        avatar = view.findViewById(R.id.avatar);
    }

}