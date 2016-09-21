package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapters.GalleryAdapter;
import com.malalaoshi.android.adapters.HighScoreAdapter;
import com.malalaoshi.android.adapters.SchoolAdapter;
import com.malalaoshi.android.fragments.TeacherInfoFragment;
import com.malalaoshi.android.network.api.CancelCollectTeacherApi;
import com.malalaoshi.android.network.api.CollectTeacherApi;
import com.malalaoshi.android.network.api.SchoolListApi;
import com.malalaoshi.android.network.api.TeacherInfoApi;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.LoginActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.entity.Achievement;
import com.malalaoshi.android.entity.DoneModel;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.HighScore;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.listener.BounceTouchListener;
import com.malalaoshi.android.network.result.SchoolListResult;
import com.malalaoshi.android.ui.widget.like.LikeButton;
import com.malalaoshi.android.ui.widget.like.OnLikeListener;
import com.malalaoshi.android.utils.FragmentUtil;
import com.malalaoshi.android.utils.LocManager;
import com.malalaoshi.android.utils.LocationUtil;
import com.malalaoshi.android.utils.MiscUtil;
import com.malalaoshi.android.utils.Number;
import com.malalaoshi.android.ui.widgets.FlowLayout;
import com.malalaoshi.android.ui.widgets.ObservableScrollView;
import com.malalaoshi.android.ui.widgets.RingProgressbar;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/3/29.
 */
public class TeacherInfoActivity extends BaseActivity {

    private static final String EXTRA_TEACHER_ID = "teacherId";

    public static void open(Context context, Long teacherId) {
        if (teacherId != null) {
            Intent intent = new Intent(context, TeacherInfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_TEACHER_ID, teacherId);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
        initViews(savedInstanceState);
    }

    private void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        long teacherId = intent.getLongExtra(EXTRA_TEACHER_ID,0);
        if (savedInstanceState==null){
            TeacherInfoFragment teacherInfoFragment = TeacherInfoFragment.newInstance(this,teacherId,false);
            FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(), null, teacherInfoFragment, TeacherInfoFragment.class.getName());
        }
    }

    @Override
    protected String getStatName() {
        return "老师详情页";
    }
}
