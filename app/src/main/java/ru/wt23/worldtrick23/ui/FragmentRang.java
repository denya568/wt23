package ru.wt23.worldtrick23.ui;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.db.UserDB;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;

public class FragmentRang extends BaseFragment {

    Button googleADSBanner, googleADSFrame, hz1, hz2;

    Typeface typeface;
    Context context;
    UserDB userDB;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rang, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity().getApplicationContext();
        userDB = DBHelper.getUser(context);
        pushOnline();

        typeface = getTypeface();

        googleADSBanner = (Button) getActivity().findViewById(R.id.googleADSBanner);
        googleADSFrame = (Button) getActivity().findViewById(R.id.googleADSFrame);
        hz1 = (Button) getActivity().findViewById(R.id.hz1);
        hz2 = (Button) getActivity().findViewById(R.id.hz2);




    }
}
