package com.example.history.bean.adapter;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.history.R;
import com.example.history.bean.DynastyContent;

import java.util.List;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<DynastyContent> dynastyContentList;
    private OnItemClickListener onItemClickListener;

    private Context context;

    public RecyclerViewAdapter(List<DynastyContent> dynastyContentList,Context context) {
        this.dynastyContentList = dynastyContentList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setData(List<DynastyContent> dataList) {
        dynastyContentList.clear(); // Clear the existing data
        dynastyContentList.addAll(dataList); // Add the new data
        notifyDataSetChanged(); // Notify the adapter of the data change
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_listview_dynasty, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(itemView);

        // 设置 item 的点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DynastyContent dc = dynastyContentList.get(position);
//        holder.imageView.setImageResource(dc.getImg());
        Glide.with(context).load("http://139.155.248.158:18080/history"+dc.getImg().substring(1)).into(holder.imageView);
        Log.e("RecyclerView","http://139.155.248.158:18080/history"+dc.getImg().substring(1));
        holder.titleTextView.setText(dc.getTitle());
        holder.briefTextView.setText(dc.getContent());
    }

    @Override
    public int getItemCount() {
        return dynastyContentList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleTextView;
        public TextView briefTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_dynasty);
            titleTextView = itemView.findViewById(R.id.title_dynasty);
            briefTextView = itemView.findViewById(R.id.brief_dynasty);
        }
    }
}

