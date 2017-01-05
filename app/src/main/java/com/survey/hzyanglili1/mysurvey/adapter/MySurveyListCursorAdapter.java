package com.survey.hzyanglili1.mysurvey.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.edit.MySurveiesActivity;

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
        abstract void onItemClicked(int surveyId,String title);
        abstract void onItemLongClicked(int surveyId,String surveyName);
        abstract void onPublicBtClicked(int surveyId,Boolean selected,int pos);
    }

    private Context context;
    private CallBack callBack;
    private Boolean isEdit;

    private int count = 0;
    private HashSet<Integer> surveyIds = new HashSet<>();

    public MySurveyListCursorAdapter(Context context, Cursor c, int flags,CallBack callBack) {
        super(context, c, flags);
        this.context = context;
        this.callBack = callBack;
    }

    class ViewHolder{
        TextView id;
        TextView surveyName;
        TextView surveyDesc;

        ImageView publicBt;
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

        viewHolder.id = (TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveyid);
        viewHolder.surveyName = (TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveyname);
        viewHolder.surveyDesc = (TextView)view.findViewById(R.id.item_mysurveylistcursoradapter_surveydesc);
        viewHolder.publicBt = (ImageView) view.findViewById(R.id.item_mysurveylistcursoradapter_public);

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
    public void bindView(View view, final Context context, final Cursor cursor) {

        final ViewHolder viewHolder= (ViewHolder)view.getTag();

        final int id = cursor.getInt(cursor.getColumnIndex("id"));
        final String name = cursor.getString(cursor.getColumnIndex("title"));
        String desc = cursor.getString(cursor.getColumnIndex("intro"));
        int status = cursor.getInt(cursor.getColumnIndex("status"));

        final int pos = cursor.getPosition();



        viewHolder.id.setText(""+id);
        viewHolder.surveyName.setText(name);
        viewHolder.surveyDesc.setText(desc);

        if (status == 2){
            viewHolder.publicBt.setSelected(true);
        }

        viewHolder.publicBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView = (ImageView) view;
//                if (imageView.isSelected()){
//                    imageView.setSelected(false);
//                }else {
//                    imageView.setSelected(true);
//                }

                Log.d("haha","cursor.getPosition()   "+pos);

                callBack.onPublicBtClicked(id,imageView.isSelected(),pos);

            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onItemClicked(id,name);

            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                callBack.onItemLongClicked(id,name);
                return true;
            }
        });

    }
}
