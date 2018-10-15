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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.GroupBattle;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import ru.wt23.worldtrick23.io.UsersGroupBattle;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListAllInprogressGroupBattlesActivity extends AppCompatActivity {
    DBHelper dbHelper;
    LinearLayout lay;
    SwipeRefreshLayout swipeUpdateInfo;
    Button shTrick, shPark, shBreak, shTramp;

    Typeface typeface;
    Context context;
    UserDB userDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_inprogress_group_battles);

        context = this;
        userDB = DBHelper.getUser(context);
        //PushOnline.push(myDB.getId());

        typeface = Typeface.createFromAsset(getAssets(), "fonts/planet_n2_cyr_lat.otf");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(getResources().getString(R.string.group_battles));
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_background));

        shTrick = (Button) findViewById(R.id.shListTrickG);
        shTrick.setTypeface(typeface);
        shPark = (Button) findViewById(R.id.shListParkG);
        shPark.setTypeface(typeface);
        shBreak = (Button) findViewById(R.id.shListBreakG);
        shBreak.setTypeface(typeface);
        shTramp = (Button) findViewById(R.id.shListTrampG);
        shTramp.setTypeface(typeface);

        lay = (LinearLayout) findViewById(R.id.groupAllBattlesLay);

        swipeUpdateInfo = (SwipeRefreshLayout) findViewById(R.id.swipeAllGroup);
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

    private String getCategory(String cat) {
        String r = "category";
        if (cat.equalsIgnoreCase("tricking")) {
            r = getResources().getString(R.string.tricking);
        }
        if (cat.equalsIgnoreCase("breakdance")) {
            r = getResources().getString(R.string.break_dance);
        }
        if (cat.equalsIgnoreCase("trampoline")) {
            r = getResources().getString(R.string.trampoline);
        }
        if (cat.equalsIgnoreCase("parkour")) {
            r = getResources().getString(R.string.parkour);
        }
        return r;
    }

    private void loadPage() {
        lay.removeAllViews();
        swipeUpdateInfo.setRefreshing(true);
        shTrick.setBackground(getResources().getDrawable(R.drawable.round_button_white));
        shPark.setBackground(getResources().getDrawable(R.drawable.round_button_white));
        shBreak.setBackground(getResources().getDrawable(R.drawable.round_button_white));
        shTramp.setBackground(getResources().getDrawable(R.drawable.round_button_white));

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit clientBattles = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 serviceBattles = clientBattles.create(ApiWT23.class);
        Call<ArrayList<GroupBattle>> groupBattleCall = serviceBattles.getGroupBattles();
        groupBattleCall.enqueue(new Callback<ArrayList<GroupBattle>>() {
            @Override
            public void onResponse(Call<ArrayList<GroupBattle>> call, Response<ArrayList<GroupBattle>> response) {
                swipeUpdateInfo.setRefreshing(false);
                if (response.code() == 200) {
                    final ArrayList<GroupBattle> groupBattles = response.body();

                    int sizeTrick = 0;
                    int sizePark = 0;
                    int sizeBreak = 0;
                    int sizeTramp = 0;

                    //
                    if (groupBattles == null || groupBattles.size() == 0) {
                        TextView tv = new TextView(getApplicationContext());
                        tv.setTypeface(typeface);
                        tv.setTextColor(getResources().getColor(R.color.colorBlack));
                        tv.setText(getResources().getString(R.string.no_battles));
                        lay.addView(tv);
                    } else {

                        final LinearLayout llTrick = new LinearLayout(getApplicationContext());
                        llTrick.setPadding(8, 8, 8, 8);
                        llTrick.setGravity(Gravity.CENTER | Gravity.TOP);
                        llTrick.setOrientation(LinearLayout.VERTICAL);

                        final LinearLayout llPark = new LinearLayout(getApplicationContext());
                        llPark.setPadding(8, 8, 8, 8);
                        llPark.setGravity(Gravity.CENTER | Gravity.TOP);
                        llPark.setOrientation(LinearLayout.VERTICAL);

                        final LinearLayout llBreak = new LinearLayout(getApplicationContext());
                        llBreak.setPadding(8, 8, 8, 8);
                        llBreak.setGravity(Gravity.CENTER | Gravity.TOP);
                        llBreak.setOrientation(LinearLayout.VERTICAL);

                        final LinearLayout llTramp = new LinearLayout(getApplicationContext());
                        llTramp.setPadding(8, 8, 8, 8);
                        llTramp.setGravity(Gravity.CENTER | Gravity.TOP);
                        llTramp.setOrientation(LinearLayout.VERTICAL);


                        for (int i = 0; i < groupBattles.size(); i++) {
                            final int finalI = i;
                            if (groupBattles.get(i).getStatus().equalsIgnoreCase("EXECUTION")) {
                                //if (groupBattles.get(i).getStatus().equalsIgnoreCase("COMPLETED")) {

                                if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("tricking"))
                                    sizeTrick++;
                                else if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("parkour"))
                                    sizePark++;
                                else if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("breakdance"))
                                    sizeBreak++;
                                else if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("trampoline"))
                                    sizeTramp++;

                                OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
                                Retrofit clientUsers = new Retrofit.Builder()
                                        .baseUrl(DBHelper.URL)
                                        .client(okHttpClient)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                ApiWT23 serviceUsers = clientUsers.create(ApiWT23.class);
                                Call<ArrayList<UsersGroupBattle>> usersGroupBatteCall = serviceUsers.getUsersGroupBattle(groupBattles.get(i).getBattleId());
                                usersGroupBatteCall.enqueue(new Callback<ArrayList<UsersGroupBattle>>() {
                                    @Override
                                    public void onResponse(Call<ArrayList<UsersGroupBattle>> call, Response<ArrayList<UsersGroupBattle>> response) {
                                        if (response.isSuccessful()) {

                                            final ArrayList<UsersGroupBattle> usersGroupBattles = response.body();

                                            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                                            final LinearLayout.LayoutParams lphlay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            LinearLayout.LayoutParams shapkaLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                            shapkaLP.setMargins(23, 0, 0, 0);

                                            LinearLayout shapka = new LinearLayout(getApplicationContext());
                                            shapka.setPadding(10, 20, 10, 40);
                                            //shapka.setBackgroundColor(getResources().getColor(R.color.colorSiteBlue));
                                            shapka.setBackground(getResources().getDrawable(R.drawable.oboi_shapka));
                                            shapka.setOrientation(LinearLayout.VERTICAL);
                                            shapka.setGravity(Gravity.CENTER);

                                            ImageView ivCategory = new ImageView(getApplicationContext());
                                            ivCategory.setImageDrawable(getResources().getDrawable(R.drawable.ic_style_black_24dp));
                                            TextView tvCategory = new TextView(getApplicationContext());
                                            tvCategory.setTypeface(typeface);
                                            tvCategory.setLayoutParams(shapkaLP);
                                            tvCategory.setGravity(Gravity.START | Gravity.CENTER);
                                            tvCategory.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                            tvCategory.setPadding(20, 10, 23, 0);
                                            tvCategory.setText(getResources().getString(R.string.category) + ": " + getCategory(groupBattles.get(finalI).getCategory()));
                                            LinearLayout categoryLay = new LinearLayout(getApplicationContext());
                                            categoryLay.setOrientation(LinearLayout.HORIZONTAL);
                                            categoryLay.setGravity(Gravity.START | Gravity.CENTER);
                                            categoryLay.setPadding(0, 10, 0, 0);
                                            categoryLay.addView(ivCategory);
                                            categoryLay.addView(tvCategory);

                                            TextView tvSeason = new TextView(getApplicationContext());
                                            tvSeason.setTypeface(typeface);
                                            tvSeason.setTextColor(getResources().getColor(R.color.colorWhite));
                                            tvSeason.setTextSize(23);
                                            tvSeason.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                                            tvSeason.setPadding(0, 0, 0, 10);
                                            //tvSeason.setText(getResources().getString(R.string.season)+season.get(i));
                                            tvSeason.setText("Сезон такойто...");

                                            ImageView ivDateStart = new ImageView(getApplicationContext());
                                            ivDateStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_history_black_24dp));
                                            TextView tvDateStart = new TextView(getApplicationContext());
                                            tvDateStart.setTypeface(typeface);
                                            tvDateStart.setLayoutParams(shapkaLP);
                                            tvDateStart.setGravity(Gravity.START | Gravity.CENTER);
                                            tvDateStart.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                            tvDateStart.setPadding(20, 10, 23, 0);
                                            tvDateStart.setText(getResources().getString(R.string.date) + ": " + groupBattles.get(finalI).getDateStart());
                                            LinearLayout dateLay = new LinearLayout(getApplicationContext());
                                            dateLay.setOrientation(LinearLayout.HORIZONTAL);
                                            dateLay.setGravity(Gravity.START | Gravity.CENTER);
                                            dateLay.addView(ivDateStart);
                                            dateLay.addView(tvDateStart);

                                            ImageView ivJudge = new ImageView(getApplicationContext());
                                            ivJudge.setImageDrawable(getResources().getDrawable(R.drawable.ic_airline_seat_recline_normal_black_24dp));
                                            TextView tvJudge = new TextView(getApplicationContext());
                                            tvJudge.setTypeface(typeface);
                                            tvJudge.setLayoutParams(shapkaLP);
                                            tvJudge.setGravity(Gravity.START | Gravity.CENTER);
                                            tvJudge.setPadding(20, 0, 23, 0);
                                            tvJudge.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                            tvJudge.setText(getResources().getString(R.string.judge) + ": " + groupBattles.get(finalI).getJudge());

                                            LinearLayout judgeLay = new LinearLayout(getApplicationContext());
                                            judgeLay.setOrientation(LinearLayout.HORIZONTAL);
                                            judgeLay.setGravity(Gravity.START);
                                            judgeLay.addView(ivJudge);
                                            judgeLay.addView(tvJudge);

                                            shapka.addView(categoryLay);
                                            shapka.addView(dateLay);
                                            shapka.addView(judgeLay);
                                            //shapka.addView(tvSeason);


                                            GridLayout layLogin = new GridLayout(getApplicationContext());
                                            layLogin.setOrientation(GridLayout.HORIZONTAL);
                                            layLogin.setColumnCount(2);
                                            layLogin.setPadding(10, 10, 10, 10);

                                            int column = 0, row = 0;
                                            for (int j = 0; j < usersGroupBattles.size(); j++) {
                                                final int finalJ = j;

                                                if (column >= 2) {
                                                    column = 0;
                                                    row++;
                                                }
                                                GridLayout.LayoutParams lpr = new GridLayout.LayoutParams();
                                                lpr.columnSpec = GridLayout.spec(column);
                                                lpr.setGravity(Gravity.CENTER);
                                                lpr.rowSpec = GridLayout.spec(row);
                                                lpr.setMargins(10, 10, 10, 10);
                                                lpr.width = (int) (displayMetrics.widthPixels / 2.4);
                                                lpr.height = displayMetrics.heightPixels / 20;

                                                final Button login = new Button(getApplicationContext());
                                                if (Integer.parseInt(usersGroupBattles.get(j).getUserId()) == (userDB.getServerId())) {
                                                    login.setTextColor(getResources().getColor(R.color.colorWhite));
                                                    login.setBackground(getResources().getDrawable(R.drawable.round_button_yolo));
                                                } else {
                                                    login.setTextColor(getResources().getColor(R.color.colorBlack));
                                                    login.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                                    login.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(ListAllInprogressGroupBattlesActivity.this, UserInfoActivity.class);
                                                            intent.putExtra("user_id", usersGroupBattles.get(finalJ).getUserId());
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    login.setOnTouchListener(new View.OnTouchListener() {
                                                        @Override
                                                        public boolean onTouch(View v, MotionEvent event) {
                                                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                                login.setBackground(getResources().getDrawable(R.drawable.round_button_green));
                                                            }
                                                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                                                login.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                                            }
                                                            return false;
                                                        }
                                                    });
                                                }
                                                login.setTypeface(typeface);
                                                login.setText(usersGroupBattles.get(j).getUserLogin());
                                                login.setGravity(Gravity.CENTER);

                                                layLogin.addView(login, lpr);
                                                column++;
                                            }

                                            final ShimmerButton shimmerButton = new ShimmerButton(getApplicationContext());
                                            shimmerButton.setTypeface(typeface);
                                            shimmerButton.setText(getResources().getString(R.string.view));
                                            shimmerButton.setTextSize(12);
                                            shimmerButton.setTextColor(getResources().getColor(R.color.colorSiteWhiteBlue));
                                            shimmerButton.setBackground(getResources().getDrawable(R.drawable.round_button));
                                            shimmerButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_play_arrow_black_24dp, 0, 0);
                                            Shimmer sshimmer = new Shimmer()
                                                    .setDirection(Shimmer.ANIMATION_DIRECTION_LTR)
                                                    .setDuration(1000)
                                                    .setStartDelay(0);
                                            shimmerButton.setPadding(10, 0, 10, 0);
                                            sshimmer.start(shimmerButton);
                                            shimmerButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //открыть баттл по id
                                                    Intent intent = new Intent(ListAllInprogressGroupBattlesActivity.this, GroupBattleInProgressActivity.class);
                                                    intent.putExtra("battle_id", groupBattles.get(finalI).getBattleId());
                                                    startActivity(intent);
                                                }
                                            });
                                            LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            sp.setMargins(0, 0, 0, 20);
                                            shimmerButton.setLayoutParams(sp);

                                            final LinearLayout ll = new LinearLayout(getApplicationContext());
                                            ll.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                            ll.setGravity(Gravity.CENTER);
                                            ll.setOrientation(LinearLayout.VERTICAL);

                                            ll.addView(shapka);
                                            ll.addView(layLogin, lphlay);
                                            ll.addView(shimmerButton);

                                            final CardView cardView = new CardView(getApplicationContext());
                                            cardView.setUseCompatPadding(true);
                                            cardView.setRadius(23);
                                            cardView.setCardElevation(8);


                                            cardView.addView(ll);
                                            if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("tricking")) {
                                                llTrick.addView(cardView);
                                            }
                                            if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("parkour")) {
                                                llPark.addView(cardView);
                                            }
                                            if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("breakdance")) {
                                                llBreak.addView(cardView);
                                            }
                                            if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("trampoline")) {
                                                llTramp.addView(cardView);
                                            }


                                        } else {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ArrayList<UsersGroupBattle>> call, Throwable t) {

                                    }
                                });

                            }
                        }


                        shTrick.setText(getResources().getString(R.string.tricking) + "(" + sizeTrick + ")");
                        if (sizeTrick > 0) {
                            shTrick.setTextColor(getResources().getColor(R.color.colorSiteGreen));
                        } else {
                            shTrick.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        }
                        shPark.setText(getResources().getString(R.string.parkour) + "(" + sizePark + ")");
                        if (sizePark > 0) {
                            shPark.setTextColor(getResources().getColor(R.color.colorSiteGreen));
                        } else {
                            shPark.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        }
                        shBreak.setText(getResources().getString(R.string.break_dance) + "(" + sizeBreak + ")");
                        if (sizeBreak > 0) {
                            shBreak.setTextColor(getResources().getColor(R.color.colorSiteGreen));
                        } else {
                            shBreak.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        }
                        shTramp.setText(getResources().getString(R.string.trampoline) + "(" + sizeTramp + ")");
                        if (sizeTramp > 0) {
                            shTramp.setTextColor(getResources().getColor(R.color.colorSiteGreen));
                        } else {
                            shTramp.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        }

                        final TextView tricking = new TextView(getApplicationContext());
                        tricking.setTypeface(typeface);
                        tricking.setText(getResources().getString(R.string.tricking) + ":");
                        tricking.setTextColor(getResources().getColor(R.color.colorBlack));
                        tricking.setPadding(10, 10, 10, 0);
                        final TextView parkour = new TextView(getApplicationContext());
                        parkour.setTypeface(typeface);
                        parkour.setPadding(10, 10, 10, 0);
                        parkour.setText(getResources().getString(R.string.parkour) + ":");
                        parkour.setTextColor(getResources().getColor(R.color.colorBlack));
                        final TextView breakdance = new TextView(getApplicationContext());
                        breakdance.setTypeface(typeface);
                        breakdance.setPadding(10, 10, 10, 0);
                        breakdance.setText(getResources().getString(R.string.break_dance) + ":");
                        breakdance.setTextColor(getResources().getColor(R.color.colorBlack));
                        final TextView trampoline = new TextView(getApplicationContext());
                        trampoline.setTypeface(typeface);
                        trampoline.setPadding(10, 10, 10, 0);
                        trampoline.setText(getResources().getString(R.string.trampoline) + ":");
                        trampoline.setTextColor(getResources().getColor(R.color.colorBlack));

                        if (sizeTrick == 0) {
                            lay.addView(llTrick);
                        } else {
                            lay.addView(tricking);
                            lay.addView(llTrick);
                        }

                        if (sizePark == 0) {
                            lay.addView(llPark);
                        } else {
                            lay.addView(parkour);
                            lay.addView(llPark);
                        }

                        if (sizeBreak == 0) {
                            lay.addView(llBreak);
                        } else {
                            lay.addView(breakdance);
                            lay.addView(llBreak);
                        }

                        if (sizeTramp == 0) {
                            lay.addView(llTramp);
                        } else {
                            lay.addView(trampoline);
                            lay.addView(llTramp);
                        }

                        //просмотр определенной категории
                        if (sizeTrick == 0) {
                            shTrick.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        } else {
                            shTrick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lay.removeAllViews();
                                    lay.addView(tricking);
                                    lay.addView(llTrick);
                                    shTrick.setBackground(getResources().getDrawable(R.drawable.round_button_pressed));
                                    shPark.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                    shBreak.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                    shTramp.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                }
                            });
                        }

                        if (sizePark == 0) {
                            shPark.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        } else {
                            shPark.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lay.removeAllViews();
                                    lay.addView(parkour);
                                    lay.addView(llPark);
                                    shPark.setBackground(getResources().getDrawable(R.drawable.round_button_pressed));
                                    shTrick.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                    shBreak.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                    shTramp.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                }
                            });
                        }

                        if (sizeBreak == 0) {
                            shBreak.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        } else {
                            shBreak.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lay.removeAllViews();
                                    lay.addView(breakdance);
                                    lay.addView(llBreak);
                                    shBreak.setBackground(getResources().getDrawable(R.drawable.round_button_pressed));
                                    shPark.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                    shTrick.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                    shTramp.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                }
                            });
                        }

                        if (sizeTramp == 0) {
                            shTramp.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        } else {
                            shTramp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lay.removeAllViews();
                                    lay.addView(trampoline);
                                    lay.addView(llTramp);
                                    shTramp.setBackground(getResources().getDrawable(R.drawable.round_button_pressed));
                                    shPark.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                    shBreak.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                    shTrick.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                }
                            });
                        }
                    }
                    //


                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                    shTrick.setTextColor(getResources().getColor(R.color.colorSiteRed));
                    shPark.setTextColor(getResources().getColor(R.color.colorSiteRed));
                    shBreak.setTextColor(getResources().getColor(R.color.colorSiteRed));
                    shTramp.setTextColor(getResources().getColor(R.color.colorSiteRed));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GroupBattle>> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(false);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }
        });


    }

}
