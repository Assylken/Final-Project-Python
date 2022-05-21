package kz.adilet.kazakhlearn.Fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

import kz.adilet.kazakhlearn.R;

public class ContentFragment extends Fragment {
    private ArrayList<YouTubePlayerView> youTubePlayerView = new ArrayList<>();
    private ArrayList<String> videoIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        youTubePlayerView.add(view.findViewById(R.id.youtube_player_view));
        youTubePlayerView.add(view.findViewById(R.id.youtube_player_view2));
        youTubePlayerView.add(view.findViewById(R.id.youtube_player_view3));
        youTubePlayerView.add(view.findViewById(R.id.youtube_player_view4));

        videoIds.add("qWp5NDkCdm4");
        videoIds.add("gyRFJ6DqSd4");
        videoIds.add("pkADX9b9i8A");
        videoIds.add("z-f83D2DwD0");

        initYouTubePlayerView();
        return view;
    }

    private void initYouTubePlayerView() {
        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .rel(0)
                .ivLoadPolicy(1)
                .ccLoadPolicy(1)
                .build();

        for (int i = 0; i < youTubePlayerView.size(); i++) {
            getLifecycle().addObserver(youTubePlayerView.get(i));

            int finalI = i;
            youTubePlayerView.get(i).initialize(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    String videoId = videoIds.get(finalI);
                    youTubePlayer.cueVideo(videoId, 0);
                }
            }, true, iFramePlayerOptions);
        }

    }
}