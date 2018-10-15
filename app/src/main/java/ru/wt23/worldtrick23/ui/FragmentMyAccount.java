package ru.wt23.worldtrick23.ui;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import info.hoang8f.widget.FButton;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.Utils.Encode;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.Me;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;

public class FragmentMyAccount extends BaseFragment {

    EditText login, pass, name, secName, patronomyc, email, instagram, aboutMySelf;
    DatePicker bd;
    TextView tvRank, wins, fails, battles, regDate;
    CheckBox trick, tramp, park, breakDance;
    FButton saveChanges, exit;
    SwipeRefreshLayout swipeUpdateInfo;
    LinearLayout lay;
    Toolbar toolbar;
    Typeface typeface;
    String break_dance = "N";
    String tricking = "N";
    String trampoline = "N";
    String parkour = "N";

    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity().getApplicationContext();
        pushOnline();
        toolbar = getToolbar();
        typeface = getTypeface();
        lay = (LinearLayout) getActivity().findViewById(R.id.accLay);
        login = (EditText) getActivity().findViewById(R.id.mylogin);
        login.setTypeface(typeface);
        pass = (EditText) getActivity().findViewById(R.id.myPass);
        pass.setTypeface(typeface);
        name = (EditText) getActivity().findViewById(R.id.name);
        name.setTypeface(typeface);
        secName = (EditText) getActivity().findViewById(R.id.secondName);
        secName.setTypeface(typeface);
        patronomyc = (EditText) getActivity().findViewById(R.id.patronymic);
        patronomyc.setTypeface(typeface);
        email = (EditText) getActivity().findViewById(R.id.email);
        email.setTypeface(typeface);
        bd = (DatePicker) getActivity().findViewById(R.id.dp_birthday);
        instagram = (EditText) getActivity().findViewById(R.id.instagram);
        instagram.setTypeface(typeface);
        aboutMySelf = (EditText) getActivity().findViewById(R.id.aboutmyself);
        aboutMySelf.setTypeface(typeface);
        regDate = (TextView) getActivity().findViewById(R.id.regDate);
        regDate.setTypeface(typeface);
        saveChanges = (FButton) getActivity().findViewById(R.id.saveChanges);
        saveChanges.setTypeface(typeface);
        tvRank = (TextView) getActivity().findViewById(R.id.karma);
        tvRank.setTypeface(typeface);
        wins = (TextView) getActivity().findViewById(R.id.wins);
        wins.setTypeface(typeface);
        fails = (TextView) getActivity().findViewById(R.id.fails);
        fails.setTypeface(typeface);
        battles = (TextView) getActivity().findViewById(R.id.battles);
        battles.setTypeface(typeface);
        trick = (CheckBox) getActivity().findViewById(R.id.cbTrick);
        trick.setTypeface(typeface);
        tramp = (CheckBox) getActivity().findViewById(R.id.cbTramp);
        tramp.setTypeface(typeface);
        park = (CheckBox) getActivity().findViewById(R.id.cbPark);
        park.setTypeface(typeface);
        breakDance = (CheckBox) getActivity().findViewById(R.id.cbBreak);
        breakDance.setTypeface(typeface);
        exit = new FButton(context);
        exit.setButtonColor(getResources().getColor(R.color.colorSiteWhiteBlue));
        exit.setTextColor(getResources().getColor(R.color.colorWhite));
        exit.setTypeface(typeface);
        exit.setText(getResources().getString(R.string.log_out));
        swipeUpdateInfo = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeMyAcc);
        swipeUpdateInfo.setColorSchemeColors(getResources().getColor(R.color.colorSiteRed),
                getResources().getColor(R.color.colorSiteGreen),
                getResources().getColor(R.color.colorSiteWhiteBlue),
                getResources().getColor(R.color.colorWhite));

        setListeners();
        updateUI(DBHelper.getUser(context));
        update();
    }

    @Override
    public void onPause() {
        super.onPause();
        toolbar.removeView(exit);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.addView(exit);
    }

    private void setListeners() {
        pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyBoard(lay);
                    return true;
                }
                return false;
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(lay);
                toolbar.removeView(exit);
                DBHelper.deleteUser(context);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentNews news = new FragmentNews();
                fragmentTransaction.replace(R.id.lay, news, null);
                fragmentTransaction.commit();
            }
        });
        name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyBoard(lay);
                    return true;
                }
                return false;
            }
        });
        secName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyBoard(lay);
                    return true;
                }
                return false;
            }
        });
        patronomyc.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyBoard(lay);
                    return true;
                }
                return false;
            }
        });
        email.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyBoard(lay);
                    return true;
                }
                return false;
            }
        });
        instagram.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyBoard(lay);
                    return true;
                }
                return false;
            }
        });
        swipeUpdateInfo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        breakDance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    break_dance = "Y";
                } else {
                    break_dance = "N";
                }
            }
        });
        trick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tricking = "Y";
                } else {
                    tricking = "N";
                }
            }
        });
        tramp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    trampoline = "Y";
                } else {
                    trampoline = "N";
                }
            }
        });
        park.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    parkour = "Y";
                } else {
                    parkour = "N";
                }
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //отправить запрос
                hideKeyBoard(lay);

                String year = String.valueOf(bd.getYear());
                String month = String.valueOf(bd.getMonth() + 1);
                String day = String.valueOf(bd.getDayOfMonth());
                if (month.length() <= 1) {
                    month = "0" + month;
                }
                if (day.length() <= 1) {
                    day = "0" + day;
                }

                UserDB me = DBHelper.getUser(context);
                me.setLogin(login.getText().toString());
                //TODO md5
                //me.setPassword(Encode.md5(login.getText().toString()+pass.getText().toString()));
                me.setPassword(pass.getText().toString());
                me.setName(name.getText().toString());
                me.setSurname(secName.getText().toString());
                me.setPatronymic(patronomyc.getText().toString());
                me.setEmail(email.getText().toString());
                me.setDateOld(day + "." + month + "." + year);
                me.setInstagram(instagram.getText().toString());
                me.setAbouth(aboutMySelf.getText().toString());
                me.setBreak(break_dance);
                me.setTricking(tricking);
                me.setTrampoline(trampoline);
                me.setParkour(parkour);
                changeMyInfo(me);
            }
        });
    }

    private void changeMyInfo(final UserDB userDBchanged) {
        swipeUpdateInfo.setRefreshing(true);
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        final UserDB userDB = DBHelper.getUser(context);
        Call<ResponseBody> meCall = service.setInfoAboutMe(userDB.getLogin(), userDB.getPassword(), userDBchanged.getPassword(), userDBchanged.getName(), userDBchanged.getSurname(),
                userDBchanged.getPatronymic(), userDBchanged.getEmail(), userDBchanged.getDateOld(), userDBchanged.getInstagram(), userDBchanged.getAbouth(), userDBchanged.getBreak(), userDBchanged.getTricking(),
                userDBchanged.getTrampoline(), userDBchanged.getParkour());
        meCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                swipeUpdateInfo.setRefreshing(false);
                if (response.code() == 200) {
                    //обновляем бд телефона
                    DBHelper.updateUser(context, userDBchanged);
                    makeText(getResources().getString(R.string.saved));
                    updateUI(userDBchanged);
                } else {
                    makeText(getResources().getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(false);
            }
        });
    }

    public void update() {
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        Call<Me> meCall = service.getInfoAboutMe(login.getText().toString(), pass.getText().toString());
        meCall.enqueue(new Callback<Me>() {
            @Override
            public void onResponse(Call<Me> call, Response<Me> response) {
                swipeUpdateInfo.setRefreshing(false);
                if (response.isSuccessful()) {
                    Me me = response.body();
                    UserDB userDB = meToUserDB(context, me);
                    userDB.setPhoneId(DBHelper.getUser(context).getPhoneId());
                    //TODO md5
                    userDB.setPassword(DBHelper.getUser(context).getPassword());
                    DBHelper.updateUser(context, userDB);
                    me.setPassword(pass.getText().toString());
                    updateUI(meToUserDB(context, me));
                }
            }

            @Override
            public void onFailure(Call<Me> call, Throwable t) {
                makeText(getResources().getString(R.string.no_network));
                makeText(t.getMessage());
                swipeUpdateInfo.setRefreshing(false);
            }
        });
    }

    private void updateUI(UserDB userDB) {
        if (userDB != null) {
            exit.setVisibility(View.VISIBLE);
            saveChanges.setVisibility(View.VISIBLE);
            login.setText(userDB.getLogin());
            pass.setText(userDB.getPassword());
            name.setText(userDB.getName());
            secName.setText(userDB.getSurname());
            patronomyc.setText(userDB.getPatronymic());
            email.setText(userDB.getEmail());

            String[] date_old = userDB.getDateOld().split("-");
            if (date_old.length < 3) {
                date_old = userDB.getDateOld().replace(".", "/").split("/");
            }
            bd.init(Integer.parseInt(date_old[2]), Integer.parseInt(date_old[1]) - 1, Integer.parseInt(date_old[0]), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

                }
            });

            instagram.setText(userDB.getInstagram());
            aboutMySelf.setText(userDB.getAbouth());
            regDate.setText(userDB.getDateOld());
            tvRank.setText(String.valueOf(userDB.getRang()));
            wins.setText(String.valueOf(userDB.getWins()));
            fails.setText(String.valueOf(userDB.getFails()));
            battles.setText(userDB.getCountBattles() + "");
            if (userDB.getTricking() != null && userDB.getTricking().equalsIgnoreCase("Y")) {
                trick.setChecked(true);
            } else trick.setChecked(false);
            if (userDB.getTrampoline() != null && userDB.getTrampoline().equalsIgnoreCase("Y")) {
                tramp.setChecked(true);
            } else tramp.setChecked(false);
            if (userDB.getParkour() != null && userDB.getParkour().equalsIgnoreCase("Y")) {
                park.setChecked(true);
            } else park.setChecked(false);
            if (userDB.getBreak() != null && userDB.getBreak().equalsIgnoreCase("Y")) {
                breakDance.setChecked(true);
            } else breakDance.setChecked(false);

            if (userDB.getTricking() != null && userDB.getTricking().equalsIgnoreCase("Y")) {
                trick.setChecked(true);
                tricking = "Y";
            } else {
                trick.setChecked(false);
                tricking = "N";
            }
            if (userDB.getTrampoline() != null && userDB.getTrampoline().equalsIgnoreCase("Y")) {
                tramp.setChecked(true);
                trampoline = "Y";
            } else {
                tramp.setChecked(false);
                trampoline = "N";
            }
            if (userDB.getParkour() != null && userDB.getParkour().equalsIgnoreCase("Y")) {
                park.setChecked(true);
                parkour = "Y";
            } else {
                park.setChecked(false);
                parkour = "N";
            }
            if (userDB.getBreak() != null && userDB.getBreak().equalsIgnoreCase("Y")) {
                breakDance.setChecked(true);
                break_dance = "Y";
            } else {
                breakDance.setChecked(false);
                break_dance = "N";
            }


        } else {
            saveChanges.setVisibility(View.INVISIBLE);
            exit.setVisibility(View.INVISIBLE);
        }


    }

}
