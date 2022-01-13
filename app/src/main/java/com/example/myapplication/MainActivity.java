package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Prevalent.Prevalent;
import com.example.myapplication.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinNowButton,loginButton;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinNowButton=(Button) findViewById(R.id.main_join_now_btn);
        loginButton=(Button) findViewById(R.id.main_login_btn);
        LoadingBar=new ProgressDialog(this);


        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,loginActivity.class);
                startActivity(intent);
            }
        });
        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        if(UserPhoneKey !="" && UserPasswordKey !=""){
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){
                AllowAccess(UserPhoneKey,UserPasswordKey);
                LoadingBar.setTitle("Already logged in");
                LoadingBar.setMessage("Please wait...");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();
            }
        }

    }


    private void AllowAccess(String phone, String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("users").child(phone).exists()){
                    Users userData=snapshot.child("users").child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "logged in successfully....", Toast.LENGTH_SHORT).show();
                            LoadingBar.dismiss();

                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.currentOnlineUser=userData;
                            startActivity(intent);
                        }
                        else{
                            LoadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "password is incorrect", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                else {
                    Toast.makeText(MainActivity.this, "Account with this"+phone+"number do not exists ", Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "you need to create a new account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}