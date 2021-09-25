package com.chat.chitchat.Frags;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.chitchat.Activities.MainActivity;
import com.chat.chitchat.R;
import com.chat.chitchat.Activities.forgetPass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class login extends Fragment {
    View view;
    String stremail, strpass;
    EditText email, pass;
    Button login;
    TextView forget;
    private FirebaseAuth auth;
    float v = 0;
    ConstraintLayout progressBar;
    ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_login, container, false);
         startViews();
         dialog = new ProgressDialog(getContext());
         dialog.setMessage("Please wait!");
         dialog.setCancelable(false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForget();
            }
        });


        login.setTranslationY(300);
        login.setAlpha(v);
        login.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();




         return view;
    }

    private void logIn() {
        if(validate()){
            dialog.show();
            auth.signInWithEmailAndPassword(stremail,strpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(), "Welcome ", Toast.LENGTH_SHORT).show();
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
    }
    private void openMain() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }
    private void openForget() {
        startActivity(new Intent(getActivity(), forgetPass.class));
    }

    private boolean validate(){
        stremail = email.getText().toString();
        strpass = pass.getText().toString();
        if(stremail.isEmpty() || strpass.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }


    public void startViews(){
        email = view.findViewById(R.id.email);
        pass = view.findViewById(R.id.pass);
        login = view.findViewById(R.id.login);
        forget = view.findViewById(R.id.frgtpass);
        auth = FirebaseAuth.getInstance();
        progressBar = view.findViewById(R.id.progress_bar);
    }
}