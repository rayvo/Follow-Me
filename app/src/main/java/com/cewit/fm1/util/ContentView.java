package com.cewit.fm1.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cewit.fm1.R;
import com.cewit.fm1.models.Place;

/**
 * Created by Taeyu Im on 18. 7. 26.
 * qvo@cs.stonybrook.edu
 */

public class ContentView extends LinearLayout {
    private TextView tvContent;


    public ContentView(Context context, String mContent) {
        super(context, null);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.content_view, this, true);
        tvContent = (TextView) getChildAt(0).findViewById(R.id.tvContent);
        tvContent.setText(mContent);
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public void setTvContent(TextView tvContent) {
        this.tvContent = tvContent;
    }
}
