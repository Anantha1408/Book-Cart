package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderCheckActivity extends AppCompatActivity {
    public TextView orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_check);
        orderStatus=findViewById(R.id.order_status);
        DatabaseReference orderRef;
        orderRef= FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingState=snapshot.child("state").getValue().toString();
                    String userName=snapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {
                        orderStatus.setText("Dear "+userName+"\nyour order has been shipped successfully");
                    }
                    else if(shippingState.equals(("not shipped")))
                    {
                        orderStatus.setText("your order has not been dispatched");


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}