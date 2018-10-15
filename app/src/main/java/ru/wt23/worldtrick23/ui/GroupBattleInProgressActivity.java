package ru.wt23.worldtrick23.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

import java.io.File;
import java.util.ArrayList;

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
import ru.wt23.worldtrick23.io.GenUsersGroupBattle;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;

public class GroupBattleInProgressActivity extends AppCompatActivity {
    DBHelper dbHelper;
    String battleId;
    LinearLayout lay;
    ScrollView mainScroll;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout tabGroup;

    Typeface typeface;
    SharedPreferences spRound;
    Context context;
    UserDB userDB;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_battle_inprogress);

        context = this;
        userDB = DBHelper.getUser(context);
        //pushOnline
        spRound = getSharedPreferences("round", MODE_PRIVATE);
        spRound.edit().putInt("number", 1).apply();

        battleId = getIntent().getStringExtra("battle_id");

        //typeface = Typeface.createFromAsset(getAssets(), "fonts/Planet_n2_cyr_lat.otf");
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Gotham_Pro.ttf");
        tabGroup = (LinearLayout) findViewById(R.id.tabGroup);


        mainScroll = (ScrollView) findViewById(R.id.gbinpScroll);
        lay = (LinearLayout) findViewById(R.id.group_lay);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeGroupInProgress);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSiteRed),
                getResources().getColor(R.color.colorSiteGreen),
                getResources().getColor(R.color.colorSiteWhiteBlue),
                getResources().getColor(R.color.colorWhite));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_background));


        loadPage();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void drawRounds(final ArrayList<GenUsersGroupBattle> genUsersArr) {
        tabGroup.removeAllViews();
        final int maxRounds = Integer.parseInt(genUsersArr.get(genUsersArr.size() - 1).getRaund());
        for (int i = 0; i < maxRounds; i++) {
            final Button bRound = new Button(getApplicationContext());
            bRound.setTypeface(typeface);
            bRound.setBackground(getResources().getDrawable(R.drawable.round_button_white));
            bRound.setText(getResources().getString(R.string.round) + " " + (i + 1));
            bRound.setTextColor(getResources().getColor(R.color.colorBlack));
            bRound.setId(i + 1);
            LinearLayout.LayoutParams lpRound = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpRound.setMargins(5, 5, 5, 0);
            bRound.setLayoutParams(lpRound);

            if (spRound.getInt("number", 1) == bRound.getId()) {
                bRound.setTextColor(getResources().getColor(R.color.colorWhite));
                bRound.setBackground(getResources().getDrawable(R.drawable.round_button));
            }
            bRound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRound(bRound.getId(), genUsersArr);
                    drawRounds(genUsersArr);
                }
            });
            tabGroup.addView(bRound);
        }

        mainScroll.performClick();
        mainScroll.setOnTouchListener(new View.OnTouchListener() {
            float xStart = 0;
            float xEnd = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xStart = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        xEnd = event.getX();
                        if (xStart > xEnd) {
                            //листаем вправо
                            if (spRound.getInt("number", 1) < maxRounds) {
                                showRound(spRound.getInt("number", 1) + 1, genUsersArr);
                                drawRounds(genUsersArr);
                            }
                        } else {
                            if (spRound.getInt("number", 1) > 1) {
                                showRound(spRound.getInt("number", 1) - 1, genUsersArr);
                                drawRounds(genUsersArr);
                            }
                        }
                        break;
                }
                return false;
            }
        });
        showRound(spRound.getInt("number", 1), genUsersArr);
    }

    private void loadPage() {
        swipeRefreshLayout.setRefreshing(true);
        //lay.removeAllViews();
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiWT23 apiWT23 = retrofit.create(ApiWT23.class);
        Call<ArrayList<GenUsersGroupBattle>> call = apiWT23.getGenUsersGroupBattle(battleId);
        call.enqueue(new Callback<ArrayList<GenUsersGroupBattle>>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onResponse(Call<ArrayList<GenUsersGroupBattle>> call, Response<ArrayList<GenUsersGroupBattle>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    final ArrayList<GenUsersGroupBattle> genUsersArr = response.body();
                    //рисуем tabItems
                    drawRounds(genUsersArr);
                } else {
                    makeText(getResources().getString(R.string.no_network));
                }

            }

            @Override
            public void onFailure(Call<ArrayList<GenUsersGroupBattle>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showRound(final int round, final ArrayList<GenUsersGroupBattle> genUsersArr) {
        spRound.edit().putInt("number", round).apply();
        lay.removeAllViews();
        GridLayout gridLayout = new GridLayout(getApplicationContext());
        gridLayout.setOrientation(GridLayout.VERTICAL);
        gridLayout.setColumnCount(2);

        int rows = 0;
        for (int i = 0; i < genUsersArr.size(); i++) {
            if (Integer.parseInt(genUsersArr.get(i).getRaund()) == round) {
                rows += 4;
            }
        }
        gridLayout.setRowCount(rows);
        for (int i = 0; i < genUsersArr.size(); i++) {
            if (round == Integer.parseInt(genUsersArr.get(i).getRaund())) {
                final int finalI = i;
                Button user1 = new Button(getApplicationContext());
                user1.setTypeface(typeface);
                user1.setText(genUsersArr.get(i).getUser1Login());
                user1.setWidth((int) (getResources().getDisplayMetrics().widthPixels / 2.3));
                user1.setHeight(100);

                TextView space = new TextView(getApplicationContext());
                space.setTypeface(typeface);
                space.setText(" ");
                space.setWidth((int) (getResources().getDisplayMetrics().widthPixels / 2.3));
                space.setHeight(100);

                Button user2 = new Button(getApplicationContext());
                user2.setTypeface(typeface);
                user2.setText(genUsersArr.get(i).getUser2Login());
                user2.setWidth((int) (getResources().getDisplayMetrics().widthPixels / 2.3));
                user2.setHeight(100);

                TextView space2 = new TextView(getApplicationContext());
                space2.setTypeface(typeface);
                space2.setText(" ");
                space2.setWidth((int) (getResources().getDisplayMetrics().widthPixels / 2.3));
                space2.setHeight(100);

                //условия
                if (Integer.parseInt(genUsersArr.get(i).getUser1Id()) == (userDB.getServerId())) {
                    user1.setBackground(getResources().getDrawable(R.drawable.round_button_yolo));
                    user1.setTextColor(getResources().getColor(R.color.blackw));
                    user1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //либо загрузить видео, либо открыть видео
                            if (genUsersArr.get(finalI).getVideo1() == null) {
                                //открыть диалог загрузки видео
                                openUploadDialog(String.valueOf(round), "user1", "video1");

                            } else {
                                JZVideoPlayerStandard.startFullscreen(GroupBattleInProgressActivity.this, JZVideoPlayerStandard.class, String.valueOf(genUsersArr.get(finalI).getVideo1()), genUsersArr.get(finalI).getUser1Login());
                            }
                        }
                    });
                } else {
                    user1.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                    user1.setTextColor(getResources().getColor(R.color.colorBlack));
                    user1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (genUsersArr.get(finalI).getVideo1() == null) {
                                Snackbar.make(lay, getResources().getString(R.string.no_video), Snackbar.LENGTH_SHORT).show();
                            } else {
                                JZVideoPlayerStandard.startFullscreen(GroupBattleInProgressActivity.this, JZVideoPlayerStandard.class, String.valueOf(genUsersArr.get(finalI).getVideo1()), genUsersArr.get(finalI).getUser1Login());
                            }
                        }
                    });
                }

                if (Integer.parseInt(genUsersArr.get(i).getUser2Id()) == (userDB.getServerId())) {
                    user2.setBackground(getResources().getDrawable(R.drawable.round_button_yolo));
                    user2.setTextColor(getResources().getColor(R.color.blackw));
                    user2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //либо загрузить видео, либо открыть видео
                            if (genUsersArr.get(finalI).getVideo2() == null) {
                                //открыть диалог загрузки видео
                                openUploadDialog(String.valueOf(round), "user2", "video2");

                            } else {
                                JZVideoPlayerStandard.startFullscreen(GroupBattleInProgressActivity.this, JZVideoPlayerStandard.class, String.valueOf(genUsersArr.get(finalI).getVideo2()), genUsersArr.get(finalI).getUser2Login());
                            }
                        }
                    });
                } else {
                    user2.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                    user2.setTextColor(getResources().getColor(R.color.colorBlack));
                    user2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (genUsersArr.get(finalI).getVideo2() == null) {
                                Snackbar.make(lay, getResources().getString(R.string.no_video), Snackbar.LENGTH_SHORT).show();
                            } else {
                                JZVideoPlayerStandard.startFullscreen(GroupBattleInProgressActivity.this, JZVideoPlayerStandard.class, String.valueOf(genUsersArr.get(finalI).getVideo2()), genUsersArr.get(finalI).getUser2Login());
                            }
                        }
                    });
                }

                gridLayout.addView(user1);
                gridLayout.addView(space);
                gridLayout.addView(user2);
                gridLayout.addView(space2);

            }

        }

        for (int i = 0; i < genUsersArr.size(); i++) {

            if (round == Integer.parseInt(genUsersArr.get(i).getRaund())) {
                final int finalI = i;
                Button rd = new Button(getApplicationContext());
                rd.setBackground(getResources().getDrawable(R.drawable.rd));
                rd.setWidth((int) (getResources().getDisplayMetrics().widthPixels / 2.3));
                rd.setHeight(100);
                rd.setEnabled(false);

                Button winner = new Button(getApplicationContext());
                winner.setTypeface(typeface);
                winner.setTextColor(getResources().getColor(R.color.colorBlack));
                winner.setWidth((int) (getResources().getDisplayMetrics().widthPixels / 2.3));
                winner.setHeight(100);
                if (genUsersArr.get(i).getWinner() == null) {
                    winner.setText(getResources().getString(R.string.unknown_winner));
                    winner.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                } else {
                    if (genUsersArr.get(i).getUser1Id().equalsIgnoreCase((String) genUsersArr.get(i).getWinner())) {
                        winner.setText(String.valueOf(genUsersArr.get(i).getUser1Login()));
                        winner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (genUsersArr.get(finalI).getVideo1() != null)
                                    JZVideoPlayerStandard.startFullscreen(GroupBattleInProgressActivity.this, JZVideoPlayerStandard.class, String.valueOf(genUsersArr.get(finalI).getVideo1()), genUsersArr.get(finalI).getUser1Login());
                                else makeText(getResources().getString(R.string.no_video));
                            }
                        });
                    } else {
                        winner.setText(String.valueOf(genUsersArr.get(i).getUser2Login()));
                        winner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (genUsersArr.get(finalI).getVideo1() != null)
                                    JZVideoPlayerStandard.startFullscreen(GroupBattleInProgressActivity.this, JZVideoPlayerStandard.class, String.valueOf(genUsersArr.get(finalI).getVideo2()), genUsersArr.get(finalI).getUser2Login());
                                else makeText(getResources().getString(R.string.no_video));
                            }
                        });
                    }
                    winner.setBackground(getResources().getDrawable(R.drawable.round_button_green));
                }


                Button ru = new Button(getApplicationContext());
                ru.setBackground(getResources().getDrawable(R.drawable.ru));
                ru.setWidth((int) (getResources().getDisplayMetrics().widthPixels / 2.3));
                ru.setHeight(100);
                ru.setEnabled(false);

                TextView space2 = new TextView(getApplicationContext());
                space2.setTypeface(typeface);
                space2.setWidth((int) (getResources().getDisplayMetrics().widthPixels / 2.3));
                space2.setHeight(100);

                gridLayout.addView(rd);
                gridLayout.addView(winner);
                gridLayout.addView(ru);
                gridLayout.addView(space2);
            }

        }
        lay.addView(gridLayout);
    }


    ShimmerButton send;

    private void openUploadDialog(final String round, final String whoUser, final String whoVideo) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        }

        LayoutInflater li = LayoutInflater.from(this);
        View orderView = li.inflate(R.layout.dialog_upload_group_video, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(orderView);
        builder.setCancelable(true);

        final AlertDialog alertDialog = builder.create();

        ImageButton ibGalery = (ImageButton) orderView.findViewById(R.id.ibUploadGroup);
        ImageButton ibCamera = (ImageButton) orderView.findViewById(R.id.ibCameraGroup);
        send = (ShimmerButton) orderView.findViewById(R.id.shsendGroupVideo);
        send.setTypeface(typeface);
        send.setVisibility(View.GONE);

        ibGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("video/*");
                startActivityForResult(gallery, OPEN_FILE);
            }
        });

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (camera.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(camera, OPEN_CAMERA);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                uploadVideo(battleId, userDB.getServerId(), file, whoUser, whoVideo, round);
            }
        });


        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    File file;

    private final int OPEN_FILE = 1;
    private final int OPEN_CAMERA = 2;
    private final int PERMISSIONS_REQUEST_CODE = 3;


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri chosseFileUri = data.getData();
            if (chosseFileUri != null) {
                Uri fileUri;
                fileUri = chosseFileUri;
                String filePath = getRealPathFromURI(getApplicationContext(), fileUri);
                file = new File(filePath);

                send.setVisibility(View.VISIBLE);
                Shimmer sshimmer = new Shimmer()
                        .setDirection(Shimmer.ANIMATION_DIRECTION_LTR)
                        .setDuration(1000)
                        .setStartDelay(0);
                sshimmer.start(send);
                send.setText(getResources().getString(R.string.upload) + "\n" + file.getName());

            }
        } else {
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.file_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideo(final String battleId, int myId, File videoFile, String user1user2, String video1video2, String raund) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(1);
        progressDialog.setTitle(getResources().getString(R.string.uploading));
        progressDialog.setMessage(getResources().getString(R.string.wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DBHelper.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiWT23 apiWT23 = retrofit.create(ApiWT23.class);

        RequestBody rbBattleId = RequestBody.create(MediaType.parse("multipart/form-data"), battleId);
        RequestBody rbMyId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(myId));
        RequestBody rbNoText = RequestBody.create(MediaType.parse("multipart/form-data"), "userfile");
        RequestBody rbUser1user2 = RequestBody.create(MediaType.parse("multipart/form-data"), user1user2);
        RequestBody rbvideo1video2 = RequestBody.create(MediaType.parse("multipart/form-data"), video1video2);
        RequestBody rbRaund = RequestBody.create(MediaType.parse("multipart/form-data"), raund);

        RequestBody videoBody = RequestBody.create(MediaType.parse("multipart/form-data"), videoFile);
        MultipartBody.Part vFile = MultipartBody.Part.createFormData("userfile", videoFile.getName(), videoBody);

        Call<ResponseBody> call = apiWT23.uploadVideoGroupBattle(rbBattleId, rbMyId, rbNoText, vFile, rbUser1user2, rbvideo1video2, rbRaund);

        //Call<ResponseBody> call = apiWT23.uploadVideoGroupBattle(battleId, myId, "", vFile, user1user2, video1video2, raund);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.cancel();
                makeText(response.toString() + "\n" + response.code() + "\n" + response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.cancel();
                makeText(t.getMessage());
            }
        });

    }

    public void makeText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
