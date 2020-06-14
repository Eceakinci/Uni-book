package com.loory.unibook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    //This is the variables that we get from user
    EditText emailText, passwordText, fullname,username;
    Button signUpIn;

    //Firebase Authentication and database for create users and store their data
    private FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.email);
        username = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        fullname = findViewById(R.id.fullname);
        signUpIn = findViewById(R.id.signUpIn);

        signUpIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_fullname = fullname.getText().toString();
                String str_email = emailText.getText().toString();
                String str_password = passwordText.getText().toString();
                String str_username = username.getText().toString();

                //if info texts is empty then show toast message
                if (TextUtils.isEmpty(str_fullname)
                        || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)
                        || TextUtils.isEmpty(str_username)) {
                    Toast.makeText(SignUpActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Password must have 6 chars", Toast.LENGTH_SHORT).show();
                } else {
                    addUser(str_email,str_username ,str_fullname,str_password);
                }
            }
        });
    }


    private void addUser(String email, final String username , final String fullname , String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userid = firebaseUser.getUid();


                    //add user to database
                    //Create file and put user's infos as an object
                    //but it's not working
                    //it can't create users file and cant put user infos
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                    HashMap<String,Object> userFile = new HashMap<>();
                    userFile.put("id",userid);
                    userFile.put("username", username);
                    userFile.put("fullname",fullname);
                    //this url is default user image's url

                    reference.setValue(userFile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //if authentication is successful then to go feed activity(home fragment)
                                //and also clear all tasks in behind(like sign up and sign in page)
                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                } else{
                    Toast.makeText(SignUpActivity.this,"You can't register with this email or password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
