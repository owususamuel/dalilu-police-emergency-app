package com.dalilu.police.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dalilu.police.R;
import com.dalilu.police.data.Alerts;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    static FirebaseFirestore mDatabase;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    DrawerLayout drawer;
    MaterialToolbar toolbar;
    private FirestoreRecyclerAdapter<Alerts, CardViewHolder> adapter;
    private Query query;
    private String alertId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.topAppBar);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationView = findViewById(R.id.navigation);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mRecyclerView = findViewById(R.id.card_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.setHasFixedSize(true);
        final CollectionReference datasRef = mDatabase.collection("Alerts");
        query = datasRef.orderBy("address", Query.Direction.ASCENDING);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        openActivity(new Intent(getApplicationContext(), MainActivity.class));
                        return true;
                    case R.id.action_map:
                        openActivity(new Intent(getApplicationContext(), MapActivity.class));
                        return true;
                    case R.id.action_account:
                        openActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        final FirestoreRecyclerOptions<Alerts> options = new FirestoreRecyclerOptions.Builder<Alerts>()
                .setQuery(query, Alerts.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Alerts, CardViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CardViewHolder holder, final int i, @NonNull final Alerts alerts) {

                alertId = alerts.getUserId();
                holder.alertNameView.setText(alerts.getUserName());
                holder.alertLocationView.setText(alerts.getAddress());

                Glide.with(getApplicationContext())
                        .load(alerts.getUrl())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.emergency_alarm)
                                .error(R.drawable.emergency_alarm))
                        .into(holder.alertMediaView);

                if (alerts.isSolved()) {
                    holder.alertButton.setBackgroundColor(Color.GRAY);
                    holder.alertButton.setText("Following");
                }

                holder.alertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseFirestore
                                .getInstance()
                                .collection("Alerts")
                                .document(options.getSnapshots().getSnapshot(i).getId())   // If document with this ID is missing then update method will fail while set method will create a document with that ID if its missing n will succeed
                                .update(    // U can create new fields with the update method that actually didn't exist previously
                                        "isSolved", true
                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                holder.alertButton.setBackgroundColor(Color.GRAY);
                                holder.alertButton.setText("Following");
                            }
                            }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to reach the Command Center", Toast.LENGTH_SHORT).show();
                            }
                        });
                                
                    }
                    
                });

            }

            @NonNull
            @Override
            public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_alert_item, parent, false);
                return new CardViewHolder(view);
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView alertNameView;
        TextView alertLocationView;
        ImageView alertMediaView;
        MaterialButton alertButton;

        CardViewHolder(View itemView) {
            super(itemView);
            alertNameView = itemView.findViewById(R.id.alertTitleTextView);
            alertLocationView = itemView.findViewById(R.id.alertLocationTextView);
            alertMediaView = itemView.findViewById(R.id.alertMediaView);
            alertButton = itemView.findViewById(R.id.alertFollowUpButton);


        }
    }



    public void openActivity(Intent intent) {
        startActivity(intent);
    }

    public void openCategory(View view) {
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null)
            startActivity(new Intent(this, LoginActivity.class));

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_user_name);
        //ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_avatar);

        navUsername.setText(currentUser.getEmail());
        //Glide.with(MainActivity.this).load(userPicture).into(navUserPhoto);
    }
}
