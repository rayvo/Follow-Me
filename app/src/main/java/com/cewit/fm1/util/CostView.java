package com.cewit.fm1.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cewit.fm1.R;
import com.cewit.fm1.models.Place;

/**
 * Created by Taeyu Im on 18. 7. 26.
 * qvo@cs.stonybrook.edu
 */

public class CostView extends LinearLayout {
    private EditText etCost;


    public CostView(Context context, String mCost) {
        super(context, null);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cost_view, this, true);

        etCost = (EditText) getChildAt(0).findViewById(R.id.etCost);
        etCost.setText(mCost);
    }

    public EditText getEtCost() {
        return etCost;
    }

    public void setEtCost(EditText etCost) {
        this.etCost = etCost;
    }
}
