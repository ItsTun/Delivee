package com.example.tunhanmyae.delivee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tunhanmyae.delivee.Common.Common;
import com.example.tunhanmyae.delivee.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    EditText e1,e2;
    Button btnLogIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        e1 = (EditText)findViewById(R.id.edtPhone);
        e2 = (EditText)findViewById(R.id.edtPass);
        btnLogIn = (Button) findViewById(R.id.btnSingIn);



        Paper.init(this);




        FirebaseDatabase database = FirebaseDatabase.getInstance();
       final DatabaseReference table_user = database.getReference("User");

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInterner(getBaseContext())) {
                    //Save user & password




                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);

                    mDialog.setMessage("Please Wating");
                    mDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(e1.getText().toString()).exists()) {
                                mDialog.dismiss();
                                User user = dataSnapshot.child(e1.getText().toString()).getValue(User.class);
                                user.setPhone(e1.getText().toString());


                                if (user.getPassword().equals(e2.getText().toString())) {

                                    Intent intent = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(intent);
                                    finish();


                                } else {
                                    Toast.makeText(SignIn.this, "Sign IN Failed!", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(SignIn.this, "User not exit!", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(SignIn.this,"Please check your connnection !",Toast.LENGTH_SHORT).show();
                    return;
                }
                }


        });

    }
}
