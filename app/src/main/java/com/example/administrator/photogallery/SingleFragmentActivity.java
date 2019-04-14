package com.example.administrator.photogallery;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    private static String TAG = "SingleFragmentActivityGuan";
    protected abstract Fragment createFragment();

    /**
     * method name:getLayoutResId
     * 该抽象方法的资源id 作为承载Fragment的视图容器（且其中必须包含有ID== R.idfragment_container 的view container）
     * @return 资源id，用于setContentView(int resId)
     */
    @LayoutRes
    protected abstract int getLayoutResId();


    /**
     * method name: onCreate
     * SingleFragmentActivity 此方法中
     * 1 设置Activity的布局资源——抽象为getlayoutResId接口;
     * 2 使用FragmentManager来加载createFragment方法返回的Fragment对象 到 R.id.fragment_container 容器中.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        Log.d(TAG, "onCreate: ");
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);//FrameLayout 作为一个fragment container
        if(fragment == null){
            Log.d(TAG, "onCreate: and fragment is NULL，create a Fragment by " +
                    "calling createFragment method");
            fragment = createFragment();//子类中需实现这个方法
            fm.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }
    }


}
