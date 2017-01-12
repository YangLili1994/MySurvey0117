package com.survey.hzyanglili1.mysurvey.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.survey.hzyanglili1.mysurvey.R;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by hzyanglili1 on 2016/11/17.
 */

public class MyResultListCursorAdapter extends CursorAdapter {

    public interface CallBack{
        abstract void itemClickHandler(int resultId,int id);
    }

    private Context context;
    private CallBack callBack;

    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式;
    private GregorianCalendar gc = new GregorianCalendar();

    public MyResultListCursorAdapter(Context context, Cursor c, int flags, CallBack callBack) {
        super(context, c, flags);
        this.context = context;
        this.callBack = callBack;
    }

    class ViewHolder{
        TextView resultId;
        TextView resultTime;
        TextView name;
        TextView sex;
        TextView age;
        TextView id;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_resultlist,viewGroup,false);

        viewHolder.resultId = (TextView)view.findViewById(R.id.item_resultlist_resultid);
        viewHolder.resultTime = (TextView)view.findViewById(R.id.item_resultlist_resulttime);
        viewHolder.name = (TextView)view.findViewById(R.id.item_resultlist_name);
        viewHolder.sex = (TextView)view.findViewById(R.id.item_resultlist_sex);
        viewHolder.age = (TextView)view.findViewById(R.id.item_resultlist_age);
        viewHolder.id = (TextView)view.findViewById(R.id.item_resultlist_id);

        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resultId = ((TextView)view.findViewById(R.id.item_resultlist_resultid)).getText().toString().trim();
                String id = ((TextView)view.findViewById(R.id.item_resultlist_id)).getText().toString().trim();
                Log.d("haha","resultid = "+resultId);

                try {
                    callBack.itemClickHandler(Integer.parseInt(resultId), Integer.parseInt(id));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });

        ViewHolder viewHolder= (ViewHolder) view.getTag();

        int resultId = cursor.getInt(cursor.getColumnIndex("result_id"));
        String time = cursor.getString(cursor.getColumnIndex("date"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String sexS = cursor.getString(cursor.getColumnIndex("sexS"));
        int age = cursor.getInt(cursor.getColumnIndex("age"));
        int id = cursor.getInt(0);

//        gc.setTimeInMillis(time);
//        String dateStr = dateformat.format(gc.getTime());

        viewHolder.resultId.setText(""+resultId);
        viewHolder.resultTime.setText(time);
        viewHolder.name.setText(name);
        viewHolder.sex.setText(sexS);
        viewHolder.age.setText(""+age);
        viewHolder.id.setText(""+id);
    }
}
