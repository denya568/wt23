package ru.wt23.worldtrick23.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

import java.io.File;
import java.util.ArrayList;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.IndividBattle;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;

public class IndividualBattleActivity extends AppCompatActivity {
    DBHelper dbHelper;
    String battle_id;
    TextView loginFrom, loginTo, tvWinner;
    LinearLayout layFrom, layTo;
    CardView lay;
    ImageButton uploadFrom, uploadTo, cameraFrom, cameraTo;
    SwipeRefreshLayout swipeUpdateInfo;
    ShimmerButton sendFrom, sendTo;

    Typeface typeface;
    Context context;
    UserDB userDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_battle);

        context = this;
        userDB = DBHelper.getUser(context);
        //PushOnline.push(myDB.getId());

        typeface = Typeface.createFromAsset(getAssets(), "fonts/planet_n2_cyr_lat.otf");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_background));

        battle_id = getIntent().getStringExtra("battle_id");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS);
        }

        loginFrom = (TextView) findViewById(R.id.loginFrom);
        loginFrom.setTypeface(typeface);
        loginTo = (TextView) findViewById(R.id.loginTo);
        loginTo.setTypeface(typeface);
        tvWinner = (TextView) findViewById(R.id.tvWinner);
        tvWinner.setTypeface(typeface);
        lay = (CardView) findViewById(R.id.layIndBattle);
        layFrom = (LinearLayout) findViewById(R.id.layFrom);
        layTo = (LinearLayout) findViewById(R.id.layTo);
        uploadFrom = (ImageButton) findViewById(R.id.ibUploadFrom);
        uploadTo = (ImageButton) findViewById(R.id.ibUploadTo);
        cameraFrom = (ImageButton) findViewById(R.id.ibCameraFrom);
        cameraTo = (ImageButton) findViewById(R.id.ibCameraTo);
        sendFrom = (ShimmerButton) findViewById(R.id.shsendFrom);
        sendFrom.setTypeface(typeface);
        sendFrom.setVisibility(View.GONE);
        sendTo = (ShimmerButton) findViewById(R.id.shsendTo);
        sendTo.setTypeface(typeface);
        sendTo.setVisibility(View.GONE);

        swipeUpdateInfo = (SwipeRefreshLayout) findViewById(R.id.swipeIndBattle);
        swipeUpdateInfo.setColorSchemeColors(getResources().getColor(R.color.colorSiteRed),
                getResources().getColor(R.color.colorSiteGreen),
                getResources().getColor(R.color.colorSiteBlue),
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

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    private void loadPage() {
        swipeUpdateInfo.setRefreshing(true);
        lay.setVisibility(View.INVISIBLE);
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(DBHelper.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 apiWT23 = retrofit.create(ApiWT23.class);
        Call<ArrayList<IndividBattle>> call = apiWT23.getAllIndividBattles();
        call.enqueue(new Callback<ArrayList<IndividBattle>>() {
            @Override
            public void onResponse(Call<ArrayList<IndividBattle>> call, Response<ArrayList<IndividBattle>> response) {
                swipeUpdateInfo.setRefreshing(false);
                if (response.isSuccessful()) {
                    lay.setVisibility(View.VISIBLE);
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(displayMetrics.widthPixels / 2, displayMetrics.heightPixels / 4);

                    String sloginFrom = "", sIdFrom = "", sloginTo = "", sIdTo = "", svideoFrom = "", svideoTo = "", swinner = "";
                    ArrayList<IndividBattle> individBattles = response.body();


                    for (int i = 0; i < individBattles.size(); i++) {
                        if (individBattles.get(i).getBattleId().equalsIgnoreCase(battle_id)) {
                            sloginFrom = individBattles.get(i).getFromUserLogin();
                            sIdFrom = individBattles.get(i).getFromUserId();
                            sloginTo = individBattles.get(i).getToUserLogin();
                            sIdTo = individBattles.get(i).getToUserId();
                            svideoFrom = individBattles.get(i).getVideoFrom();
                            svideoTo = individBattles.get(i).getVideoTo();
                            swinner = individBattles.get(i).getWinner();
                            break;
                        }
                    }
                    final String finalSIdFrom = sIdFrom;
                    final String finalSIdTo = sIdTo;
                    final String finalSloginFrom = sloginFrom;
                    final String finalSvideoFrom = svideoFrom;
                    final String finalSvideoTo = svideoTo;
                    final String finalSloginTo = sloginTo;
                    final String finalSwinner = swinner;

                    loginFrom.setText(sloginFrom);
                    if (!sIdFrom.equals(userDB.getServerId())) {
                        loginFrom.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                                intent.putExtra("user_id", finalSIdFrom);
                                startActivity(intent);
                            }
                        });
                    }
                    loginTo.setText(sloginTo);
                    if (!sIdTo.equals(userDB.getServerId())) {
                        loginTo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                                intent.putExtra("user_id", finalSIdTo);
                                startActivity(intent);
                            }
                        });
                    }

                    if (Integer.parseInt(sIdFrom) == (userDB.getServerId()) && svideoFrom.equalsIgnoreCase("N")) {
                        uploadFrom.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //закачка видоса
                                openGallery();
                                sendFrom.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        uploadVideo(battle_id, file, "video_from");
                                    }
                                });

                            }
                        });
                        cameraFrom.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //intent камеры
                                openCamera();
                                sendFrom.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        uploadVideo(battle_id, file, "video_from");
                                    }
                                });

                            }
                        });


                    }
                    if (Integer.parseInt(sIdFrom) == (userDB.getServerId()) && !svideoFrom.equalsIgnoreCase("N")) {
                        layFrom.removeAllViews();
                        TextView showVid = new TextView(getApplicationContext());
                        showVid.setTypeface(typeface);
                        showVid.setText(getResources().getString(R.string.open_your_video));
                        showVid.setTextColor(getResources().getColor(R.color.colorBlack));
                        showVid.setTextSize(15);

                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.play));
                        imageView.setLayoutParams(lp);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, displayMetrics.heightPixels / 4));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*Intent intent = new Intent(IndividualBattleActivity.this, VideoActivity.class);
                                intent.putExtra("login", finalSloginFrom);
                                intent.putExtra("url", finalSvideoFrom);
                                startActivity(intent);*/
                                JZVideoPlayerStandard.startFullscreen(IndividualBattleActivity.this, JZVideoPlayerStandard.class, finalSvideoFrom, finalSloginFrom);

                            }
                        });

                        layFrom.addView(showVid);
                        layFrom.addView(imageView);
                    }
                    if (Integer.parseInt(sIdTo) == (userDB.getServerId()) && svideoTo.equalsIgnoreCase("N")) {
                        uploadTo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //закачка видоса
                                openGallery();
                                sendTo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        uploadVideo(battle_id, file, "video_to");
                                    }
                                });
                            }
                        });
                        cameraTo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //intent камеры
                                openCamera();
                                sendTo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        uploadVideo(battle_id, file, "video_to");
                                    }
                                });
                            }
                        });
                    }
                    if (Integer.parseInt(sIdTo) == (userDB.getServerId()) && !svideoTo.equalsIgnoreCase("N")) {
                        layTo.removeAllViews();
                        TextView showVid = new TextView(getApplicationContext());
                        showVid.setTypeface(typeface);
                        showVid.setText(getResources().getString(R.string.open_your_video));
                        showVid.setTextColor(getResources().getColor(R.color.colorBlack));
                        showVid.setTextSize(15);

                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.play));
                        imageView.setLayoutParams(lp);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, displayMetrics.heightPixels / 4));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*Intent intent = new Intent(IndividualBattleActivity.this, VideoActivity.class);
                                intent.putExtra("login", finalSloginTo);
                                intent.putExtra("url", finalSvideoTo);
                                startActivity(intent);*/
                                JZVideoPlayerStandard.startFullscreen(IndividualBattleActivity.this, JZVideoPlayerStandard.class, finalSvideoTo, finalSloginTo);
                            }
                        });

                        layTo.addView(showVid);
                        layTo.addView(imageView);
                    }


                    if (Integer.parseInt(sIdTo) != (userDB.getServerId()) && svideoTo.equalsIgnoreCase("N")) {
                        layTo.removeAllViews();
                        TextView noVid = new TextView(getApplicationContext());
                        noVid.setTypeface(typeface);
                        noVid.setText(sloginTo + " " + getResources().getString(R.string.missing_video));
                        noVid.setTextColor(getResources().getColor(R.color.colorBlack));
                        layTo.addView(noVid);
                    }
                    if (Integer.parseInt(sIdTo) != (userDB.getServerId()) && !svideoTo.equalsIgnoreCase("N")) {
                        layTo.removeAllViews();
                        TextView showVid = new TextView(getApplicationContext());
                        showVid.setTypeface(typeface);
                        showVid.setText(getResources().getString(R.string.open_opponent_video));
                        showVid.setTextColor(getResources().getColor(R.color.colorBlack));
                        showVid.setTextSize(15);

                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.play));
                        imageView.setLayoutParams(lp);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, displayMetrics.heightPixels / 4));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*Intent intent = new Intent(IndividualBattleActivity.this, VideoActivity.class);
                                intent.putExtra("login", finalSloginTo);
                                intent.putExtra("url", finalSvideoTo);
                                startActivity(intent);*/
                                JZVideoPlayerStandard.startFullscreen(IndividualBattleActivity.this, JZVideoPlayerStandard.class, finalSvideoTo, finalSloginTo);
                            }
                        });


                        layTo.addView(showVid);
                        layTo.addView(imageView);
                    }
                    if (Integer.parseInt(sIdFrom) != (userDB.getServerId()) && svideoFrom.equalsIgnoreCase("N")) {
                        layFrom.removeAllViews();
                        TextView noVid = new TextView(getApplicationContext());
                        noVid.setTypeface(typeface);
                        noVid.setTextColor(getResources().getColor(R.color.colorBlack));
                        noVid.setText(sloginFrom + " " + getResources().getString(R.string.missing_video));
                        layFrom.addView(noVid);
                    }
                    if (Integer.parseInt(sIdFrom) != (userDB.getServerId()) && !svideoFrom.equalsIgnoreCase("N")) {
                        layFrom.removeAllViews();
                        TextView showVid = new TextView(getApplicationContext());
                        showVid.setTypeface(typeface);
                        showVid.setText(getResources().getString(R.string.open_opponent_video));
                        showVid.setTextColor(getResources().getColor(R.color.colorBlack));
                        showVid.setTextSize(15);

                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.play));
                        imageView.setLayoutParams(lp);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, displayMetrics.heightPixels / 4));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*Intent intent = new Intent(IndividualBattleActivity.this, VideoActivity.class);
                                intent.putExtra("login", finalSloginFrom);
                                intent.putExtra("url", finalSvideoFrom);
                                startActivity(intent);*/
                                JZVideoPlayerStandard.startFullscreen(IndividualBattleActivity.this, JZVideoPlayerStandard.class, finalSvideoFrom, finalSloginFrom);
                            }
                        });


                        layFrom.addView(showVid);
                        layFrom.addView(imageView);
                    }
                    if (swinner != null && !swinner.equals(userDB.getServerId())) {

                        tvWinner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(IndividualBattleActivity.this, UserInfoActivity.class);
                                intent.putExtra("user_id", finalSwinner);
                                startActivity(intent);
                            }
                        });
                    }
                    if (swinner != null && swinner.equals(sIdFrom)) {
                        tvWinner.setText(sloginFrom);
                    } else if (swinner != null && swinner.equals(sIdTo)) {
                        tvWinner.setText(sloginTo);
                    } else {
                        tvWinner.setText(getResources().getString(R.string.unknown_winner));
                    }


                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<IndividBattle>> call, Throwable t) {
                swipeUpdateInfo.setRefreshing(true);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }
        });

    }

    final int OPEN_FILE = 1;
    final int OPEN_CAMERA = 2;
    final int PERMISSIONS = 3;
    File file;

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("video/*");
        startActivityForResult(gallery, OPEN_FILE);
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (camera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camera, OPEN_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri chosseFileUri = data.getData();
            if (chosseFileUri != null) {
                String filePath = getRealPathFromURI(getApplicationContext(), chosseFileUri);
                file = new File(filePath);

                Shimmer sshimmer = new Shimmer()
                        .setDirection(Shimmer.ANIMATION_DIRECTION_LTR)
                        .setDuration(1000)
                        .setStartDelay(0);
                sshimmer.start(sendTo);
                sshimmer.start(sendFrom);
                sendFrom.setVisibility(View.VISIBLE);
                sendTo.setVisibility(View.VISIBLE);
                sendTo.setText(getResources().getString(R.string.upload) + "\n" + file.getName());
                sendFrom.setText(getResources().getString(R.string.upload) + "\n" + file.getName());

            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.file_error) + "\n", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideo(String battle_id, File videoFile, String who) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle(getResources().getString(R.string.uploading));
        pd.setMessage(getResources().getString(R.string.wait));
        pd.setCancelable(false);
        pd.show();
        RequestBody videoBody = RequestBody.create(MediaType.parse("multipart/form-data"), videoFile);
        MultipartBody.Part vFile = MultipartBody.Part.createFormData("userfile", videoFile.getName(), videoBody);

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 apiWT23 = retrofit.create(ApiWT23.class);
        Call<ResponseBody> call = apiWT23.uploadVideoIndBattle(battle_id, who, vFile);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.cancel();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
                loadPage();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.cancel();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                loadPage();
            }
        });

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
