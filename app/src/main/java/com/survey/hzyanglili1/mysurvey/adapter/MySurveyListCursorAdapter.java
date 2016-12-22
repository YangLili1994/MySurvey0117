package com.survey.hzyanglili1.mysurvey.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.survey.hzyanglili1.mysurvey.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class MySurveyListCursorAdapter extends CursorAdapter {

    public interface CallBack{
        abstract void itemClickHandler(int surveyId);
        abstract void notifyCheckButtonChange(int count);
    }

    private Context context;
    private CallBack callBack;
    private Boolean isEdit;

    private int count = 0;
    private HashSet<Integer> surveyIds = new HashSet<>();

    public MySurveyListCursorAdapter(Context context, Cursor c, int flags,Boolean isEdit, CallBack callBack) {
        super(context, c, flags);
        this.context = context;
        this.callBack = callBack;
        this.isEdit = isEdit;
    }

    class ViewHolder{
        TextView id;
        TextView surveyName;
        TextView surveyDesc;

        CheckBox checkBox;
    }

    /**
     * 调用newView来实例化view
     * @param context
     * @param cursor
     * @param viewGroup
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_mysurveylistcursoradapter,viewGroup,false);

        viewHolder.id = (TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveyid);
        viewHolder.surveyName = (TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveyname);
        viewHolder.surveyDesc = (TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveydesc);
        viewHolder.checkBox = (CheckBox) view.findViewById(R.id.item_mysurveylistcursoradapter_checkbox);

        view.setTag(viewHolder);
        return view;
    }

    public int[] getSelectedSurveyIds(){
        int[] Ids = null;

        Ids = new int[surveyIds.size()];
        int i = 0;

        for (Integer s : surveyIds){
            Ids[i++] = s;
        }

        return Ids;
    }

    /**
     * 调用bindView来绘制view
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final ViewHolder viewHolder= (ViewHolder)view.getTag();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String surveyId = ((TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveyid)).getText().toString().trim();
                Log.d("haha","surveyid = "+surveyId);

                if(!isEdit){
                    callBack.itemClickHandler(Integer.parseInt(surveyId));
                }else {
                    viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
                }

            }
        });



        final int id = cursor.getInt(cursor.getColumnIndex("survey_id"));
        String name = cursor.getString(cursor.getColumnIndex("survey_name"));
        String desc = cursor.getString(cursor.getColumnIndex("survey_desc"));

        viewHolder.id.setText(""+id);
        viewHolder.surveyName.setText(name);
        viewHolder.surveyDesc.setText(desc);

        if (isEdit){
            viewHolder.checkBox.setVisibility(View.VISIBLE);

            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (b){
                        surveyIds.add(id);
                        count++;
                    }else {
                        count--;
                        surveyIds.remove(id);
                    }

                    callBack.notifyCheckButtonChange(count);

                }
            });
        }

    }
}
