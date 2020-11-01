package com.dalilu.police.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dalilu.police.R;
import com.dalilu.police.data.Alerts;
import com.dalilu.police.data.Police;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    DrawerLayout drawer;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initViews();
    }

    private void initViews() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        getPoliceData(currentUser.getEmail());
        toolbar = findViewById(R.id.topAppBar);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationView = findViewById(R.id.navigation);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        return true;
                    case R.id.action_map:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        return true;
                    case R.id.action_account:
                        startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                        return true;
                }
                return false;
            }
        });
    }

    public void openResetPassword(View view) {
    }


    // READ Police Data
    private void getPoliceData(String userEmail) {

        Query query = FirebaseFirestore.getInstance()
                .collection("Police")
                .whereEqualTo("userEmail", userEmail);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    //...
                    return;
                }

                // Convert query snapshot to a list of alerts
                List<Police> police = snapshot.toObjects(Police.class);

                TextView username = findViewById(R.id.user_profile_username);
                TextView fullName = findViewById(R.id.user_profile_full_name);
                TextView telephone = findViewById(R.id.user_profile_phone);
                TextView email = findViewById(R.id.user_profile_email);
                ImageView userPhoto = findViewById(R.id.user_profile_roundedimage);

                Toast.makeText(UserProfileActivity.this, "This is police data"+ police.get(0), Toast.LENGTH_SHORT).show();

                username.setText(police.get(0).getUserName());
                fullName.setText(police.get(0).getUserFullName());
                telephone.setText(police.get(0).getPhoneNumber());
                email.setText(police.get(0).getUserEmail());

                Glide.with(getApplicationContext())
                        .load(police.get(0).getUserPhotoUrl())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar))
                        .into(userPhoto);


            }
        });

//        FirebaseFirestore
//                .getInstance()
//                .collection("Police")
//                .whereEqualTo("userEmail", userEmail)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                        TextView username = findViewById(R.id.nav_user_name);
//                        TextView fullName = findViewById(R.id.user_profile_full_name);
//                        TextView telephone = findViewById(R.id.user_profile_phone);
//                        TextView email = findViewById(R.id.user_profile_email);
//                        ImageView userPhoto = findViewById(R.id.user_profile_roundedimage);
//
//                        List<Alerts> alerts = queryDocumentSnapshots.toObjects(Alerts.class);
//
//                        Police police = (Police) queryDocumentSnapshots.getDocuments().get(0).getData();
//                        Toast.makeText(UserProfileActivity.this, "This is police data"+ police, Toast.LENGTH_SHORT).show();
//
//                        username.setText(police.getUserName());
//                        fullName.setText(police.getUserFullName());
//                        telephone.setText(police.getPhoneNumber());
//                        email.setText(police.getUserEmail());
//
//                        Glide.with(getApplicationContext())
//                                .load(police.getUserPhotoUrl())
//                                .apply(new RequestOptions()
//                                        .centerCrop()
//                                        .placeholder(R.drawable.avatar)
//                                        .error(R.drawable.avatar))
//                                .into(userPhoto);
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
    }
}