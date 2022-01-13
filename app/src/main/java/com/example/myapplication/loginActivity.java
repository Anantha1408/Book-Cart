package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Admin.AdminCategoryActivity;
import com.example.myapplication.Prevalent.Prevalent;
import com.example.myapplication.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {
    private EditText InputNumber,InputPassword;
    private Button LoginButton;
    private ProgressDialog LoadingBar;
    private String parentDbName="users";
    private CheckBox chkBoxRememberMe;
    private TextView AdminLink,NotAdminLink,ForgetPasswordLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton=(Button) findViewById(R.id.login_btn);
        InputPassword=(EditText) findViewById(R.id.login_password_input);
        InputNumber=(EditText) findViewById(R.id.login_phone_number_input);
        LoadingBar=new ProgressDialog(this);
        AdminLink=(TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink=(TextView) findViewById(R.id.not_admin_panel_link);
        ForgetPasswordLink=findViewById(R.id.forgot_password_link);

        chkBoxRememberMe=(CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });
        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                chkBoxRememberMe.setVisibility(View.GONE);
                ForgetPasswordLink.setVisibility(View.GONE);
                parentDbName="Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                chkBoxRememberMe.setVisibility(View.VISIBLE);
                ForgetPasswordLink.setVisibility(View.VISIBLE);
                parentDbName="users";

            }
        });
    }

    private void LoginUser() {
        String phone=InputNumber.getText().toString();
        String password=InputPassword.getText().toString();

         if(TextUtils.isEmpty(phone) && phone.length()<10){
            Toast.makeText(this,"please enter valid phone number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please enter your password", Toast.LENGTH_SHORT).show();
        }
        else{
             LoadingBar.setTitle("Login Account");
             LoadingBar.setMessage("Please wait,while we are checking the credentials.");
             LoadingBar.setCanceledOnTouchOutside(false);
             LoadingBar.show();

             AllowAccesToAccount(phone,password);
         }
    }

    private void AllowAccesToAccount(String phone, String password) {

        if (chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);

        }


        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()){
                    Users userData=snapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            if (parentDbName.equals("Admins")){
                                Toast.makeText(loginActivity.this, "welcome admin,you logged in successfully....", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();

                                Intent intent=new Intent(loginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("users")){
                                Toast.makeText(loginActivity.this, "logged in successfully....", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();

                                Intent intent=new Intent(loginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUser=userData;
                                startActivity(intent);


                            }
                        }
                        else{
                            LoadingBar.dismiss();
                            Toast.makeText(loginActivity.this, "password is incorrect", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                else {
                    Toast.makeText(loginActivity.this, "Account with this"+phone+"number do not exists ", Toast.LENGTH_SHORT).show();
                   LoadingBar.dismiss();
                    Toast.makeText(loginActivity.this, "you need to create a new account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}