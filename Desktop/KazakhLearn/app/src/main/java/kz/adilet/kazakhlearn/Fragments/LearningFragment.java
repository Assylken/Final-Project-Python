package kz.adilet.kazakhlearn.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kz.adilet.kazakhlearn.FavouriteViewedActivity;
import kz.adilet.kazakhlearn.Model.ExampleAdapter;
import kz.adilet.kazakhlearn.Model.Examples;
import kz.adilet.kazakhlearn.Model.Request;
import kz.adilet.kazakhlearn.R;
import kz.adilet.kazakhlearn.SearchActivity;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class LearningFragment extends Fragment {

    private boolean mProcessFav = false;
    private boolean mProcessViewed = false;
    private boolean leftClicked = false;
    private boolean rightClicked = false;

    Thread thread;
    Calendar calendar;
    SimpleDateFormat hoursFormat, dateFormat, dayFormat;

    FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mDatabaseFav, mDatabaseViewed;
    SharedPreferences saveStatePref;
    int currentWordId, maxSize;
    Request request;

    Button leftBtn, rightBtn, filterBtn, checkedBtn, searchBtn;
    TextView timeTV, dateTv, dayTv, word, transcript, wordCount;
    ImageButton favouriteBtn, photoBtn, speechWord, speechExample;

    ListView meaningsLV, examplesLV;

    ArrayList<Examples> arrayE = new ArrayList<>();
    ExampleAdapter adapterE;
    Examples examples;

    ArrayAdapter adapterM;
    ArrayList<String> arrayM;

    String TAG = "myLogs", category;

    private TextToSpeech mTTS;

    MediaPlayer mp;

    Animation animation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learning, container, false);

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        mp = MediaPlayer.create(getActivity(), R.raw.click);

        hoursFormat = new SimpleDateFormat("hh:mm");
        dateFormat = new SimpleDateFormat("MMMM d");
        dayFormat = new SimpleDateFormat("EEEE");

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(1000);
                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                calendar = Calendar.getInstance();
                                timeTV.setText(String.valueOf(hoursFormat.format(calendar.getTime())));
                                dayTv.setText(String.valueOf(dayFormat.format(calendar.getTime())));
                                dateTv.setText(String.valueOf(dateFormat.format(calendar.getTime())));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();

    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialise your views
        timeTV = view.findViewById(R.id.timeTV);
        dateTv = view.findViewById(R.id.dateTV);
        dayTv = view.findViewById(R.id.dayTV);

        leftBtn = view.findViewById(R.id.chevron_left);
        rightBtn = view.findViewById(R.id.chevron_right);
        filterBtn = view.findViewById(R.id.filterBtn);
        favouriteBtn = view.findViewById(R.id.favouriteBtn);
        checkedBtn = view.findViewById(R.id.ic_check_circle);
        searchBtn = view.findViewById(R.id.searchBtn);
        photoBtn = view.findViewById(R.id.photoBtn);
        speechWord = view.findViewById(R.id.speechWord);
        speechExample = view.findViewById(R.id.speechExample);

        word = view.findViewById(R.id.word);
        transcript = view.findViewById(R.id.transcript);
        wordCount = view.findViewById(R.id.wordCount);

        meaningsLV = view.findViewById(R.id.meanings);
        examplesLV = view.findViewById(R.id.examples);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        adapterE = new ExampleAdapter(getActivity(), arrayE);
        examplesLV.setAdapter(adapterE);

        arrayM = new ArrayList<>();
        adapterM = new ArrayAdapter(getActivity(), R.layout.meaning_item_list, R.id.meaningsTV, arrayM);
        meaningsLV.setAdapter(adapterM);

        mAuth = FirebaseAuth.getInstance();

        saveStatePref = this.getActivity().getSharedPreferences("SaveState", Context.MODE_PRIVATE);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("words/categories");
        mDatabaseFav = FirebaseDatabase.getInstance().getReference().child("favourite");
        mDatabaseViewed = FirebaseDatabase.getInstance().getReference().child("viewed");
        mDatabase.keepSynced(true);
        mDatabaseFav.keepSynced(true);
        mDatabaseViewed.keepSynced(true);

        currentWordId = saveStatePref.getInt("currentWordId", 1);
        maxSize = saveStatePref.getInt("maxSize", 1);

        mTTS = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.forLanguageTag("rus"));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                        Toast.makeText(getActivity(), "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                    Toast.makeText(getActivity(), "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getData();

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                leftClicked = true;
                currentWordId--;
                if (currentWordId < 1) {
                    currentWordId = 1;
                    getData();
                } else {
                    getData();
                }
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                rightClicked = true;
                currentWordId++;
                if (currentWordId > maxSize) {
                    currentWordId = maxSize;
                    getData();
                } else {
                    getData();
                }
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                showAlertDialog();
            }
        });

        favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                addOrDeleteFavourite();
            }
        });

        checkedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedBtn.startAnimation(animation);
                playClick();
                addOrDeleteViewed();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.image_view_custom_dialog);

                ImageView image = dialog.findViewById(R.id.wordImage);

                if (request.getImage() != null && !request.getImage().equals("")) {
                    Picasso.get().load(request.getImage()).into(image);
                }

                dialog.show();
            }
        });

        speechWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                speak(request.getWord());
            }
        });

        speechExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                speak(examples.getText1());
            }
        });
    }

    private void getData() {

        arrayM.clear();
        arrayE.clear();

        category = saveStatePref.getString("Sort", "Colours");
        filterBtn.setText(category);

        mDatabaseViewed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).child(category).hasChild(String.valueOf(currentWordId))) {
                    checkedBtn.setBackgroundResource(R.drawable.ic_check_circle_true);
                    if (rightClicked) {
                        rightClicked = false;
                        currentWordId++;
                        getData();
                    } else if (leftClicked) {
                        leftClicked = false;
                        currentWordId--;
                        getData();
                    }
                } else {
                    checkedBtn.setBackgroundResource(R.drawable.ic_check_circle_false);
                    rightClicked = false;
                    leftClicked = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxSize = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child(category).child(String.valueOf(currentWordId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getKey());
                request = new Request();
                request = dataSnapshot.getValue(Request.class);
                Log.d("WTF", String.valueOf(currentWordId));
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

        wordCount.setText(currentWordId + "/" + maxSize);


        mDatabaseFav.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).child(category).hasChild(String.valueOf(currentWordId))) {
                    favouriteBtn.setImageResource(R.drawable.ic_bookmark_red);
                } else {
                    favouriteBtn.setImageResource(R.drawable.ic_bookmark_grey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SharedPreferences.Editor editor = saveStatePref.edit();
        editor.putInt("currentWordId", currentWordId);
        editor.putInt("maxSize", maxSize);
        editor.apply();

    }

    private void addOrDeleteFavourite() {
        mProcessFav = true;

        mDatabaseFav.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mProcessFav) {
                    if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).child(category).hasChild(String.valueOf(currentWordId))) {
                        mDatabaseFav.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId)).removeValue();
                        mProcessFav = false;
                    } else {
                        mDatabaseFav.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                .child("word").setValue(request.getWord());
                        mDatabaseFav.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                .child("transcript").setValue(request.getTranscript());
                        for (int i = 0; i < arrayM.size(); i++) {
                            mDatabaseFav.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                    .child("meanings").child(String.valueOf(i + 1)).setValue(arrayM.get(i));
                        }
                        mDatabaseFav.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId)).child("examples")
                                .child(String.valueOf(arrayE.size())).child("text1").setValue(examples.getText1());
                        mDatabaseFav.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId)).child("examples")
                                .child(String.valueOf(arrayE.size())).child("text2").setValue(examples.getText2());
                        mDatabaseFav.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                .child("image").setValue(request.getImage());
                        mDatabaseFav.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                .child("searchMean").setValue(request.getSearchMean());

                        mProcessFav = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addOrDeleteViewed() {
        mProcessViewed = true;

        mDatabaseViewed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessViewed) {
                    if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).child(category).hasChild(String.valueOf(currentWordId))) {
                        mDatabaseViewed.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId)).removeValue();
                        mProcessViewed = false;
                    } else {
                        mDatabaseViewed.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                .child("word").setValue(request.getWord());
                        mDatabaseViewed.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                .child("transcript").setValue(request.getTranscript());
                        for (int i = 0; i < arrayM.size(); i++) {
                            mDatabaseViewed.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                    .child("meanings").child(String.valueOf(i + 1)).setValue(arrayM.get(i));
                        }
                        mDatabaseViewed.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId)).child("examples")
                                .child(String.valueOf(arrayE.size())).child("text1").setValue(examples.getText1());
                        mDatabaseViewed.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId)).child("examples")
                                .child(String.valueOf(arrayE.size())).child("text2").setValue(examples.getText2());
                        mDatabaseViewed.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                .child("image").setValue(request.getImage());
                        mDatabaseViewed.child(mAuth.getCurrentUser().getUid()).child(category).child(String.valueOf(currentWordId))
                                .child("searchMean").setValue(request.getSearchMean());

                        checkedBtn.setBackgroundResource(R.drawable.ic_check_circle_true);
                        mProcessViewed = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void showAlertDialog() {
        String[] options = {"Colours", "Food and drinks", "Numbers", "Shopping and services", "Favourites", "Viewed"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sorting:");
        builder.setIcon(R.drawable.ic_sort);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    SharedPreferences.Editor editor = saveStatePref.edit();
                    editor.putString("Sort", "Colours");
                    editor.apply();
                    currentWordId = 1;
                    getData();
                }
                if (which == 1) {
                    SharedPreferences.Editor editor = saveStatePref.edit();
                    editor.putString("Sort", "Food and drinks");
                    editor.apply();
                    currentWordId = 1;
                    getData();
                }
                if (which == 2) {
                    SharedPreferences.Editor editor = saveStatePref.edit();
                    editor.putString("Sort", "Numbers");
                    editor.apply();
                    currentWordId = 1;
                    getData();
                }
                if (which == 3) {
                    SharedPreferences.Editor editor = saveStatePref.edit();
                    editor.putString("Sort", "Shopping and services");
                    editor.apply();
                    currentWordId = 1;
                    getData();
                }
                if (which == 4) {
                    Intent intent = new Intent(getActivity(), FavouriteViewedActivity.class);
                    intent.putExtra("bool", true);
                    startActivity(intent);
                }
                if (which == 5) {
                    Intent intent = new Intent(getActivity(), FavouriteViewedActivity.class);
                    intent.putExtra("bool", false);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }

    private void speak(String text) {

        mTTS.setPitch(1f);
        mTTS.setSpeechRate(1f);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void playClick() {
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = MediaPlayer.create(getActivity(), R.raw.click);
            }
            mp.start();
        } catch (Exception e) {
            System.out.println("RAL");
            e.printStackTrace();
        }
    }
}
