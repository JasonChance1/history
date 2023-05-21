package com.example.history.bean.adapter;

import android.content.ClipData;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.history.R;
import com.example.history.bean.model.Item;

import java.util.List;

import utils.ToastUtil;

public class BookDownloadAdapter extends RecyclerView.Adapter<BookDownloadAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;

    public BookDownloadAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_book_download, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.progressBar.setProgress(0);
        holder.progressBar.setVisibility(View.GONE);
        holder.downloadButton.setVisibility(View.VISIBLE);

        holder.downloadButton.setOnClickListener(v -> {
            downloadButtonClickListener.onDownloadButtonClick(position, new DownloadProgressListener() {
                @Override
                public void onProgressUpdated(int progress) {
                    if(progress>=100){
                        ToastUtil.showMsg(context,"下载完成");
                        holder.progressBar.setVisibility(View.GONE);
                        holder.downloadButton.setText("阅读");
                    }
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.progressBar.setProgress(progress);

                }
            });
        });
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
        });
    }
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    private DownloadButtonClickListener downloadButtonClickListener;

    public void setDownloadButtonClickListener(DownloadButtonClickListener listener) {
        this.downloadButtonClickListener = listener;
    }

    public interface DownloadButtonClickListener {
        void onDownloadButtonClick(int position, DownloadProgressListener progressListener);
    }

    public interface DownloadProgressListener {
        void onProgressUpdated(int progress);
    }
    @Override
    public int getItemCount() {
        Log.e("获取到的数量：",itemList.size()+"");
        return itemList.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ProgressBar progressBar;
        Button downloadButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.book_title);
            progressBar = itemView.findViewById(R.id.download_progres);
            downloadButton = itemView.findViewById(R.id.download_btn);
        }
    }
}
