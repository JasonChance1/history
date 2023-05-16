package listview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.history.R;
import com.example.history.bean.DynastyContent;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DynastyListViewAdapter extends BaseAdapter {
    public Context mcontext;
    private List<DynastyContent> mDynastyList;
    public LayoutInflater mLayoutInflater;

    public DynastyListViewAdapter(Context context,List<DynastyContent> dynastyContents){
        mcontext=context;
        mDynastyList = dynastyContents;
        mLayoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mDynastyList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDynastyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null){
            view=mLayoutInflater.inflate(R.layout.layout_listview_dynasty,null);
            holder=new ViewHolder();
            holder.imageView=view.findViewById(R.id.img_dynasty);
            holder.title=view.findViewById(R.id.title_dynasty);
            holder.brief=view.findViewById(R.id.brief_dynasty);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }
        DynastyContent dc=mDynastyList.get(i);
        holder.brief.setText(dc.getBrief());

        Glide.with(mcontext).load("139.155.248.158:18080/history"+dc.getImg().substring(1)).into(holder.imageView);
        Log.e("TAG","----"+dc.getImg().substring(1));
        holder.title.setText(dc.getTitle());
        return view;
    }

    static class ViewHolder{
        public ImageView imageView;
        public TextView brief;
        public TextView title;
    }
}
