package ru.wt23.worldtrick23.ui.baseUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;
import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.evbus.Stuff;
import ru.wt23.worldtrick23.io.Magazine;
import ru.wt23.worldtrick23.ui.PhotoActivity;

public class MagazineAdapter extends RecyclerView.Adapter<MagazineAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<Magazine> magazineList;
    Context context;

    public MagazineAdapter(Context context, List<Magazine> magazineList) {
        this.context = context;
        this.magazineList = magazineList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.magazine_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Magazine magazine = magazineList.get(i);
        viewHolder.tvStuffName.setText(magazine.getName());
        Picasso.get().load(magazine.getFoto())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(viewHolder.ivStuffSrc);
        viewHolder.ivStuffSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PhotoActivity.class);
                intent.putExtra("name", magazine.getName());
                intent.putExtra("link", magazine.getFullFoto());
                context.startActivity(intent);
            }
        });
        viewHolder.tvStuffText.setText(magazine.getText());
        viewHolder.tvStuffPrice.setText(magazine.getPrice() + " " + context.getResources().getString(R.string.rank_money));
        viewHolder.tvStuffCount.setText(context.getResources().getString(R.string.in_stock) + magazine.getCount());
        final SharedPreferences sp = context.getSharedPreferences("stuff", Context.MODE_PRIVATE);
        if (sp.contains(magazine.getId())) {
            viewHolder.tvStuffTotalCount.setText("" + sp.getInt(magazine.getId(), 0));
        } else {
            viewHolder.tvStuffTotalCount.setText("0");
        }
        viewHolder.bMinus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = (Integer.parseInt(viewHolder.tvStuffTotalCount.getText().toString()) - 1);
                if (result > 0) {
                    viewHolder.tvStuffTotalCount.setText(String.valueOf(result));
                } else {
                    viewHolder.tvStuffTotalCount.setText(String.valueOf(0));
                }
            }
        });
        viewHolder.bPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.tvStuffTotalCount.setText(String.valueOf(Integer.parseInt(viewHolder.tvStuffTotalCount.getText().toString()) + 1));
            }
        });
        viewHolder.bMinus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = (Integer.parseInt(viewHolder.tvStuffTotalCount.getText().toString()) - 10);
                if (result > 0) {
                    viewHolder.tvStuffTotalCount.setText(String.valueOf(result));
                } else {
                    viewHolder.tvStuffTotalCount.setText(String.valueOf(0));
                }
            }
        });
        viewHolder.bPlus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.tvStuffTotalCount.setText(String.valueOf(Integer.parseInt(viewHolder.tvStuffTotalCount.getText().toString()) + 10));
            }
        });

        viewHolder.tvStuffTotalCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = Integer.parseInt(s.toString());
                if (count > 0) {
                    //сохраняем в sp
                    sp.edit().putInt(magazine.getId(), count).apply();
                } else {
                    sp.edit().remove(magazine.getId()).apply();
                }
                Stuff stuff = new Stuff(magazine.getId(), magazine.getName(), count, Integer.parseInt(magazine.getPrice()));
                EventBus.getDefault().post(stuff);
            }
        });
        Stuff stuff = new Stuff(magazine.getId(), magazine.getName(), sp.getInt(magazine.getId(), 0), Integer.parseInt(magazine.getPrice()));
        EventBus.getDefault().post(stuff);
    }

    @Override
    public int getItemCount() {
        return magazineList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvStuffName, tvStuffText, tvStuffPrice, tvStuffCount, tvStuffTotalCount, tvFab;
        final ImageView ivStuffSrc;
        FButton bMinus1, bMinus10, bPlus1, bPlus10;

        ViewHolder(View view) {
            super(view);
            tvFab = (TextView) view.findViewById(R.id.tvFab);
            tvStuffName = (TextView) view.findViewById(R.id.tv_stuffName);
            ivStuffSrc = (ImageView) view.findViewById(R.id.iv_stuffSrc);
            tvStuffText = (TextView) view.findViewById(R.id.tv_stuffText);
            tvStuffPrice = (TextView) view.findViewById(R.id.tv_stuffPrice);
            tvStuffCount = (TextView) view.findViewById(R.id.tv_stuffCount);
            bMinus1 = (FButton) view.findViewById(R.id.b_minus1);
            bMinus10 = (FButton) view.findViewById(R.id.b_minus10);
            tvStuffTotalCount = (TextView) view.findViewById(R.id.tv_stuffTotalCount);
            bPlus1 = (FButton) view.findViewById(R.id.b_plus1);
            bPlus10 = (FButton) view.findViewById(R.id.b_plus10);
        }
    }
}
