package ru.wt23.worldtrick23.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
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
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;

public class FragmentSearch extends BaseFragment {
    AutoCompleteTextView etFind;
    ImageButton ibSearch;
    LinearLayout lay;
    SwipeRefreshLayout swipeRefreshLayout;

    DBHelper dbHelper;
    Typeface typeface;
    Context context;
    UserDB userDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity().getApplicationContext();
        userDB = DBHelper.getUser(context);
        pushOnline();

        typeface = getTypeface();

        etFind = (AutoCompleteTextView) getActivity().findViewById(R.id.etSearch);
        etFind.setTypeface(typeface);
        etFind.setThreshold(1);
        ibSearch = (ImageButton) getActivity().findViewById(R.id.ibSearch);
        lay = (LinearLayout) getActivity().findViewById(R.id.searchLay);

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeSearch);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSiteRed),
                getResources().getColor(R.color.colorSiteGreen),
                getResources().getColor(R.color.colorSiteWhiteBlue),
                getResources().getColor(R.color.colorWhite));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage("");
                etFind.setText("");
                hideKeyBoard();
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        loadPage("");
        swipeRefreshLayout.setRefreshing(true);

    }

    private void loadPage(final String who) {
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<WT23User>> users = service.getWtUsers(userDB.getServerId());
        users.enqueue(new Callback<ArrayList<WT23User>>() {
            @Override
            public void onResponse(Call<ArrayList<WT23User>> call, Response<ArrayList<WT23User>> response) {
                if (response.isSuccessful()) {
                    final ArrayList<WT23User> wt23Users = response.body();

                    swipeRefreshLayout.setRefreshing(false);
                    lay.removeAllViews();

                    ArrayList<String> userLogins = new ArrayList<>();
                    for (int i = 0; i < wt23Users.size(); i++) {
                        userLogins.add(wt23Users.get(i).getLogin());
                    }
                    //etFind.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, userLogins));
                    etFind.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.actv_style, userLogins));
                    etFind.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            switch (keyEvent.getKeyCode()) {
                                case KeyEvent.KEYCODE_ENTER:
                                    String login = etFind.getText().toString();
                                    loadPage(login);
                                    hideKeyBoard();
                                    return true;
                            }
                            return false;
                        }
                    });
                    ibSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String login = etFind.getText().toString();
                            loadPage(login);
                            hideKeyBoard();
                        }
                    });

                    for (int i = 0; i < wt23Users.size(); i++) {
                        final int finalI = i;
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (GridLayout.LayoutParams.MATCH_PARENT), ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.weight = 137;
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams((int) (GridLayout.LayoutParams.MATCH_PARENT), ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp2.weight = 26;

                        if (who.equalsIgnoreCase("")) {
                            Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                            TextView step = new TextView(getActivity());
                            step.setTypeface(boldTypeface);
                            step.setTextSize(13);
                            step.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                            step.setBackground(getResources().getDrawable(R.drawable.ic_person_white_24dp));
                            step.setPadding(0, 23, 0, 0);
                            step.setTextColor(getResources().getColor(R.color.colorBlack));
                            step.setText(String.valueOf(i + 1));
                            step.setLayoutParams(lp);


                            TextView tvLogin = new TextView(getActivity());
                            tvLogin.setTypeface(typeface);
                            tvLogin.setTextColor(getResources().getColor(R.color.colorWhite));
                            tvLogin.setTextSize(17);
                            tvLogin.setGravity(Gravity.CENTER | Gravity.START);
                            tvLogin.setText(wt23Users.get(i).getLogin());
                            tvLogin.setLayoutParams(lp2);
                            tvLogin.setPadding(10, 23, 10, 23);
                            tvLogin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                                    intent.putExtra("user_id", wt23Users.get(finalI).getId());
                                    startActivity(intent);
                                }
                            });

                            TextView tvRang = new TextView(getActivity());
                            tvRang.setTypeface(typeface);
                            tvRang.setTextColor(getResources().getColor(R.color.colorSiteGreen));
                            tvRang.setGravity(Gravity.END);
                            tvRang.setText(String.valueOf(wt23Users.get(i).getRang()));
                            tvRang.setLayoutParams(lp);
                            tvRang.setPadding(5, 23, 5, 23);


                            LinearLayout l = new LinearLayout(getActivity());
                            l.setGravity(Gravity.CENTER);
                            l.setWeightSum(300);
                            l.setOrientation(LinearLayout.HORIZONTAL);
                            l.setBackground(getResources().getDrawable(R.drawable.round_button));
                            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            llp.setMargins(10, 5, 10, 5);
                            l.setLayoutParams(llp);

                            l.addView(step);
                            l.addView(tvLogin);
                            l.addView(tvRang);

                            lay.addView(l);
                        } else if (wt23Users.get(i).getLogin().contains(who)) {
                            Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                            TextView step = new TextView(getActivity());
                            step.setTypeface(boldTypeface);
                            step.setTextSize(13);
                            step.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                            step.setBackground(getResources().getDrawable(R.drawable.ic_person_white_24dp));
                            step.setPadding(0, 23, 0, 0);
                            step.setTextColor(getResources().getColor(R.color.colorBlack));
                            step.setText(String.valueOf(i + 1));
                            step.setLayoutParams(lp);


                            TextView tvLogin = new TextView(getActivity());
                            tvLogin.setTypeface(typeface);
                            tvLogin.setTextColor(getResources().getColor(R.color.colorWhite));
                            tvLogin.setTextSize(17);
                            tvLogin.setGravity(Gravity.CENTER | Gravity.START);
                            tvLogin.setText(wt23Users.get(i).getLogin());
                            tvLogin.setLayoutParams(lp2);
                            tvLogin.setPadding(10, 23, 10, 23);
                            tvLogin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                                    intent.putExtra("user_id", wt23Users.get(finalI).getId());
                                    startActivity(intent);
                                }
                            });

                            TextView tvRang = new TextView(getActivity());
                            tvRang.setTypeface(typeface);
                            tvRang.setTextColor(getResources().getColor(R.color.colorSiteGreen));
                            tvRang.setGravity(Gravity.END);
                            tvRang.setText(String.valueOf(wt23Users.get(i).getRang()));
                            tvRang.setLayoutParams(lp);
                            tvRang.setPadding(5, 23, 5, 23);


                            LinearLayout l = new LinearLayout(getActivity());
                            l.setGravity(Gravity.CENTER);
                            l.setWeightSum(300);
                            l.setOrientation(LinearLayout.HORIZONTAL);
                            l.setBackground(getResources().getDrawable(R.drawable.round_button));
                            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            llp.setMargins(10, 5, 10, 5);
                            l.setLayoutParams(llp);

                            l.addView(step);
                            l.addView(tvLogin);
                            l.addView(tvRang);

                            lay.addView(l);
                        }


                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.no_network) + "\n" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WT23User>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(lay.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


}
