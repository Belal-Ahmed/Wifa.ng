package com.storerepublic.wifaapp.Blog.adapter;

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

import java.util.List;

import com.storerepublic.wifaapp.R;
import com.storerepublic.wifaapp.helper.BlogItemOnclicklinstener;
import com.storerepublic.wifaapp.modelsList.blogModel;
import com.storerepublic.wifaapp.utills.SettingsMain;

public class ItemBlogAdapter extends RecyclerView.Adapter<ItemBlogAdapter.CustomViewHolder> {

    SettingsMain settingsMain;
    private List<blogModel> feedItemList;
    private Context mContext;
    private BlogItemOnclicklinstener oNItemClickListener;

    public ItemBlogAdapter(Context context, List<blogModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        settingsMain = new SettingsMain(context);

    }

    @Override
    public ItemBlogAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blog, null);
        return new ItemBlogAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemBlogAdapter.CustomViewHolder customViewHolder, int i) {
        final blogModel feedItem = feedItemList.get(i);

        customViewHolder.name.setText(feedItemList.get(i).getName());
        customViewHolder.comment.setText(feedItemList.get(i).getComments());
        customViewHolder.date.setText(feedItemList.get(i).getDate());
        customViewHolder.readmore.setText(feedItemList.get(i).getReadMore());
        customViewHolder.readmore.setTextColor(Color.parseColor(settingsMain.getMainColor()));


        if (feedItem.isHasImage()) {
            if (!TextUtils.isEmpty(feedItem.getImage())) {
                Picasso.with(mContext).load(feedItem.getImage())
                        .resize(270, 270)
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(customViewHolder.imageView);
            }
        } else {
            customViewHolder.imageView.setVisibility(View.GONE);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oNItemClickListener.onItemClick(feedItem);
            }
        };

        customViewHolder.view.setOnClickListener(listener);

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public BlogItemOnclicklinstener getOnItemClickListener() {
        return oNItemClickListener;
    }

    public void setOnItemClickListener(BlogItemOnclicklinstener onItemClickListener) {
        this.oNItemClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, comment, date, readmore;
        RelativeLayout view;

        CustomViewHolder(View view) {
            super(view);

            this.view = view.findViewById(R.id.linear_layout_card_view);

            this.imageView = view.findViewById(R.id.image_view);

            this.name = view.findViewById(R.id.text_view_name);
            this.comment = view.findViewById(R.id.comments);
            this.date = view.findViewById(R.id.date);
            this.readmore = view.findViewById(R.id.read_more);
        }
    }
}
