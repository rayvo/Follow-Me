package com.cewit.fm1.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cewit.fm1.R;
import com.cewit.fm1.models.PlaceMenuItem;

import java.util.List;

/**
 * Created by Taeyu Im on 18. 9. 14.
 * qvo@cs.stonybrook.edu
 */

public class ListPopupMenuAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<PlaceMenuItem> itemList;
    Context context;

    public ListPopupMenuAdapter(Context mContext, List<PlaceMenuItem> mItemList) {
        layoutInflater = LayoutInflater.from(mContext);
        itemList = mItemList;
        context = mContext;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public PlaceMenuItem getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.place_menu_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(getItem(position).getTitle());
        holder.ivImage.setImageResource(R.drawable.jeju000);

        int imageId = R.mipmap.ic_launcher;
        if (context.getResources().getIdentifier(getItem(position).getId(), "drawable", context.getPackageName()) != 0) {
            imageId =context.getResources().getIdentifier(getItem(position).getId(), "drawable", context.getPackageName());
        }
        holder.ivImage.setImageResource(imageId);
        holder.tvAddress.setText(getItem(position).getAddress());
        holder.tvNumber.setText(getItem(position).getNumber());
        holder.tvType.setText(getItem(position).getType());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvAddress;
        TextView tvNumber;
        TextView tvType;
        ImageView ivImage;
        Button btnSkip;
        Button btnChange;
        Button btnExit;

        ViewHolder(View view) {
            tvTitle = view.findViewById(R.id.tvName);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvNumber = view.findViewById(R.id.tvNumber);
            tvType = view.findViewById(R.id.tvType);
            ivImage = view.findViewById(R.id.ivImage);
            btnSkip = view.findViewById(R.id.btnSkip);
            btnChange = view.findViewById(R.id.btnChange);
            btnExit = view.findViewById(R.id.btnExit);
        }
    }

}
