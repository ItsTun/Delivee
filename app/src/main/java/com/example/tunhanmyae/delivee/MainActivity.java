package com.example.tunhanmyae.delivee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tunhanmyae.delivee.Common.Common;
import com.example.tunhanmyae.delivee.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button btnsignUp,btnSignIn;
    TextView txtInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnsignUp = (Button) findViewById(R.id.btnSignUp);
        txtInfo = (TextView) findViewById(R.id.txtInfo);



        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Nabila.ttf");
        txtInfo.setTypeface(typeface);

        Paper.init(this);


        btnsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),SignIn.class);
                startActivity(intent);

            }
        });

        //check remember
        String user = Paper.book().read(Common.USER_Key);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if(user != null && pwd.isEmpty())
        {
            login(user,pwd);
        }
    }

    private void login(final String phone, final String pwd) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectedToInterner(getBaseContext())) {



            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);

            mDialog.setMessage("Please Wating");
            mDialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists()) {
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);


                        if (user.getPassword().equals(pwd)) {

                            Intent intent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(intent);
                            finish();


                        } else {
                            Toast.makeText(MainActivity.this, "Sign IN Failed!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "User not exit!", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(MainActivity.this,"Please check your connnection !",Toast.LENGTH_SHORT).show();
            return;
        }

    }


}
