package com.example.tunhanmyae.delivee;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tunhanmyae.delivee.Common.Common;
import com.example.tunhanmyae.delivee.Interface.ItemClickListener;
import com.example.tunhanmyae.delivee.Model.Category;
import com.example.tunhanmyae.delivee.Model.Token;
import com.example.tunhanmyae.delivee.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseDatabase database;
    DatabaseReference category;
    TextView navHead;
    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);


        //intit FireBase

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        Paper.init(this);





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent cartintent = new Intent(getApplicationContext(),Cart.class);
               startActivity(cartintent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //Set Name For User
        View headerView = navigationView.getHeaderView(0);
        navHead = (TextView) headerView.findViewById(R.id.navHead);
//        navHead.setText(Common.currentUser.getName());


        //load menu
        recyclerMenu = (RecyclerView) findViewById(R.id.recMenu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);
        if (Common.isConnectedToInterner(this))
        loadMenu();
        else
        {
            Toast.makeText(this,"Please check your connection !",Toast.LENGTH_SHORT).show();
            return;
        }

        //init services
        updateToken(FirebaseInstanceId.getInstance().getToken());







    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token,false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {

         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,R.layout.menu_item,MenuViewHolder.class,category
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                 viewHolder.txtMenuName.setText(model.getName());
                 Picasso.with(getBaseContext()).load(model.getImage()).into
                         (viewHolder.imageView);
                 final Category clickItem = model;
                 viewHolder.setItemClickListener(new ItemClickListener() {
                     @Override
                     public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(getApplicationContext(),FoodList.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                     }
                 });


            }
        };
        recyclerMenu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.refresh)
        {
            loadMenu();

        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navHead) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(getApplicationContext(),Cart.class);
            startActivity(intent);

        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent(getApplicationContext(),OrderStatus.class);
            startActivity(intent);

        } else if (id == R.id.nav_orders) {



        } else if (id == R.id.nav_logOut) {
            Paper.book().destroy();
            Intent intent = new Intent(getApplicationContext(),SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
