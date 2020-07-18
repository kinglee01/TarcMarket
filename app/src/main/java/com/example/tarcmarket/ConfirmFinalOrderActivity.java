package com.example.tarcmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.example.tarcmarket.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity
{
    private EditText nameEditText, phoneEditText, addressEditText, cityEditText, creditCardNo, cvvNo;
    private Button confirmOrderBtn;

    private  String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = RM" + totalAmount, Toast.LENGTH_SHORT).show();

        nameEditText = (EditText) findViewById(R.id.shipment_name);
        addressEditText = (EditText) findViewById(R.id.shipment_address);



        final CardForm cardForm = findViewById(R.id.card_form);
        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .setup(ConfirmFinalOrderActivity.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardForm.isValid()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ConfirmFinalOrderActivity.this);
                    alertBuilder.setTitle("Confirm before purchase");
                    alertBuilder.setMessage("Card number: " + cardForm.getCardNumber() + "\n" +
                            "Card expiry date: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                            "Card CVV: " + cardForm.getCvv() + "\n" +
                            "Postal code: " + cardForm.getPostalCode() + "\n" +
                            "Phone number: " + cardForm.getMobileNumber());
                    alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ConfirmOder();
                            dialogInterface.dismiss();
                            Toast.makeText(ConfirmFinalOrderActivity.this, "Thank you for purchase", Toast.LENGTH_LONG).show();
                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Please complete the form", Toast.LENGTH_LONG).show();
                }
            }
        });


////        phoneEditText = (EditText) findViewById(R.id.shipment_phone_number);
////        cityEditText = (EditText) findViewById(R.id.shipment_city);
////        creditCardNo = (EditText) findViewById(R.id.credit_card);
////        cvvNo = (EditText)findViewById(R.id.cvv_number);
//
//
//        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                Check();
//
//
//            }
//        });
//    }
//
//    private void Check()
//    {
//        if (TextUtils.isEmpty(nameEditText.getText().toString()))
//        {
//            Toast.makeText(this, "Please Enter Your Username.", Toast.LENGTH_SHORT).show();
//        }
//        else if (TextUtils.isEmpty(creditCardNo.getText().toString()))
//        {
//            Toast.makeText(this, "Please Enter Your credit card.", Toast.LENGTH_SHORT).show();
//        }
//        else if (TextUtils.isEmpty(cvvNo.getText().toString()))
//        {
//            Toast.makeText(this, "Please Enter Your cvv.", Toast.LENGTH_SHORT).show();
//        }
//        else if (TextUtils.isEmpty(phoneEditText.getText().toString()))
//        {
//            Toast.makeText(this, "Please Enter Your phone number.", Toast.LENGTH_SHORT).show();
//        }
//        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
//        {
//            Toast.makeText(this, "Please Enter Your Address.", Toast.LENGTH_SHORT).show();
//        }
//        else if (TextUtils.isEmpty(cityEditText.getText().toString()))
//        {
//            Toast.makeText(this, "Please Enter Your Living City.", Toast.LENGTH_SHORT).show();
//        }
//
//        else
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmFinalOrderActivity.this);
//            builder.setTitle("Confirm checkout: ");
//
//            builder.setMessage("Card number: " + creditCardNo.getText().toString() + "\n" +
//                    "Card CVV: " + cvvNo.getText().toString() + "\n" +
//                    "Address: " + addressEditText.getText().toString() + "\n" +
//                    "Phone number: " + phoneEditText.getText().toString());
//            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//
//                    ConfirmOder();
//                    dialogInterface.dismiss();
//                    Toast.makeText(ConfirmFinalOrderActivity.this, "Thank you for purchase", Toast.LENGTH_LONG).show();
//                }
//            });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//
//        }
    }

    private void ConfirmOder()
    {
        final String saveCurrentDate,saveCurrentTime;
        final CardForm cardForm = findViewById(R.id.card_form);



        Calendar calForDate= Calendar.getInstance();
        SimpleDateFormat currentDate =  new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime =  new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("creditNo", cardForm.getCardNumber());
        ordersMap.put("cvv", cardForm.getCvv());
        ordersMap.put("postcode", cardForm.getPostalCode());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("phone", cardForm.getMobileNumber());




        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Thank you, your final order has been done.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}
