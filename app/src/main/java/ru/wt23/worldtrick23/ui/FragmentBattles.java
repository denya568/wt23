package ru.wt23.worldtrick23.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import info.hoang8f.widget.FButton;
import retrofit2.Callback;
import retrofit2.Response;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.MyDuel;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;

public class FragmentBattles extends BaseFragment {
    LinearLayout lay;
    FButton bTrick, bTramp, bBreak, bPark, bMyBattles, bBattlesInProgress;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    Typeface typeface;
    SwipeSelector swipeSelector;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battles, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pushOnline();

        typeface = getTypeface();
        context = getActivity().getApplicationContext();

        lay = (LinearLayout) getActivity().findViewById(R.id.content_battles);
        swipeSelector = (SwipeSelector) getActivity().findViewById(R.id.swipeSelector);
        bTrick = (FButton) getActivity().findViewById(R.id.b_trick);
        bTramp = (FButton) getActivity().findViewById(R.id.b_tramp);
        bBreak = (FButton) getActivity().findViewById(R.id.b_break);
        bPark = (FButton) getActivity().findViewById(R.id.b_parkour);
        bMyBattles = (FButton) getActivity().findViewById(R.id.b_myBattles);
        bBattlesInProgress = (FButton) getActivity().findViewById(R.id.b_battlesInProcess);

        swipeSelector.setItems(
                new SwipeItem(0, getResources().getString(R.string.individ_battles), ""),
                new SwipeItem(1, getResources().getString(R.string.group_battles), ""));
        swipeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {
                if ((Integer) item.value == 0) {
                    if (DBHelper.getBattleRequestsCount(context) > 0) {
                        bMyBattles.setTextColor(getResources().getColor(R.color.colorWhite));
                        bMyBattles.setText(getResources().getString(R.string.my_battles) + " (" + DBHelper.getBattleRequestsCount(context) + ")");
                    } else {
                        bMyBattles.setTextColor(getResources().getColor(R.color.colorBlack));
                        bMyBattles.setText(getResources().getString(R.string.my_battles));
                    }

                } else {
                    bMyBattles.setText(getResources().getString(R.string.my_battles));
                    //TODO проверить наличие меня в груп баттлах
                    //если я есть, то
                    bMyBattles.setTextColor(getResources().getColor(R.color.colorWhite));
                    //иначе
                    bMyBattles.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }
        });

        bMyBattles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) swipeSelector.getSelectedItem().value == 0) {
                    Intent intent = new Intent(getActivity(), MyBattlesActivity.class);
                    intent.putExtra("type", "ind");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), MyBattlesActivity.class);
                    intent.putExtra("type", "group");
                    startActivity(intent);
                }

            }
        });
        //все идущие баттлы сайта
        bBattlesInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) swipeSelector.getSelectedItem().value == 0) {
                    Intent intent = new Intent(getActivity(), ListAllInprogressIndividBattlesActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ListAllInprogressGroupBattlesActivity.class);
                    startActivity(intent);
                }
            }
        });
        bTrick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) swipeSelector.getSelectedItem().value == 0) {
                    Intent intent = new Intent(getActivity(), ListIndividualBattleActivity.class);
                    intent.putExtra("category", "tricking");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ListGroupBattleActivity.class);
                    intent.putExtra("category", "tricking");
                    startActivity(intent);
                }
            }
        });


        bTramp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) swipeSelector.getSelectedItem().value == 0) {
                    Intent intent = new Intent(getActivity(), ListIndividualBattleActivity.class);
                    intent.putExtra("category", "trampoline");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ListGroupBattleActivity.class);
                    intent.putExtra("category", "trampoline");
                    startActivity(intent);
                }
            }
        });

        bBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) swipeSelector.getSelectedItem().value == 0) {
                    Intent intent = new Intent(getActivity(), ListIndividualBattleActivity.class);
                    intent.putExtra("category", "breakdance");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ListGroupBattleActivity.class);
                    intent.putExtra("category", "breakdance");
                    startActivity(intent);
                }
            }
        });

        bPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) swipeSelector.getSelectedItem().value == 0) {
                    Intent intent = new Intent(getActivity(), ListIndividualBattleActivity.class);
                    intent.putExtra("category", "parkour");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ListGroupBattleActivity.class);
                    intent.putExtra("category", "parkour");
                    startActivity(intent);
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeBattles);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSiteRed),
                getResources().getColor(R.color.colorSiteGreen),
                getResources().getColor(R.color.colorSiteWhiteBlue),
                getResources().getColor(R.color.colorWhite));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMyReqs();
            }
        });
        loadMyReqs();
    }

    private void loadMyReqs() {
        if ((Integer) swipeSelector.getSelectedItem().value == 0) {
            swipeRefreshLayout.setRefreshing(true);
            if (!hasConnection(context)) {
                makeText(getResources().getString(R.string.no_network));
                bMyBattles.setTextColor(getResources().getColor(R.color.colorBlack));
                swipeRefreshLayout.setRefreshing(false);
            } else {
                final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
                Retrofit client1 = new Retrofit.Builder()
                        .baseUrl(DBHelper.URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final ApiWT23 service1 = client1.create(ApiWT23.class);
                Call<ArrayList<MyDuel>> myDuelCall = service1.getDuelsForMe(DBHelper.getUser(context).getServerId());
                myDuelCall.enqueue(new Callback<ArrayList<MyDuel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<MyDuel>> call, Response<ArrayList<MyDuel>> response) {
                        if (response.body() != null && response.body().size() > 0) {
                            int indSize = response.body().size();
                            bMyBattles.setText(getResources().getString(R.string.my_battles) + " (" + indSize + ")");
                            bMyBattles.setTextColor(getResources().getColor(R.color.colorWhite));
                            DBHelper.setBattleRequestsCount(context, indSize);
                        } else {
                            bMyBattles.setText(getResources().getString(R.string.my_battles));
                            bMyBattles.setTextColor(getResources().getColor(R.color.colorBlack));
                            DBHelper.setBattleRequestsCount(context, 0);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<ArrayList<MyDuel>> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        } else swipeRefreshLayout.setRefreshing(false);
    }
}


