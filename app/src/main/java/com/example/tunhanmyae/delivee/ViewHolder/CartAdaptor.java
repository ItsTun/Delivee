package com.example.tunhanmyae.delivee.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.tunhanmyae.delivee.Common.Common;
import com.example.tunhanmyae.delivee.Interface.ItemClickListener;
import com.example.tunhanmyae.delivee.Model.Order;
import com.example.tunhanmyae.delivee.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener
{
    public TextView txt_cart_name,txt_price;
    public ImageView img_cart_count;

    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public void setTxt_price(TextView txt_price) {
        this.txt_price = txt_price;
    }

    public void setImg_cart_count(ImageView img_cart_count) {
        this.img_cart_count = img_cart_count;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        img_cart_count = (ImageView) itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select Action !");
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);


    }
}

public class CartAdaptor extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData = new ArrayList<>();
    private Context context;



    public CartAdaptor(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout,viewGroup,false);
        return new CartViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i) {

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+listData.get(i).getQuantity(), Color.RED);
        cartViewHolder.img_cart_count.setImageDrawable(drawable);
        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price =(Integer.parseInt(listData.get(i).getPrice()))*(Integer.parseInt(listData.get(i).getQuantity()));
        cartViewHolder.txt_price.setText(fmt.format(price));
        cartViewHolder.txt_cart_name.setText(listData.get(i).getProductName());



    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
