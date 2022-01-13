package com.example.myapplication.Admin;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.AdminOders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity
{
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList=findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOders> options=
                new FirebaseRecyclerOptions.Builder<AdminOders>()
                .setQuery(ordersRef,AdminOders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOders,AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder Holder, @SuppressLint("RecyclerView") int pos, @NonNull AdminOders model) {
                      Holder.userName.setText("Name:"+model.getName());
                      Holder.userPhoneNumber.setText("Phone Number:"+model.getPhone());
                      Holder.userTotalPrice.setText("Total Amount :"+model.getTotalAmount());
                      Holder.userDateTime.setText("Order At:"+model.getDate() + " "+model.getTime());
                    //int a= Holder.getBindingAdapterPosition();
                      Holder.userShippingAddress.setText("Shipping Address:"+model.getAddress()+", " +model.getCity());
                        Holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          String uID=getRef(pos).getKey();
                         // Toast.makeText(AdminNewOrdersActivity.this, String.valueOf(uID), Toast.LENGTH_LONG).show();

                          Intent intent=new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                          intent.putExtra("uid",uID);
                          startActivity(intent);
                      }
                  });
                        Holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]=new CharSequence[]{
                                        "Yes",
                                        "No"
                                };
                                AlertDialog.Builder builder=new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Have you shipped this order products ?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which==0)
                                        {
                                            String uID=getRef(pos).getKey();




                                            RemoveOrder(uID);
                                        }
                                        else{
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
//        FirebaseRecyclerAdapter<AdminOders,AdminOrdersViewHolder> adapter=new FirebaseRecyclerAdapter<AdminOders, AdminOrdersViewHolder>() {
//            @Override
//            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder Holder, int i, @NonNull AdminOders model ) {
//                Holder.userName.setText("Name:"+model.getName());
//                      Holder.userPhoneNumber.setText("Phone Number:"+model.getPhone());
//                      Holder.userTotalPrice.setText("Total Amount :"+model.getTotalAmount());
//                      Holder.userDateTime.setText("Order At:"+model.getDate() + " "+model.getTime());
////                    int a= Holder.getBindingAdapterPosition();
//                      Holder.userShippingAddress.setText("Shipping Address:"+model.getAddress()+", " +model.getCity());
//
//                      Holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
//                          @Override
//                          public void onClick(View v) {
//                              String uid=getRef(Holder.getLayoutPosition()).getKey();
//                          }
//                      });
//            }
//
//            @NonNull
//            @Override
//            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                return null;
//            }
//        };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }




    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{
        public TextView userName,userPhoneNumber,userTotalPrice,userDateTime,userShippingAddress;
        public Button ShowOrdersBtn;


        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.order_user_name);
            userPhoneNumber=itemView.findViewById(R.id.order_Phone_Number);
            userTotalPrice=itemView.findViewById(R.id.order_total_price);
            userDateTime=itemView.findViewById(R.id.order_date_time);
            userShippingAddress=itemView.findViewById(R.id.order_address_city);
            ShowOrdersBtn=itemView.findViewById(R.id.show_all_products_btn);
        }
    }

    private void RemoveOrder(String uID) {
        ordersRef.child(uID).removeValue();
    }
}