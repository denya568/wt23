package ru.wt23.worldtrick23.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.InputStream;
import java.net.URL;

import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.PushOnline;

public class PhotoActivity extends AppCompatActivity {
    String name, link;
    LinearLayout lay;
    SubsamplingScaleImageView ivPhoto;
    Context context;
    UserDB userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        context = this;
        userDB = DBHelper.getUser(context);
        //PushOnline.push(myDB.getId());

        name = getIntent().getStringExtra("name");
        link = getIntent().getStringExtra("link");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_background));
        actionBar.setSubtitle(name);

        lay = (LinearLayout) findViewById(R.id.photoLay);
        ivPhoto = (SubsamplingScaleImageView) findViewById(R.id.ivPhoto);
        /*Picasso picasso = new Picasso.Builder(PhotoActivity.this)
                .downloader(new OkHttp3Downloader(UnsafeOkHttpClient.getUnsafeOkHttpClient()))
                .build();
        picasso.load(link)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(ivPhoto);
        */
        LoadPhoto loadPhoto = new LoadPhoto();
        loadPhoto.execute();


    }

    private class LoadPhoto extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap;
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                bitmap = BitmapFactory.decodeStream(is);

            } catch (Exception e) {
                e.printStackTrace();
                bitmap = null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ivPhoto.setImage(ImageSource.bitmap(bitmap));
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
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

}
