package com.oenik.bir.skillgame;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends BaseAdapter {

    private List<Result> results = new ArrayList<Result>();

    private int gold = Color.parseColor("#ffd700");
    private int silver = Color.parseColor("#c0c0c0");
    private int bronze = Color.parseColor("#cd7f32");

    public ResultAdapter(List<Result> results) {
        this.results = results;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Result getItem(int i) {
        return results.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.result_layout, null);
        }

        Result n = getItem(i);

        TextView actualTextView = (TextView)view.findViewById(R.id.result);
        actualTextView.setText(n.getResultString());

        ImageView actualImageView = (ImageView)view.findViewById(R.id.medal);

        switch(i){
            case 0:actualImageView.setImageResource(R.drawable.gold);
                break;
            case 1:actualImageView.setImageResource(R.drawable.silver);
                break;
            case 2: actualImageView.setImageResource(R.drawable.bronze);
                break;
            default:actualImageView.setImageResource(0);
        }


        return view;
    }
}
