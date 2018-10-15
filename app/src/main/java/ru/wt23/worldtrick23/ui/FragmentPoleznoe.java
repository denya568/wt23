package ru.wt23.worldtrick23.ui;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.io.PushOnline;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;

public class FragmentPoleznoe extends BaseFragment {
    TextView poleznoe;
    LinearLayout lay;
    LinearLayout l;

    Typeface typeface;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poleznoe, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pushOnline();

        //typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Planet_n2_cyr_lat.otf");
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Gotham_Pro.ttf");

        poleznoe = (TextView) getActivity().findViewById(R.id.poleznoe);
        poleznoe.setTypeface(typeface);



    }
}