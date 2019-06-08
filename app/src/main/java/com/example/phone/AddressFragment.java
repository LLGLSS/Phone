package com.example.phone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.phone.R.id.words;


public class AddressFragment extends Fragment implements View.OnClickListener, Charbar.onWordsChangeListener{

    private  LayoutInflater currurInflater;
    private  View currurView;
    private FloatingActionButton floatingActionButton;
    private TextView tv,warnText;
    private Charbar word;
    private MainActivity mainActivity;
    private ListView listView;
    private List<AddressPeople> list;
    private MyDatabaseHelper dbHelper;
    //Android.os.Handler负责接收，并按计划发送和处理消息；
    //获取当前进程的looper对象,Looper类，是用来封装消息循环和消息队列的一个类
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //在主线程中,收到子线程发来消息
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currurInflater = inflater;
        currurView = currurInflater.inflate(R.layout.fragment_address, container, false);
        dbHelper = new MyDatabaseHelper(getContext(),"People.db",null,1);
        floatingActionButton = (FloatingActionButton)currurView.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        tv = (TextView)currurView.findViewById(R.id.tv);
        warnText = (TextView)currurView.findViewById(R.id.warnText);
        word = (Charbar)currurView.findViewById(words);
        listView = (ListView) currurView.findViewById(R.id.list);
        word.setOnWordsChangeListener(this);
        initData();
        if (list.size()>0){
            listView.setAdapter(new MyAdapter(getContext(),list));
        }else{
            warnText.setVisibility(View.VISIBLE);
        }
        //listView的滑动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            //为ListView设置滑动监听，来改变右侧字母列表的状态
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) { }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //当滑动列表的时候，更新右侧字母列表的选中状态
                word.setTouchIndex(list.get(firstVisibleItem).getHeaderWord());
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(),Information_Detail.class);
                intent.putExtra("name",list.get(position).getName());
                intent.putExtra("phone",list.get(position).getPhone());
                intent.putExtra("address",list.get(position).getAddress());
                startActivity(intent);
                getActivity().finish();
            }
        });
        return currurView;
    }


    // 当 fragment 第一次与 Activity 产生关联时就会调用，以后不再调用
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        mainActivity.setHandler(handler);
    }

    //手指按下字母改变监听回调
    @Override
    public void wordsChange(String words) {
        updateWord(words);
        updateListView(words);
    }

    /**
     * @param words 首字母
     */
    private void updateListView(String words) {
        for (int i = 0; i < list.size(); i++) {
            String headerWord = list.get(i).getHeaderWord();
            //将手指按下的字母与列表中相同字母开头的项找出来
            if (words.equals(headerWord)) {
                //将列表选中哪一个
                listView.setSelection(i);
                //找到开头的一个即可
                return;
            }
        }
    }
    /**
     * 更新中央的字母提示
     *
     * @param words 首字母
     */
    private void updateWord(String words) {

        tv.setText(words);
        tv.setVisibility(View.VISIBLE);
        //清空之前的所有消息
        handler.removeCallbacksAndMessages(null);
        //500ms后让tv隐藏
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setVisibility(View.GONE);
            }
        }, 500);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.floatingActionButton:
                Intent intent = new Intent(getActivity(),AddPeopleActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    /**
     * 初始化联系人列表信息
     */
    private void initData() {
        list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("people",null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                String address = cursor.getString(cursor.getColumnIndex("address"));

                list.add(new AddressPeople(name,phone,address));
            }while (cursor.moveToNext());
        }
        cursor.close();

        //对集合排序
        Collections.sort(list, new Comparator<AddressPeople>() {
            @Override
            public int compare(AddressPeople lhs, AddressPeople rhs) {
                //根据拼音进行排序
                return lhs.getPinyin().compareTo(rhs.getPinyin());
            }
        });
    }

    //list适配器
    public class MyAdapter extends BaseAdapter {
        private List<AddressPeople> list;
        private LayoutInflater inflater;

        public MyAdapter(Context context, List<AddressPeople> list) {
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item, null);
                holder.tv_word = (TextView) convertView.findViewById(R.id.tv_word);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String word = list.get(position).getHeaderWord();
            holder.tv_word.setText(word);
            holder.tv_name.setText(list.get(position).getName());
            //将相同字母开头的合并在一起
            if (position == 0) {
                //第一个是一定显示的
                holder.tv_word.setVisibility(View.VISIBLE);
            } else {
                //后一个与前一个对比,判断首字母是否相同，相同则隐藏
                String headerWord = list.get(position - 1).getHeaderWord();
                if (word.equals(headerWord)) {
                    holder.tv_word.setVisibility(View.GONE);
                } else {
                    holder.tv_word.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView tv_word;
            private TextView tv_name;
        }
    }

}

