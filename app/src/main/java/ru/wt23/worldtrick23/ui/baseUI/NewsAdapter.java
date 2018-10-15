package ru.wt23.worldtrick23.ui.baseUI;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaedongchicken.ytplayer.YoutubePlayerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.New;
import ru.wt23.worldtrick23.io.NewsComment;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<New> newsList;
    Context context;

    public NewsAdapter(Context context, List<New> newsList) {
        this.context = context;
        this.newsList = newsList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.news_item, viewGroup, false);
        return new NewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsAdapter.ViewHolder viewHolder, int i) {
        final New news = newsList.get(i);
        viewHolder.tvDate.setText(news.getDate());
        viewHolder.tvName.setText(news.getName());
        viewHolder.tvText.setText(news.getText());

        if (news.getVideo().equalsIgnoreCase("") || news.getVideo().equalsIgnoreCase("null")) {
            viewHolder.youtubePlayer.setVisibility(View.GONE);
            viewHolder.jzPlayer.setVisibility(View.GONE);
        } else if (news.getVideo().contains("youtube")) {
            viewHolder.jzPlayer.setVisibility(View.GONE);
            String videoId = news.getVideo();
            videoId = videoId.substring(videoId.lastIndexOf("=") + 1);
            viewHolder.youtubePlayer.initialize(videoId, new YoutubePlayerView.YouTubeListener() {
                @Override
                public void onReady() {

                }

                @Override
                public void onStateChange(YoutubePlayerView.STATE state) {

                }

                @Override
                public void onPlaybackQualityChange(String arg) {

                }

                @Override
                public void onPlaybackRateChange(String arg) {

                }

                @Override
                public void onError(String arg) {

                }

                @Override
                public void onApiChange(String arg) {

                }

                @Override
                public void onCurrentSecond(double second) {

                }

                @Override
                public void onDuration(double duration) {

                }

                @Override
                public void logs(String log) {

                }
            });
        } else {
            viewHolder.youtubePlayer.setVisibility(View.GONE);
            JZVideoPlayerStandard jzVideoPlayerStandard = viewHolder.jzPlayer;
            jzVideoPlayerStandard.setUp(news.getVideo(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
            jzVideoPlayerStandard.changeUiToPlayingShow();
            //jzVideoPlayerStandard.setDrawingCacheEnabled(true);
            //jzVideoPlayerStandard.setDrawingCacheQuality(JZVideoPlayerStandard.DRAWING_CACHE_QUALITY_LOW);
            jzVideoPlayerStandard.fullscreenButton.setVisibility(View.INVISIBLE);
        }

        viewHolder.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, DBHelper.URL.replace("/api", "") + "main/news/" + news.getNewsId());
                context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share)));
            }
        });

        viewHolder.tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(news);
            }
        });
        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiWT23 service = client.create(ApiWT23.class);
        final Call<ArrayList<NewsComment>> callNews = service.getNewsComments(news.getNewsId());
        callNews.enqueue(new Callback<ArrayList<NewsComment>>() {
            @Override
            public void onResponse(Call<ArrayList<NewsComment>> call, Response<ArrayList<NewsComment>> response1) {
                ArrayList<NewsComment> newsComments = response1.body();
                if (newsComments != null) {
                    viewHolder.tvComment.setText(String.valueOf(newsComments.size()));
                } else {
                    viewHolder.tvComment.setText("0");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<NewsComment>> call, Throwable t) {
                viewHolder.tvComment.setText("0");
            }
        });


    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvName, tvText, tvComment, tvShare;
        JZVideoPlayerStandard jzPlayer;
        YoutubePlayerView youtubePlayer;

        ViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tv_newsDate);
            tvName = (TextView) view.findViewById(R.id.tv_newsName);
            jzPlayer = (JZVideoPlayerStandard) view.findViewById(R.id.jzPlayer);
            youtubePlayer = view.findViewById(R.id.ytPlayer);
            tvText = (TextView) view.findViewById(R.id.tv_newsText);
            tvComment = (TextView) view.findViewById(R.id.tv_newsComment);
            tvShare = (TextView) view.findViewById(R.id.tv_newsShare);
        }
    }
}
