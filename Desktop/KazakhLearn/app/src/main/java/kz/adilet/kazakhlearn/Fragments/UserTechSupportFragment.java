package kz.adilet.kazakhlearn.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import kz.adilet.kazakhlearn.Model.TechSupport;
import kz.adilet.kazakhlearn.R;

public class UserTechSupportFragment extends Fragment {

    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<TechSupport, TechSupportViewHolder> firebaseRecyclerAdapter;

    RecyclerView techSupportRV;
    ImageButton addBtn;

    RelativeLayout l1;
    LinearLayout l2;

    EditText headingET, descET;
    TextView cancel;
    Button okBtn;
    String count;

    TechSupport techSupport;

    Calendar calendar;
    SimpleDateFormat dateFormat;

    boolean isAdd = false;

    MediaPlayer mp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_tech_support, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("tech_support");
        mDatabase.keepSynced(true);

        techSupportRV = rootView.findViewById(R.id.recyclerTechSupport);

        addBtn = rootView.findViewById(R.id.addBtn);
        l1 = rootView.findViewById(R.id.l1);
        l2 = rootView.findViewById(R.id.l2);

        headingET = rootView.findViewById(R.id.headingET);
        descET = rootView.findViewById(R.id.descET);
        cancel = rootView.findViewById(R.id.cancelTV);
        okBtn = rootView.findViewById(R.id.okBtn);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        techSupport = new TechSupport();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dSnapshot : dataSnapshot.getChildren()) {
                    count = dSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        showMessages();

        techSupportRV.setHasFixedSize(true);
        techSupportRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                isAdd = true;
                addData(isAdd);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                isAdd(false);
            }
        });

        return rootView;
    }

    private void showMessages() {
        Query query = mDatabase.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<TechSupport> postsOptions = new FirebaseRecyclerOptions.Builder<TechSupport>().setQuery(query, TechSupport.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TechSupport, TechSupportViewHolder>(postsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final TechSupportViewHolder holder, int i, @NonNull TechSupport techSupport) {
                holder.setDetails(techSupport.getHeading(), techSupport.getDesc(), techSupport.getDate(), techSupport.getAnswer());

                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playClick();
                        DatabaseReference ref = getRef(holder.getAdapterPosition());

                        ref.removeValue();
                    }
                });
            }

            @NonNull
            @Override
            public TechSupportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tech_support_item_list, parent, false);

                return new TechSupportViewHolder(view);
            }
        };
        techSupportRV.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void addData(final boolean isAdd) {
        isAdd(isAdd);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                if (headingET.length() == 0) {
                    headingET.setError("Please fill out this field");
                } else if (descET.length() == 0) {
                    descET.setError("Please fill out this field");
                } else {
                    if (count == null) {
                        count = "0";
                    }
                    techSupport.setHeading(headingET.getText().toString().toLowerCase());
                    techSupport.setDesc(descET.getText().toString().toLowerCase());
                    calendar = Calendar.getInstance();
                    techSupport.setDate(String.valueOf(dateFormat.format(calendar.getTime())));
                    techSupport.setUserEmail(mAuth.getCurrentUser().getEmail());
                    techSupport.setUid(mAuth.getCurrentUser().getUid());
                    techSupport.setAnswer("");

                    mDatabase.child(String.valueOf(Integer.valueOf(count)+1)).setValue(techSupport);
                    Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
                    isAdd(false);
                }
            }
        });


    }

    private void isAdd(boolean isAdd) {
        if (isAdd) {
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.VISIBLE);
        } else {
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.GONE);

        }
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

    private class TechSupportViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton deleteBtn;

        public TechSupportViewHolder(@NonNull View itemView) {
            super(itemView);

            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            mView = itemView;
        }

        public void setDetails(String mHeading, String mDesc, String mDate, String mResponse) {
            TextView heading = mView.findViewById(R.id.heading);
            TextView desc = mView.findViewById(R.id.desc);
            TextView date = mView.findViewById(R.id.date);
            TextView userEmail = mView.findViewById(R.id.userEmail);
            TextView response = mView.findViewById(R.id.response);
            userEmail.setVisibility(View.GONE);

            if (mResponse != null) {
                if (!mResponse.isEmpty()) {
                    response.setText("Response: "+mResponse);
                }
            }
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
                mp = MediaPlayer.create(getActivity(), R.raw.click);
            } mp.start();
        } catch(Exception e) { e.printStackTrace(); }
    }
}