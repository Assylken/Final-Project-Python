package kz.adilet.kazakhlearn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.abdularis.civ.CircleImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import kz.adilet.kazakhlearn.Model.Users;

public class ProfileChangeActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;

    CircleImageView profilePhoto;
    EditText userProfileNameEV, userProfilePhoneEV, userProfileSocialNetworkEV;
    Button saveUserBtn;
    FirebaseUser user;
    FirebaseStorage mFirebaseStorage;

    Uri uriProfileImage;

    String profileImgUrl;

    Users usersQuery;

    ProgressDialog pD;
    MediaPlayer mp;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        profilePhoto = findViewById(R.id.userProfileImageIV);
        userProfileNameEV = findViewById(R.id.userProfileNameTV);
        userProfilePhoneEV = findViewById(R.id.userProfilePhoneTV);
        userProfileSocialNetworkEV = findViewById(R.id.userProfileSocialNetworkTV);
        saveUserBtn = findViewById(R.id.userSaveBtn);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        mFirebaseStorage = FirebaseStorage.getInstance();

        pD = new ProgressDialog(this);
        mp = MediaPlayer.create(this, R.raw.click);

        usersQuery = new Users();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);

        mDatabase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersQuery = new Users();
                usersQuery = dataSnapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                saveUserInfo();
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                showImageChooser();
            }
        });
    }

    private void saveUserInfo() {
        pD.setMessage("Profile updating...");
        pD.show();
        final String displayName = userProfileNameEV.getText().toString();
        String profilePhone = userProfilePhoneEV.getText().toString();
        String profileSocialNetwork = userProfileSocialNetworkEV.getText().toString();

        if (displayName.isEmpty() && profilePhone.length() == 0 && profileSocialNetwork.length() == 0 && uriProfileImage == null) {
            Toast.makeText(getApplicationContext(), "Please, fill out at least one field or upload image", Toast.LENGTH_SHORT).show();
            pD.dismiss();
        }

        if (!displayName.isEmpty()) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mDatabase.child(user.getUid()).child("username").setValue(displayName);
                        Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                        pD.dismiss();
                        finish();
                    }
                }
            });
        }
        if (profilePhone.length() != 0) {
            mDatabase.child(user.getUid()).child("phone").setValue(profilePhone);
            Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            pD.dismiss();
        }
        if (profileSocialNetwork.length() != 0) {
            mDatabase.child(user.getUid()).child("socialNetwork").setValue(profileSocialNetwork);
            Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            pD.dismiss();
        }
        if (uriProfileImage != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(profileImgUrl)).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mDatabase.child(user.getUid()).child("image").setValue(profileImgUrl);
                        Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                        pD.dismiss();
                        finish();
                    }
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                profilePhoto.setImageBitmap(bitmap);
                Picasso.get().load(uriProfileImage).into(profilePhoto);
                if (usersQuery.getImage() != null && !usersQuery.getImage().isEmpty()) {
                    deletePrevImage();
                }
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deletePrevImage() {
        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(usersQuery.getImage());
        photoRef.delete();
    }

    private void uploadImageToFirebaseStorage() {
        pD.setMessage("Photo uploading...");
        pD.show();
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profileImage/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImgUrl = uri.toString();
                                    pD.dismiss();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pD.dismiss();
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pD.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    public void playClick() {
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = MediaPlayer.create(this, R.raw.click);
            } mp.start();
        } catch(Exception e) { e.printStackTrace(); }
    }
}
