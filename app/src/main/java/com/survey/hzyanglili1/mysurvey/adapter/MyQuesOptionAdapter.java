package com.survey.hzyanglili1.mysurvey.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by hzyanglili1 on 2016/12/22.
 */

public class MyQuesOptionAdapter extends BaseAdapter {

    private Context context;
    private List<String> optionTitles;
    private List<String> optionImages;
    private MyQuesOptionAdapterCallBack callBack;

    public interface MyQuesOptionAdapterCallBack{
        void deleteOption(int position);
        void addImage(int position);
    }

    public MyQuesOptionAdapter(Context context,List<String> optionTitles, List<String> optionImages,MyQuesOptionAdapterCallBack callBack) {
        this.context = context;
        this.optionTitles = optionTitles;
        this.optionImages = optionImages;
        this.callBack = callBack;
    }



    @Override
    public int getCount() {
        //两者最小值
        return (optionImages.size() < optionTitles.size()) ? optionImages.size():optionTitles.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;

        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_questionoption,viewGroup,false);
            viewHolder.optionTitle = (EditText) view.findViewById(R.id.item_questionoption_title);
            viewHolder.optionImage = (ImageView) view.findViewById(R.id.item_questionoption_image);
            viewHolder.delImage = (ImageView) view.findViewById(R.id.item_questionoption_delete);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.optionTitle.setText(optionTitles.get(i));
        if (optionImages.get(i) != null){
            Log.d("lala","position "+i+"imagepath "+optionImages.get(i));
            Bitmap bmp = MySurveyApplication.decodeSampledBitmapFromFile(optionImages.get(i),64,64);
            viewHolder.optionImage.setImageBitmap(bmp);
            bmp = null;
        }

        viewHolder.delImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("haha","delete option clicked. position = "+i);
                callBack.deleteOption(i);

            }
        });


        viewHolder.optionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("haha","add option image clicked.");

                callBack.addImage(i);

            }
        });

        return view;
    }

    class ViewHolder{
        public EditText optionTitle;
        public ImageView optionImage;
        public ImageView delImage;

    }


}
