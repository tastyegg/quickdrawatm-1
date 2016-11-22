package com.quickdraw.quickdrawatm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NFCTransaction extends AppCompatActivity {

    TextView tv;

    Button depositer;

    String message;
    String username;

    float changeAmount;

    private DatabaseReference mDatabase;
    private static final String FIREBASE_URL = "https://quickdraw-db.firebaseio.com/";

    private Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(61, 61, 101));
        }

        setContentView(R.layout.activity_nfctransaction);

        message = getIntent().getStringExtra("Quickdraw Transaction Details");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        tv = (TextView) findViewById(R.id.textView);
        depositer = (Button) findViewById(R.id.depositCash);
        depositer.setVisibility(View.INVISIBLE);

        String[] messageVariables = message.split(" ");

        if (messageVariables[messageVariables.length - 2].equalsIgnoreCase("Deposit")) {
            //Do Deposit
            changeAmount = Float.parseFloat(messageVariables[messageVariables.length - 1]);
            String name = messageVariables[0];
            if (messageVariables.length > 3) {
                for (int i = 1; i < messageVariables.length - 2; i++) {
                    name += messageVariables[i];
                }
            }
            deposit(name);
        }

        else if (messageVariables[messageVariables.length - 2].equalsIgnoreCase("Withdraw")) {
            //Do Withdraw
            changeAmount = Float.parseFloat(messageVariables[messageVariables.length - 1]);
            String name = messageVariables[0];
            if (messageVariables.length > 3) {
                for (int i = 1; i < messageVariables.length - 2; i++) {
                    name += messageVariables[i];
                }
            }
            withdraw(name);
        }
    }


    public void withdraw(String userID) {
        depositer.setVisibility(View.VISIBLE);

        firebaseRef.child(userID).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                float newBalance = user.getBalance() - changeAmount;

                user.setBalance(newBalance);

                user.addTransaction(-changeAmount);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
                String currentDate = dateFormat.format(date);
                user.addDate(currentDate);

                firebaseRef.child(user.getUserID()).setValue(user);

                DecimalFormat df = new DecimalFormat("0.00");
                String result = df.format(changeAmount);
                String bResult = df.format(newBalance);

                tv.setText("Success !\nYou withdrew $" + result + "\nBalance is now $" + bResult);

                makeDoneButton();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                tv.setText("Withdraw unsuccessful.");
            }
        });
    }

    public void deposit(String ID) {
        username = ID;
        tv.setText("Insert Cash into ATM");
        depositer.setText("Insert $" + changeAmount);
        depositer.setVisibility(View.VISIBLE);

        depositer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = username;
                firebaseRef.child(userID).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                    @Override
                    public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        float newBalance = user.getBalance() + changeAmount;

                        user.setBalance(newBalance);

                        user.addTransaction(changeAmount);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        Date date = new Date();
                        String currentDate = dateFormat.format(date);
                        user.addDate(currentDate);

                        firebaseRef.child(user.getUserID()).setValue(user);

                        DecimalFormat df = new DecimalFormat("0.00");
                        String result = df.format(changeAmount);
                        String bResult = df.format(newBalance);

                        tv.setText("Success !\nYou deposited $" + result + "\nBalance is now $" + bResult);

                        makeDoneButton();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        tv.setText("Deposit unsuccessful.");
                    }
                });
            }
        });
    }

    public void makeDoneButton() {
        depositer.setText("DONE");
        depositer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NFCTransaction.this, MainActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() { startActivity(new Intent(NFCTransaction.this, MainActivity.class)); }
}
