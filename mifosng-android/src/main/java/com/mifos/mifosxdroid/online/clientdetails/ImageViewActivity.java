package com.mifos.mifosxdroid.online.clientdetails;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.mifos.mifosxdroid.R;
import com.mifos.utils.ImageLoaderUtils;

public class ImageViewActivity extends AppCompatActivity {

    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_image);

        mImageView= (ImageView)findViewById(R.id.imageView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id= extras.getInt("resId");
            ImageLoaderUtils.loadImage(getApplicationContext(),id ,mImageView);
        }

    }
}
