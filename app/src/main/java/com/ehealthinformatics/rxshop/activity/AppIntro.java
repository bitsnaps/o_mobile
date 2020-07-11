package com.ehealthinformatics.rxshop.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.ehealthinformatics.odoorx.rxshop.R;
import com.ehealthinformatics.odoorx.rxshop.config.IntroSliderItems;
import com.ehealthinformatics.widgets.slider.SliderView;

public class AppIntro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);
        SliderView sliderView = (SliderView) findViewById(R.id.sliderView);
        IntroSliderItems sliderItems = new IntroSliderItems();
        if (!sliderItems.getItems().isEmpty()) {
            sliderView.setItems(getSupportFragmentManager(), sliderItems.getItems());
        } else {
            finish();
        }
    }
}
