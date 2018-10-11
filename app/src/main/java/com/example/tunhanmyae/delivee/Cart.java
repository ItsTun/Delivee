package com.example.tunhanmyae.delivee;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tunhanmyae.delivee.Common.Common;
import com.example.tunhanmyae.delivee.Database.Database;
import com.example.tunhanmyae.delivee.Model.MyResponse;
import com.example.tunhanmyae.delivee.Model.Notification;
import com.example.tunhanmyae.delivee.Model.Order;
import com.example.tunhanmyae.delivee.Model.Request;
import com.example.tunhanmyae.delivee.Model.Sender;
import com.example.tunhanmyae.delivee.Model.Token;
import com.example.tunhanmyae.delivee.Remote.APIServe;
import com.example.tunhanmyae.delivee.ViewHolder.CartAdaptor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    TextView totalPrice;
    Button btnPlace;
    List<Order> cart = new ArrayList<>();
    CartAdaptor adaptor;
    APIServe mserve;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init Service
        mserve = Common.getFCMService();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        totalPrice = (TextView) findViewById(R.id.Total);
        btnPlace = (Button) findViewById(R.id.btnPlaceOrder);
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cart.size()>0)
                {
                    showAlertDialog();
                }
                else
                    Toast.makeText(Cart.this,"Your cart is empty !",Toast.LENGTH_SHORT).show();
                



            }

            private void showAlertDialog() {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(Cart.this);
                alertdialog.setTitle("One More Step!");
                alertdialog.setMessage("Enter your address");


                LayoutInflater inflater = getLayoutInflater();
                View order_address_comment = inflater.inflate(R.layout.order_address_coment,null);
                final MaterialEditText edtAddress = (MaterialEditText) order_address_comment.findViewById(R.id.editAddress);
                final MaterialEditText edtComment = (MaterialEditText) order_address_comment.findViewById(R.id.editComment);


                alertdialog.setView(order_address_comment);
                alertdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

                alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                edtAddress.getText().toString(),
                                totalPrice.getText().toString(),
                                "0",
                                edtComment.getText().toString(),
                                cart
                        );
                        Toast.makeText(Cart.this,"Thank you , Order Please",Toast.LENGTH_SHORT).show();

                        //Submit to Firebase
                        //We will be using System.CurrentMilli to key
                        String order_number = String.valueOf(System.currentTimeMillis());

                        requests.child(order_number).setValue(request);

                        //Delect Cart
                        new Database(getBaseContext()).cleanCart();
                        sendNotificationOrder(order_number);

//
                        finish();


                    }
                });
                alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertdialog.show();
            }
        });
        
        loadListFood();


    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShop:dataSnapshot.getChildren())
                {
                    Token serverToken = postSnapShop.getValue(Token.class);

                    //Create raw payload to send
                    Notification notification = new Notification("EatIt","You have new order"+ order_number);
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mserve.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else
                                            Toast.makeText(Cart.this, "Failed!!!", Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Error",t.getMessage());

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void loadListFood() {

        cart = new Database(this).getCarts();
        adaptor = new CartAdaptor(cart,this);
        adaptor.notifyDataSetChanged();
        recyclerView.setAdapter(adaptor);

        int total = 0;
        for(Order order:cart)
            total+= (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        totalPrice.setText(fmt.format(total));



    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))

            deleteCart(item.getOrder());
        return true;

    }

    private void deleteCart(int order) {
        cart.remove(order);
        new Database(this).cleanCart();
        for (Order item:cart)
        {
            new Database(this).addToCart(item);
        }
        loadListFood();
    }
}
