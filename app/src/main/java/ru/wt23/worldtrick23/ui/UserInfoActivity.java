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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.ResponseBody;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.CategoryIndividBattle;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import ru.wt23.worldtrick23.io.WT23User;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserInfoActivity extends AppCompatActivity {
    String userID;
    DBHelper dbHelper;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView userRang, userBattles, userWins, userFails, userName, userSecondName, userBirthday, userInstagram, userAboutmyself;
    LinearLayout userCategories;
    ImageView ava;

    Typeface typeface;
    Context context;
    UserDB userDB;
    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user);

        context = this;
        userDB = DBHelper.getUser(context);
        //PushOnline.push(myDB.getId());

        userID = getIntent().getStringExtra("user_id");

        typeface = Typeface.createFromAsset(getAssets(), "fonts/planet_n2_cyr_lat.otf");

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_background));

        ava = (ImageView) findViewById(R.id.userAvatar);
        ava.setImageResource(R.mipmap.icon);

        userAboutmyself = (TextView) findViewById(R.id.userAboutmyself);
        userAboutmyself.setTypeface(typeface);
        userInstagram = (TextView) findViewById(R.id.userInstagram);
        userInstagram.setTypeface(typeface);
        userBirthday = (TextView) findViewById(R.id.userBirthday);
        userBirthday.setTypeface(typeface);
        userSecondName = (TextView) findViewById(R.id.userSecondName);
        userSecondName.setTypeface(typeface);
        userName = (TextView) findViewById(R.id.userName);
        userName.setTypeface(typeface);
        userCategories = (LinearLayout) findViewById(R.id.userCategories);
        userFails = (TextView) findViewById(R.id.userFails);
        userFails.setTypeface(typeface);
        userWins = (TextView) findViewById(R.id.userWins);
        userWins.setTypeface(typeface);
        userBattles = (TextView) findViewById(R.id.userBattles);
        userBattles.setTypeface(typeface);
        userRang = (TextView) findViewById(R.id.userRang);
        userRang.setTypeface(typeface);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeUserWtAcc);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSiteRed),
                getResources().getColor(R.color.colorSiteGreen),
                getResources().getColor(R.color.colorSiteWhiteBlue),
                getResources().getColor(R.color.colorWhite));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage();
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        loadPage();
        swipeRefreshLayout.setRefreshing(true);
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

    private void loadPage() {
        userCategories.removeAllViews();
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<WT23User>> usersCall = service.getWtUsers(userDB.getServerId());
        usersCall.enqueue(new Callback<ArrayList<WT23User>>() {
            @Override
            public void onResponse(Call<ArrayList<WT23User>> call, Response<ArrayList<WT23User>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    ArrayList<WT23User> users = response.body();
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getId().equalsIgnoreCase(userID)) {
                            actionBar.setSubtitle(users.get(i).getLogin());
                            //userLogin.setText(suserLogin);
                            userAboutmyself.setText(users.get(i).getAbouth());
                            userInstagram.setText(users.get(i).getInstagram());
                            userBirthday.setText(users.get(i).getDateOld());
                            userSecondName.setText(users.get(i).getSurname());
                            userName.setText(users.get(i).getName());
                            userFails.setText(String.valueOf(users.get(i).getFails()));
                            userWins.setText(String.valueOf(users.get(i).getWins()));
                            userBattles.setText(String.valueOf(users.get(i).getWins() + users.get(i).getFails()));
                            userRang.setText(String.valueOf(users.get(i).getRang()));

                            //рисуем категории в которых участыует
                            Call<ArrayList<CategoryIndividBattle>> callCategory = service.getIndividBattlesByCategory("tricking", userDB.getServerId());
                            callCategory.enqueue(new Callback<ArrayList<CategoryIndividBattle>>() {
                                @Override
                                public void onResponse(Call<ArrayList<CategoryIndividBattle>> call, Response<ArrayList<CategoryIndividBattle>> response) {
                                    if (response.isSuccessful()) {
                                        final ArrayList<CategoryIndividBattle> tricking = response.body();
                                        for (int j = 0; j < tricking.size(); j++) {
                                            if (tricking.get(j).getUserId().equalsIgnoreCase(userID)) {
                                                TextView tvTrick = new TextView(getApplicationContext());
                                                tvTrick.setTypeface(typeface);
                                                tvTrick.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                                tvTrick.setText(getResources().getString(R.string.tricking));

                                                Button battleTricking = new Button(getApplicationContext());
                                                battleTricking.setTypeface(typeface);
                                                checkBattle(battleTricking, tricking.get(j), "tricking");

                                                LinearLayout hlay = new LinearLayout(getApplicationContext());
                                                hlay.setOrientation(LinearLayout.HORIZONTAL);
                                                hlay.setWeightSum(200);
                                                hlay.setPadding(0, 5, 0, 5);
                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                lp.weight = 100;
                                                hlay.addView(tvTrick, lp);
                                                hlay.addView(battleTricking, lp);
                                                userCategories.addView(hlay);
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ArrayList<CategoryIndividBattle>> call, Throwable t) {

                                }
                            });
                            callCategory = service.getIndividBattlesByCategory("trampoline", userDB.getServerId());
                            callCategory.enqueue(new Callback<ArrayList<CategoryIndividBattle>>() {
                                @Override
                                public void onResponse(Call<ArrayList<CategoryIndividBattle>> call, Response<ArrayList<CategoryIndividBattle>> response) {
                                    if (response.isSuccessful()) {
                                        ArrayList<CategoryIndividBattle> trampoline = response.body();
                                        for (int j = 0; j < trampoline.size(); j++) {
                                            if (trampoline.get(j).getUserId().equalsIgnoreCase(userID)) {
                                                TextView tvTramp = new TextView(getApplicationContext());
                                                tvTramp.setTypeface(typeface);
                                                tvTramp.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                                tvTramp.setText(getResources().getString(R.string.trampoline));

                                                Button battleTamp = new Button(getApplicationContext());
                                                battleTamp.setTypeface(typeface);
                                                checkBattle(battleTamp, trampoline.get(j), "trampoline");

                                                LinearLayout hlay = new LinearLayout(getApplicationContext());
                                                hlay.setOrientation(LinearLayout.HORIZONTAL);
                                                hlay.setWeightSum(200);
                                                hlay.setPadding(0, 5, 0, 5);
                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                lp.weight = 100;
                                                hlay.addView(tvTramp, lp);
                                                hlay.addView(battleTamp, lp);
                                                userCategories.addView(hlay);
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ArrayList<CategoryIndividBattle>> call, Throwable t) {

                                }
                            });
                            callCategory = service.getIndividBattlesByCategory("breakdance", userDB.getServerId());
                            callCategory.enqueue(new Callback<ArrayList<CategoryIndividBattle>>() {
                                @Override
                                public void onResponse(Call<ArrayList<CategoryIndividBattle>> call, Response<ArrayList<CategoryIndividBattle>> response) {
                                    if (response.isSuccessful()) {
                                        ArrayList<CategoryIndividBattle> breakdance = response.body();
                                        for (int j = 0; j < breakdance.size(); j++) {
                                            if (breakdance.get(j).getUserId().equalsIgnoreCase(userID)) {
                                                TextView tvBreak = new TextView(getApplicationContext());
                                                tvBreak.setTypeface(typeface);
                                                tvBreak.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                                tvBreak.setText(getResources().getString(R.string.break_dance));

                                                Button battleBreak = new Button(getApplicationContext());
                                                battleBreak.setTypeface(typeface);
                                                checkBattle(battleBreak, breakdance.get(j), "breakdance");

                                                LinearLayout hlay = new LinearLayout(getApplicationContext());
                                                hlay.setOrientation(LinearLayout.HORIZONTAL);
                                                hlay.setPadding(0, 5, 0, 5);
                                                hlay.setWeightSum(200);
                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                lp.weight = 100;
                                                hlay.addView(tvBreak, lp);
                                                hlay.addView(battleBreak, lp);
                                                userCategories.addView(hlay);
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ArrayList<CategoryIndividBattle>> call, Throwable t) {

                                }
                            });
                            callCategory = service.getIndividBattlesByCategory("parkour", userDB.getServerId());
                            callCategory.enqueue(new Callback<ArrayList<CategoryIndividBattle>>() {
                                @Override
                                public void onResponse(Call<ArrayList<CategoryIndividBattle>> call, Response<ArrayList<CategoryIndividBattle>> response) {
                                    if (response.isSuccessful()) {
                                        ArrayList<CategoryIndividBattle> parkour = response.body();
                                        for (int j = 0; j < parkour.size(); j++) {
                                            if (parkour.get(j).getUserId().equalsIgnoreCase(userID)) {
                                                TextView tvParkour = new TextView(getApplicationContext());
                                                tvParkour.setTypeface(typeface);
                                                tvParkour.setTextColor(getResources().getColor(R.color.colorSiteYolo));
                                                tvParkour.setText(getResources().getString(R.string.parkour));

                                                Button battleParkour = new Button(getApplicationContext());
                                                battleParkour.setTypeface(typeface);
                                                checkBattle(battleParkour, parkour.get(j), "parkour");

                                                LinearLayout hlay = new LinearLayout(getApplicationContext());
                                                hlay.setOrientation(LinearLayout.HORIZONTAL);
                                                hlay.setWeightSum(200);
                                                hlay.setPadding(0, 5, 0, 5);
                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                lp.weight = 100;
                                                hlay.addView(tvParkour, lp);
                                                hlay.addView(battleParkour, lp);
                                                userCategories.addView(hlay);
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ArrayList<CategoryIndividBattle>> call, Throwable t) {

                                }
                            });
                            //


                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network) + "\n" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WT23User>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkBattle(Button button, final CategoryIndividBattle categoryIndividBattle, final String category) {
        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        if (!categoryIndividBattle.getBattleId().equalsIgnoreCase("N") && categoryIndividBattle.getAccept().equalsIgnoreCase("N")) {
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
                    Call<ResponseBody> cancelCall = service.stopBattle(categoryIndividBattle.getBattleId(), userDB.getServerId(), "");
                    cancelCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                    loadPage();
                }
            });

        } else if (categoryIndividBattle.getBattleId().equalsIgnoreCase("N") && categoryIndividBattle.getAccept().equalsIgnoreCase("N")) {
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
                    Call<ResponseBody> startCall = service.startBattle(userDB.getServerId(), categoryIndividBattle.getUserId(), category, "");
                    startCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.you_challenged) + " " + categoryIndividBattle.getUserLogin() + " " + getResources().getString(R.string.to_a_battle) + "!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(UserInfoActivity.this, IndividualBattleActivity.class);
                    intent.putExtra("battle_id", categoryIndividBattle.getBattleId());
                    startActivity(intent);
                }
            });
        }
    }

}
