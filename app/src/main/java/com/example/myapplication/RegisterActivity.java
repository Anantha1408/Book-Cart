package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button CreateAccountButton;
    private EditText InputName,InputPhoneNumber,InputPassword;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        CreateAccountButton=(Button) findViewById(R.id.register_btn);
        InputName=(EditText) findViewById(R.id.register_username_input);
        InputPassword=(EditText) findViewById(R.id.register_password_input);
        InputPhoneNumber=(EditText) findViewById(R.id.register_phone_number_input);
        LoadingBar=new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount()
    {
        String name=InputName.getText().toString();
        String phone=InputPhoneNumber.getText().toString();
        String password=InputPassword.getText().toString();
        if(name.length()==0){
            Toast.makeText(this,"please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if(name.length()<=5 ){
            Toast.makeText(this,"please enter name with moe than 6 characters", Toast.LENGTH_SHORT).show();
        }
        else if(!name.matches("[a-zA-Z][a-zA-Z ]+[a-zA-Z]$")){
            Toast.makeText(this,"please enter only alphabets for your name", Toast.LENGTH_SHORT).show();
        }
        else if(name.length()>30){
            Toast.makeText(this,"please enter  valid name with less than 30 characters", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone) ){
            Toast.makeText(this,"please enter your  phone number", Toast.LENGTH_SHORT).show();
        }
        else if(!phone.matches("[6-9]{1}[0-9]{9}")){
            Toast.makeText(this,"please enter your valid phone number", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please enter your password", Toast.LENGTH_SHORT).show();
        }
        else if(!password.matches("^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,}$") ){
            Toast.makeText(this,"please enter vaild password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LoadingBar.setTitle("Create Account");
            LoadingBar.setMessage("Please wait,while we are checking the credentials.");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            validatephoneNumber(name,phone,password);
        }
    }

    private void validatephoneNumber(String name, String phone, String password) {
final DatabaseReference RootRef;
RootRef= FirebaseDatabase.getInstance().getReference();
RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (!(snapshot.child("users").child(phone).exists())){
            HashMap<String,Object> userdatamap=new HashMap<>();
            userdatamap.put("phone",phone);
            userdatamap.put("password",password);
            userdatamap.put("name",name);
            RootRef.child("users").child(phone).updateChildren(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Congratulations,your account has been created", Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();
                        Intent intent=new Intent(RegisterActivity.this,loginActivity.class);
                        startActivity(intent);
                    }
                    else {
                        LoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Network Error:Please Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else{
            Toast.makeText(RegisterActivity.this, "this username already exists", Toast.LENGTH_SHORT).show();
            LoadingBar.dismiss();
            Toast.makeText(RegisterActivity.this, "Please try again using another number", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
    }
}