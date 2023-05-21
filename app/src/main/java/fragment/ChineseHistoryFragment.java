package fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.history.DetailChineseHistoryActivity;
import com.example.history.R;
import com.example.history.activity.BookDownLoadActivity;
import com.example.history.bean.DynastyContent;
import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.GetDCCallable;
import com.example.history.bean.Threads.SetRecord;
//import com.mysql.fabric.xmlrpc.base.Data;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import listview.DynastyListViewAdapter;
import utils.ToastUtil;

public class ChineseHistoryFragment extends Fragment {
    private ListView dynasty_listview;
    private MySqliteOpenHelper db;
    private String username;
    private Button book;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chinese_history,container,false);
        book = view.findViewById(R.id.book);
        db = new MySqliteOpenHelper(getActivity());
        username = db.getCurrentUser().getUsername();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dynasty_listview=view.findViewById(R.id.dynasty_listview);

        try {
            dynasty_listview.setAdapter(new DynastyListViewAdapter(getActivity(),getDynastyContentFromDatabase()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dynasty_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(getContext(), DetailChineseHistoryActivity.class);
                List<DynastyContent> dynastyContentList=null;
                try {
                    dynastyContentList=getDynastyContentFromDatabase();
                    Log.d("Chinese",dynastyContentList.toString());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DynastyContent clickData= dynastyContentList.get(position);
                SetRecord setRecord = new SetRecord(username,clickData.getId().toString(),"1","1");//recordType取值为1代表浏览历史,option取值为1代表中国史
                setRecord.start();
                Log.d("Chinese","传递的内容为:+id为"+clickData.getId()+",title:"+clickData.getTitle());
                intent.putExtra("data",clickData.getContent());
                startActivity(intent);
            }
        });
        dynasty_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("是否加入收藏？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<DynastyContent>  dynastyContentList= null;
                        try {
                            dynastyContentList = getDynastyContentFromDatabase();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        DynastyContent clickData= dynastyContentList.get(position);
                        SetRecord setRecord = new SetRecord(username,clickData.getId().toString(),"0","1");
                        setRecord.start();
                        ToastUtil.showMsg(getContext(),"收藏成功");
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                return true;
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookDownLoadActivity.class);
                startActivity(intent);
            }
        });
    }

    //获取所有中国史内容
    @SuppressLint("LongLogTag")
    private List<DynastyContent> getDynastyContentFromDatabase() throws ExecutionException, InterruptedException {
        GetDCCallable getDCCallable=new GetDCCallable("1");
        FutureTask futureTask=new FutureTask(getDCCallable);
        new Thread(futureTask).start();
        List<DynastyContent> list= (List<DynastyContent>) futureTask.get();
        Log.d("TestgetDynastyContentFromDatabase",list.toString());
        return list;
    }


}