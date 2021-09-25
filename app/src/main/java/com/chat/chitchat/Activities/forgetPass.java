package com.chat.chitchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chat.chitchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgetPass extends AppCompatActivity {
    private FirebaseAuth auth;
    EditText email;
    Button forget;
    private String stremail;
    ConstraintLayout progressBar;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        forget = findViewById(R.id.forget);
        progressBar = findViewById(R.id.progress_bar);
        dialog = new ProgressDialog(forgetPass.this);
        dialog.setMessage("Please wait!");
        dialog.setCancelable(false);
    }

    public void forgetPass(View view){
        if (validate()){
            dialog.show();
            auth.sendPasswordResetEmail(stremail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(forgetPass.this, "Link sent !\nPlease Check your Email !", Toast.LENGTH_SHORT).show();
                            openAccount();
                            dialog.dismiss();
                            }
                            else{
                                Toast.makeText(forgetPass.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
        }


    }

    private void openAccount() {
        startActivity(new Intent(forgetPass.this, account.class));
        finish();
    }

    private boolean validate(){
        stremail = email.getText().toString();
        if(stremail.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }
}