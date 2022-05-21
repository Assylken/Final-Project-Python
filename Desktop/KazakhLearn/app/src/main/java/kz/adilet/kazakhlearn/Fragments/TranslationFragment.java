package kz.adilet.kazakhlearn.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import kz.adilet.kazakhlearn.R;

public class TranslationFragment extends Fragment {

    Button cyrToLat, latToCyr;
    EditText inputET, outputET;
    String inputText, outputText;

    MediaPlayer mp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translation, container, false);

        cyrToLat = rootView.findViewById(R.id.cyrToLat);
        latToCyr = rootView.findViewById(R.id.latToCyr);

        inputET = rootView.findViewById(R.id.source_text);
        outputET = rootView.findViewById(R.id.output_text);

        cyrToLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                cyrToLatTrans();
            }
        });

        latToCyr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                latToCyrTrans();
            }
        });

        return rootView;
    }

    private void latToCyrTrans() {
        inputText = inputET.getText().toString();
        inputText = inputText.replace('Á','Ә');
        inputText = inputText.replace('á','ә');
        inputText = inputText.replace('B','Б');
        inputText = inputText.replace('b','б');
        inputText = inputText.replace('V','В');
        inputText = inputText.replace('v','в');
        inputText = inputText.replace('G','Г');
        inputText = inputText.replace('g','г');
        inputText = inputText.replace('Ǵ','Ғ');
        inputText = inputText.replace('ǵ','ғ');
        inputText = inputText.replace('D','Д');
        inputText = inputText.replace('d','д');
        inputText = inputText.replace('J','Ж');
        inputText = inputText.replace('j','ж');
        inputText = inputText.replace('Z','З');
        inputText = inputText.replace('z','з');
        inputText = inputText.replace('I','И');
        inputText = inputText.replace('ı','и');
        inputText = inputText.replace('I','Й');
        inputText = inputText.replace('ı','й');
        inputText = inputText.replace('K','К');
        inputText = inputText.replace('k','к');
        inputText = inputText.replace('Q','Қ');
        inputText = inputText.replace('q','қ');
        inputText = inputText.replace('L','Л');
        inputText = inputText.replace('l','л');
        inputText = inputText.replace('M','М');
        inputText = inputText.replace('m','м');
        inputText = inputText.replace('N','Н');
        inputText = inputText.replace('n','н');
        inputText = inputText.replace('Ń','Ң');
        inputText = inputText.replace('ń','ң');
        inputText = inputText.replace('Ó','Ө');
        inputText = inputText.replace('ó','ө');
        inputText = inputText.replace('P','П');
        inputText = inputText.replace('p','п');
        inputText = inputText.replace('R','Р');
        inputText = inputText.replace('r','р');
        inputText = inputText.replace('S','С');
        inputText = inputText.replace('s','с');
        inputText = inputText.replace('T','Т');
        inputText = inputText.replace('t','т');
        inputText = inputText.replace('Ý','У');
        inputText = inputText.replace('ý','у');
        inputText = inputText.replace('U','Ұ');
        inputText = inputText.replace('u','ұ');
        inputText = inputText.replace('Ú','Ү');
        inputText = inputText.replace('ú','ү');
        inputText = inputText.replace('F','Ф');
        inputText = inputText.replace('f','ф');
        inputText = inputText.replace('H','Х');
        inputText = inputText.replace('h','х');
        inputText = inputText.replace('H','Һ');
        inputText = inputText.replace('h','һ');
        inputText = inputText.replace("Ch","Ч");
        inputText = inputText.replace("ch","ч");
        inputText = inputText.replace("Sh","Ш");
        inputText = inputText.replace("sh","ш");
        inputText = inputText.replace('Y','Ы');
        inputText = inputText.replace('y','ы');
        inputText = inputText.replace("Iý","Ю");
        inputText = inputText.replace("ıý","ю");
        inputText = inputText.replace("Ia","Я");
        inputText = inputText.replace("ıa","я");
        outputText = inputText;
        outputET.setText(outputText);
    }

    private void cyrToLatTrans() {
        inputText = inputET.getText().toString();
        inputText = inputText.replace('Ә', 'Á');
        inputText = inputText.replace('ә', 'á');
        inputText = inputText.replace('Б', 'B');
        inputText = inputText.replace('б','b');
        inputText = inputText.replace('В','V');
        inputText = inputText.replace('в','v');
        inputText = inputText.replace('Г','G');
        inputText = inputText.replace('г','g');
        inputText = inputText.replace('Ғ','Ǵ');
        inputText = inputText.replace('ғ','ǵ');
        inputText = inputText.replace('Д','D');
        inputText = inputText.replace('д','d');
        inputText = inputText.replace('Ж','J');
        inputText = inputText.replace('ж','j');
        inputText = inputText.replace('З','Z');
        inputText = inputText.replace('з','z');
        inputText = inputText.replace('И','I');
        inputText = inputText.replace('и','ı');
        inputText = inputText.replace('Й','I');
        inputText = inputText.replace('й','ı');
        inputText = inputText.replace('К','K');
        inputText = inputText.replace('к','k');
        inputText = inputText.replace('Қ','Q');
        inputText = inputText.replace('қ','q');
        inputText = inputText.replace('Л','L');
        inputText = inputText.replace('л','l');
        inputText = inputText.replace('М','M');
        inputText = inputText.replace('м','m');
        inputText = inputText.replace('Н','N');
        inputText = inputText.replace('н','n');
        inputText = inputText.replace('Ң','Ń');
        inputText = inputText.replace('ң','ń');
        inputText = inputText.replace('Ө','Ó');
        inputText = inputText.replace('ө','ó');
        inputText = inputText.replace('П','P');
        inputText = inputText.replace('п','p');
        inputText = inputText.replace('Р','R');
        inputText = inputText.replace('р','r');
        inputText = inputText.replace('С','S');
        inputText = inputText.replace('с','s');
        inputText = inputText.replace('Т','T');
        inputText = inputText.replace('т','t');
        inputText = inputText.replace('У','Ý');
        inputText = inputText.replace('у','ý');
        inputText = inputText.replace('Ұ','U');
        inputText = inputText.replace('ұ','u');
        inputText = inputText.replace('Ү','Ú');
        inputText = inputText.replace('ү','ú');
        inputText = inputText.replace('Ф','F');
        inputText = inputText.replace('ф','f');
        inputText = inputText.replace('Х','H');
        inputText = inputText.replace('х','h');
        inputText = inputText.replace('Һ','H');
        inputText = inputText.replace('һ','h');
        inputText = inputText.replace("Ч","Ch");
        inputText = inputText.replace("ч","ch");
        inputText = inputText.replace("Ш","Sh");
        inputText = inputText.replace("ш","sh");
        inputText = inputText.replace('Ы','Y');
        inputText = inputText.replace('ы','y');
        inputText = inputText.replace("Ъ","");
        inputText = inputText.replace("ъ","");
        inputText = inputText.replace("Ь","");
        inputText = inputText.replace("ь","");
        inputText = inputText.replace("Ю","Iý");
        inputText = inputText.replace("ю","ıý");
        inputText = inputText.replace("Я","Ia");
        inputText = inputText.replace("я","ıa");

        outputText = inputText;
        outputET.setText(outputText);
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
            } mp.start();
        } catch(Exception e) { e.printStackTrace(); }
    }
}