package ru.wt23.worldtrick23.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.evbus.Stuff;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.Magazine;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;
import ru.wt23.worldtrick23.ui.baseUI.MagazineAdapter;

public class FragmentMagazine extends BaseFragment {
    DBHelper dbHelper;
    LinearLayout lay;
    AlertDialog.Builder builder;
    SwipeRefreshLayout swipeUpdateInfo;
    SharedPreferences sp;
    Typeface typeface;
    RecyclerView recyclerView;
    List<Magazine> magazineList;
    TextView tvFab;
    FloatingActionButton fab;
    UserDB userDB;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_magazine, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity().getApplicationContext();
        pushOnline();
        userDB = DBHelper.getUser(context);
        typeface = getTypeface();
        lay = (LinearLayout) getActivity().findViewById(R.id.magazineLay);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_magazine);
        recyclerView.setHasFixedSize(true);
        sp = getActivity().getSharedPreferences("stuff", Context.MODE_PRIVATE);
        if (!DBHelper.getShopSaveChanges(context)) {
            sp.edit().clear().apply();
        }

        fab = (FloatingActionButton) getActivity().findViewById(R.id.shopFab);
        tvFab = (TextView) getActivity().findViewById(R.id.tvFab);
        tvFab.setTypeface(typeface);

        swipeUpdateInfo = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeMagaz);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp.getAll().size() != 0) {
                    final ArrayList<Stuff> stuffs = new ArrayList<>();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < magazineList.size(); i++) {
                        if (sp.contains(magazineList.get(i).getId())) {
                            int sum = sp.getInt(magazineList.get(i).getId(), 0);
                            int val = Integer.parseInt(magazineList.get(i).getPrice()) * sum;
                            sb.append(magazineList.get(i).getName() + " - " + sum + " (" + val + " " + getResources().getString(R.string.rank_money) + ")\n");

                            stuffs.add(new Stuff(magazineList.get(i).getId(),
                                    magazineList.get(i).getName(),
                                    sum,
                                    Integer.parseInt(magazineList.get(i).getPrice())));
                        }
                    }
                    if (userDB.getRang() < calculatePrice(stuffs)) {
                        Snackbar.make(lay, getResources().getString(R.string.low_rang), Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    LayoutInflater li = LayoutInflater.from(getActivity());
                    final View orderView = li.inflate(R.layout.magazine_order, null);
                    final AlertDialog.Builder builder = showAlert(0, orderView, null, null, false);
                    final TextView orderList = (TextView) orderView.findViewById(R.id.orderList);
                    orderList.setTypeface(typeface);
                    orderList.setText(sb.toString() + "\n" + calculatePrice(stuffs) + getResources().getString(R.string.rank_money));

                    final TextView loginSend = (TextView) orderView.findViewById(R.id.loginSend);
                    loginSend.setTypeface(typeface);
                    final EditText nameSend = (EditText) orderView.findViewById(R.id.nameSend);
                    nameSend.setTypeface(typeface);
                    final EditText secondNameSend = (EditText) orderView.findViewById(R.id.secondNameSend);
                    secondNameSend.setTypeface(typeface);
                    final EditText patronymicSend = (EditText) orderView.findViewById(R.id.patronymicSend);
                    patronymicSend.setTypeface(typeface);
                    final EditText addressSend = (EditText) orderView.findViewById(R.id.addressSend);
                    addressSend.setTypeface(typeface);
                    final EditText tel = (EditText) orderView.findViewById(R.id.telephone);
                    tel.setTypeface(typeface);
                    final EditText zipCodeSend = (EditText) orderView.findViewById(R.id.zipCodeSend);
                    zipCodeSend.setTypeface(typeface);
                    Button close = (Button) orderView.findViewById(R.id.close);
                    close.setTypeface(typeface);
                    Button ok = (Button) orderView.findViewById(R.id.ok);
                    ok.setTypeface(typeface);

                    //присваиваем значения из бд
                    loginSend.setText(userDB.getLogin());
                    nameSend.setText(userDB.getName());
                    secondNameSend.setText(userDB.getSurname());
                    patronymicSend.setText(userDB.getPatronymic());
                    if (userDB.getPostMail() == null || userDB.getPostMail().equalsIgnoreCase("null")) {
                        addressSend.setText("");
                    } else {
                        addressSend.setText(userDB.getPostMail());
                    }

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (orderList.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.didnt_order), Toast.LENGTH_LONG).show();
                            } else {
                                //проверяем все ли заполнено
                                if (nameSend.getText().toString().equals("")
                                        || secondNameSend.getText().toString().equals("")
                                        || zipCodeSend.getText().toString().equals("")
                                        || tel.getText().toString().equals("")
                                        || addressSend.getText().toString().equals("")) {

                                    Snackbar.make(orderView, getResources().getString(R.string.fill_fields), Snackbar.LENGTH_SHORT).show();

                                } else {
                                    //отпавить Post запрос
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("user_id", userDB.getServerId());
                                    map.put("name", nameSend.getText().toString());
                                    map.put("surname", secondNameSend.getText().toString());
                                    map.put("patronymic", patronymicSend.getText().toString());
                                    map.put("address", addressSend.getText().toString());
                                    map.put("tel", tel.getText().toString());
                                    map.put("zipcode", zipCodeSend.getText().toString());


                                    if (sendPostOrder(stuffs, map)) {
                                        Snackbar.make(lay, getResources().getString(R.string.we_will_send), Snackbar.LENGTH_LONG).show();
                                        alertDialog.cancel();
                                        userDB.setPostMail(addressSend.getText().toString());
                                        DBHelper.updateUser(context, userDB);
                                    } else {
                                        Snackbar.make(orderView, getResources().getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                                    }


                                }
                            }
                        }
                    });

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.cancel();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    List<Stuff> stuffs = new ArrayList<>();

    @Subscribe
    public void onMessageEvent(Stuff stuff) {
        boolean isExists = false;
        for (int i = 0; i < stuffs.size(); i++) {
            if (stuffs.get(i).idStuff.equalsIgnoreCase(stuff.idStuff)) {
                stuffs.set(i, stuff);
                isExists = true;
                break;
            }
        }
        if (!isExists) stuffs.add(stuff);
        tvFab.setText(calculatePrice(stuffs) + " " + getResources().getString(R.string.rank_money));
    }

    private void loadPage() {
        swipeUpdateInfo.setRefreshing(true);
        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<Magazine>> magazineCall = service.getMagazine();
        magazineCall.enqueue(new Callback<ArrayList<Magazine>>() {
            @Override
            public void onResponse(Call<ArrayList<Magazine>> call, Response<ArrayList<Magazine>> response) {

                if (response.isSuccessful()) {
                    magazineList = response.body();
                    MagazineAdapter magazineAdapter = new MagazineAdapter(getActivity(), magazineList);
                    recyclerView.setAdapter(magazineAdapter);

                } else {
                    makeText(getResources().getString(R.string.no_network) + "\n" + response.code());
                }
                swipeUpdateInfo.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<Magazine>> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(false);
                makeText(t.getMessage());
            }
        });
    }

    private int calculatePrice(List<Stuff> stuffs) {
        int sum = 0;
        for (int i = 0; i < stuffs.size(); i++) {
            sum += stuffs.get(i).price * stuffs.get(i).sum;
        }
        return sum;
    }

    private boolean sendPostOrder(ArrayList<Stuff> stuffs, Map<String, Object> userInfo) {
        final boolean[] result = {false};
        JSONObject userInfoJSON = new JSONObject();
        try {
            userInfoJSON.put("user_id", Integer.parseInt(String.valueOf(userInfo.get("user_id"))));
            userInfoJSON.put("name", userInfo.get("name"));
            userInfoJSON.put("surname", userInfo.get("surname"));
            userInfoJSON.put("patronymic", userInfo.get("patronymic"));
            userInfoJSON.put("address", userInfo.get("address"));
            userInfoJSON.put("tel", userInfo.get("tel"));
            userInfoJSON.put("zipcode", userInfo.get("zipcode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray dataJSON = new JSONArray();
        try {
            for (int i = 0; i < stuffs.size(); i++) {
                JSONObject dataJsonObject = new JSONObject();
                dataJsonObject.put("product_id", stuffs.get(i).idStuff);
                dataJsonObject.put("count_product", stuffs.get(i).sum);

                dataJSON.put(dataJsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        final Call<ResponseBody> magazineCall = service.sendMagazineOrder(userInfoJSON.toString(), dataJSON.toString());

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (magazineCall.execute().code() == 200) result[0] = true;
                    else result[0] = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];

    }

}