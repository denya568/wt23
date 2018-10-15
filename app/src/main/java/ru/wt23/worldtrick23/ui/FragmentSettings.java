package ru.wt23.worldtrick23.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.db.DBHelper;
import ru.wt23.worldtrick23.ui.baseUI.BaseFragment;

public class FragmentSettings extends BaseFragment {
    Typeface typeface;
    Context context;

    //notices
    Switch switch_newsChanges, switch_indChanges, switch_groupReqs, switchShopNotices;
    //content
    Switch switch_shopSaveChanges, switch_showStream;

    String NOTICES = "notices";
    String CONTENT = "content";
    SharedPreferences spNotices;
    SharedPreferences spContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity().getApplicationContext();
        spNotices = context.getSharedPreferences(NOTICES, Context.MODE_PRIVATE);
        spContent = context.getSharedPreferences(CONTENT, Context.MODE_PRIVATE);

        typeface = getTypeface();

        //notices
        switch_newsChanges = (Switch) getActivity().findViewById(R.id.switch_newsChanges);
        switch_newsChanges.setTypeface(typeface);

        switch_indChanges = (Switch) getActivity().findViewById(R.id.switch_indChanges);
        switch_indChanges.setTypeface(typeface);

        switch_groupReqs = (Switch) getActivity().findViewById(R.id.switch_groupChanges);
        switch_groupReqs.setTypeface(typeface);

        switchShopNotices = (Switch) getActivity().findViewById(R.id.switch_shopNotices);
        switchShopNotices.setTypeface(typeface);


        //content
        switch_shopSaveChanges = (Switch) getActivity().findViewById(R.id.switch_shop_save_changes);
        switch_shopSaveChanges.setTypeface(typeface);

        switch_showStream = (Switch) getActivity().findViewById(R.id.switch_showStream);
        switch_showStream.setTypeface(typeface);


        setData();
        setActions();


    }

    private void setData() {
        //notices

        //content
        if (DBHelper.getShopSaveChanges(context)) {
            switch_shopSaveChanges.setChecked(true);
        } else switch_shopSaveChanges.setChecked(false);

        if (DBHelper.getNewsShowStreams(context)) {
            switch_showStream.setChecked(true);
        }

    }

    private void setActions() {
        //слушатель notices


        //слушатель content
        switch_shopSaveChanges.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DBHelper.setShopSaveChanges(context, isChecked);
            }
        });
        switch_showStream.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DBHelper.setNewsShowStreams(context, isChecked);
            }
        });
    }


}