package kz.adilet.kazakhlearn.Fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.abdularis.civ.CircleImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import kz.adilet.kazakhlearn.AdminActivity;
import kz.adilet.kazakhlearn.FavouriteViewedActivity;
import kz.adilet.kazakhlearn.LoginActivity;
import kz.adilet.kazakhlearn.Model.Users;
import kz.adilet.kazakhlearn.ProfileChangeActivity;
import kz.adilet.kazakhlearn.R;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseFav, mDatabaseUsers, mDatabaseLearned;
    ImageView setProfileInfoIV;
    TextView uPNameTV, uPAddressTV, uPFavouritesTV, uPLearnedTV, uPEmailTV, uPPhoneTV, uPSocialNetworkTV, enterAdmin;
    Button exitButton;
    CircleImageView uPImageIV;
    //MediaPlayer mpClick;
    int favCount, learnedCount;
    RelativeLayout favLay, learnedLay;

    MediaPlayer mp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseFav = FirebaseDatabase.getInstance().getReference().child("favourite");
        mDatabaseLearned = FirebaseDatabase.getInstance().getReference().child("viewed");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDatabaseFav.keepSynced(true);
        mDatabaseLearned.keepSynced(true);
        mDatabaseUsers.keepSynced(true);

        uPImageIV = rootView.findViewById(R.id.userProfileImageIV);
        setProfileInfoIV = rootView.findViewById(R.id.setProfileInfoIV);

        favLay = rootView.findViewById(R.id.favLay);
        learnedLay = rootView.findViewById(R.id.learnedLay);

        uPNameTV = rootView.findViewById(R.id.userProfileNameTV);
        uPAddressTV = rootView.findViewById(R.id.userProfileAddressTV);
        uPFavouritesTV = rootView.findViewById(R.id.userProfileFavouritesTV);
        uPLearnedTV = rootView.findViewById(R.id.userProfileLearnedTV);
        uPEmailTV = rootView.findViewById(R.id.userProfileEmailTV);
        uPPhoneTV = rootView.findViewById(R.id.userProfilePhoneTV);
        uPSocialNetworkTV = rootView.findViewById(R.id.userProfileSocialNetworkTV);
        enterAdmin = rootView.findViewById(R.id.enterAdmin);
        exitButton = rootView.findViewById(R.id.userExitBtn);

        favCount = 0;
        learnedCount = 0;

        mDatabaseFav.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    favCount = (int)(favCount + dataSnapshot1.getChildrenCount());
                }
                uPFavouritesTV.setText(String.valueOf(favCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseLearned.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    learnedCount = (int)(learnedCount + dataSnapshot1.getChildrenCount());
                }
                uPLearnedTV.setText(String.valueOf(learnedCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);

                if (users != null) {
                    if (users.getPhone() != null) {
                        uPPhoneTV.setText(users.getPhone());
                    }
                    if (users.getSocialNetwork() != null) {
                        uPSocialNetworkTV.setText(users.getSocialNetwork());
                    }
                    if (users.getImage() != null) {
                        Picasso.get().load(users.getImage()).into(uPImageIV);
                    }

                    if (users.getUsername() != null) {
                        uPNameTV.setText(users.getUsername());
                    }
                    if (mAuth.getCurrentUser().getEmail() != null){
                        uPEmailTV.setText(mAuth.getCurrentUser().getEmail());
                    }
                    if (users.isAdmin()){
                        enterAdmin.setVisibility(View.VISIBLE);
                        enterAdmin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                playClick();
                                sendUserToAdminActivity();
                            }
                        });
                    } else {
                        enterAdmin.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        setProfileInfoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                startActivity(new Intent(getActivity(), ProfileChangeActivity.class));
            }
        });

        favLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(getActivity(), FavouriteViewedActivity.class);
                intent.putExtra("bool", true);
                startActivity(intent);
            }
        });

        learnedLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(getActivity(), FavouriteViewedActivity.class);
                intent.putExtra("bool", false);
                startActivity(intent);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                signOutUser();
            }
        });

        return rootView;
    }

    private void sendUserToAdminActivity() {
        Intent intent = new Intent(getActivity(), AdminActivity.class);
        startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        mp = MediaPlayer.create(getActivity(), R.raw.click);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialise your views
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    public void playClick() {
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = MediaPlayer.create(getActivity(), R.raw.click);
            } mp.start();
        } catch(Exception e) { e.printStackTrace(); }
    }
}