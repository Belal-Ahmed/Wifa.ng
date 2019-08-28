package com.storerepublic.wifaapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;
import com.storerepublic.wifaapp.R;
import com.storerepublic.wifaapp.helper.CatSubCatOnclicklinstener;
import com.storerepublic.wifaapp.modelsList.catSubCatlistModel;
import com.storerepublic.wifaapp.utills.AdsTimerConvert;
import com.storerepublic.wifaapp.utills.SettingsMain;

public class ItemSearchFeatureAdsAdapter extends RecyclerView.Adapter<ItemSearchFeatureAdsAdapter.CustomViewHolder> {

    private SettingsMain settingsMain;
    private ArrayList<catSubCatlistModel> list;
    private CatSubCatOnclicklinstener oNItemClickListener;
    private Context mContext;

    public ItemSearchFeatureAdsAdapter(Context context, ArrayList<catSubCatlistModel> feedItemList) {
        this.list = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemof_feature_search, null);
        settingsMain = new SettingsMain(mContext);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {

        final catSubCatlistModel feedItem = list.get(position);

        holder.titleTextView.setText(list.get(position).getCardName());
        holder.priceTV.setText(list.get(position).getPrice());
        holder.locationTV.setText(list.get(position).getLocation());
        holder.featureText.setText(list.get(position).getAddTypeFeature());
        holder.featureText.setBackgroundColor(Color.parseColor("#E52D27"));

        if (feedItem.isIs_show_countDown()) {
            holder.cv_countdownView.setVisibility(View.VISIBLE);
            holder.cv_countdownView.start(AdsTimerConvert.adforest_bidTimer(feedItem.getTimer_array()));
        }

        if (!TextUtils.isEmpty(feedItem.getImageResourceId())) {
            Picasso.with(mContext).load(feedItem.getImageResourceId())
                    .resize(250, 250).centerCrop()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.mainImage);
        }
//        holder.saveTV.setText(list.get(position).getFavBtnText());
//        if (list.get(position).getIsfav() == 1) {
//            holder.saveTV.setTextColor(Color.parseColor( settingsMain.getMainColor()));
//
//            Drawable drawableCardViewMessage = DrawableColorChanger.changeDrawableColor(mContext,
//                    R.drawable.ic_favorite_border, Color.parseColor(settingsMain.getMainColor()));
//            holder.saveTV.setCompoundDrawablesWithIntrinsicBounds(drawableCardViewMessage, null, null, null);
//        } else {
//            holder.saveTV.setTextColor(mContext.getResources().getColor(R.color.white_greyish));
//        }

//        View.OnClickListener listener1 = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                oNItemClickListener.addToFavClick(v, list.get(position).getId());
//            }
//        };
//        holder.saveTV.setOnClickListener(listener1);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oNItemClickListener.onItemClick(feedItem);
            }
        };
//        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    oNItemClickListener.onItemTouch(feedItem);
//                }
//                return true;
//            }
//        };
        holder.linearLayoutMain.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    public CatSubCatOnclicklinstener getOnItemClickListener() {
        return oNItemClickListener;
    }

    public void setOnItemClickListener(CatSubCatOnclicklinstener onItemClickListener) {
        this.oNItemClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        CountdownView cv_countdownView;
        private TextView titleTextView, priceTV, locationTV, featureText;
        private ImageView mainImage;
        private RelativeLayout linearLayoutMain;

        CustomViewHolder(View v) {
            super(v);

            titleTextView = v.findViewById(R.id.text_view_name);
            featureText = v.findViewById(R.id.textView4);
            priceTV = v.findViewById(R.id.prices);
            locationTV = v.findViewById(R.id.location);
            mainImage = v.findViewById(R.id.image_view);
            cv_countdownView = v.findViewById(R.id.cv_countdownView);

            priceTV.setTextColor(Color.parseColor(settingsMain.getMainColor()));

            linearLayoutMain = v.findViewById(R.id.linear_layout_card_view);
        }
    }
}