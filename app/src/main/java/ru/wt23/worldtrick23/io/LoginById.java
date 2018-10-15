package ru.wt23.worldtrick23.io;


import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wt23.worldtrick23.db.DBHelper;

public class LoginById {
    public LoginById() {

    }

    public static String getLoginById(final int myID, final String userID) {
        final String[] login = {""};

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
                Retrofit client = new Retrofit.Builder()
                        .baseUrl(DBHelper.URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiWT23 service = client.create(ApiWT23.class);
                Call<ArrayList<WT23User>> usersCall = service.getWtUsers(myID);
                try {
                    if (usersCall.execute().isSuccessful()) {
                        ArrayList<WT23User> wt23Users = usersCall.clone().execute().body();
                        for (int i = 0; i < wt23Users.size(); i++) {
                            String id = wt23Users.get(i).getId();
                            if (id.equalsIgnoreCase(userID)) {
                                login[0] =  wt23Users.get(i).getLogin();
                                break;
                            }
                        }
                    } else {
                        login[0] = "404";
                    }
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

        return login[0];
    }
}
