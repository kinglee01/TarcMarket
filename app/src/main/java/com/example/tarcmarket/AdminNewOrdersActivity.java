package com.example.tarcmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tarcmarket.Model.AdminNewOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity
{
    private RecyclerView newOrdersList;
    private DatabaseReference newOrdersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        newOrdersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        newOrdersList = findViewById(R.id.new_orders_list);
        newOrdersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<AdminNewOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminNewOrders>()
                .setQuery(newOrdersRef, AdminNewOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminNewOrders,AdminOrdersViewHolder> adapter =
                 new FirebaseRecyclerAdapter<AdminNewOrders, AdminOrdersViewHolder>(options) {
                     @Override
                     protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminNewOrders model)
                     {
                             holder.customerName.setText("CustomerName : " + model.getName());
                             holder.customerPhoneNumber.setText("CustomerPhone: " + model.getPhone());
                             holder.customerTotalPrice.setText("Total Price : RM" + model.getTotalAmount());
                             holder.customerDateTime .setText("Customer order date:" + model.getDate() + "," + model.getTime());
                             holder.customerAddress.setText("Customer Address : " + model.getAddress() + "," +model.getCity());

                             holder.ShowCustomerOrdersButton.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v)
                                 {
                                     String uID = getRef(position).getKey();

                                     Intent intent = new Intent(AdminNewOrdersActivity.this,AdminUserProductsActivity.class);
                                     intent.putExtra("uid", uID);
                                     startActivity(intent);

                                 }
                             });

                              holder.itemView.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v)
                                  {
                                      CharSequence options[] = new CharSequence[]
                                              {
                                                      "Yes",
                                                      "No"
                                              };

                                      AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                      builder.setTitle("Shipping progress arrival.");

                                      builder.setItems(options, new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialogInterface, int i)
                                          {
                                              if (i == 0)
                                              {
                                                String uID = getRef(position).getKey();

                                                RemoveOrder(uID);
                                              }
                                              else
                                              {
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
                     public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                     {
                         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                         return new AdminOrdersViewHolder(view);                     }
                 };

        newOrdersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView customerName, customerPhoneNumber, customerTotalPrice, customerDateTime, customerAddress;
        public Button ShowCustomerOrdersButton;

        public AdminOrdersViewHolder(@NonNull View itemView)
        {
            super(itemView);

            customerName = itemView.findViewById(R.id.customer_name);
            customerPhoneNumber = itemView.findViewById(R.id.customer_order_phone_number);
            customerTotalPrice = itemView.findViewById(R.id.total_order__price);
            customerDateTime = itemView.findViewById(R.id.customer_ordered_time_and_date);
            customerAddress = itemView.findViewById(R.id.customer_order_address);
            ShowCustomerOrdersButton =itemView.findViewById(R.id.show_customer_ordered_products);


        }
    }


    private void RemoveOrder(String uID)
    {
        newOrdersRef.child(uID).removeValue();
    }
}
