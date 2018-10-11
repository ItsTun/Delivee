package com.example.tunhanmyae.delivee.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tunhanmyae.delivee.Interface.ItemClickListener;
import com.example.tunhanmyae.delivee.R;

import org.w3c.dom.Text;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderAddress= (TextView) itemView.findViewById(R.id.order_address);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

       this.itemClickListener= itemClickListener;
    }
}
