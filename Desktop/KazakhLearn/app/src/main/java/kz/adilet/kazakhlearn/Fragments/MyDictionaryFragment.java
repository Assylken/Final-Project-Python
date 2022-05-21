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

import kz.adilet.kazakhlearn.Model.Request;
import kz.adilet.kazakhlearn.R;

public class MyDictionaryFragment extends Fragment {

    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<Request, MyDictionaryViewHolder> firebaseRecyclerAdapter;

    Request request;

    RecyclerView dictionaryRV;
    ImageButton addBtn;

    RelativeLayout l1;
    LinearLayout l2;

    EditText wordET, transcriptET, meanET;
    TextView cancel;
    Button okBtn;
    String count;

    boolean isAdd = false;

    MediaPlayer mp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_dictionary, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("user_dictionary");
        mDatabase.keepSynced(true);

        dictionaryRV = rootView.findViewById(R.id.recyclerDictionary);

        addBtn = rootView.findViewById(R.id.addWordBtn);
        l1 = rootView.findViewById(R.id.l1);
        l2 = rootView.findViewById(R.id.l2);

        wordET = rootView.findViewById(R.id.wordET);
        transcriptET = rootView.findViewById(R.id.transcriptET);
        meanET = rootView.findViewById(R.id.meanET);

        cancel = rootView.findViewById(R.id.cancelTV);
        okBtn = rootView.findViewById(R.id.okBtn);

        request = new Request();

        mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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

        showDictionary();

        dictionaryRV.setHasFixedSize(true);
        dictionaryRV.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    private void addData(final boolean isAdd) {
        isAdd(isAdd);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                if (wordET.length() == 0) {
                    wordET.setError("Please fill out this field");
                } else if (transcriptET.length() == 0) {
                    transcriptET.setError("Please fill out this field");
                } else if (meanET.length() == 0) {
                    meanET.setError("Please fill out this field");
                } else {
                    if (count == null) {
                        count = "0";
                    }
                    request.setWord(wordET.getText().toString().toLowerCase());
                    request.setSearchMean(meanET.getText().toString().toLowerCase());
                    request.setTranscript(transcriptET.getText().toString().toLowerCase());

                    mDatabase.child(mAuth.getCurrentUser().getUid()).child(String.valueOf(Integer.valueOf(count)+1)).setValue(request);
                    Toast.makeText(getActivity(), "Word added", Toast.LENGTH_SHORT).show();
                    isAdd(false);
                }
            }
        });


    }

    private void showDictionary() {

        Query query = mDatabase.child(mAuth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<Request> postsOptions = new FirebaseRecyclerOptions.Builder<Request>().setQuery(query, Request.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, MyDictionaryViewHolder>(postsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final MyDictionaryViewHolder holder, int i, @NonNull Request request) {
                holder.setDetails(request.getWord(), request.getTranscript(), request.getSearchMean());

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
            public MyDictionaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_dictionary_item_list, parent, false);

                return new MyDictionaryViewHolder(view);
            }
        };
        dictionaryRV.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
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

    private class MyDictionaryViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton deleteBtn;

        public MyDictionaryViewHolder(@NonNull View itemView) {
            super(itemView);

            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            mView = itemView;
        }

        public void setDetails(String mWord, String mTranscript, String mMeaning) {
            TextView word = mView.findViewById(R.id.diWord);
            TextView transcript = mView.findViewById(R.id.diTranscript);
            TextView meaning = mView.findViewById(R.id.diMean);

            meaning.setText(mMeaning);
            word.setText(mWord);
            transcript.setText(mTranscript);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
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