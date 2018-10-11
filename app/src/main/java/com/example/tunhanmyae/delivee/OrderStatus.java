package com.example.tunhanmyae.delivee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.tunhanmyae.delivee.Common.Common;
import com.example.tunhanmyae.delivee.Interface.ItemClickListener;
import com.example.tunhanmyae.delivee.Model.Order;
import com.example.tunhanmyae.delivee.Model.Request;
import com.example.tunhanmyae.delivee.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database=FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent()!= null)
        {

            loadOrder(Common.currentUser.getPhone());
        }
        else
            loadOrder(getIntent().getStringExtra("userPhone"));





    }

    private void loadOrder(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,
                R.layout.order_layout,OrderViewHolder.class,requests.orderByChild("phone").equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }




            private String convertCodeToStatus(String status) {
                if (status.equals("0"))
                {
                    return "Placed";
                }

                else if(status.equals("1"))
                {
                    return "On my way!";
                }
                else
                    return "Shipped";
            }
        };
        recyclerView.setAdapter(adapter);
    }
}
