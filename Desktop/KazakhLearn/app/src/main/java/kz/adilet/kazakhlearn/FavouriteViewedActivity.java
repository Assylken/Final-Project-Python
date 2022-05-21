package kz.adilet.kazakhlearn;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.abdularis.civ.CircleImageView;
import com.google.firebase.auth.FirebaseAuth;
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

public class FavouriteViewedActivity extends AppCompatActivity {

    private static final String TAG = "abc";
    Toolbar toolbar;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabaseFav, mDatabaseViewed;
    Spinner spinner;
    RecyclerView favRV;
    TextView titleFav;
    Query query;
    String spinnerText, ref;
    FirebaseRecyclerAdapter<Request, FavViewHolder> firebaseRecyclerAdapter;

    TextView word, transcript;
    ImageButton imageButton;
    ListView meaningsLV, examplesLV;

    ArrayList<Examples> arrayE = new ArrayList<>();
    ExampleAdapter adapterE;
    Examples examples;

    ArrayAdapter adapterM;
    ArrayList<String> arrayM;

    Request request;

    RelativeLayout l1;
    LinearLayout l2;

    MediaPlayer mp;

    boolean isData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_viewed);

        titleFav = findViewById(R.id.titleFav);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabaseFav = FirebaseDatabase.getInstance().getReference().child("favourite");
        mDatabaseViewed = FirebaseDatabase.getInstance().getReference().child("viewed");
        mDatabaseFav.keepSynced(true);
        mDatabaseViewed.keepSynced(true);

        mp = MediaPlayer.create(this, R.raw.click);

        favRV = findViewById(R.id.recyclerFavViewed);
        favRV.setHasFixedSize(true);
        favRV.setLayoutManager(new LinearLayoutManager(this));

        word = findViewById(R.id.word);
        transcript = findViewById(R.id.transcript);
        imageButton = findViewById(R.id.photoBtn);

        meaningsLV = findViewById(R.id.meanings);
        examplesLV = findViewById(R.id.examples);

        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);

        adapterE = new ExampleAdapter(this, arrayE);
        examplesLV.setAdapter(adapterE);

        arrayM = new ArrayList<>();
        adapterM = new ArrayAdapter(this, R.layout.meaning_item_list, R.id.meaningsTV, arrayM);
        meaningsLV.setAdapter(adapterM);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.spinner, R.layout.spinner_text);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerText = parent.getItemAtPosition(position).toString();
                init();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                Dialog dialog = new Dialog(FavouriteViewedActivity.this);
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

    private void init() {
        final Bundle bundle = getIntent().getExtras();

        if (bundle.getBoolean("bool")) {
            titleFav.setText("Favourites");
            query = mDatabaseFav.child(mAuth.getCurrentUser().getUid()).child(spinnerText);
        } else {
            titleFav.setText("Viewed");
            query = mDatabaseViewed.child(mAuth.getCurrentUser().getUid()).child(spinnerText);
        }

        FirebaseRecyclerOptions<Request> postsOptions = new FirebaseRecyclerOptions.Builder<Request>().setQuery(query, Request.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, FavViewHolder>(postsOptions) {

            @NonNull
            @Override
            public FavViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.favourite_viewed_item_list, viewGroup, false);

                return new FavViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FavViewHolder holder, int position, @NonNull Request model) {


                holder.setDetails(model.getWord(), model.getTranscript(), model.getImage(), model.getSearchMean());

                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playClick();
                        DatabaseReference ref = getRef(holder.getAdapterPosition());

                        Log.d(TAG, "onBindViewHolder: " + ref);
                        ref.removeValue();
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playClick();
                        ref = String.valueOf(getRef(holder.getAdapterPosition()));
                        ref = ref.replace("https://kazakhenglishstudy.firebaseio.com/", "");
                        ref = ref.replace("%20", " ");

                        isData = true;
                        getData(isData);
                    }
                });
            }

        };
        favRV.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public class FavViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageButton deleteBtn;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);

            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            mView = itemView;
        }

        public void setDetails(String mWord, String mTranscript, String mImage, String mMeaning) {
            TextView word = mView.findViewById(R.id.favWord);
            TextView transcript = mView.findViewById(R.id.favTranscript);
            CircleImageView imageView = mView.findViewById(R.id.circleImage);
            TextView meaning = mView.findViewById(R.id.favMean);

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
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
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
