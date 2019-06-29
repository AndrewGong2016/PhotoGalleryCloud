package com.example.administrator.photogallery;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }

    /**
     *
     *implement superclass method
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

}
