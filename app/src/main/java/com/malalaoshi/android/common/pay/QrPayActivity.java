package com.malalaoshi.android.common.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.utils.FragmentUtil;
import com.malalaoshi.android.utils.MiscUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/11/7.
 */

public class QrPayActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;

    public static void launch(Context context, CreateCourseOrderResultEntity entity){
        if (entity!=null){
            Intent intent = new Intent(context, QrPayActivity.class);
            intent.putExtra(QrPayFragment.ARG_ORDER_INFO, entity);
            context.startActivity(intent);
        }else{
            MiscUtil.toast("订单信息不完整!");
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commen_title);
        ButterKnife.bind(this);
        titleBarView.setOnTitleBarClickListener(this);
        if (savedInstanceState==null){
            QrPayFragment qrPayFragment = QrPayFragment.newInstance((CreateCourseOrderResultEntity) getIntent().getSerializableExtra(QrPayFragment.ARG_ORDER_INFO));
            FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(), null,
                    qrPayFragment, QrPayFragment.class.getName());
        }
    }

    @Override
    protected String getStatName() {
        return null;
    }

    @Override
    public void onTitleLeftClick() {
        finish();
    }

    @Override
    public void onTitleRightClick() {

    }
}
