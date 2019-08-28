package com.storerepublic.wifaapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.storerepublic.wifaapp.Blog.adapter.ItemBlogCommentReplyAdapter;
import com.storerepublic.wifaapp.R;
import com.storerepublic.wifaapp.helper.BlogCommentOnclicklinstener;
import com.storerepublic.wifaapp.modelsList.blogCommentsModel;

public class ItemRatingListAdapter extends RecyclerView.Adapter<ItemRatingListAdapter.CustomViewHolder> {

    private List<blogCommentsModel> feedItemList;
    private Context mContext;
    private BlogCommentOnclicklinstener oNItemClickListener;

    public ItemRatingListAdapter(Context context, List<blogCommentsModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ItemRatingListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rating, null);
        return new ItemRatingListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemRatingListAdapter.CustomViewHolder customViewHolder, int i) {
        final blogCommentsModel feedItem = feedItemList.get(i);


        customViewHolder.name.setText(feedItemList.get(i).getName());
        customViewHolder.message.setText(feedItemList.get(i).getMessage());
        customViewHolder.date.setText(feedItemList.get(i).getDate());
        customViewHolder.reply.setText(feedItemList.get(i).getReply());

        customViewHolder.ratingBar.setRating(Float.parseFloat(feedItem.getRating()));

        customViewHolder.reply.setVisibility(feedItem.isCanReply() ? View.VISIBLE : View.GONE);

        if (!TextUtils.isEmpty(feedItem.getImage())) {
            Picasso.with(mContext).load(feedItem.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView);
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oNItemClickListener.onItemClick(feedItem);
            }
        };

        customViewHolder.reply.setOnClickListener(listener);

        if (feedItemList.get(i).getHasReplyList()) {
            ItemBlogCommentReplyAdapter itemSendRecMesageAdapter = new ItemBlogCommentReplyAdapter(mContext, feedItemList.get(i).getListitemsiner());
            if (feedItemList.get(i).getListitemsiner().size() > 0)
                customViewHolder.recyclerView.setAdapter(itemSendRecMesageAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public BlogCommentOnclicklinstener getOnItemClickListener() {
        return oNItemClickListener;
    }

    public void setOnItemClickListener(BlogCommentOnclicklinstener onItemClickListener) {
        this.oNItemClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, message, date, reply;
        RatingBar ratingBar;
        RecyclerView recyclerView;

        CustomViewHolder(View view) {
            super(view);

            this.imageView = view.findViewById(R.id.image_view);

            this.name = view.findViewById(R.id.text_viewName);
            this.ratingBar = view.findViewById(R.id.ratingBar);
            this.message = view.findViewById(R.id.prices);
            this.date = view.findViewById(R.id.loginTime);
            this.reply = view.findViewById(R.id.verified);

            LayerDrawable stars = (LayerDrawable) this.ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);

            this.recyclerView = view.findViewById(R.id.sublist);

            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(mContext);
            MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(MyLayoutManager);
            recyclerView.setNestedScrollingEnabled(false);

        }
    }
}
