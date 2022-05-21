package kz.adilet.kazakhlearn.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Random;

import kz.adilet.kazakhlearn.FavouriteViewedActivity;
import kz.adilet.kazakhlearn.Model.Request;
import kz.adilet.kazakhlearn.R;

public class QuizFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseFav, mDatabaseSearch;
    private Button[] buttons;
    private Button submit_btn, filterBtn;
    private TextView wordTV, correctness, questionCounter;

    private ProgressBar timerPB;
    private TextView timerTV;
    private CountDownTimer countDownTimer;

    private String category = "Colours";

    private RelativeLayout quiz_diff_rl, quiz_rl;
    private Button quiz_easy_btn, quiz_med_btn, quiz_hard_btn;

    private Request request;

    private ArrayList<String> word = new ArrayList<>();
    private ArrayList<String> answer = new ArrayList<>();
    private ArrayList<String> options = new ArrayList<>();
    private Random random = new Random();

    private int count, correctCount, difficulty;

    private MediaPlayer mp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseFav = FirebaseDatabase.getInstance().getReference().child("words/categories");
        mDatabaseSearch = FirebaseDatabase.getInstance().getReference().child("search");

        mDatabaseFav.keepSynced(true);
        mDatabaseSearch.keepSynced(true);

        buttons = new Button[4];
        buttons[0] = rootView.findViewById(R.id.btn1);
        buttons[1] = rootView.findViewById(R.id.btn2);
        buttons[2] = rootView.findViewById(R.id.btn3);
        buttons[3] = rootView.findViewById(R.id.btn4);
        wordTV = rootView.findViewById(R.id.wordTV);
        correctness = rootView.findViewById(R.id.correctness);
        questionCounter = rootView.findViewById(R.id.questionCounter);
        submit_btn = rootView.findViewById(R.id.submit_btn);

        timerPB = rootView.findViewById(R.id.progress_bar_timer);
        timerTV = rootView.findViewById(R.id.text_view_progress);

        quiz_diff_rl = rootView.findViewById(R.id.quiz_diff_rl);
        quiz_rl = rootView.findViewById(R.id.quiz_rl);
        quiz_easy_btn = rootView.findViewById(R.id.quiz_easy_btn);
        quiz_med_btn = rootView.findViewById(R.id.quiz_med_btn);
        quiz_hard_btn = rootView.findViewById(R.id.quiz_hard_btn);

        filterBtn = rootView.findViewById(R.id.filterBtn);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        quiz_easy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                difficulty = 30000;
                quiz_diff_rl.setVisibility(View.GONE);
                quiz_rl.setVisibility(View.VISIBLE);
                fillArrays();
            }
        });

        quiz_med_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                difficulty = 15000;
                quiz_diff_rl.setVisibility(View.GONE);
                quiz_rl.setVisibility(View.VISIBLE);
                fillArrays();
            }
        });

        quiz_hard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                difficulty = 5000;
                quiz_diff_rl.setVisibility(View.GONE);
                quiz_rl.setVisibility(View.VISIBLE);
                fillArrays();
            }
        });

        return rootView;
    }

    private void fillArrays() {
        mDatabaseFav.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d("dD", "onDataChange: " + dataSnapshot1.getKey());

                    request = new Request();
                    request = dataSnapshot1.getValue(Request.class);
                    word.add(request.getWord());
                    answer.add(request.getSearchMean());
                    Log.d("dD", "onDataChange: " + request.getWord());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseSearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d("dD", "onDataChange: " + dataSnapshot1.getKey());
                    request = new Request();
                    request = dataSnapshot1.getValue(Request.class);
                    options.add(request.getSearchMean());
                    Log.d("dD", "onDataChange: " + request.getWord());
                }
                count = 0;
                if (word.size() == 0) {
                    Toast.makeText(getActivity(), "Please, add words in favourite to start quiz", Toast.LENGTH_SHORT).show();
                } else {
                    execute();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void execute() {
        setButtonsClickable(true);
        setButtonsUnSelected();
        correctness.setVisibility(View.GONE);
        final int n = random.nextInt(4);
        buttons[n].setText(answer.get(count));
        wordTV.setText(word.get(count));
        questionCounter.setText((count + 1) + "/" + answer.size());

        for (int i = 0; i < buttons.length; i++) {
            if (i != n) {
                int z = random.nextInt(options.size());
                buttons[i].setText(options.get(z));
            }
        }

        buttons[0].setOnClickListener(this);
        buttons[1].setOnClickListener(this);
        buttons[2].setOnClickListener(this);
        buttons[3].setOnClickListener(this);

        startTimer();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                setButtonsClickable(false);
                countDownTimer.cancel();
                buttons[n].setBackgroundResource(R.drawable.rounded_button_correct);
                if (buttons[n].getTag() == "Clicked") {
                    correctness.setText("Correct");
                    correctness.setTextColor(Color.GREEN);
                    correctness.setVisibility(View.VISIBLE);
                    correctCount++;
                } else {
                    correctness.setText("Wrong");
                    correctness.setTextColor(Color.RED);
                    correctness.setVisibility(View.VISIBLE);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count++;
                        if (count == answer.size()) {
                            correctness.setText("Correct " + correctCount + "/" + answer.size());
                            correctness.setTextColor(Color.WHITE);
                            correctness.setVisibility(View.VISIBLE);
                        } else {
                            execute();
                        }
                    }
                }, 2000);

            }
        });
    }

    @Override
    public void onClick(View v) {
        playClick();
        deleteTag();
        setButtonsUnSelected();
        v.setBackgroundResource(R.drawable.rounded_button_selected);
        v.setTag("Clicked");
    }

    private void deleteTag() {
        buttons[0].setTag("");
        buttons[1].setTag("");
        buttons[2].setTag("");
        buttons[3].setTag("");
    }

    private void setButtonsUnSelected() {
        buttons[0].setBackgroundResource(R.drawable.rounded_button_transparent);
        buttons[1].setBackgroundResource(R.drawable.rounded_button_transparent);
        buttons[2].setBackgroundResource(R.drawable.rounded_button_transparent);
        buttons[3].setBackgroundResource(R.drawable.rounded_button_transparent);
    }

    private void setButtonsClickable(boolean clickable) {
        buttons[0].setClickable(clickable);
        buttons[1].setClickable(clickable);
        buttons[2].setClickable(clickable);
        buttons[3].setClickable(clickable);
        submit_btn.setClickable(clickable);
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

    public void playClick() {
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = MediaPlayer.create(getActivity(), R.raw.click);
            }
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(difficulty, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTV.setText(millisUntilFinished / 1000 + "s");
                timerPB.setProgress((int) ((100 * (millisUntilFinished / 1000))) / (difficulty / 1000));
            }

            public void onFinish() {
                count++;
                if (count == answer.size()) {
                    correctness.setText("Correct " + correctCount + "/" + answer.size());
                    correctness.setTextColor(Color.WHITE);
                    correctness.setVisibility(View.VISIBLE);
                } else {
                    execute();
                }
            }

        }.start();
    }

    private void showAlertDialog() {
        String[] options = {"Colours", "Food and drinks", "Numbers", "Shopping and services"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Category:");
        builder.setIcon(R.drawable.ic_sort);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                category = options[which];
                filterBtn.setText(category);
            }
        });
        builder.show();
    }
}