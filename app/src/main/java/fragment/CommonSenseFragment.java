package fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.history.DetailChineseHistoryActivity;
import com.example.history.R;
import com.example.history.bean.DynastyContent;
import com.example.history.bean.HttpClient;
import com.example.history.bean.Threads.SearchCallable;
import com.example.history.bean.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import utils.ToastUtil;

public class CommonSenseFragment extends Fragment {

    private RecyclerView rv;
    private Button findBtn;
    private EditText findKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_sense, container, false);
        init(view);


        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = findKey.getText().toString().trim();
                Log.d("Search","关键词："+keyword);
                try {
                    List<DynastyContent> result = getSearchContent(keyword);
                    Log.d("Search","查询结果："+result);
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(result);
                    adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getContext(), DetailChineseHistoryActivity.class);
                            DynastyContent dc = result.get(position);
                            intent.putExtra("data",dc.getContent());
                            startActivity(intent);
                        }
                    });
                    rv.setAdapter(adapter);
                } catch (ExecutionException e) {
                    ToastUtil.showMsg(getContext(),"没有相关内容");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ToastUtil.showMsg(getContext(),"没有相关内容");
                }

            }
        });

        return view;
    }


    private void init(View v){
        findBtn=v.findViewById(R.id.find_btn);
        rv=v.findViewById(R.id.recyclerView);
        findKey=v.findViewById(R.id.find_input);
    }

    private List<DynastyContent> getSearchContent(String keyword) throws ExecutionException, InterruptedException {
        SearchCallable searchCallable = new SearchCallable(keyword);
        List<DynastyContent> result = new ArrayList<>();
        FutureTask<List<DynastyContent>> futureTask=new FutureTask<>(searchCallable);
        Log.d("Search","创建futureTask");
        Thread thread=new Thread(futureTask);
        thread.start();
        Log.d("Search","开启线程");
        try {
            while (!futureTask.isDone()) {
                Thread.sleep(100); // 等待100毫秒
            }
            result = futureTask.get();
            Log.d("Search","执行结果："+ result);
            return result;
        } catch (InterruptedException | ExecutionException e) {
            Log.d("Search", "Error retrieving search results", e);
            return Collections.emptyList(); // 或者以其他方式处理错误
        }
    }
}