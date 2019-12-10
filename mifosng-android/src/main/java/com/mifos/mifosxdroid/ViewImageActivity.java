package com.mifos.mifosxdroid;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewImageActivity extends AppCompatActivity {
    @BindView(R.id.view_image_name)
    TextView fullName;

    @BindView(R.id.view_image_photo)
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.bind(this);
        image.setImageBitmap((Bitmap) getIntent().getParcelableExtra("clientImage"));
        fullName.setText(getIntent().getStringExtra("clientName"));
    }
}
