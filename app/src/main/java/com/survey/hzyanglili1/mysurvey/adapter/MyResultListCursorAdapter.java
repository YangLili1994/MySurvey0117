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
        abstract void itemClickHandler(int resultId);
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
        TextView resultDesc;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_resultlist,viewGroup,false);

        viewHolder.resultId = (TextView)view.findViewById(R.id.item_resultlist_resultid);
        viewHolder.resultTime = (TextView)view.findViewById(R.id.item_resultlist_resulttime);
        viewHolder.resultDesc = (TextView)view.findViewById(R.id.item_resultlist_resultDesc);

        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resultId = ((TextView)view.findViewById(R.id.item_resultlist_resultid)).getText().toString().trim();
                Log.d("haha","resultid = "+resultId);

                callBack.itemClickHandler(Integer.parseInt(resultId));

            }
        });

        ViewHolder viewHolder= (ViewHolder) view.getTag();

        int id = cursor.getInt(cursor.getColumnIndex("result_id"));
        long time = cursor.getLong(cursor.getColumnIndex("result_time"));
        String desc = "填写人信息";

        gc.setTimeInMillis(time);
        String dateStr = dateformat.format(gc.getTime());

        viewHolder.resultId.setText(""+id);
        viewHolder.resultTime.setText(dateStr);
        viewHolder.resultDesc.setText(desc);
    }
}
