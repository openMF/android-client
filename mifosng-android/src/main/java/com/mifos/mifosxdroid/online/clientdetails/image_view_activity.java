package com.mifos.mifosxdroid.online.clientdetails;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.utils.ImageLoaderUtils;

public class image_view_activity extends AppCompatActivity {

    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_activity);

        mImageView= (ImageView)findViewById(R.id.imageView);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null);
        {
            int resId = bundle.getInt("resId");
            ImageLoaderUtils.loadImage(getApplicationContext() , resId, mImageView);
           // mImageView.setImageResource(resId);
        }
    }
}
