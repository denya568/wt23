package ru.wt23.worldtrick23.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.ApiWT23;
import ru.wt23.worldtrick23.io.Me;
import ru.wt23.worldtrick23.io.MyDuel;
import ru.wt23.worldtrick23.io.UnsafeOkHttpClient;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomSheetBehavior bottomSheetBehavior;
    Typeface typeface;
    Toolbar toolbar;

    RoundedImageView avatar;
    TextView myLogin, myName;
    LinearLayout headerAcc;

    TextView tvNavBattles, tvNavRang; //шторка
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawer;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        typeface = Typeface.createFromAsset(getAssets(), "fonts/planet_n2_cyr_lat.otf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/planet_n2_cyr_lat.otf");

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setHideable(true);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                updateMyInfo();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorBlack));
        drawerToggle.syncState();
        toolbar.setNavigationIcon(R.mipmap.icon);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                for (PermissionRequest permissionRequest : permissions) {
                    token.continuePermissionRequest();
                }
            }
        }).check();

        if (DBHelper.getUser(context) != null) {
            //openLastSesh();
        } else openAuth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        int id = item.getItemId();
        UserDB currentUser = DBHelper.getUser(this);

        if (id == R.id.nav_news) {
            FragmentNews fragmentNews = new FragmentNews();
            fragmentTransaction.replace(R.id.lay, fragmentNews, null);
            fragmentTransaction.commit();
            toolbar.setSubtitle(item.getTitle());

        } else if (id == R.id.nav_battles) {
            if (currentUser != null) {
                FragmentBattles fragmentBattles = new FragmentBattles();
                fragmentTransaction.replace(R.id.lay, fragmentBattles, null);
                fragmentTransaction.commit();
                toolbar.setSubtitle(item.getTitle());
            } else {
                openAuth();
            }

        } else if (id == R.id.nav_wallpapers) {
            if (currentUser != null) {
                FragmentOboi fragmentOboi = new FragmentOboi();
                fragmentTransaction.replace(R.id.lay, fragmentOboi, null);
                fragmentTransaction.commit();
                toolbar.setSubtitle(item.getTitle());
            } else {
                openAuth();
            }

        } else if (id == R.id.nav_rang) {
            if (currentUser != null) {
                FragmentRang fragmentRang = new FragmentRang();
                fragmentTransaction.replace(R.id.lay, fragmentRang, null);
                fragmentTransaction.commit();
                toolbar.setSubtitle(item.getTitle());
            } else {
                openAuth();
            }

        } else if (id == R.id.nav_poleznoe) {
            if (currentUser != null) {
                /*FragmentPoleznoe fragmentPoleznoe = new FragmentPoleznoe();
                fragmentTransaction.replace(R.id.lay, fragmentPoleznoe, null);
                fragmentTransaction.commit();*/
                toolbar.setSubtitle(item.getTitle());
            } else {
                openAuth();
            }

        } else if (id == R.id.nav_tutorials) {
            if (currentUser != null) {
                /*FragmentTutorials fragmentTutorials = new FragmentTutorials();
                fragmentTransaction.replace(R.id.lay, fragmentTutorials, null);
                fragmentTransaction.commit();*/
                toolbar.setSubtitle(item.getTitle());
            } else {
                openAuth();
            }

        } else if (id == R.id.nav_shop) {
            if (currentUser != null) {
                FragmentMagazine fragmentMagazine = new FragmentMagazine();
                fragmentTransaction.replace(R.id.lay, fragmentMagazine, null);
                fragmentTransaction.commit();
                toolbar.setSubtitle(item.getTitle() + " (" + currentUser.getRang() + " " + getResources().getString(R.string.rank_money) + ")");
            } else {
                openAuth();
            }

        } else if (id == R.id.nav_find) {
            if (currentUser != null) {
                FragmentSearch fragmentSearch = new FragmentSearch();
                fragmentTransaction.replace(R.id.lay, fragmentSearch, null);
                fragmentTransaction.commit();
                toolbar.setSubtitle(item.getTitle());
            } else {
                openAuth();
            }

        } else if (id == R.id.nav_settings) {
            if (currentUser != null) {
                FragmentSettings fragmentSettings = new FragmentSettings();
                fragmentTransaction.replace(R.id.lay, fragmentSettings, null);
                fragmentTransaction.commit();
                toolbar.setSubtitle(item.getTitle());
            } else {
                openAuth();
            }
        }

        item.setChecked(true);
        if (currentUser != null) {
            DBHelper.setLastSesh(this, id);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openAuth() {
        FragmentAutorization fragmentAutorization = new FragmentAutorization();
        fragmentTransaction.replace(R.id.lay, fragmentAutorization, null);
        fragmentTransaction.commit();
        toolbar.setSubtitle(getResources().getString(R.string.sign_in));
    }

    private void openLastSesh() {
        //TODO обновляем БД (getInfoAboutMe)
        Fragment fragment = null;
        int navItem = DBHelper.getLastSesh(context);
        if (navItem == R.id.nav_news) {
            fragment = new FragmentAutorization();
        } else if (navItem == R.id.nav_battles) {
            fragment = new FragmentBattles();
        } else if (navItem == R.id.nav_wallpapers) {
            fragment = new FragmentOboi();
        } //TODO доделать


        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.lay, fragment, null);
        fragmentTransaction.commit();
    }

    private void updateMyInfo() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        //<nav шторка
        tvNavBattles = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_battles));
        tvNavBattles.setGravity(Gravity.CENTER_VERTICAL);
        tvNavBattles.setTypeface(typeface);
        tvNavBattles.setTextColor(getResources().getColor(R.color.colorSiteRed));
        tvNavRang = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_rang));
        tvNavRang.setGravity(Gravity.CENTER_VERTICAL);
        tvNavRang.setTypeface(typeface);
        tvNavRang.setTextColor(getResources().getColor(R.color.colorBlack));
        //nav шторка>

        headerAcc = (LinearLayout) findViewById(R.id.headerAcc);
        avatar = (RoundedImageView) findViewById(R.id.avatarRound);
        myLogin = (TextView) findViewById(R.id.tvMyLogin);
        myLogin.setTypeface(typeface);
        myName = (TextView) findViewById(R.id.tvMyName);
        myName.setTypeface(typeface);

        if (DBHelper.getUser(context) != null) {
            final UserDB userDB = DBHelper.getUser(context);
            tvNavBattles.setText(DBHelper.getBattleRequestsCount(context) == 0 ? "" : "+" + DBHelper.getBattleRequestsCount(context));
            tvNavRang.setText(userDB.getRang() + "");
            myLogin.setText(userDB.getLogin());
            myName.setText(userDB.getName());

            headerAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    FragmentMyAccount fragmentMyAccount = new FragmentMyAccount();
                    fragmentTransaction.replace(R.id.lay, fragmentMyAccount, null);
                    fragmentTransaction.commit();
                    drawer.closeDrawer(GravityCompat.START);
                    toolbar.setSubtitle(myLogin.getText());
                }
            });

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
                        //TODO md5
                        me.setPassword(userDB.getPassword());
                        myLogin.setText(me.getLogin());
                        myName.setText(me.getName());
                        tvNavRang.setText(String.valueOf(me.getRang()));
                    }
                }

                @Override
                public void onFailure(Call<Me> call, Throwable t) {
                    myLogin.setText(userDB.getLogin());
                    myName.setText(userDB.getName());
                    tvNavRang.setText(String.valueOf(userDB.getRang()));
                }
            });

            /*okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
            client = new Retrofit.Builder()
                    .baseUrl(DBHelper.URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = client.create(ApiWT23.class);*/
            Call<ArrayList<MyDuel>> myDuelCall = service.getDuelsForMe(userDB.getServerId());
            myDuelCall.enqueue(new Callback<ArrayList<MyDuel>>() {
                @Override
                public void onResponse(Call<ArrayList<MyDuel>> call, Response<ArrayList<MyDuel>> response) {
                    if (response.isSuccessful()) {
                        ArrayList<MyDuel> myDuels = response.body();
                        int value = myDuels != null ? myDuels.size() : 0;
                        if (value != 0) {
                            tvNavBattles.setText("+" + value);
                            DBHelper.setBattleRequestsCount(context, value);
                        } else tvNavBattles.setText("");

                    }
                }

                @Override
                public void onFailure(Call<ArrayList<MyDuel>> call, Throwable t) {
                }
            });

        } else {

            avatar.setImageDrawable(getResources().getDrawable(R.mipmap.icon));
            myLogin.setText(getResources().getString(R.string.log_in));
            myName.setText("");

            headerAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    FragmentAutorization fragmentAutorization = new FragmentAutorization();
                    fragmentTransaction.replace(R.id.lay, fragmentAutorization, null);
                    fragmentTransaction.commit();
                    drawer.closeDrawer(GravityCompat.START);
                    toolbar.setSubtitle(getResources().getString(R.string.log_in));
                }
            });
        }
    }

    public static final class FontsOverride {
        static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
            final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
            replaceFont(staticTypefaceFieldName, regular);
        }

        static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
            try {
                final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
                staticField.setAccessible(true);
                staticField.set(null, newTypeface);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
