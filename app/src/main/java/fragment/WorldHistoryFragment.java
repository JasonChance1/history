package fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.history.DetailChineseHistoryActivity;
import com.example.history.MyCollectionsActivity;
import com.example.history.R;
import com.example.history.bean.DynastyContent;
import com.example.history.bean.MySqliteOpenHelper;
import com.example.history.bean.Threads.GetDCCallable;
import com.example.history.bean.Threads.SearchCallable;
import com.example.history.bean.Threads.SetRecord;
import com.example.history.bean.adapter.CustomSpinnerAdapter;
import com.example.history.bean.adapter.RecyclerViewAdapter;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import listview.DynastyListViewAdapter;
import utils.ToastUtil;

public class WorldHistoryFragment extends Fragment {
    private Spinner continent,country;
    private List<String> continentList = new ArrayList<>();
    private List<String> asia = Arrays.asList("日本", "韩国","印度");
    private List<String> europe = Arrays.asList("德国", "法国","意大利","荷兰","西班牙");
    private List<String> africa = Arrays.asList("喀麦隆", "埃及","加纳","马达加斯加","利比亚");
    private List<String> southA = Arrays.asList("巴西", "阿根廷","智利","哥伦比亚");
    private List<String> northA = Arrays.asList("美国", "加拿大","墨西哥");
    private List<String> oceania = Arrays.asList("澳大利亚","新西兰");
    private List<String> arctic = Arrays.asList("挪威","芬兰");
    
    private String currentCountry;
    private List<String> currentList;

    private List<List<String>> countriesList = new ArrayList<>();
    private ListView listView;
    private List<DynastyContent> list=new ArrayList<>();
    private ArrayAdapter continentAdapter,countryAdapter;

    private TextView screen;
    private LinearLayout screenBox;

    private List<DynastyContent> screenResultList;
    private Boolean flag = false;

    private MySqliteOpenHelper db;

    private DynastyListViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_world_history, container, false);
        initData();
        initView(view);

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

        if(flag){
            adapter = new DynastyListViewAdapter(getContext(),screenResultList);
        }else {
            adapter = new DynastyListViewAdapter(getContext(),list);
        }

        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            bindEvent();
    }

    private void bindEvent(){
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), DetailChineseHistoryActivity.class);
            List<DynastyContent> dynastyContentList= new ArrayList<>();
            try {
                dynastyContentList=getWorldHistoryFromDatabase();
                Log.d("WorldHistory",dynastyContentList.toString());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DynastyContent clickData= dynastyContentList.get(position);
            Log.d("WorldHistory",db.getCurrentUser().getUsername()+","+clickData.getId().toString());
            SetRecord setRecord = new SetRecord(db.getCurrentUser().getUsername(),clickData.getId().toString(),"1","2");//recordType取值为1代表浏览历史,option取值为1代表中国史
            setRecord.start();

            intent.putExtra("data",clickData.getContent());
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            new XPopup.Builder(getContext()).asConfirm("", "是否加入收藏", new OnConfirmListener() {
                @Override
                public void onConfirm() {
                    List<DynastyContent>  dynastyContentList= null;
                    try {
                        dynastyContentList = getWorldHistoryFromDatabase();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DynastyContent clickData= dynastyContentList.get(position);
                    SetRecord setRecord = new SetRecord(db.getCurrentUser().getUsername(),clickData.getId().toString(),"0","2");
                    setRecord.start();
                    ToastUtil.showMsg(getContext(),"收藏成功");
                }
            }).show();

            return true;
        });


        continent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCountryAdapter(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //国家下拉选项
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCountry = currentList.get(position);
                SearchCallable searchCallable = new SearchCallable(currentCountry,"1");
                FutureTask futureTask = new FutureTask(searchCallable);
                Thread thread = new Thread(futureTask);
                thread.start();
                try {
                    screenResultList = (List<DynastyContent>) futureTask.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    screenBox.setVisibility(View.VISIBLE);
                }else{
                    screenBox.setVisibility(View.GONE);
                }
                flag = !flag;
            }
        });
    }

    private void initData(){
        db = new MySqliteOpenHelper(getContext());

        continentList.clear();
        continentList.add("亚洲");
        continentList.add("欧洲");
        continentList.add("非洲");
        continentList.add("南美洲");
        continentList.add("北美洲");
        continentList.add("大洋洲");
        continentList.add("南极洲");

        countriesList.clear();
        countriesList.add(asia);
        countriesList.add(europe);
        countriesList.add(africa);
        countriesList.add(southA);
        countriesList.add(northA);
        countriesList.add(oceania);
        countriesList.add(arctic);
    }

    private void initView(View view){
        screen = view.findViewById(R.id.screenBtn);
        continent = view.findViewById(R.id.continent);
        country = view.findViewById(R.id.country);
        listView = view.findViewById(R.id.listview);
        screenBox = view.findViewById(R.id.screenBox);

        continentAdapter = new ArrayAdapter(getActivity(),androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,continentList);
        continent.setAdapter(continentAdapter);

        CustomSpinnerAdapter countryAdapter = new CustomSpinnerAdapter(countriesList.get(0));
        country.setAdapter(countryAdapter);
    }
    private void updateCountryAdapter(int position) {
        currentList = countriesList.get(position);
        ArrayAdapter<String> newAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, currentList);
        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(newAdapter);
    }

    @SuppressLint("LongLogTag")
    private List<DynastyContent> getWorldHistoryFromDatabase() throws ExecutionException, InterruptedException {
        GetDCCallable getDCCallable=new GetDCCallable("2");
        FutureTask futureTask=new FutureTask(getDCCallable);
        new Thread(futureTask).start();
        List<DynastyContent> list= (List<DynastyContent>) futureTask.get();
        return list;
    }
}