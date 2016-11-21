package com.survey.hzyanglili1.mysurvey.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.edit.MySurveiesActivity;
import com.survey.hzyanglili1.mysurvey.activity.edit.SurveyPrelookActivity;
import com.survey.hzyanglili1.mysurvey.activity.use.SurveyListActivity;
import com.survey.hzyanglili1.mysurvey.utils.CountHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends BaseActivity {

    private TextView showBt = null;
    private TextView editBt = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showBt = (TextView)findViewById(R.id.activity_main_show) ;
        editBt = (TextView)findViewById(R.id.activity_main_edit);


        showBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, SurveyListActivity.class));

            }
        });

        editBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, MySurveiesActivity.class));
            }
        });



        CountHelper countHelper = CountHelper.getInstance(this);
        countHelper.setSurveyCount(1);
        Log.d("haha","surveycount = "+countHelper.getSurveyCount());

        countHelper.setOptionCount(2);
        Log.d("haha","optioncount = "+countHelper.getOptionCount());

        countHelper.setQuestionCount(3);
        Log.d("haha","quescount = "+countHelper.getQuestionCount());



    }
}
