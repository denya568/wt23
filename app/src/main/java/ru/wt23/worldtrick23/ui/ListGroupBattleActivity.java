package ru.wt23.worldtrick23.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.ResponseBody;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.GroupBattle;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import ru.wt23.worldtrick23.io.UsersGroupBattle;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListGroupBattleActivity extends AppCompatActivity {
    DBHelper dbHelper;
    LinearLayout lay;
    SwipeRefreshLayout swipeUpdateInfo;
    ActionBar actionBar;
    String categoryIntent;

    Typeface typeface;
    Context context;
    UserDB userDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group_battle);

        context = this;
        userDB = DBHelper.getUser(context);
        //PushOnline.push(myDB.getId());

        typeface = Typeface.createFromAsset(getAssets(), "fonts/planet_n2_cyr_lat.otf");

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_background));

        categoryIntent = getIntent().getStringExtra("category");

        if (categoryIntent.equals("breakdance")) {
            actionBar.setSubtitle(getResources().getString(R.string.break_dance) + " - " + getResources().getString(R.string.group_battles));
        }
        if (categoryIntent.equals("parkour")) {
            actionBar.setSubtitle(getResources().getString(R.string.parkour) + " - " + getResources().getString(R.string.group_battles));
        }
        if (categoryIntent.equals("tricking")) {
            actionBar.setSubtitle(getResources().getString(R.string.tricking) + " - " + getResources().getString(R.string.group_battles));
        }
        if (categoryIntent.equals("trampoline")) {
            actionBar.setSubtitle(getResources().getString(R.string.trampoline) + " - " + getResources().getString(R.string.group_battles));
        }
        lay = (LinearLayout) findViewById(R.id.groupBreakBattleLay);

        swipeUpdateInfo = (SwipeRefreshLayout) findViewById(R.id.swipeGroupBreak);
        swipeUpdateInfo.setColorSchemeColors(getResources().getColor(R.color.colorSiteRed),
                getResources().getColor(R.color.colorSiteGreen),
                getResources().getColor(R.color.colorSiteWhiteBlue),
                getResources().getColor(R.color.colorWhite));

        swipeUpdateInfo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage();
            }
        });
        loadPage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void acceptBattle(String battleId) {
        swipeUpdateInfo.setRefreshing(true);
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 apiWT23 = retrofit.create(ApiWT23.class);
        Call<ResponseBody> call = apiWT23.acceptGroupBattle(battleId, userDB.getServerId(), "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                swipeUpdateInfo.setRefreshing(false);
                if (response.code() == 200) {
                    makeToast(getResources().getString(R.string.you_are_down));
                    loadPage();
                } else if (response.code() == 503) {
                    makeToast(getResources().getString(R.string.you_late));
                    loadPage();
                } else {
                    makeToast(getResources().getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(false);
                makeToast(getResources().getString(R.string.no_network));
            }
        });

    }

    private void loadPage() {
        swipeUpdateInfo.setRefreshing(true);
        lay.removeAllViews();

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<GroupBattle>> groupBattlesCall = service.getGroupBattles();
        groupBattlesCall.enqueue(new Callback<ArrayList<GroupBattle>>() {
            @Override
            public void onResponse(Call<ArrayList<GroupBattle>> call, Response<ArrayList<GroupBattle>> response) {
                swipeUpdateInfo.setRefreshing(false);

                if (response.isSuccessful()) {
                    final ArrayList<GroupBattle> groupBattles = new ArrayList<>();
                    ArrayList<GroupBattle> battles = response.body();
                    for (int i = 0; i < battles.size(); i++) {
                        if (battles.get(i).getCategory().equalsIgnoreCase(categoryIntent) && battles.get(i).getStatus().equalsIgnoreCase("ACTIVE")) {
                            groupBattles.add(battles.get(i));
                        }
                    }

                    if (groupBattles.size() == 0) {
                        TextView tv = new TextView(getApplicationContext());
                        tv.setTypeface(typeface);
                        tv.setTextColor(getResources().getColor(R.color.colorBlack));
                        tv.setText(getResources().getString(R.string.no_battles));
                        lay.addView(tv);
                    } else {
                        LinearLayout mainLay = new LinearLayout(getApplicationContext());
                        mainLay.setGravity(Gravity.CENTER);
                        mainLay.setOrientation(LinearLayout.VERTICAL);

                        final int[] column = {0};
                        final int[] row = {0};

                        for (int i = 0; i < groupBattles.size(); i++) {
                            final int finalI = i;

                            final GridLayout ll = new GridLayout(getApplicationContext());
                            ll.setOrientation(GridLayout.VERTICAL);
                            ll.setColumnCount(2);
                            ll.setPadding(10, 10, 10, 10);

                            final Button signIn = new Button(getApplicationContext());
                            signIn.setVisibility(View.INVISIBLE);
                            signIn.setTypeface(typeface);
                            signIn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            signIn.setBackground(getResources().getDrawable(R.drawable.round_button));
                            signIn.setText(getResources().getString(R.string.join));
                            signIn.setTextColor(getResources().getColor(R.color.colorWhite));
                            signIn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    acceptBattle(groupBattles.get(finalI).getBattleId());
                                }
                            });
                            LinearLayout.LayoutParams shapkaLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            shapkaLP.setMargins(20, 0, 0, 0);
                            TextView tvSeason = new TextView(getApplicationContext());
                            tvSeason.setTypeface(typeface);
                            tvSeason.setTextColor(getResources().getColor(R.color.colorWhite));
                            tvSeason.setGravity(Gravity.CENTER);
                            tvSeason.setTextSize(20);
                            tvSeason.setPadding(10, 10, 10, 10);
                            tvSeason.setText(getResources().getString(R.string.season) + ": " + "такой-то");

                            ImageView ivUsers = new ImageView(getApplicationContext());
                            ivUsers.setImageDrawable(getResources().getDrawable(R.drawable.ic_people_outline_black_24dp));
                            final TextView tvUsers = new TextView(getApplicationContext());
                            tvUsers.setTypeface(typeface);
                            tvUsers.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                            tvUsers.setLayoutParams(shapkaLP);
                            tvUsers.setPadding(20, 0, 23, 0);
                            tvUsers.setGravity(Gravity.START | Gravity.CENTER);


                            OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(DBHelper.URL)
                                    .client(okHttpClient)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            ApiWT23 service = retrofit.create(ApiWT23.class);
                            Call<ArrayList<UsersGroupBattle>> usersCall = service.getUsersGroupBattle(groupBattles.get(i).getBattleId());
                            usersCall.enqueue(new Callback<ArrayList<UsersGroupBattle>>() {
                                @Override
                                public void onResponse(Call<ArrayList<UsersGroupBattle>> call, Response<ArrayList<UsersGroupBattle>> response) {
                                    if (response.isSuccessful()) {
                                        final ArrayList<UsersGroupBattle> users = response.body();

                                        if (users == null || users.size() == 0) {
                                            signIn.setVisibility(View.VISIBLE);
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setTypeface(typeface);
                                            tv.setTextColor(getResources().getColor(R.color.colorBlack));
                                            tv.setTextSize(25);
                                            tv.setText(getResources().getString(R.string.no_one_recorded));
                                            ll.addView(tv);
                                            tvUsers.setText(getResources().getString(R.string.users_count) + ": (0/" + groupBattles.get(finalI).getMaxUsers() + ")");
                                        } else {
                                            tvUsers.setText(getResources().getString(R.string.users_count) + ": (" + users.size() + "/" + groupBattles.get(finalI).getMaxUsers() + ")");
                                            for (int j = 0; j < users.size(); j++) {
                                                if (Integer.parseInt(users.get(j).getUserId())==(userDB.getServerId())) {
                                                    signIn.setVisibility(View.GONE);
                                                    break;
                                                } else {
                                                    signIn.setVisibility(View.VISIBLE);
                                                }
                                            }
                                            for (int j = 0; j < users.size(); j++) {
                                                final int finalJ = j;

                                                if (column[0] >= 2) {
                                                    column[0] = 0;
                                                    row[0]++;
                                                }
                                                GridLayout.LayoutParams lpr = new GridLayout.LayoutParams();
                                                lpr.columnSpec = GridLayout.spec(column[0]);
                                                lpr.setGravity(Gravity.CENTER);
                                                lpr.rowSpec = GridLayout.spec(row[0]);
                                                lpr.setMargins(10, 10, 10, 10);
                                                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                                                lpr.width = (int) (displayMetrics.widthPixels / 2.4);
                                                lpr.height = displayMetrics.heightPixels / 20;

                                                final Button login = new Button(getApplicationContext());
                                                if (Integer.parseInt(users.get(j).getUserId())==(userDB.getServerId())) {
                                                    login.setTextColor(getResources().getColor(R.color.colorWhite));
                                                    login.setBackground(getResources().getDrawable(R.drawable.round_button_yolo));
                                                } else {
                                                    login.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                                    login.setTextColor(getResources().getColor(R.color.colorBlack));
                                                    login.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(ListGroupBattleActivity.this, UserInfoActivity.class);
                                                            intent.putExtra("user_id", users.get(finalJ).getUserId());
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                                login.setTypeface(typeface);
                                                login.setText(users.get(j).getUserLogin());
                                                login.setGravity(Gravity.CENTER);
                                                ll.addView(login, lpr);
                                                column[0]++;
                                            }
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<ArrayList<UsersGroupBattle>> call, Throwable t) {

                                }
                            });

                            LinearLayout usersLay = new LinearLayout(getApplicationContext());
                            usersLay.setGravity(Gravity.START);
                            usersLay.setOrientation(LinearLayout.HORIZONTAL);
                            usersLay.addView(ivUsers);
                            usersLay.addView(tvUsers);

                            /*ImageView ivDate = new ImageView(getApplicationContext());
                            ivDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_history_black_24dp));
                            TextView tvDate = new TextView(getApplicationContext());
                            tvDate.setTypeface(typeface);
                            tvDate.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                            tvDate.setLayoutParams(shapkaLP);
                            tvDate.setPadding(20, 0, 23, 0);
                            tvDate.setGravity(Gravity.START | Gravity.CENTER);
                            tvDate.setText(getResources().getString(R.string.date) + ": (" + dateStart.get(i) + ")");

                            LinearLayout dateLay = new LinearLayout(getApplicationContext());
                            dateLay.setGravity(Gravity.START);
                            dateLay.setOrientation(LinearLayout.HORIZONTAL);
                            dateLay.addView(ivDate);
                            dateLay.addView(tvDate);*/

                            ImageView ivJudge = new ImageView(getApplicationContext());
                            ivJudge.setImageDrawable(getResources().getDrawable(R.drawable.ic_airline_seat_recline_normal_black_24dp));
                            TextView tvJudge = new TextView(getApplicationContext());
                            tvJudge.setTypeface(typeface);
                            tvJudge.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                            tvJudge.setLayoutParams(shapkaLP);
                            tvJudge.setPadding(20, 0, 23, 0);
                            tvJudge.setGravity(Gravity.START | Gravity.CENTER);
                            tvJudge.setText(getResources().getString(R.string.judge) + ": " + groupBattles.get(i).getJudge());

                            LinearLayout judgeLay = new LinearLayout(getApplicationContext());
                            judgeLay.setGravity(Gravity.START);
                            judgeLay.setOrientation(LinearLayout.HORIZONTAL);
                            judgeLay.addView(ivJudge);
                            judgeLay.addView(tvJudge);


                            LinearLayout titleCard = new LinearLayout(getApplicationContext());
                            //titleCard.setBackgroundColor(getResources().getColor(R.color.colorSiteBlue));
                            titleCard.setBackground(getResources().getDrawable(R.drawable.oboi_shapka));
                            titleCard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            titleCard.setOrientation(LinearLayout.VERTICAL);
                            titleCard.setGravity(Gravity.CENTER);
                            titleCard.setPadding(0, 0, 0, 23);

                            //titleCard.addView(tvSeason);
                            titleCard.addView(usersLay);
                            //titleCard.addView(dateLay);
                            titleCard.addView(judgeLay);

                            CardView cardView = new CardView(getApplicationContext());
                            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                            cardView.setContentPadding(0, 0, 0, 10);
                            cardView.setRadius(23);
                            cardView.setUseCompatPadding(true);
                            cardView.setCardElevation(8);
                            cardView.setForegroundGravity(Gravity.CENTER);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                            mainLay.addView(titleCard);
                            mainLay.addView(ll, lp);
                            mainLay.addView(signIn);

                            cardView.addView(mainLay);

                            lay.addView(cardView);
                        }
                    }

                } else {
                    makeToast(getResources().getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GroupBattle>> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(false);
            }
        });

    }

}