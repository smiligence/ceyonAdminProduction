package com.smiligence.techAdmin.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.techAdmin.R;

import java.util.List;

public class ImageSelectedAdapter extends RecyclerView.Adapter<ImageSelectedAdapter.ImageViewHolder> {
    private Context mcontext;
    private List<Bitmap> categoryDetailsList;
    private OnItemClicklistener mlistener;
    ImageViewHolder imageViewHolder;
    private int indicator;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public ImageSelectedAdapter(Context context, List<Bitmap> catagories) {
        mcontext = context;
        categoryDetailsList = catagories;
        this.indicator = indicator;
    }

    @NonNull

    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.catagory_grid, parent, false);
        imageViewHolder = new ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }


    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
      //  CategoryDetails categoryDetails = categoryDetailsList.get(position);

        if(categoryDetailsList==null||categoryDetailsList.size()==0){
            holder.catagoryName.setVisibility(View.VISIBLE);
            holder.catagoryName.setText("No images Selected");
        }else {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher);
            requestOptions.error(R.mipmap.ic_launcher);
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(categoryDetailsList.get(position)).fitCenter().into(holder.itemImages);
            holder.catagoryName.setVisibility(View.INVISIBLE);
        }

    }


    public int getItemCount() {
        return categoryDetailsList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView catagoryName;
        ImageView itemImages;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);
            catagoryName = itemView.findViewById(R.id.catagoryName_adapter);
            itemImages = itemView.findViewById(R.id.catagory_imageAdapter);
            catagoryName.setSelected(true);

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