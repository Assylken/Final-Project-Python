package kz.adilet.kazakhlearn;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import kz.adilet.kazakhlearn.Model.TechSupport;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class AdminActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<TechSupport, AdminViewHolder> firebaseRecyclerAdapter;

    RecyclerView techSupportRV;

    TechSupport techSupport;

    AlertDialog.Builder ad;

    String response;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("tech_support");
        mDatabase.keepSynced(true);

        techSupportRV = findViewById(R.id.recyclerTechSupport);

        techSupport = new TechSupport();

        mp = MediaPlayer.create(this, R.raw.click);

        showMessages();

        techSupportRV.setHasFixedSize(true);
        techSupportRV.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showMessages() {
        Query query = mDatabase;

        FirebaseRecyclerOptions<TechSupport> postsOptions = new FirebaseRecyclerOptions.Builder<TechSupport>().setQuery(query, TechSupport.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TechSupport, AdminViewHolder>(postsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final AdminViewHolder holder, int i, @NonNull TechSupport techSupport) {
                holder.setDetails(techSupport.getHeading(), techSupport.getDesc(), techSupport.getDate(), techSupport.getUserEmail(), techSupport.getAnswer());

                holder.response.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playClick();
                        ad = new AlertDialog.Builder(AdminActivity.this);
                        ad.setTitle("Attention!");
                        ad.setMessage("Answer");

                        LinearLayout linearLayout = new LinearLayout(AdminActivity.this);
                        final EditText responseET = new EditText(AdminActivity.this);
                        responseET.setHint("Type here...");
                        responseET.setMinEms(16);

                        linearLayout.addView(responseET);
                        linearLayout.setPadding(10, 10, 10, 10);

                        ad.setView(linearLayout);

                        ad.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                playClick();
                                response = responseET.getText().toString().trim();
                                Log.d(TAG, "response: " + response);
                                if (response.length() == 0) {
                                    Toast.makeText(AdminActivity.this, "Please, give the response", Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference ref = getRef(holder.getAdapterPosition());

                                    ref.child("answer").setValue(response);
                                    Toast.makeText(AdminActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                playClick();
                                dialog.dismiss();
                            }
                        });
                        ad.setCancelable(false);
                        ad.create().show();
                    }

                    });
            }

            @NonNull
            @Override
            public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tech_support_item_list, parent, false);

                return new AdminViewHolder(view);
            }
        };
        techSupportRV.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private class AdminViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton deleteBtn;
        TextView response;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);

            response = itemView.findViewById(R.id.response);

            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            deleteBtn.setVisibility(View.GONE);
            mView = itemView;
        }

        public void setDetails(String mHeading, String mDesc, String mDate, String mUserEmail, String mResponse) {
            TextView heading = mView.findViewById(R.id.heading);
            TextView desc = mView.findViewById(R.id.desc);
            TextView date = mView.findViewById(R.id.date);
            TextView userEmail = mView.findViewById(R.id.userEmail);
            TextView response = mView.findViewById(R.id.response);
            userEmail.setVisibility(View.VISIBLE);

            if (mResponse != null) {
                if (!mResponse.isEmpty()) {
                    response.setText(mResponse);
                }
            }
            userEmail.setText(mUserEmail);
            date.setText(mDate);
            heading.setText(mHeading);
            desc.setText(mDesc);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
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
