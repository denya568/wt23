package ru.wt23.worldtrick23.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import info.hoang8f.widget.FButton;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.Me;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;

public class FragmentAutorization extends BaseFragment {

    BottomSheetBehavior bottomSheetBehavior;
    EditText login, password;
    TextView restorePass;
    FButton enter, newRegistration;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    LinearLayout lay;
    AlertDialog.Builder builder;
    DBHelper dbHelper;
    Context context;

    EditText regLogin, regPass, regSecondName, regName, regPatronymic, regEmail;
    FButton bRegistration;
    LinearLayout regLay;
    DatePicker regBirthday;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_autorization, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearFocus();
            }
        });
        return rootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity().getApplicationContext();

        LinearLayout llBottomSheet = (LinearLayout) getActivity().findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        //bottomSheetBehavior.setPeekHeight(340);
        bottomSheetBehavior.setHideable(true);
        final LinearLayout bottomLay = (LinearLayout) getActivity().findViewById(R.id.bottomLay);
        final View view = getLayoutInflater().inflate(R.layout.fragment_registration, null);
        regLay = (LinearLayout) view.findViewById(R.id.regLay);
        regLogin = (EditText) view.findViewById(R.id.regLogin);
        regLogin.setTypeface(getTypeface());
        regPass = (EditText) view.findViewById(R.id.regPass);
        regPass.setTypeface(getTypeface());
        regEmail = (EditText) view.findViewById(R.id.regEmail);
        regEmail.setTypeface(getTypeface());
        regSecondName = (EditText) view.findViewById(R.id.regSecondName);
        regSecondName.setTypeface(getTypeface());
        regName = (EditText) view.findViewById(R.id.regName);
        regName.setTypeface(getTypeface());
        regPatronymic = (EditText) view.findViewById(R.id.regPatronymic);
        regPatronymic.setTypeface(getTypeface());
        regBirthday = (DatePicker) view.findViewById(R.id.regBirthday);
        regBirthday.init(1990, 0, 23, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

            }
        });
        bRegistration = (FButton) view.findViewById(R.id.registrate);
        bRegistration.setTypeface(getTypeface());
        bRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!regEmail.getText().toString().contains("@")) {
                    Snackbar.make(lay, getResources().getString(R.string.email_not_exist), Snackbar.LENGTH_SHORT).show();
                } else if (regLogin.getText().toString().length() == 0
                        || regPass.getText().toString().length() == 0
                        || regEmail.getText().toString().length() == 0
                        || regName.getText().toString().length() == 0) {
                    Snackbar.make(lay, getResources().getString(R.string.fill_fields), Snackbar.LENGTH_SHORT).show();
                } else {
                    hideKeyBoard(lay);
                    String year = String.valueOf(regBirthday.getYear());
                    String month = String.valueOf(regBirthday.getMonth() + 1);
                    if (month.length() <= 1) {
                        month = "0" + month;
                    }
                    String day = String.valueOf(regBirthday.getDayOfMonth());
                    if (day.length() <= 1) {
                        day = "0" + day;
                    }
                    UserDB userDB = new UserDB();
                    userDB.setLogin(regLogin.getText().toString());
                    userDB.setPassword(regPass.getText().toString());
                    userDB.setEmail(regEmail.getText().toString());
                    userDB.setSurname(regSecondName.getText().toString());
                    userDB.setName(regName.getText().toString());
                    userDB.setPatronymic(regPatronymic.getText().toString());
                    userDB.setDateOld(day + "." + month + "." + year);
                    MyAsynk createAcc = new MyAsynk();
                    createAcc.execute(userDB);
                }
            }
        });


        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        lay = (LinearLayout) getActivity().findViewById(R.id.autorizationLayout);
        login = (EditText) getActivity().findViewById(R.id.login);
        login.setTypeface(getTypeface());

        login.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    signIn(login.getText().toString(), password.getText().toString());
                    return true;
                }
                return false;
            }
        });

        password = (EditText) getActivity().findViewById(R.id.password);
        password.setTypeface(getTypeface());
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    signIn(login.getText().toString(), password.getText().toString());
                    return true;
                }
                return false;
            }
        });
        enter = (FButton) getActivity().findViewById(R.id.enter);
        enter.setTypeface(getTypeface());
        newRegistration = (FButton) getActivity().findViewById(R.id.newRegistration);
        newRegistration.setTypeface(getTypeface());
        restorePass = (TextView) getActivity().findViewById(R.id.forgetPass);
        restorePass.setTypeface(getTypeface());
        restorePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                bottomLay.removeAllViews();
                View view = getLayoutInflater().inflate(R.layout.restore_pass, null);
                final FButton recover = (FButton) view.findViewById(R.id.b_recover);
                recover.setTypeface(getTypeface());
                recover.setTextColor(getResources().getColor(R.color.colorWhite));
                final EditText etMail = (EditText) view.findViewById(R.id.etMailRestore);
                etMail.setTypeface(getTypeface());

                etMail.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                            if (TextUtils.isEmpty(etMail.getText().toString()))
                                restorePassword(etMail.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });
                recover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restorePassword(etMail.getText().toString());
                    }
                });
                bottomLay.addView(view);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(login.getText().toString(), password.getText().toString());
            }
        });
        newRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomLay.removeAllViews();
                regLogin.setText(login.getText().toString());
                regPass.setText(password.getText().toString());
                bottomLay.addView(view);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }


    private void restorePassword(String email) {
        //TODO start loader
        hideKeyBoard(lay);
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DBHelper.URL.replace("/api", ""))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 service = retrofit.create(ApiWT23.class);
        Call<ResponseBody> call = service.restorePassword(email, "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case 200:
                        makeText(getResources().getString(R.string.info_email_natice));
                        break;
                    case 401:
                        makeText(getResources().getString(R.string.blocked_user) + "\n" + getResources().getString(R.string.contact_support));
                        break;
                    case 503:
                        makeText(getResources().getString(R.string.email_not_exist));
                        break;
                    default:
                        makeText(getResources().getString(R.string.no_network));
                        break;
                }
                //TODO end loader
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                makeText(getResources().getString(R.string.no_network));
                //TODO end loader
            }
        });
    }

    private void signIn(final String sLogin, final String sPassword) {
        if (sLogin.length() == 0 || sPassword.length() == 0) {
            makeText(getResources().getString(R.string.fill_fields));
        } else {
            //TODO start loader
            //OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(DBHelper.URL)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiWT23 service = client.create(ApiWT23.class);

            Call<ResponseBody> signInCall = service.signIn(login.getText().toString(), password.getText().toString());
            signInCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        getInfoAboutMe(login.getText().toString(), password.getText().toString());
                    } else if (response.code() == 503) {
                        makeText(getResources().getString(R.string.incorrect_l_or_p));
                    } else if (response.code() == 401) {
                        makeText(getResources().getString(R.string.user) + " " + login + " " + getResources().getString(R.string.blocked));
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
    }

    private void getInfoAboutMe(final String login, final String password) {
        //OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                //.client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        Call<Me> meCall = service.getInfoAboutMe(login, password);
        meCall.enqueue(new Callback<Me>() {
            @Override
            public void onResponse(Call<Me> call, Response<Me> response) {
                if (response.code() == 200) {
                    hideKeyBoard(lay);
                    Me me = response.body();
                    //TODO md5
                    me.setPassword(password);
                    DBHelper.createUser(context, me);

                    makeText(getResources().getString(R.string.successfully_logged));
                    makeSnack(lay, getResources().getString(R.string.hello) + " " + me.getName());

                    FragmentMyAccount fragmentMyAccount = new FragmentMyAccount();
                    fragmentTransaction.replace(R.id.lay, fragmentMyAccount, null);
                    fragmentTransaction.commit();

                } else if (response.code() == 401) {
                    makeText(getResources().getString(R.string.user) + " " + login + " " + getResources().getString(R.string.blocked));
                    Snackbar.make(lay, getResources().getString(R.string.contact_support), Snackbar.LENGTH_SHORT).show();

                } else if (response.code() == 503) {
                    makeText(getResources().getString(R.string.incorrect_l_or_p));
                } else {
                    makeText(getResources().getString(R.string.no_network));
                }
            }

            //TODO end loader

            @Override
            public void onFailure(Call<Me> call, Throwable t) {
                makeText(getResources().getString(R.string.no_network) + "sdf");
                //TODO end loader
            }
        });
    }


    private class MyAsynk extends AsyncTask<UserDB, Void, Integer> {
        UserDB userDB;

        @Override
        protected Integer doInBackground(UserDB... userDBS) {
            final int[] respCode = {0};
            userDB = userDBS[0];
            //OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(DBHelper.URL.replace("/api", ""))
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiWT23 apiWT23 = retrofit.create(ApiWT23.class);
            Call<ResponseBody> call = apiWT23.registration(userDB.getLogin(), userDB.getPassword(), userDB.getEmail(), userDB.getSurname(), userDB.getName(), userDB.getPatronymic(), userDB.getDateOld(), "");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    respCode[0] = response.code();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

            return respCode[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO start loader
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 404) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(lay, getResources().getString(R.string.successfully_regisrated) + "\n" + getResources().getString(R.string.info_email_natice) + "\n" + userDB.getEmail(), Snackbar.LENGTH_LONG).show();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                //TODO end loader
                signIn(userDB.getLogin(), userDB.getPassword());
            }
        }
    }

}
