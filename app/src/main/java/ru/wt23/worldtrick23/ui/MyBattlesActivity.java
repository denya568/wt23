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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.GroupBattle;
import ru.wt23.worldtrick23.io.IndividBattle;
import ru.wt23.worldtrick23.io.LoginById;
import ru.wt23.worldtrick23.io.MyDuel;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import ru.wt23.worldtrick23.io.UsersGroupBattle;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyBattlesActivity extends AppCompatActivity {
    SwipeRefreshLayout swipe;
    TextView sumBattles, sumReqs;
    LinearLayout myReqsLay, myBattlesLay;
    DBHelper dbHelper;

    Typeface typeface;
    String extraType;
    Context context;
    UserDB userDB;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_battles_activity);

        context = this;
        userDB = DBHelper.getUser(context);
        //pushOnline
        extraType = getIntent().getStringExtra("type");

        typeface = Typeface.createFromAsset(getAssets(), "fonts/planet_n2_cyr_lat.otf");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_background));
        if (extraType.equalsIgnoreCase("ind")) {
            actionBar.setSubtitle(getResources().getString(R.string.my_ind_battles));
        } else {
            actionBar.setSubtitle(getResources().getString(R.string.my_group_battles));
        }

        sumReqs = (TextView) findViewById(R.id.sumMyReqs);
        sumReqs.setTypeface(typeface);
        sumBattles = (TextView) findViewById(R.id.sumMyBattles);
        sumBattles.setTypeface(typeface);
        myReqsLay = (LinearLayout) findViewById(R.id.myReqsLay);
        myBattlesLay = (LinearLayout) findViewById(R.id.myBattlesLay);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeMyBattles);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorSiteRed),
                getResources().getColor(R.color.colorSiteGreen),
                getResources().getColor(R.color.colorSiteWhiteBlue),
                getResources().getColor(R.color.colorWhite));
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (extraType.equalsIgnoreCase("ind")) {
                    loadPageIND();
                } else {
                    loadPageGROUP();
                }
            }
        });

        if (extraType.equalsIgnoreCase("ind")) {
            loadPageIND();
        } else {
            loadPageGROUP();
        }
    }


    private void loadPageIND() {
        myBattlesLay.removeAllViews();
        myReqsLay.removeAllViews();

        swipe.setRefreshing(true);
        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<IndividBattle>> individBattlesCall = service.getAllIndividBattles();
        individBattlesCall.enqueue(new Callback<ArrayList<IndividBattle>>() {
            @Override
            public void onResponse(Call<ArrayList<IndividBattle>> call, Response<ArrayList<IndividBattle>> response) {
                swipe.setRefreshing(false);
                int battlesInProgress = 0;
                if (response.isSuccessful()) {
                    final ArrayList<IndividBattle> individBattles = response.body();
                    for (int i = 0; i < individBattles.size(); i++) {
                        if (Integer.parseInt(individBattles.get(i).getFromUserId())==(userDB.getServerId()) || Integer.parseInt(individBattles.get(i).getToUserId())==(userDB.getServerId())) {
                            battlesInProgress++;
                        }
                    }
                    sumBattles.setText(getResources().getString(R.string.battles_in_process) + " (" + battlesInProgress + ")");

                    final LinearLayout llTrick = new LinearLayout(getApplicationContext());
                    llTrick.setPadding(8, 0, 8, 8);
                    llTrick.setGravity(Gravity.CENTER | Gravity.TOP);
                    llTrick.setOrientation(LinearLayout.VERTICAL);

                    final LinearLayout llPark = new LinearLayout(getApplicationContext());
                    llPark.setPadding(8, 0, 8, 8);
                    llPark.setGravity(Gravity.CENTER | Gravity.TOP);
                    llPark.setOrientation(LinearLayout.VERTICAL);

                    final LinearLayout llBreak = new LinearLayout(getApplicationContext());
                    llBreak.setPadding(8, 0, 8, 8);
                    llBreak.setGravity(Gravity.CENTER | Gravity.TOP);
                    llBreak.setOrientation(LinearLayout.VERTICAL);

                    final LinearLayout llTramp = new LinearLayout(getApplicationContext());
                    llTramp.setPadding(8, 0, 8, 8);
                    llTramp.setGravity(Gravity.CENTER | Gravity.TOP);
                    llTramp.setOrientation(LinearLayout.VERTICAL);

                    int sizeTrick = 0;
                    int sizePark = 0;
                    int sizeBreak = 0;
                    int sizeTramp = 0;
                    for (int i = 0; i < individBattles.size(); i++) {
                        if (individBattles.get(i).getCategory().equalsIgnoreCase("tricking") &&
                                (Integer.parseInt(individBattles.get(i).getFromUserId())==(userDB.getServerId()) || Integer.parseInt(individBattles.get(i).getToUserId())!=(userDB.getServerId()))) {
                            sizeTrick++;
                        }

                        if (individBattles.get(i).getCategory().equalsIgnoreCase("parkour") &&
                                (Integer.parseInt(individBattles.get(i).getFromUserId())==(userDB.getServerId()) || Integer.parseInt(individBattles.get(i).getToUserId())==(userDB.getServerId()))) {
                            sizePark++;
                        }
                        if (individBattles.get(i).getCategory().equalsIgnoreCase("breakdance") &&
                                (Integer.parseInt(individBattles.get(i).getFromUserId())==(userDB.getServerId()) || Integer.parseInt(individBattles.get(i).getToUserId())==(userDB.getServerId()))) {
                            sizeBreak++;
                        }

                        if (individBattles.get(i).getCategory().equalsIgnoreCase("trampoline") &&
                                (Integer.parseInt(individBattles.get(i).getFromUserId())==(userDB.getServerId()) || Integer.parseInt(individBattles.get(i).getToUserId())==(userDB.getServerId()))) {
                            sizeTramp++;
                        }

                    }

                    for (int i = 0; i < individBattles.size(); i++) {
                        if (Integer.parseInt(individBattles.get(i).getToUserId())==(userDB.getServerId()) || Integer.parseInt(individBattles.get(i).getFromUserId())==(userDB.getServerId())) {

                            final LinearLayout.LayoutParams lphlay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            final LinearLayout hlay = new LinearLayout(getApplicationContext());
                            hlay.setOrientation(LinearLayout.HORIZONTAL);
                            hlay.setPadding(0, 20, 0, 20);
                            hlay.setGravity(Gravity.CENTER);

                            final int a = i;
                            TextView tvFromULogin = new TextView(getApplicationContext());
                            tvFromULogin.setTypeface(typeface);
                            tvFromULogin.setText(individBattles.get(i).getFromUserLogin());
                            tvFromULogin.setGravity(Gravity.CENTER);
                            tvFromULogin.setPadding(0, 0, 8, 0);
                            if (Integer.parseInt(individBattles.get(i).getFromUserId())==(userDB.getServerId())) {
                                tvFromULogin.setTextColor(getResources().getColor(R.color.colorSiteRed));
                            } else {
                                tvFromULogin.setTextColor(getResources().getColor(R.color.colorSiteBlue));
                                tvFromULogin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //открыть стр. этого юзера по ID
                                        Intent intent = new Intent(MyBattlesActivity.this, UserInfoActivity.class);
                                        intent.putExtra("user_id", individBattles.get(a).getFromUserId());
                                        startActivity(intent);

                                    }
                                });
                            }

                            TextView tvToULogin = new TextView(getApplicationContext());
                            tvToULogin.setTypeface(typeface);
                            tvToULogin.setGravity(Gravity.CENTER);
                            tvToULogin.setText(individBattles.get(i).getToUserLogin());
                            tvToULogin.setPadding(0, 0, 8, 0);

                            if (Integer.parseInt(individBattles.get(i).getToUserId())==(userDB.getServerId())) {
                                tvToULogin.setTextColor(getResources().getColor(R.color.colorSiteRed));
                            } else {
                                tvToULogin.setTextColor(getResources().getColor(R.color.colorSiteBlue));
                                tvToULogin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //открыть стр. этого юзера по ID
                                        Intent intent = new Intent(MyBattlesActivity.this, UserInfoActivity.class);
                                        intent.putExtra("user_id", individBattles.get(a).getToUserId());
                                        startActivity(intent);
                                    }
                                });
                            }

                            final ShimmerButton shimmerButton = new ShimmerButton(getApplicationContext());
                            shimmerButton.setTypeface(typeface);
                            shimmerButton.setText(getResources().getString(R.string.view));
                            shimmerButton.setTextSize(12);
                            shimmerButton.setTextColor(getResources().getColor(R.color.colorBlack));
                            shimmerButton.setBackground(getResources().getDrawable(R.drawable.round_button_green));
                            shimmerButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            shimmerButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_people_black_24dp, 0, 0);

                            Shimmer sshimmer = new Shimmer()
                                    .setDirection(Shimmer.ANIMATION_DIRECTION_LTR)
                                    .setDuration(1000)
                                    .setStartDelay(0);
                            shimmerButton.setPadding(10, 0, 10, 0);
                            sshimmer.start(shimmerButton);


                            if (individBattles.get(i).getVideoFrom().equalsIgnoreCase("N") && individBattles.get(i).getVideoTo().equalsIgnoreCase("N")) {
                                shimmerButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_people_outline_black_24dp, 0, 0);
                                shimmerButton.setText(getResources().getString(R.string.view));
                                shimmerButton.setTextSize(11);
                                sshimmer.setDirection(Shimmer.ANIMATION_DIRECTION_RTL)
                                        .setDuration(1000)
                                        .setStartDelay(500)
                                        .start(shimmerButton);
                            } else if (individBattles.get(i).getVideoFrom().equalsIgnoreCase("N") && !individBattles.get(i).getVideoTo().equalsIgnoreCase("N")) {
                                shimmerButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_person_black_24dp, 0, 0);
                            } else if (!individBattles.get(i).getVideoFrom().equalsIgnoreCase("N") && individBattles.get(i).getVideoTo().equalsIgnoreCase("N")) {
                                shimmerButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_person_black_24dp, 0, 0);
                            }

                            TextView tvVS = new TextView(getApplicationContext());
                            tvVS.setTypeface(typeface);
                            tvVS.setPadding(10, 0, 10, 0);
                            tvVS.setTextColor(getResources().getColor(R.color.colorBlack));
                            tvVS.setText(getResources().getString(R.string.vs));

                            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                            final ViewGroup.LayoutParams lpp = new LinearLayout.LayoutParams((int) (displayMetrics.widthPixels / (float) 6), ViewGroup.LayoutParams.WRAP_CONTENT);

                            hlay.addView(tvFromULogin, lpp);
                            hlay.addView(tvVS);
                            hlay.addView(tvToULogin, lpp);

                            final LinearLayout ll = new LinearLayout(getApplicationContext());
                            ll.setGravity(Gravity.CENTER);
                            ll.setOrientation(LinearLayout.VERTICAL);
                            ll.addView(hlay, lphlay);
                            ll.addView(shimmerButton);

                            LinearLayout indProgLay = new LinearLayout(getApplicationContext());
                            indProgLay.setOrientation(LinearLayout.VERTICAL);
                            indProgLay.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 0, 0, 20);
                            indProgLay.setLayoutParams(lp);

                            shimmerButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MyBattlesActivity.this, IndividualBattleActivity.class);
                                    intent.putExtra("battle_id", individBattles.get(a).getBattleId());
                                    startActivity(intent);
                                }
                            });

                            indProgLay.addView(ll);
                            if (individBattles.get(i).getCategory().equalsIgnoreCase("tricking")) {
                                llTrick.addView(indProgLay);
                            }
                            if (individBattles.get(i).getCategory().equalsIgnoreCase("parkour")) {
                                llPark.addView(indProgLay);
                            }
                            if (individBattles.get(i).getCategory().equalsIgnoreCase("breakdance")) {
                                llBreak.addView(indProgLay);
                            }
                            if (individBattles.get(i).getCategory().equalsIgnoreCase("trampoline")) {
                                llTramp.addView(indProgLay);
                            }

                        }
                    }
                    final TextView tricking = new TextView(getApplicationContext());
                    tricking.setTypeface(typeface);
                    tricking.setTextColor(getResources().getColor(R.color.colorBlack));
                    tricking.setText(getResources().getString(R.string.tricking) + ":");
                    final TextView parkour = new TextView(getApplicationContext());
                    parkour.setTypeface(typeface);
                    parkour.setTextColor(getResources().getColor(R.color.colorBlack));
                    parkour.setText(getResources().getString(R.string.parkour) + ":");
                    final TextView breakdance = new TextView(getApplicationContext());
                    breakdance.setTypeface(typeface);
                    breakdance.setTextColor(getResources().getColor(R.color.colorBlack));
                    breakdance.setText(getResources().getString(R.string.break_dance) + ":");
                    final TextView trampoline = new TextView(getApplicationContext());
                    trampoline.setTypeface(typeface);
                    trampoline.setTextColor(getResources().getColor(R.color.colorBlack));
                    trampoline.setText(getResources().getString(R.string.trampoline) + ":");

                    if (sizeTrick == 0) {
                        myBattlesLay.addView(llTrick);
                    } else {
                        myBattlesLay.addView(tricking);
                        myBattlesLay.addView(llTrick);
                    }

                    if (sizePark == 0) {
                        myBattlesLay.addView(llPark);
                    } else {
                        myBattlesLay.addView(parkour);
                        myBattlesLay.addView(llPark);
                    }

                    if (sizeBreak == 0) {
                        myBattlesLay.addView(llBreak);
                    } else {
                        myBattlesLay.addView(breakdance);
                        myBattlesLay.addView(llBreak);
                    }

                    if (sizeTramp == 0) {
                        myBattlesLay.addView(llTramp);
                    } else {
                        myBattlesLay.addView(trampoline);
                        myBattlesLay.addView(llTramp);
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<IndividBattle>> call, Throwable t) {
                swipe.setRefreshing(false);
            }
        });

        Call<ArrayList<MyDuel>> myDuelsCall = service.getDuelsForMe(userDB.getServerId());
        myDuelsCall.enqueue(new Callback<ArrayList<MyDuel>>() {
            @Override
            public void onResponse(Call<ArrayList<MyDuel>> call, Response<ArrayList<MyDuel>> response) {
                swipe.setRefreshing(false);
                int myReqs = 0;

                if (response.isSuccessful()) {
                    final ArrayList<MyDuel> myDuels = response.body();
                    if (myDuels != null) {
                        myReqs = myDuels.size();
                    } else {
                        myReqs = 0;
                    }

                    sumReqs.setText(getResources().getString(R.string.challenge_from) + " (" + myReqs + ")");
                    for (int i = 0; i < myReqs; i++) {
                        TextView user = new TextView(getApplicationContext());
                        user.setTypeface(typeface);
                        user.setGravity(Gravity.CENTER);
                        user.setTextColor(getResources().getColor(R.color.colorSiteBlue));
                        user.setText(LoginById.getLoginById(userDB.getServerId(), myDuels.get(i).getFromUser()));

                        final int a = i;
                        user.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MyBattlesActivity.this, UserInfoActivity.class);
                                intent.putExtra("user_id", myDuels.get(a).getFromUser());
                                startActivity(intent);
                            }
                        });
                        String categ = "";
                        if (myDuels.get(i).getCategory().equalsIgnoreCase("trampoline")) {
                            categ = getResources().getString(R.string.trampoline);
                        }
                        if (myDuels.get(i).getCategory().equalsIgnoreCase("tricking")) {
                            categ = getResources().getString(R.string.tricking);
                        }
                        if (myDuels.get(i).getCategory().equalsIgnoreCase("breakdance")) {
                            categ = getResources().getString(R.string.break_dance);
                        }
                        if (myDuels.get(i).getCategory().equalsIgnoreCase("parkour")) {
                            categ = getResources().getString(R.string.parkour);
                        }

                        TextView txt = new TextView(getApplicationContext());
                        txt.setTypeface(typeface);
                        txt.setGravity(Gravity.CENTER);
                        txt.setTextColor(getResources().getColor(R.color.colorBlack));
                        txt.setText(getResources().getString(R.string.you_were_challenged) + "\n" + categ);

                        ImageButton cancel = new ImageButton(getApplicationContext());
                        cancel.setImageResource(R.drawable.ic_cancel_black_24dp);
                        cancel.setBackground(getResources().getDrawable(R.drawable.round_button));
                        cancel.setPadding(20, 20, 20, 20);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //отправка отказа
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(DBHelper.URL)
                                        .client(okHttpClient)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                ApiWT23 service1 = retrofit.create(ApiWT23.class);
                                Call<ResponseBody> cancelBattleCall = service1.stopBattle(myDuels.get(a).getBattleId(), userDB.getServerId(), "");
                                int response = 0;
                                cancelBattleCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.code() != 200) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });

                                //обновить lay
                                loadPageIND();
                            }
                        });
                        ImageButton start = new ImageButton(getApplicationContext());
                        start.setImageResource(R.drawable.ic_check_circle_black_24dp);
                        start.setBackground(getResources().getDrawable(R.drawable.round_button));
                        start.setPadding(20, 20, 20, 20);
                        start.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //отправка согласия

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(DBHelper.URL)
                                        .client(okHttpClient)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                ApiWT23 service1 = retrofit.create(ApiWT23.class);
                                Call<ResponseBody> acceptBattleCall = service1.acceptBattle(myDuels.get(a).getBattleId(), userDB.getServerId(), "");
                                acceptBattleCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.code() != 200) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });

                                //обновить lay
                                loadPageIND();
                            }
                        });
                        TextView date = new TextView(getApplicationContext());
                        date.setTypeface(typeface);
                        date.setGravity(Gravity.CENTER);
                        date.setTextColor(getResources().getColor(R.color.colorGray));
                        date.setText(getResources().getString(R.string.make_decision) + " " + myDuels.get(i).getDateEnd());

                        LinearLayout ll = new LinearLayout(getApplicationContext());
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.setGravity(Gravity.CENTER);
                        ll.setBackgroundResource(R.drawable.round_button_white);
                        ll.setPadding(15, 15, 15, 15);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lpBtn.setMargins(5, 5, 5, 5);

                        LinearLayout hlay = new LinearLayout(getApplicationContext());
                        hlay.setOrientation(LinearLayout.HORIZONTAL);
                        hlay.setGravity(Gravity.CENTER);

                        hlay.addView(start, lpBtn);
                        hlay.addView(cancel, lpBtn);

                        ll.addView(user, lp);
                        ll.addView(txt, lp);
                        ll.addView(hlay, lp);
                        ll.addView(date, lp);

                        LinearLayout indReqLay = new LinearLayout(getApplicationContext());
                        indReqLay.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams lpLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, 0, 0, 20);

                        indReqLay.addView(ll, lp);

                        myReqsLay.addView(indReqLay, lpLay);
                    }

                } else {
                    swipe.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ArrayList<MyDuel>> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadPageGROUP() {
        swipe.setRefreshing(true);
        myReqsLay.removeAllViews();
        myBattlesLay.removeAllViews();
        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit clientBattles = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 serviceBattles = clientBattles.create(ApiWT23.class);
        Call<ArrayList<GroupBattle>> groupBattlesCall = serviceBattles.getGroupBattles();

        final int[] battlesInProcess = {0};
        final int[] myReqs = {0};

        groupBattlesCall.enqueue(new Callback<ArrayList<GroupBattle>>() {
            @Override
            public void onResponse(Call<ArrayList<GroupBattle>> call, Response<ArrayList<GroupBattle>> response) {
                swipe.setRefreshing(false);
                if (response.isSuccessful()) {
                    final ArrayList<GroupBattle> groupBattles = response.body();

                    //
                    for (int i = 0; i < groupBattles.size(); i++) {
                        final int finalI = i;
                        Retrofit client = new Retrofit.Builder()
                                .baseUrl(DBHelper.URL)
                                .client(okHttpClient)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        ApiWT23 service = client.create(ApiWT23.class);
                        Call<ArrayList<UsersGroupBattle>> usersGroupBattleCall = service.getUsersGroupBattle(groupBattles.get(i).getBattleId());
                        usersGroupBattleCall.enqueue(new Callback<ArrayList<UsersGroupBattle>>() {
                            @Override
                            public void onResponse(Call<ArrayList<UsersGroupBattle>> call, Response<ArrayList<UsersGroupBattle>> response) {
                                if (response.isSuccessful()) {
                                    ArrayList<UsersGroupBattle> usersGroupBattles = response.body();

                                    //
                                    boolean isMe = false;
                                    if (usersGroupBattles != null) {
                                        for (int j = 0; j < usersGroupBattles.size(); j++) {
                                            if (Integer.parseInt(usersGroupBattles.get(j).getUserId())==(userDB.getServerId())) {
                                                isMe = true;
                                            }
                                        }
                                    }

                                    if (groupBattles.get(finalI).getStatus().equalsIgnoreCase("EXECUTION") && isMe) {
                                        battlesInProcess[0]++;


                                        LinearLayout.LayoutParams titleLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        titleLP.setMargins(0, 0, 0, 0);

                                        /*TextView tvSeason = new TextView(getApplicationContext());
                                        tvSeason.setTypeface(typeface);
                                        tvSeason.setTextColor(getResources().getColor(R.color.colorBlack));
                                        tvSeason.setGravity(Gravity.CENTER);
                                        tvSeason.setBackgroundColor(getResources().getColor(R.color.colorSiteBlue));
                                        tvSeason.setText(getResources().getString(R.string.season) + ": " + season.get(i));*/

                                        ImageView ivCategory = new ImageView(getApplicationContext());
                                        ivCategory.setImageDrawable(getResources().getDrawable(R.drawable.ic_style_black_24dp));
                                        TextView tvCategory = new TextView(getApplicationContext());
                                        tvCategory.setTypeface(typeface);
                                        tvCategory.setTextColor(getResources().getColor(R.color.colorBlack));
                                        tvCategory.setGravity(Gravity.CENTER | Gravity.START);
                                        tvCategory.setPadding(10, 0, 0, 0);
                                        tvCategory.setLayoutParams(titleLP);
                                        tvCategory.setText(getResources().getString(R.string.category) + ": " + groupBattles.get(finalI).getCategory());
                                        LinearLayout categoryLay = new LinearLayout(getApplicationContext());
                                        categoryLay.setOrientation(LinearLayout.HORIZONTAL);
                                        categoryLay.setGravity(Gravity.CENTER);
                                        categoryLay.setPadding(0, 10, 0, 0);
                                        categoryLay.addView(ivCategory);
                                        categoryLay.addView(tvCategory);

                                        ImageView ivDateStart = new ImageView(getApplicationContext());
                                        ivDateStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_history_black_24dp));
                                        TextView tvDateStart = new TextView(getApplicationContext());
                                        tvDateStart.setTypeface(typeface);
                                        tvDateStart.setTextColor(getResources().getColor(R.color.colorSiteRed));
                                        tvDateStart.setGravity(Gravity.CENTER | Gravity.START);
                                        tvDateStart.setPadding(10, 0, 0, 0);
                                        tvDateStart.setLayoutParams(titleLP);
                                        tvDateStart.setText(getResources().getString(R.string.date) + ": " + groupBattles.get(finalI).getDateStart());
                                        LinearLayout dateStartLay = new LinearLayout(getApplicationContext());
                                        dateStartLay.setOrientation(LinearLayout.HORIZONTAL);
                                        dateStartLay.setGravity(Gravity.CENTER);
                                        dateStartLay.setPadding(0, 10, 0, 0);
                                        dateStartLay.addView(ivDateStart);
                                        dateStartLay.addView(tvDateStart);

                                        ImageView ivCountUsers = new ImageView(getApplicationContext());
                                        ivCountUsers.setImageDrawable(getResources().getDrawable(R.drawable.ic_people_outline_black_24dp));
                                        TextView tvCountUsers = new TextView(getApplicationContext());
                                        tvCountUsers.setTypeface(typeface);
                                        tvCountUsers.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                        tvCountUsers.setGravity(Gravity.CENTER | Gravity.START);
                                        tvCountUsers.setPadding(10, 0, 0, 0);
                                        tvCountUsers.setLayoutParams(titleLP);
                                        tvCountUsers.setText(getResources().getString(R.string.users_count) + ": " + groupBattles.get(finalI).getMaxUsers());
                                        LinearLayout countUsersLay = new LinearLayout(getApplicationContext());
                                        countUsersLay.setOrientation(LinearLayout.HORIZONTAL);
                                        countUsersLay.setGravity(Gravity.CENTER);
                                        countUsersLay.setPadding(0, 10, 0, 0);
                                        countUsersLay.addView(ivCountUsers);
                                        countUsersLay.addView(tvCountUsers);

                                        /*TextView tvRoundsCount = new TextView(getApplicationContext());
                                        tvRoundsCount.setTypeface(typeface);
                                        tvRoundsCount.setTextColor(getResources().getColor(R.color.colorBlack));
                                        tvRoundsCount.setGravity(Gravity.CENTER);
                                        //tvRoundsCount.setText("Число раундов: " + rounds.get(i));
                                        tvRoundsCount.setText("Число раундов: xz");*/

                                        ShimmerButton shimmerButton = new ShimmerButton(getApplicationContext());
                                        shimmerButton.setTypeface(typeface);
                                        shimmerButton.setText(getResources().getString(R.string.view));
                                        shimmerButton.setTextColor(getResources().getColor(R.color.colorBlack));
                                        shimmerButton.setBackground(getResources().getDrawable(R.drawable.round_button_green));
                                        shimmerButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        shimmerButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_people_black_24dp, 0, 0);

                                        Shimmer sshimmer = new Shimmer()
                                                .setDirection(Shimmer.ANIMATION_DIRECTION_LTR)
                                                .setDuration(1000)
                                                .setStartDelay(0);
                                        shimmerButton.setPadding(10, 0, 10, 0);
                                        sshimmer.start(shimmerButton);

                                        final ArrayList<GroupBattle> finalGroupBattles = groupBattles;
                                        shimmerButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent openBattle = new Intent(MyBattlesActivity.this, GroupBattleInProgressActivity.class);
                                                openBattle.putExtra("battle_id", finalGroupBattles.get(finalI).getBattleId());
                                                startActivity(openBattle);
                                            }
                                        });

                                        LinearLayout ll = new LinearLayout(getApplicationContext());
                                        ll.setOrientation(LinearLayout.VERTICAL);
                                        ll.setGravity(Gravity.CENTER);
                                        ll.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        lp.setMargins(0, 0, 0, 20);

                                        //ll.addView(tvSeason, lp);
                                        ll.addView(categoryLay);
                                        ll.addView(dateStartLay);
                                        ll.addView(countUsersLay);
                                        //ll.addView(tvRoundsCount);
                                        ll.addView(shimmerButton);

                                        myBattlesLay.addView(ll, lp);
                                    }

                                    if (groupBattles.get(finalI).getStatus().equalsIgnoreCase("ACTIVE") && isMe) {
                                        myReqs[0]++;

                                        LinearLayout.LayoutParams titleLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        titleLP.setMargins(0, 0, 0, 0);

                                        /*TextView tvSeason = new TextView(getApplicationContext());
                                        tvSeason.setTypeface(typeface);
                                        tvSeason.setTextColor(getResources().getColor(R.color.colorBlack));
                                        tvSeason.setGravity(Gravity.CENTER);
                                        tvSeason.setBackgroundColor(getResources().getColor(R.color.colorSiteBlue));
                                        tvSeason.setText(getResources().getString(R.string.season) + ": " + season.get(i));*/

                                        ImageView ivCategory = new ImageView(getApplicationContext());
                                        ivCategory.setImageDrawable(getResources().getDrawable(R.drawable.ic_style_black_24dp));
                                        TextView tvCategory = new TextView(getApplicationContext());
                                        tvCategory.setTypeface(typeface);
                                        tvCategory.setTextColor(getResources().getColor(R.color.colorBlack));
                                        tvCategory.setGravity(Gravity.CENTER | Gravity.START);
                                        tvCategory.setPadding(10, 0, 0, 0);
                                        String categ = "";
                                        if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("trampoline")) {
                                            categ = getResources().getString(R.string.trampoline);
                                        }
                                        if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("tricking")) {
                                            categ = getResources().getString(R.string.tricking);
                                        }
                                        if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("breakdance")) {
                                            categ = getResources().getString(R.string.break_dance);
                                        }
                                        if (groupBattles.get(finalI).getCategory().equalsIgnoreCase("parkour")) {
                                            categ = getResources().getString(R.string.parkour);
                                        }
                                        tvCategory.setLayoutParams(titleLP);
                                        tvCategory.setText(getResources().getString(R.string.category) + ": " + categ);
                                        LinearLayout categoryLay = new LinearLayout(getApplicationContext());
                                        categoryLay.setOrientation(LinearLayout.HORIZONTAL);
                                        categoryLay.setGravity(Gravity.CENTER);
                                        categoryLay.setPadding(0, 10, 0, 0);
                                        categoryLay.addView(ivCategory);
                                        categoryLay.addView(tvCategory);

                                        ImageView ivUsersCount = new ImageView(getApplicationContext());
                                        ivUsersCount.setImageDrawable(getResources().getDrawable(R.drawable.ic_people_outline_black_24dp));
                                        TextView tvUsersCount = new TextView(getApplicationContext());
                                        tvUsersCount.setTypeface(typeface);
                                        tvUsersCount.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                        tvUsersCount.setGravity(Gravity.CENTER | Gravity.START);
                                        tvUsersCount.setPadding(10, 0, 0, 0);
                                        tvUsersCount.setLayoutParams(titleLP);
                                        tvUsersCount.setText(getResources().getString(R.string.users_count) + ": " + usersGroupBattles.size() + "/" + groupBattles.get(finalI).getMaxUsers());
                                        LinearLayout usersCountLay = new LinearLayout(getApplicationContext());
                                        usersCountLay.setOrientation(LinearLayout.HORIZONTAL);
                                        usersCountLay.setGravity(Gravity.CENTER);
                                        usersCountLay.setPadding(0, 10, 0, 0);
                                        usersCountLay.addView(ivUsersCount);
                                        usersCountLay.addView(tvUsersCount);

                                        final LinearLayout usersLay = new LinearLayout(getApplicationContext());
                                        usersLay.setOrientation(LinearLayout.VERTICAL);
                                        usersLay.setGravity(Gravity.START);
                                        for (int j = 0; j < usersGroupBattles.size(); j++) {
                                            final int finalJ = j;

                                            ShimmerButton btUser = new ShimmerButton(getApplicationContext());
                                            btUser.setTextColor(getResources().getColor(R.color.colorWhite));
                                            btUser.setText(usersGroupBattles.get(j).getUserLogin());

                                            if (Integer.parseInt(usersGroupBattles.get(j).getUserId())!=(userDB.getServerId())) {
                                                final ArrayList<UsersGroupBattle> finalUsersGroupBattles = usersGroupBattles;
                                                btUser.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent openUser = new Intent(MyBattlesActivity.this, UserInfoActivity.class);
                                                        openUser.putExtra("user_id", finalUsersGroupBattles.get(finalJ).getUserId());
                                                        startActivity(openUser);
                                                    }
                                                });
                                                btUser.setBackground(getResources().getDrawable(R.drawable.round_button));
                                                btUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_white_24dp, 0, 0, 0);
                                            } else {
                                                btUser.setBackground(getResources().getDrawable(R.drawable.round_button_yolo));
                                                btUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);
                                                btUser.setTextColor(getResources().getColor(R.color.colorBlack));
                                            }

                                            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                                            LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, displayMetrics.heightPixels / 20);
                                            //LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            blp.setMargins(0, 0, 0, 15);
                                            btUser.setGravity(Gravity.CENTER);
                                            usersLay.addView(btUser, blp);
                                        }

                                        final LinearLayout ll = new LinearLayout(getApplicationContext());
                                        ll.setOrientation(LinearLayout.VERTICAL);
                                        ll.setGravity(Gravity.CENTER);
                                        ll.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        lp.setMargins(0, 0, 0, 20);

                                        final ImageButton showHide = new ImageButton(getApplicationContext());
                                        showHide.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        showHide.setBackground(getResources().getDrawable(R.drawable.ic_arrow_downward_black_24dp));
                                        showHide.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!usersLay.isShown()) {
                                                    ll.addView(usersLay);
                                                    showHide.setBackground(getResources().getDrawable(R.drawable.ic_arrow_upward_black_24dp));
                                                } else {
                                                    ll.removeView(usersLay);
                                                    showHide.setBackground(getResources().getDrawable(R.drawable.ic_arrow_downward_black_24dp));
                                                }
                                            }
                                        });

                                        //ll.addView(tvSeason, lp);
                                        ll.addView(categoryLay);
                                        ll.addView(usersCountLay);
                                        ll.addView(showHide);
                                        myReqsLay.addView(ll, lp);

                                    }
                                    sumBattles.setText(getResources().getString(R.string.battles_in_process) + " (" + battlesInProcess[0] + ")");
                                    sumReqs.setText(getResources().getString(R.string.gather_battle) + " (" + myReqs[0] + ")");
                                    //
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<UsersGroupBattle>> call, Throwable t) {

                            }
                        });

                    }


                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<GroupBattle>> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }
        });

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
}
