package com.survey.hzyanglili1.mysurvey.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.survey.hzyanglili1.mysurvey.R;

import java.util.HashSet;

/**
 * Created by hzyanglili1 on 2016/12/29.
 */

public class MyUsedSurveyListAdapter extends CursorAdapter {

    public interface CallBack{
        abstract void onItemClicked(int surveyId,String title);
    }

    private Context context;
    private MyUsedSurveyListAdapter.CallBack callBack;
    private Boolean isEdit;

    private int count = 0;
    private HashSet<Integer> surveyIds = new HashSet<>();

    public MyUsedSurveyListAdapter(Context context, Cursor c, int flags, MyUsedSurveyListAdapter.CallBack callBack) {
        super(context, c, flags);
        this.context = context;
        this.callBack = callBack;
    }

    private class ViewHolder{
        TextView id;
        TextView surveyName;
        TextView surveyDesc;

    }

    /**
     * 调用newView来实例化view
     * @param context
     * @param cursor
     * @param viewGroup
     * @return
     */
    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_mysurveylistcursoradapter,viewGroup,false);

        ((ImageView)view.findViewById(R.id.item_mysurveylistcursoradapter_public)).setVisibility(View.GONE);

        viewHolder.id = (TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveyid);
        viewHolder.surveyName = (TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveyname);
        viewHolder.surveyDesc = (TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveydesc);


        view.setTag(viewHolder);

        return view;
    }


    /**
     * 调用bindView来绘制view
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ViewHolder viewHolder= (ViewHolder)view.getTag();

        final int id = cursor.getInt(cursor.getColumnIndex("id"));
        final String name = cursor.getString(cursor.getColumnIndex("title"));
        String desc = cursor.getString(cursor.getColumnIndex("intro"));



        viewHolder.id.setText(""+id);
        viewHolder.surveyName.setText(name);
        viewHolder.surveyDesc.setText(desc);



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onItemClicked(id,name);

            }
        });


    }
}
