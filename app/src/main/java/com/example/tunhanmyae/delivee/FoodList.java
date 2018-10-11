package com.example.tunhanmyae.delivee;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tunhanmyae.delivee.Common.Common;
import com.example.tunhanmyae.delivee.Database.Database;
import com.example.tunhanmyae.delivee.Interface.ItemClickListener;
import com.example.tunhanmyae.delivee.Model.Category;
import com.example.tunhanmyae.delivee.Model.Food;
import com.example.tunhanmyae.delivee.ViewHolder.FoodViewHolder;
import com.example.tunhanmyae.delivee.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FoodList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId = "";

    //Search Functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favourites
    Database localDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        //LocalDB
        localDB = new Database(this);


        recyclerView = (RecyclerView) findViewById(R.id.searchBarRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
            Toast.makeText(this, categoryId, Toast.LENGTH_SHORT).show();





        }
        if (!categoryId.isEmpty() && categoryId != null) {
            if (Common.isConnectedToInterner(getBaseContext()))
                loadListFood(categoryId);
            else {
                Toast.makeText(FoodList.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        //Search funcitonlity

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Search Food...");

        loadSuggest();



        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for (String search:suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void loadSuggest() {
        foodList.orderByChild("MenuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Food item = postSnapshot.getValue(Food.class);
                    suggestList.add(item.getName());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_list,
                FoodViewHolder.class,
                foodList.orderByChild("Name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {

                viewHolder.FoodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.FoodImage);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(getApplicationContext(),FoodDetail.class);
                        foodList.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodList);

                    }
                });

            }
        };
        recyclerView.setAdapter(searchAdapter);
    }





    private void loadListFood(String categoryId){
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_list,
                FoodViewHolder.class,
                foodList.orderByChild("MenuId").equalTo(categoryId)) { //like Select * from Foods where MenuID =
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.FoodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.FoodImage);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent foodDetail =  new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        //Set Adapter
        Log.d("TAG", ""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);
    }
}


    









