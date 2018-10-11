package com.example.tunhanmyae.delivee.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tunhanmyae.delivee.Interface.ItemClickListener;
import com.example.tunhanmyae.delivee.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView FoodName;
    public ImageView FoodImage,fav_image;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
        super(itemView);
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        FoodName = (TextView) itemView.findViewById(R.id.food_name);
        FoodImage = (ImageView) itemView.findViewById(R.id.food_image);
        fav_image = (ImageView) itemView.findViewById(R.id.fav);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);


    }
}
