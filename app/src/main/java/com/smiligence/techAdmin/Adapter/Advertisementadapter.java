package com.smiligence.techAdmin.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.techAdmin.R;
import com.smiligence.techAdmin.bean.AdvertisementDetails;

import java.util.List;

import static com.smiligence.techAdmin.common.Constant.INACTIVE_STATUS;


public class Advertisementadapter extends RecyclerView.Adapter<Advertisementadapter.ImageViewHolder> {
    private Context mcontext;
    private List<AdvertisementDetails> categoryDetailsList;
    private OnItemClicklistener mlistener;
    ImageViewHolder imageViewHolder;
    private int indicator;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public Advertisementadapter(Context context, List<AdvertisementDetails> catagories) {
        mcontext = context;
        categoryDetailsList = catagories;
        this.indicator = indicator;
    }

    @NonNull

    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.view_advertisements, parent, false);
        imageViewHolder = new ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }


    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        AdvertisementDetails categoryDetails = categoryDetailsList.get(position);

        if (INACTIVE_STATUS.equalsIgnoreCase(categoryDetails.getAdvertisementstatus())) {
            holder.linearLayout.setBackgroundColor(Color.rgb(123, 123, 123));
            holder.itemImages.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);

        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);
        Glide.with(mcontext)
                .setDefaultRequestOptions(requestOptions)
                .load(categoryDetails.getImage()).fitCenter().into(holder.itemImages);


    }


    public int getItemCount() {
        return categoryDetailsList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView catagoryName;
        ImageView itemImages;
        LinearLayout linearLayout;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);

            itemImages = itemView.findViewById(R.id.adv_image);

linearLayout=itemView.findViewById(R.id.linearlayout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClicklistener != null) {
                        int Position = getAdapterPosition();
                        if (Position != RecyclerView.NO_POSITION) {
                            itemClicklistener.Onitemclick(Position);
                        }
                    }
                }
            });


        }
    }
}