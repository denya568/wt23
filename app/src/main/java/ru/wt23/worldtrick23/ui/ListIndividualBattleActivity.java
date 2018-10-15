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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.CategoryIndividBattle;
import ru.wt23.worldtrick23.io.IndividBattle;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListIndividualBattleActivity extends AppCompatActivity {
    DBHelper dbHelper;
    LinearLayout lay;
    SwipeRefreshLayout swipeUpdateInfo;
    String category;

    Typeface typeface;
    Context context;
    UserDB userDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_individual_battle);

        context = this;
        userDB = DBHelper.getUser(context);
        //PushOnline.push(myDB.getId());

        typeface = Typeface.createFromAsset(getAssets(), "fonts/planet_n2_cyr_lat.otf");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        category = getIntent().getStringExtra("category");
        if (category.equals("tricking")) {
            actionBar.setSubtitle(getResources().getString(R.string.tricking) + " - " + getResources().getString(R.string.individ_battles));
        }
        if (category.equals("breakdance")) {
            actionBar.setSubtitle(getResources().getString(R.string.break_dance) + " - " + getResources().getString(R.string.individ_battles));
        }
        if (category.equals("trampoline")) {
            actionBar.setSubtitle(getResources().getString(R.string.trampoline) + " - " + getResources().getString(R.string.individ_battles));
        }
        if (category.equals("parkour")) {
            actionBar.setSubtitle(getResources().getString(R.string.parkour) + " - " + getResources().getString(R.string.individ_battles));
        }

        lay = (LinearLayout) findViewById(R.id.individBattleLay);

        swipeUpdateInfo = (SwipeRefreshLayout) findViewById(R.id.swipeInd);
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

    private void loadPage() {
        swipeUpdateInfo.setRefreshing(true);
        lay.removeAllViews();
        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<CategoryIndividBattle>> battleCall = service.getIndividBattlesByCategory(category, userDB.getServerId());
        battleCall.enqueue(new Callback<ArrayList<CategoryIndividBattle>>() {
            @Override
            public void onResponse(Call<ArrayList<CategoryIndividBattle>> call, final Response<ArrayList<CategoryIndividBattle>> response) {
                swipeUpdateInfo.setRefreshing(false);
                if (response.isSuccessful()) {
                    final ArrayList<CategoryIndividBattle> battles = response.body();
                    int size = battles.size();
                    if (battles == null || size == 0) {
                        size = 0;
                        TextView tvNoBattles = new TextView(getApplicationContext());
                        tvNoBattles.setTypeface(typeface);
                        tvNoBattles.setTextColor(getResources().getColor(R.color.colorBlack));
                        lay.addView(tvNoBattles);
                    } else {

                        for (int i = 0; i < battles.size(); i++) {
                            final int finalI = i;
                            CardView cardView = new CardView(getApplicationContext());
                            cardView.setRadius(8);
                            cardView.setUseCompatPadding(true);
                            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                            CardView.LayoutParams loginParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            loginParams.setMargins(10, 0, (int) ((displayMetrics.widthPixels / 3)) + 10, 0);
                            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(displayMetrics.widthPixels / 3, ViewGroup.LayoutParams.WRAP_CONTENT);

                            LinearLayout hlay = new LinearLayout(getApplicationContext());
                            hlay.setOrientation(LinearLayout.HORIZONTAL);
                            hlay.setGravity(Gravity.CENTER | Gravity.END);

                            TextView login = new TextView(getApplicationContext());
                            login.setTypeface(typeface);
                            login.setText(battles.get(i).getUserLogin());
                            login.setTextSize(23);
                            login.setGravity(Gravity.CENTER | Gravity.START);
                            login.setTextColor(getResources().getColor(R.color.colorBlack));
                            login.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent openUser = new Intent(ListIndividualBattleActivity.this, UserInfoActivity.class);
                                    openUser.putExtra("user_id", battles.get(finalI).getUserId());
                                    startActivity(openUser);
                                }
                            });

                            Button button = new Button(getApplicationContext());
                            button.setTypeface(typeface);
                            button.setTextSize(15);
                            button.setPadding(8, 8, 8, 8);

                            if (!battles.get(i).getBattleId().equalsIgnoreCase("N") && battles.get(i).getAccept().equalsIgnoreCase("N")) {
                                button.setText(getResources().getString(R.string.cancel));
                                button.setTextColor(getResources().getColor(R.color.colorWhite));
                                button.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                                //post для отмены вызова
                                //f5 страницу
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl(DBHelper.URL)
                                                .client(okHttpClient)
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();
                                        ApiWT23 service = retrofit.create(ApiWT23.class);
                                        Call<ResponseBody> cancelCall = service.stopBattle(battles.get(finalI).getBattleId(), userDB.getServerId(), "");
                                        cancelCall.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    makeToast(getResources().getString(R.string.cancelled));
                                                } else {
                                                    makeToast(getResources().getString(R.string.no_network));
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                        loadPage();
                                    }
                                });

                            } else if (battles.get(i).getBattleId().equalsIgnoreCase("N") && battles.get(i).getAccept().equalsIgnoreCase("N")) {
                                button.setText(getResources().getString(R.string.battle));
                                button.setTextColor(getResources().getColor(R.color.colorWhite));
                                button.setBackground(getResources().getDrawable(R.drawable.round_button_green));
                                //post для вызова на баттл
                                //f5 страницу
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl(DBHelper.URL)
                                                .client(okHttpClient)
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();
                                        ApiWT23 service = retrofit.create(ApiWT23.class);
                                        Call<ResponseBody> startCall = service.startBattle(userDB.getServerId(), battles.get(finalI).getUserId(), category, "");
                                        startCall.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    makeToast(getResources().getString(R.string.you_challenged) + " " + battles.get(finalI).getUserLogin() + " " + getResources().getString(R.string.to_a_battle) + "!");
                                                } else {
                                                    makeToast(getResources().getString(R.string.no_network));
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                        loadPage();
                                    }
                                });

                            } else {
                                button.setText(getResources().getString(R.string.go_to_battle));
                                button.setTextColor(getResources().getColor(R.color.colorWhite));
                                button.setBackground(getResources().getDrawable(R.drawable.round_button));
                                //открыть новое активити с этим баттлом по id
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ListIndividualBattleActivity.this, IndividualBattleActivity.class);
                                        intent.putExtra("battle_id", battles.get(finalI).getBattleId());
                                        startActivity(intent);
                                    }
                                });
                            }

                            cardView.addView(login, loginParams);
                            hlay.addView(button, bp);
                            cardView.addView(hlay, cp);
                            lay.addView(cardView, cp);
                        }
                    }

                } else {
                    makeToast(getResources().getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CategoryIndividBattle>> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(false);
            }
        });

    }


}