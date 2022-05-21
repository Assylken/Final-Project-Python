package kz.adilet.kazakhlearn;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.abdularis.civ.CircleImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kz.adilet.kazakhlearn.Model.ExampleAdapter;
import kz.adilet.kazakhlearn.Model.Examples;
import kz.adilet.kazakhlearn.Model.Request;

public class SearchActivity extends AppCompatActivity {
    private String TAG = "log";
    String ref;

    EditText searchET;
    RecyclerView searchRV;

    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<Request, RequestViewHolder> firebaseRecyclerAdapter;

    TextView word, transcript;
    ImageButton imageButton;
    ListView meaningsLV, examplesLV;

    ArrayList<Examples> arrayE = new ArrayList<>();
    ExampleAdapter adapterE;
    Examples examples;

    ArrayAdapter adapterM;
    ArrayList<String> arrayM;

    Request request;

    LinearLayout l1, l2;

    boolean isData = false;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mDatabase = FirebaseDatabase.getInstance().getReference("search");
        mDatabase.keepSynced(true);

        searchET = findViewById(R.id.searchET);
        searchRV = findViewById(R.id.recyclerSearch);

        word = findViewById(R.id.word);
        transcript = findViewById(R.id.transcript);
        imageButton = findViewById(R.id.photoBtn);

        meaningsLV = findViewById(R.id.meanings);
        examplesLV = findViewById(R.id.examples);

        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);

        mp = MediaPlayer.create(this, R.raw.click);

        adapterE = new ExampleAdapter(this, arrayE);
        examplesLV.setAdapter(adapterE);

        arrayM = new ArrayList<>();
        adapterM = new ArrayAdapter(this, R.layout.meaning_item_list, R.id.meaningsTV, arrayM);
        meaningsLV.setAdapter(adapterM);

        searchRV.setHasFixedSize(true);
        searchRV.setLayoutManager(new LinearLayoutManager(this));

        if (searchET != null && searchET.getText() != null && !searchET.getText().equals("")) {
            searchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    firebaseSearch(s.toString().toLowerCase());
                }
            });
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                Dialog dialog = new Dialog(SearchActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.image_view_custom_dialog);

                ImageView image = dialog.findViewById(R.id.wordImage);

                if (request.getImage() != null && !request.getImage().equals("")) {
                    Picasso.get().load(request.getImage()).into(image);
                }

                dialog.show();
            }
        });
    }

    private void firebaseSearch(String searchText) {

        Query query = mDatabase.orderByChild("word").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Request> postsOptions = new FirebaseRecyclerOptions.Builder<Request>().setQuery(query, Request.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(postsOptions) {

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.search_item_list, viewGroup, false);

                return new RequestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Request model) {
                holder.setDetails(model.getWord(), model.getTranscript(), model.getImage(), model.getSearchMean());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playClick();
                        ref = String.valueOf(getRef(holder.getAdapterPosition()));
                        ref = ref.replace("https://kazakhenglishstudy.firebaseio.com/","");
                        ref = ref.replace("%20"," ");

                        isData = true;
                        getData(isData);
                    }
                });
            }

        };
        searchRV.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDetails(String mWord, String mTranscript, String mImage, String mMeaning) {
            TextView word = mView.findViewById(R.id.searchWord);
            TextView transcript = mView.findViewById(R.id.searchTranscript);
            CircleImageView imageView = mView.findViewById(R.id.wordImg);
            TextView meaning = mView.findViewById(R.id.searchMean);

            meaning.setText(mMeaning);
            word.setText(mWord);
            transcript.setText("(" + mTranscript + ")");

            if (mImage != null && !mImage.equals("")) {
                Picasso.get().load(mImage).into(imageView);
            }
        }

    }

    private void isData(boolean isData) {
        if (isData) {
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.VISIBLE);
        } else {
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.GONE);

        }
    }

    private void getData(boolean isData) {
        isData(isData);

        arrayM.clear();
        arrayE.clear();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ref);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "asd: " + dataSnapshot.getKey());

                request = new Request();
                request = dataSnapshot.getValue(Request.class);
                word.setText(request.getWord());
                transcript.setText(request.getTranscript());


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals("meanings")) {
                        for (DataSnapshot child2 : child.getChildren()) {
                            String meanText = child2.getValue(String.class);
                            arrayM.add(meanText);
                            adapterM.notifyDataSetChanged();
                        }
                    }

                    if (child.getKey().equals("examples")) {
                        for (DataSnapshot child2 : child.getChildren()) {
                            examples = new Examples();
                            examples = child2.getValue(Examples.class);
                            arrayE.add(new Examples(examples.getText1(), examples.getText2()));
                            adapterE.notifyDataSetChanged();
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (isData) {
            isData = false;
            isData(isData);
        } else {
            super.onBackPressed();
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
