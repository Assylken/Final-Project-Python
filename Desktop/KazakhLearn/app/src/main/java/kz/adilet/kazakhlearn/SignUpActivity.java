package kz.adilet.kazakhlearn;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextUsername;
    String username;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    TextView logInTV;

    CircularProgressButton btn;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.editTextUsername);

        logInTV = findViewById(R.id.textViewLogin);
        btn = findViewById(R.id.buttonSignUp);

        mp = MediaPlayer.create(this, R.raw.click);

        mAuth = FirebaseAuth.getInstance();

        logInTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playClick();
                finish();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playClick();
                registerUser();
            }
        });

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();

        //Проверка полей
        if (email.isEmpty()) {
            editTextEmail.setError("Email required");
            editTextEmail.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            editTextUsername.setError("Username required");
            editTextUsername.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please, enter valid Email address");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters long");
            editTextPassword.requestFocus();
            return;
        }

        btn.startAnimation();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    btnAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendEmailVerificationMessage();
                        }
                    }, 200);
                    
                } else {
                    btnAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Проверка пользователя по e-mail
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "This user is already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 200);
                }
            }
        });
    }

    private void sendEmailVerificationMessage() {
        user = mAuth.getCurrentUser();
        username = editTextUsername.getText().toString().trim();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Registration was successful, we sent you an email. Please, confirm", Toast.LENGTH_SHORT).show();

                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                        user.updateProfile(profile);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                        mDatabase.child(user.getUid()).child("username").setValue(username);

                        sendUserToLoginActivity();
                        mAuth.signOut();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });
        }
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void btnAnimation() {
        btn.revertAnimation();
        btn.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_button_transparent));
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