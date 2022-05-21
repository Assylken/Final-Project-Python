package kz.adilet.kazakhlearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.abdularis.civ.CircleImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import kz.adilet.kazakhlearn.Fragments.ContentFragment;
import kz.adilet.kazakhlearn.Fragments.LearningFragment;
import kz.adilet.kazakhlearn.Fragments.MyDictionaryFragment;
import kz.adilet.kazakhlearn.Fragments.ProfileFragment;
import kz.adilet.kazakhlearn.Fragments.QuizFragment;
import kz.adilet.kazakhlearn.Fragments.TranslationFragment;
import kz.adilet.kazakhlearn.Fragments.UserTechSupportFragment;
import kz.adilet.kazakhlearn.Model.Users;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar topToolbar;
    private DrawerLayout drawer;
    NavigationView navigationView;
    CircleImageView userImage;
    TextView userName;
    View headView;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    Users usersModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(topToolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headView = navigationView.getHeaderView(0);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, topToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LearningFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_learn);
        }
        navViewUserInfo();
    }

    private void navViewUserInfo() {
        userImage = headView.findViewById(R.id.userProfileImage);
        userName = headView.findViewById(R.id.userProfileName);

        FirebaseUser user = mAuth.getCurrentUser();

        mDatabase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersModel = new Users();
                usersModel = dataSnapshot.getValue(Users.class);

                if (usersModel != null){
                    if (usersModel.getImage() != null) {
                        Picasso.get().load(usersModel.getImage()).into(userImage);
                    }

                    if (usersModel.getUsername() != null) {
                        userName.setText(usersModel.getUsername());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {   //checks for the menu item
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_learn:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LearningFragment()).commit();
                break;
            case R.id.nav_translation:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TranslationFragment()).commit();
                break;
            case R.id.nav_quiz:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new QuizFragment()).commit();
                break;
            case R.id.nav_content:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContentFragment()).commit();
                break;
            case R.id.nav_my_dictionary:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyDictionaryFragment()).commit();
                break;
            case R.id.nav_tech_support:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserTechSupportFragment()).commit();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
