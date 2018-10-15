package ru.wt23.worldtrick23.ui;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.jaedongchicken.ytplayer.YoutubePlayerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import info.hoang8f.widget.FButton;
import okhttp3.ResponseBody;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;

import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.New;
import ru.wt23.worldtrick23.io.NewsComment;
import ru.wt23.worldtrick23.io.StreamBattle;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;
import ru.wt23.worldtrick23.ui.baseUI.NewsAdapter;

public class FragmentNews extends BaseFragment {
    LinearLayout mainLay;
    SwipeRefreshLayout swipeUpdateInfo;
    RecyclerView recyclerView;
    LinearLayout bottomLay;
    BottomSheetBehavior bottomSheetBehavior;
    List<New> newsList;

    Typeface typeface;
    Context context;
    UserDB userDB;
    DBHelper dbHelper;
    boolean mLoading = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity().getApplicationContext();
        userDB = DBHelper.getUser(context);
        if (userDB != null) pushOnline();
        typeface = getTypeface();

        mainLay = (LinearLayout) getActivity().findViewById(R.id.newsLay);
        LinearLayout llBottomSheet = (LinearLayout) getActivity().findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setHideable(true);
        bottomLay = (LinearLayout) getActivity().findViewById(R.id.bottomLay);

        swipeUpdateInfo = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeNews);
        swipeUpdateInfo.setColorSchemeColors(getResources().getColor(R.color.colorSiteRed),
                getResources().getColor(R.color.colorSiteGreen),
                getResources().getColor(R.color.colorSiteWhiteBlue),
                getResources().getColor(R.color.colorWhite));


        swipeUpdateInfo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage(null);
                swipeUpdateInfo.setRefreshing(true);
            }
        });

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_news);
        recyclerView.setHasFixedSize(true);

        loadPage(null);
        swipeUpdateInfo.setRefreshing(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        JZVideoPlayer.releaseAllVideos();
        super.onPause();
    }

    @Subscribe
    public void onMessageEvent(New news) {
        updateBottom(news.getNewsId());
    }

    private void loadPage(final List<New> listToShow) {

        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<New>> newsCall = null;
        if (listToShow == null || listToShow.size() == 0) {
            newsCall = service.getNews(DBHelper.getNewsCount(context));
        } else {
            newsCall = service.getNews(listToShow.get(listToShow.size() - 1).getNewsId(), DBHelper.getNewsCount(context));
        }

        newsCall.enqueue(new Callback<ArrayList<New>>() {
            @Override
            public void onResponse(Call<ArrayList<New>> call, final Response<ArrayList<New>> response) {
                swipeUpdateInfo.setRefreshing(false);
                if (response.isSuccessful()) {
                    final List<New> respList = response.body();
                    Log.d("*************", respList.size() + "");
                    if (listToShow == null || listToShow.size() == 0) {
                        newsList = respList;
                        NewsAdapter newsAdapter = new NewsAdapter(getActivity(), newsList);
                        newsAdapter.notifyItemRemoved(newsList.size() - 1);
                        recyclerView.swapAdapter(newsAdapter, true); //добавка
                        recyclerView.setAdapter(newsAdapter);
                    } else {
                        NewsAdapter newsAdapter = new NewsAdapter(getActivity(), newsList);
                        newsAdapter.notifyItemInserted(newsList.size() - 1);
                        newsList.addAll(respList);
                        newsAdapter.notifyDataSetChanged();
                    }


                    if (respList == null || respList.size() < DBHelper.getNewsCount(context)) {
                        mLoading = false;
                    } else {
                        mLoading = true;
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            //int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItem = linearLayoutManager.getItemCount();
                            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                            if (mLoading && lastVisibleItem == totalItem - 1) {
                                mLoading = true;
                                makeText("пополнить массив ");
                                recyclerView.clearOnScrollListeners();
                                loadPage(newsList);
                            }
                        }

                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                        }
                    });

                    //рисуем трансляцию
                   /* final List<CardView> streamCard = getStreams();
                    handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            if (msg.what == 1) {
                                if (DBHelper.getNewsShowStreams(context)) {
                                    for (int i = 0; i < streamCard.size(); i++) {
                                        streamLay.addView(streamCard.get(i), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    }
                                }
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });*/

                } else {
                    makeText(getResources().getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<New>> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(false);
                makeText(getResources().getString(R.string.no_network));
            }
        });
    }


    private void updateBottom(final String newsId) {
        bottomLay.removeAllViews();
        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<NewsComment>> callNews = service.getNewsComments(newsId);
        callNews.clone().enqueue(new Callback<ArrayList<NewsComment>>() {
            @Override
            public void onResponse(Call<ArrayList<NewsComment>> call, final Response<ArrayList<NewsComment>> response2) {
                if (response2.isSuccessful()) {
                    final ArrayList<NewsComment> newsComments = response2.body();
                    LinearLayout sendLay = new LinearLayout(context);
                    sendLay.setOrientation(LinearLayout.HORIZONTAL);
                    sendLay.setGravity(Gravity.CENTER);
                    final EditText etMyComment = new EditText(context);
                    etMyComment.setTypeface(typeface);
                    etMyComment.setMaxLines(3);
                    etMyComment.setTextColor(getResources().getColor(R.color.colorBlack));
                    etMyComment.setBackground(getResources().getDrawable(R.drawable.round_et));
                    FButton bSend = new FButton(context);
                    bSend.setTypeface(typeface);
                    bSend.setButtonColor(getResources().getColor(R.color.colorSiteWhiteBlue));
                    bSend.setTextColor(getResources().getColor(R.color.colorWhite));
                    bSend.setText(getResources().getString(R.string.send));
                    bSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //отправляем коммент
                            Call<ResponseBody> responseBodyCall = service.writeComment(userDB.getServerId(), userDB.getPassword(), etMyComment.getText().toString(), newsId);
                            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response3) {
                                    if (response3.code() == 200) {
                                        hideKeyBoard();
                                        etMyComment.setText("");
                                        loadPage(newsList);
                                        updateBottom(newsId);
                                        //TODO перевести скролл к этой новости
                                    } else {
                                        makeText(getResources().getString(R.string.no_network));
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    makeText(getResources().getString(R.string.no_network));
                                }
                            });
                        }
                    });
                    LinearLayout.LayoutParams etLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    etLP.weight = 1;
                    etLP.height = 150;
                    etLP.setMargins(10, 5, 10, 5);
                    LinearLayout.LayoutParams btLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    btLP.weight = 4;
                    btLP.height = 150;
                    btLP.setMargins(10, 5, 10, 5);
                    sendLay.setPadding(0, 23, 0, 23);
                    sendLay.addView(etMyComment, etLP);
                    sendLay.addView(bSend, btLP);
                    if (userDB == null) {
                        sendLay.setVisibility(View.GONE);
                    }
                    bottomLay.addView(sendLay);

                    if (newsComments != null) {
                        for (int j = newsComments.size() - 1; j >= 0; j--) {
                            final int finalJ = j;
                            CardView cvComment = new CardView(context);
                            cvComment.setUseCompatPadding(true);
                            LinearLayout commentLay = new LinearLayout(context);
                            commentLay.setOrientation(LinearLayout.VERTICAL);
                            commentLay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            commentLay.setPadding(10, 0, 0, 0);

                            TextView commentDate = new TextView(context);
                            commentDate.setTypeface(typeface);
                            commentDate.setTextSize(12);
                            commentDate.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                            commentDate.setText(newsComments.get(j).getDate().replace(" ", "\n"));

                            TextView login = new TextView(context);
                            login.setTypeface(typeface);
                            login.setTextSize(20);
                            login.setTextColor(getResources().getColor(R.color.colorSiteBlue));
                            login.setText(newsComments.get(j).getUserLogin() + ":");
                            login.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (userDB != null) {
                                        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                                        intent.putExtra("user_id", newsComments.get(finalJ).getUserId());
                                        startActivity(intent);
                                    }
                                }
                            });

                            TextView comment = new TextView(context);
                            comment.setTypeface(typeface);
                            comment.setTextSize(15);
                            comment.setTextColor(getResources().getColor(R.color.colorBlack));
                            comment.setText(newsComments.get(j).getComment());

                            commentLay.addView(commentDate);
                            commentLay.addView(login);
                            commentLay.addView(comment);

                            ImageView deleteComment = new ImageView(context);
                            deleteComment.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_black_24dp));
                            if (Integer.parseInt(newsComments.get(j).getUserId()) != (userDB.getServerId())) {
                                deleteComment.setVisibility(View.GONE);
                            }
                            deleteComment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Call<ResponseBody> call = service.deleteComment(newsComments.get(finalJ).getCommentId());
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response3) {
                                            if (response3.code() == 200) {
                                                makeText(getResources().getString(R.string.comment_deleted));
                                                hideKeyBoard();
                                                loadPage(newsList);
                                                updateBottom(newsId);
                                            } else {
                                                makeText(getResources().getString(R.string.no_network));
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            makeText(getResources().getString(R.string.no_network));
                                        }
                                    });
                                }
                            });

                            LinearLayout hLay = new LinearLayout(context);
                            hLay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            hLay.setOrientation(LinearLayout.HORIZONTAL);
                            hLay.setGravity(Gravity.CENTER);
                            hLay.setWeightSum(200);
                            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp1.weight = 20;
                            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp2.weight = 180;
                            hLay.addView(commentLay, lp1);
                            hLay.addView(deleteComment, lp2);

                            cvComment.addView(hLay);
                            bottomLay.addView(cvComment);
                        }
                    }

                } else {
                    ImageView iv = new ImageView(context);
                    iv.setImageDrawable(getResources().getDrawable(R.drawable.error));
                    bottomLay.addView(iv);
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onFailure(Call<ArrayList<NewsComment>> call, Throwable t) {
                ImageView iv = new ImageView(context);
                iv.setImageDrawable(getResources().getDrawable(R.drawable.error));
                bottomLay.addView(iv);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

    }

    Handler handler;

    private List<CardView> getStreams() {
        final List<CardView> list = new ArrayList<>();

        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<StreamBattle>> streamsCall = service.getStreamBattles();
        streamsCall.enqueue(new Callback<ArrayList<StreamBattle>>() {
            @Override
            public void onResponse(Call<ArrayList<StreamBattle>> call, Response<ArrayList<StreamBattle>> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    final ArrayList<StreamBattle> streams = response.body();
                    if (streams != null) {
                        for (int i = 0; i < streams.size(); i++) {
                            if (streams.get(i).getStatus().equalsIgnoreCase("EXECUTION")) {
                                CardView cardView = new CardView(context);
                                cardView.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                                //cardView.setRadius(23);
                                cardView.setUseCompatPadding(true);
                                //cardView.setCardElevation(8);
                                cardView.setForegroundGravity(Gravity.CENTER);
                                LinearLayout layout = new LinearLayout(context);
                                layout.setOrientation(LinearLayout.VERTICAL);

                                final TextView tname = new TextView(context);
                                tname.setTypeface(typeface);
                                TextView tdate = new TextView(context);
                                tdate.setTypeface(typeface);

                                tname.setGravity(Gravity.CENTER);
                                tname.setTextColor(getResources().getColor(R.color.colorWhite));
                                tname.setTextSize(30);
                                tname.setPadding(0, 40, 0, 0);
                                tname.setText(streams.get(i).getNameBattle());

                                tdate.setTextSize(13);
                                tdate.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                tdate.setGravity(Gravity.LEFT | Gravity.TOP);
                                tdate.setPadding(5, 5, 0, 0);
                                tdate.setText(streams.get(i).getDateStart());

                                CardView shapkaName = new CardView(context);
                                shapkaName.setBackgroundResource(R.drawable.oboi_shapka);
                                shapkaName.setRadius(23);
                                shapkaName.setCardElevation(0);
                                shapkaName.setContentPadding(10, 0, 10, 10);
                                shapkaName.addView(tname);
                                shapkaName.addView(tdate);

                                View view = getActivity().getLayoutInflater().inflate(R.layout.video, null);
                                JZVideoPlayerStandard player = (JZVideoPlayerStandard) view.findViewById(R.id.videoplayer);
                                YoutubePlayerView ytplayer = (YoutubePlayerView) view.findViewById(R.id.ytPlayer);
                                ytplayer.setVisibility(View.GONE);
                                player.setUp(streams.get(i).getRtmp(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, getResources().getString(R.string.stream) + " " + streams.get(i).getCategory());
                                LinearLayout videoLay = new LinearLayout(context);
                                videoLay.setOrientation(LinearLayout.VERTICAL);
                                videoLay.addView(view);


                                LinearLayout line = new LinearLayout(context);
                                line.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));

                                LinearLayout actionLay = new LinearLayout(context);
                                actionLay.setOrientation(LinearLayout.HORIZONTAL);
                                actionLay.setGravity(Gravity.END);
                                LinearLayout.LayoutParams actionLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                actionLP.setMargins(23, 23, 23, 0);

                                final TextView tvComment = new TextView(context);
                                tvComment.setTextColor(getResources().getColor(R.color.colorGray));
                                tvComment.setGravity(Gravity.CENTER);
                                tvComment.setTextSize(13);
                                tvComment.setPadding(0, 0, 0, 20);
                                tvComment.setBackground(getResources().getDrawable(R.drawable.ic_mode_comment_black_24dp));
                                //TODO сделать комментирование трансляции
                            /*final Call<ArrayList<StreamComment>> callStreamComment = service.getStreamComments(streams.get(i).getBattleId());
                            callNews.enqueue(new Callback<ArrayList<NewsComment>>() {
                                @Override
                                public void onResponse(Call<ArrayList<NewsComment>> call, Response<ArrayList<NewsComment>> response1) {
                                    ArrayList<NewsComment> newsComments = response1.body();
                                    if (newsComments != null) {
                                        tvComment.setText(String.valueOf(newsComments.size()));
                                    } else {
                                        tvComment.setText("0");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ArrayList<NewsComment>> call, Throwable t) {
                                    tvComment.setText("0");
                                }
                            });
                            tvComment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (myDB.getCheckDB())
                                        updateBottom(streams.get(finalI).getBattleId(), service);
                                }
                            });*/

                                actionLay.addView(tvComment, actionLP);


                                //отображаем нарисованное
                                LinearLayout l = new LinearLayout(context);
                                l.setGravity(17);
                                l.setOrientation(LinearLayout.VERTICAL);

                                l.addView(shapkaName);
                                l.addView(videoLay);
                                l.addView(line);
                                l.addView(actionLay);

                                cardView.addView(l);
                                list.add(cardView);
                            }
                        }
                        if (list.size() != 0) handler.sendEmptyMessage(1);
                        else handler.sendEmptyMessage(0);
                    }


                }
            }

            @Override
            public void onFailure(Call<ArrayList<StreamBattle>> call, Throwable t) {
                handler.sendEmptyMessage(0);
            }
        });
        return list;
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLay.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}