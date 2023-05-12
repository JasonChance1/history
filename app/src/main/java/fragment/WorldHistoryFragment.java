package fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.history.DetailChineseHistoryActivity;
import com.example.history.MyCollectionsActivity;
import com.example.history.R;
import com.example.history.bean.DynastyContent;
import com.example.history.bean.Threads.GetDCCallable;
import com.example.history.bean.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import listview.DynastyListViewAdapter;

public class WorldHistoryFragment extends Fragment {

    private ListView listView;
    private List<DynastyContent> list=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_world_history, container, false);
        listView = view.findViewById(R.id.listview);

        GetDCCallable getDCCallable = new GetDCCallable("2");//2代表世界史，1代表中国史
        FutureTask futureTask = new FutureTask(getDCCallable);
        Thread thread = new Thread(futureTask);
        thread.start();
        try {
            list = (List<DynastyContent>) futureTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DynastyListViewAdapter adapter = new DynastyListViewAdapter(getContext(),list);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DynastyContent dynastyContent = list.get(position);
                Intent intent = new Intent(getContext(), DetailChineseHistoryActivity.class);
                intent.putExtra("data",dynastyContent.getContent());
                startActivity(intent);
            }
        });
    }
}