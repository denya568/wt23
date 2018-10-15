package ru.wt23.worldtrick23.ui.baseUI;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.Me;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import ru.wt23.worldtrick23.ui.MainActivity;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class BaseFragment extends Fragment {
    private AlertDialog.Builder dialogBuilder = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void updateMyInfo() {
        final UserDB userDB = DBHelper.getUser(getActivity().getApplicationContext());
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 service = client.create(ApiWT23.class);
        Call<Me> meCall = service.getInfoAboutMe(userDB.getLogin(), userDB.getPassword());
        meCall.enqueue(new Callback<Me>() {
            @Override
            public void onResponse(Call<Me> call, Response<Me> response) {
                if (response.isSuccessful()) {
                    Me me = response.body();
                    me.setPassword(userDB.getPassword());
                    DBHelper.deleteUser(getActivity().getApplicationContext());
                    DBHelper.createUser(getActivity().getApplicationContext(), me);
                }
            }

            @Override
            public void onFailure(Call<Me> call, Throwable t) {

            }
        });
    }

    public UserDB meToUserDB(Context context, Me me) {
        UserDB userDB = DBHelper.getUser(context);
        userDB.setServerId(Integer.parseInt(me.getId()));
        userDB.setLogin(me.getLogin());
        userDB.setPassword(me.getPassword());
        userDB.setName(me.getName());
        userDB.setSurname(me.getSurname());
        userDB.setPatronymic(me.getPatronymic());
        userDB.setEmail(me.getEmail());
        userDB.setDateOld(me.getDateOld());
        userDB.setDateReg(me.getDateReg());
        userDB.setCategory(me.getCategory());
        userDB.setActive(me.getActive());
        userDB.setInstagram(me.getInstagram());
        userDB.setAbouth(me.getAbouth());
        userDB.setBreak(me.getBreak());
        userDB.setTricking(me.getTricking());
        userDB.setTrampoline(me.getTrampoline());
        userDB.setParkour(me.getParkour());
        userDB.setPostMail(me.getPostMail());
        userDB.setRang(me.getRang());
        userDB.setWins(me.getWins());
        userDB.setFails(me.getFails());
        userDB.setCountBattles(me.getCountBattles());
        return userDB;
    }

    public void pushOnline() {
        final UserDB userDB = DBHelper.getUser(getActivity().getApplicationContext());
        if (userDB != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(DBHelper.URL + "set_online?login_id=" + userDB.getServerId());
                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.connect();
                        InputStreamReader in = new InputStreamReader(conn.getInputStream());
                        if (conn.getResponseCode() == 200) {
                            //збс...
                        }
                        conn.disconnect();
                        in.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public Typeface getTypeface() {
        return Typeface.createFromAsset(getActivity().getAssets(), "fonts/planet_n2_cyr_lat.otf");
    }

    public void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public AlertDialog.Builder showAlert(int icon, View view, String title, String message, boolean cancelable) {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        if (icon != 0) {
            dialogBuilder.setIcon(icon);
        }
        if (view != null) {
            dialogBuilder.setView(view);
        }
        if (title != null) {
            dialogBuilder.setTitle(title);
        }
        if (message != null) {
            dialogBuilder.setMessage(message);
        }
        dialogBuilder.setCancelable(cancelable);

        return dialogBuilder;
    }

    public Toolbar getToolbar() {
        return (Toolbar) getActivity().findViewById(R.id.toolbar);
    }

    public void makeSnack(View view, Object txt) {
        Snackbar.make(view, txt.toString(), Snackbar.LENGTH_SHORT).show();
    }

    public void makeText(Object txt) {
        Toast.makeText(getActivity().getApplicationContext(), txt.toString(), Toast.LENGTH_SHORT).show();
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor, int rectColor) {
        text = " " + text + " ";
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);

        Paint rect = new Paint();
        rect.setColor(rectColor);

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.drawRect(0, baseline, width, 0, rect);
        canvas.drawText(text, 0, baseline - 4, paint);

        return image;
    }

}