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
import ru.wt23.worldtrick23.io.IndividBattle;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListAllInprogressIndividBattlesActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_list_all_inprogress_individ_battles);

        context = this;
        userDB = DBHelper.getUser(context);
        //PushOnline.push(myDB.getId());

        typeface = Typeface.createFromAsset(getAssets(), "fonts/planet_n2_cyr_lat.otf");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(getResources().getString(R.string.individ_battles));
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_background));

        //showLoader();

        shTrick = (Button) findViewById(R.id.shListTrick);
        shTrick.setTypeface(typeface);
        shPark = (Button) findViewById(R.id.shListPark);
        shPark.setTypeface(typeface);
        shBreak = (Button) findViewById(R.id.shListBreak);
        shBreak.setTypeface(typeface);
        shTramp = (Button) findViewById(R.id.shListTramp);
        shTramp.setTypeface(typeface);

        lay = (LinearLayout) findViewById(R.id.individAllBattlesLay);

        swipeUpdateInfo = (SwipeRefreshLayout) findViewById(R.id.swipeAllInd);
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

    private void loadPage() {
        lay.removeAllViews();
        swipeUpdateInfo.setRefreshing(true);

        shBreak.setBackground(getResources().getDrawable(R.drawable.round_button_white));
        shPark.setBackground(getResources().getDrawable(R.drawable.round_button_white));
        shTrick.setBackground(getResources().getDrawable(R.drawable.round_button_white));
        shTramp.setBackground(getResources().getDrawable(R.drawable.round_button_white));

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<IndividBattle>> battlesCall = service.getAllIndividBattles();
        battlesCall.enqueue(new Callback<ArrayList<IndividBattle>>() {
            @Override
            public void onResponse(Call<ArrayList<IndividBattle>> call, Response<ArrayList<IndividBattle>> response) {
                if (response.isSuccessful()) {
                    final ArrayList<IndividBattle> individBattles = response.body();
                    if (individBattles == null || individBattles.size() == 0) {
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

                        int sizeTrick = 0;
                        int sizePark = 0;
                        int sizeBreak = 0;
                        int sizeTramp = 0;

                        for (int i = 0; i < individBattles.size(); i++) {
                            if (individBattles.get(i).getCategory().equalsIgnoreCase("tricking"))
                                sizeTrick++;
                            else if (individBattles.get(i).getCategory().equalsIgnoreCase("parkour"))
                                sizePark++;
                            else if (individBattles.get(i).getCategory().equalsIgnoreCase("breakdance"))
                                sizeBreak++;
                            else if (individBattles.get(i).getCategory().equalsIgnoreCase("trampoline"))
                                sizeTramp++;
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
                        }else {
                            shBreak.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        }
                        shTramp.setText(getResources().getString(R.string.trampoline) + "(" + sizeTramp + ")");
                        if (sizeTramp > 0) {
                            shTramp.setTextColor(getResources().getColor(R.color.colorSiteGreen));
                        } else {
                            shTramp.setTextColor(getResources().getColor(R.color.colorSiteRed));
                        }

                        for (int i = 0; i < individBattles.size(); i++) {
                            final int finalI = i;

                            final LinearLayout.LayoutParams lphlay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            final LinearLayout hlay = new LinearLayout(getApplicationContext());
                            hlay.setOrientation(LinearLayout.HORIZONTAL);
                            hlay.setGravity(Gravity.CENTER);

                            TextView tvFromULogin = new TextView(getApplicationContext());
                            tvFromULogin.setTypeface(typeface);
                            tvFromULogin.setGravity(Gravity.CENTER);
                            tvFromULogin.setText(individBattles.get(i).getFromUserLogin());
                            tvFromULogin.setPadding(0, 0, 8, 0);

                            if (Integer.parseInt(individBattles.get(i).getFromUserId())==(userDB.getServerId())) {
                                tvFromULogin.setTextColor(getResources().getColor(R.color.colorSiteRed));
                            } else {
                                tvFromULogin.setTextColor(getResources().getColor(R.color.colorSiteBlue));
                                tvFromULogin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //открыть стр. этого юзера по ID
                                        Intent intent = new Intent(ListAllInprogressIndividBattlesActivity.this, UserInfoActivity.class);
                                        intent.putExtra("user_id", individBattles.get(finalI).getFromUserId());
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
                                        Intent intent = new Intent(ListAllInprogressIndividBattlesActivity.this, UserInfoActivity.class);
                                        intent.putExtra("user_id", individBattles.get(finalI).getToUserId());
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
                            shimmerButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_people_black_24dp, 0, 0);

                            Shimmer sshimmer = new Shimmer()
                                    .setDirection(Shimmer.ANIMATION_DIRECTION_LTR)
                                    .setDuration(1000)
                                    .setStartDelay(0);
                            shimmerButton.setPadding(10, 0, 10, 0);
                            sshimmer.start(shimmerButton);

                            if (individBattles.get(i).getVideoFrom().equalsIgnoreCase("N") && individBattles.get(i).getVideoTo().equalsIgnoreCase("N")) {
                                shimmerButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_people_outline_black_24dp, 0, 0);
                                shimmerButton.setBackground(getResources().getDrawable(R.drawable.round_button));
                                shimmerButton.setText(getResources().getString(R.string.expectation));
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

                            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                            final ViewGroup.LayoutParams lpp = new LinearLayout.LayoutParams((int) (displayMetrics.widthPixels / (float) 2.76), ViewGroup.LayoutParams.WRAP_CONTENT);

                            TextView tvVS = new TextView(getApplicationContext());
                            tvVS.setTypeface(typeface);
                            tvVS.setGravity(Gravity.CENTER);
                            tvVS.setTextColor(getResources().getColor(R.color.colorBlack));
                            tvVS.setText(getResources().getString(R.string.vs));

                            ViewGroup.LayoutParams lppp = new LinearLayout.LayoutParams((int) (displayMetrics.widthPixels / (float) 3), ViewGroup.LayoutParams.WRAP_CONTENT);

                            hlay.addView(tvFromULogin, lppp);
                            hlay.addView(tvVS);
                            hlay.addView(tvToULogin, lppp);
                            hlay.addView(shimmerButton);

                            final LinearLayout ll = new LinearLayout(getApplicationContext());
                            ll.setGravity(Gravity.CENTER);
                            ll.setOrientation(LinearLayout.VERTICAL);

                            ll.addView(hlay, lphlay);

                            final CardView cardView = new CardView(getApplicationContext());
                            cardView.setUseCompatPadding(true);
                            cardView.setRadius(8);
                            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                            cardView.setCardElevation(8);
                            CardView.LayoutParams cp = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            cp.setMargins(0, 0, 0, 23);
                            cardView.setLayoutParams(cp);

                            shimmerButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ListAllInprogressIndividBattlesActivity.this, IndividualBattleActivity.class);
                                    intent.putExtra("battle_id", individBattles.get(finalI).getBattleId());
                                    startActivity(intent);
                                }
                            });

                            cardView.addView(ll);
                            if (individBattles.get(i).getCategory().equalsIgnoreCase("tricking")) {
                                llTrick.addView(cardView);
                            }
                            if (individBattles.get(i).getCategory().equalsIgnoreCase("parkour")) {
                                llPark.addView(cardView);
                            }
                            if (individBattles.get(i).getCategory().equalsIgnoreCase("breakdance")) {
                                llBreak.addView(cardView);
                            }
                            if (individBattles.get(i).getCategory().equalsIgnoreCase("trampoline")) {
                                llTramp.addView(cardView);
                            }

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

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                    shTrick.setTextColor(getResources().getColor(R.color.colorSiteRed));
                    shPark.setTextColor(getResources().getColor(R.color.colorSiteRed));
                    shBreak.setTextColor(getResources().getColor(R.color.colorSiteRed));
                    shTramp.setTextColor(getResources().getColor(R.color.colorSiteRed));
                }
                swipeUpdateInfo.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<IndividBattle>> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(false);
                TextView tv = new TextView(getApplicationContext());
                tv.setTypeface(typeface);
                tv.setTextColor(getResources().getColor(R.color.colorBlack));
                tv.setText(getResources().getString(R.string.no_battles));
                lay.addView(tv);
            }
        });


    }

}
