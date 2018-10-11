package com.example.tunhanmyae.delivee;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.tunhanmyae.delivee.Common.Common;
import com.example.tunhanmyae.delivee.Database.Database;
import com.example.tunhanmyae.delivee.Model.Food;
import com.example.tunhanmyae.delivee.Model.Order;
import com.example.tunhanmyae.delivee.Model.Rating;
import com.example.tunhanmyae.delivee.Model.User;
import com.example.tunhanmyae.delivee.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart,btnRating;
    ElegantNumberButton numberButton;
    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;
    Food currentFood;
    RatingBar ratingBar;
    User user=Common.currentUser;




    String foodId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);






        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingTbl = database.getReference("Rating");

        //Init View
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);
        btnRating = (FloatingActionButton) findViewById(R.id.btnRating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();

            }
        });



        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));


                Toast.makeText(FoodDetail.this,"Added to Cart",Toast.LENGTH_SHORT).show();
            }
        });



        food_description = (TextView) findViewById(R.id.food_description);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);
        food_image = (ImageView) findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get food ID from Intent
        if(getIntent() != null)
        {
            foodId = getIntent().getStringExtra("FoodId");

        }
        if(!foodId.isEmpty() && foodId!= null)
        {
            if (Common.isConnectedToInterner(getBaseContext()))
            {
                getDetailFood(foodId);
                getRatingFood(foodId);
            }

            else
            {
                Toast.makeText(FoodDetail.this,"Please check your connection !",Toast.LENGTH_SHORT).show();
                return;
            }
        }



    }

    private void getRatingFood(String foodId) {

        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;



                }

                if (count != 0)
                {
                    float average = sum/count;
                    ratingBar.setRating(average);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showRatingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite OK","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some starts and gice your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setCommentTextColor(android.R.color.white)
                .setWindowAnimation(R.style.RatingDialogFadeanim)
                .create(FoodDetail.this)
                .show();


    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                currentFood = dataSnapshot.getValue(Food.class);

                //Set Image
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getName());

                food_description.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        final Rating rating = new Rating(user.getPhone(),
                foodId,
                String.valueOf(i),
                s);

        ratingTbl.child(user.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getPhone()).exists())
                {
                    ratingTbl.child(user.getPhone()).removeValue();

                    ratingTbl.child(user.getPhone()).setValue(rating);


                }
                else
                {
                    ratingTbl.child(user.getPhone()).setValue(rating);

                }

                Toast.makeText(FoodDetail.this,"Thank you",Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}

