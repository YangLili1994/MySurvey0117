package com.survey.hzyanglili1.mysurvey.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
            viewHolder.optionImage = (CircleImageView) view.findViewById(R.id.item_questionoption_image);
            viewHolder.delImage = (ImageView) view.findViewById(R.id.item_questionoption_delete);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (optionTitles.get(i) != null){
            viewHolder.optionTitle.setText(optionTitles.get(i));
        }

        if (optionImages.get(i) != null){

            //Bitmap bmp = MySurveyApplication.decodeSampledBitmapFromFile(optionImages.get(i),80,80);

            Bitmap bmp = BitmapFactory.decodeFile(optionImages.get(i));
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

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return view;
    }

    class ViewHolder{
        public EditText optionTitle;
        public CircleImageView optionImage;
        public ImageView delImage;

    }


}
