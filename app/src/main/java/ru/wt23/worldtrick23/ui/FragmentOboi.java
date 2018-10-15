package ru.wt23.worldtrick23.ui;

import android.Manifest;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import ru.wt23.worldtrick23.io.Wallpaper;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;

public class FragmentOboi extends BaseFragment {
    ScrollView scrollView;
    LinearLayout lay;
    SwipeRefreshLayout swipeUpdateInfo;

    Typeface typeface;
    Context context;
    UserDB userDB;
    Picasso picasso;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_oboi, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity().getApplicationContext();
        userDB = DBHelper.getUser(context);
        pushOnline();

        typeface = getTypeface();

        lay = (LinearLayout) getActivity().findViewById(R.id.lay_oboi);
        scrollView = (ScrollView) getActivity().findViewById(R.id.scroll_oboi);

        swipeUpdateInfo = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeOboi);
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

        picasso = new Picasso.Builder(getActivity().getApplicationContext())
                .downloader(new OkHttp3Downloader(UnsafeOkHttpClient.getUnsafeOkHttpClient()))
                .build();
    }

    private void loadPage() {
        swipeUpdateInfo.setRefreshing(true);
        final OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiWT23 service = client.create(ApiWT23.class);
        Call<ArrayList<Wallpaper>> oboiCall = service.getOboi();
        oboiCall.enqueue(new Callback<ArrayList<Wallpaper>>() {
            @Override
            public void onResponse(Call<ArrayList<Wallpaper>> call, Response<ArrayList<Wallpaper>> response) {
                swipeUpdateInfo.setRefreshing(false);
                if (response.isSuccessful()) {
                    lay.removeAllViews();
                    final ArrayList<Wallpaper> oboi = response.body();
                    for (int i = 0; i < oboi.size(); i++) {
                        final int finalI = i;
                        LinearLayout l = new LinearLayout(getActivity());
                        l.setOrientation(LinearLayout.VERTICAL);
                        l.setGravity(Gravity.CENTER);

                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        cardParams.setMargins(0, 0, 0, 10);
                        final CardView cardView = new CardView(getActivity());
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        cardView.setContentPadding(0, 0, 0, 10);
                        cardView.setRadius(23);
                        cardView.setUseCompatPadding(true);
                        cardView.setCardElevation(8);

                        android.support.v7.widget.Toolbar toolbar = new android.support.v7.widget.Toolbar(getActivity());
                        toolbar.inflateMenu(R.menu.oboi_menu);
                        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.download:
                                        //загрузка файла

                                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                        } else {
                                            //perm granted
                                            downloadIMG(oboi.get(finalI).getLink());
                                        }
                                        return true;
                                    case R.id.copyLink:
                                        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clipData = ClipData.newPlainText("", oboi.get(finalI).getLink());
                                        clipboardManager.setPrimaryClip(clipData);
                                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.copied), Toast.LENGTH_SHORT).show();
                                        return true;
                                    case R.id.open_browser:
                                        Uri address = Uri.parse(oboi.get(finalI).getLink());
                                        Intent openlinkIntent = new Intent(Intent.ACTION_VIEW, address);
                                        startActivity(openlinkIntent);
                                        return true;
                                }
                                return false;
                            }
                        });

                        CardView shapka = new CardView(getActivity());
                        shapka.setBackgroundResource(R.drawable.oboi_shapka);
                        shapka.setRadius(23);
                        shapka.setCardElevation(0);
                        shapka.setContentPadding(10, 0, 10, 10);
                        shapka.addView(toolbar);

                        TextView tvName = new TextView(getActivity());
                        tvName.setTypeface(typeface);
                        tvName.setTextColor(getResources().getColor(R.color.colorWhite));
                        String name = oboi.get(i).getFile();
                        name = name.substring(0, name.length() - 4);
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        tvName.setText(name);
                        tvName.setTextSize(20);
                        tvName.setGravity(Gravity.CENTER);

                        shapka.addView(tvName);

                        final ImageView imageView = new ImageView(getActivity());
                        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                        ViewGroup.LayoutParams lpp = new LinearLayout.LayoutParams((int) (displayMetrics.widthPixels / 1.2), (int) (displayMetrics.heightPixels / 2.7));
                        imageView.setPadding(0, 10, 0, 10);
                        imageView.setLayoutParams(lpp);
                        final String finalName = name;
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent openPhoto = new Intent(getActivity(), PhotoActivity.class);
                                openPhoto.putExtra("name", finalName);
                                openPhoto.putExtra("link", oboi.get(finalI).getLink());
                                startActivity(openPhoto);
                            }
                        });

                        picasso.load(oboi.get(finalI).getLink())
                                .placeholder(R.drawable.loading) //показываем что-то, пока не загрузится указанная картинка
                                .error(R.drawable.error) // показываем что-то, если не удалось скачать картинку
                                .into(imageView);

                        l.addView(shapka);
                        l.addView(imageView);

                        cardView.addView(l);
                        lay.addView(cardView, cardParams);
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.no_network) + "\n" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Wallpaper>> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(false);
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void downloadIMG(final String link) {
        final boolean[] complete = {true};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = picasso.load(link).get();
                    File path = new File(Environment.getExternalStorageDirectory().toString() + "/wt23/");
                    if (!path.exists()) {
                        path.mkdir();
                    }
                    File file = new File(path, link.substring(link.lastIndexOf("/") + 1, link.length() - 4) + ".jpg");
                    OutputStream fOut = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                    MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                } catch (Exception e) {
                    complete[0] = false;
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (complete[0]) {
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.downloaded), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.file_error), Toast.LENGTH_SHORT).show();
        }


    }


}
