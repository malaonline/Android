package com.malalaoshi.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.malalaoshi.android.activitys.CourseConfirmActivity;
import com.malalaoshi.android.activitys.GalleryActivity;
import com.malalaoshi.android.activitys.GalleryPreviewActivity;
import com.malalaoshi.android.adapters.GalleryAdapter;
import com.malalaoshi.android.adapters.HighScoreAdapter;
import com.malalaoshi.android.adapters.SchoolAdapter;
import com.malalaoshi.android.core.base.BaseFragment;
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
import com.malalaoshi.android.network.api.CancelCollectTeacherApi;
import com.malalaoshi.android.network.api.CollectTeacherApi;
import com.malalaoshi.android.network.api.SchoolListApi;
import com.malalaoshi.android.network.api.TeacherInfoApi;
import com.malalaoshi.android.network.result.SchoolListResult;
import com.malalaoshi.android.ui.widget.like.LikeButton;
import com.malalaoshi.android.ui.widget.like.OnLikeListener;
import com.malalaoshi.android.ui.widgets.FlowLayout;
import com.malalaoshi.android.ui.widgets.ObservableScrollView;
import com.malalaoshi.android.ui.widgets.RingProgressbar;
import com.malalaoshi.android.managers.LocManager;
import com.malalaoshi.android.utils.LocationUtil;
import com.malalaoshi.android.utils.MiscUtil;
import com.malalaoshi.android.utils.Number;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by kang on 16/9/21.
 */

public class TeacherInfoFragment extends BaseFragment  implements View.OnClickListener, ObservableScrollView.ScrollViewListener, TitleBarView.OnTitleBarClickListener,
        AdapterView.OnItemClickListener, OnLikeListener {

    private static final String ARG_TEACHER_ID = "teacherId";
    private static final String ARG_SI_SHOW_SCHOOLS = "isshowschools";
    private static int REQUEST_CODE_LOGIN = 1000;

    //教师id
    private Long mTeacherId;

    //是否显示学习中心
    private boolean isShowSchools = false;

    //所有教学中心
    private List<School> mAllSchools = null;

    //第一个显示的教学中心
    private List<School> mFirstSchool = null;

    //教师信息请求结果
    private Teacher mTeacher;

    //标题栏
    @Bind(R.id.titleBar)
    protected TitleBarView titleBarView;

    @Bind(R.id.view_line)
    protected View viewLine;

    //
    @Bind(R.id.scroll_view)
    protected ObservableScrollView scrollView;

    //背景图
    @Bind(R.id.header_image_view)
    protected View headerImage;

    //头像
    @Bind(R.id.parent_teacher_detail_head_portrait)
    protected MalaImageView mHeadPortrait;

    //教师姓名
    @Bind(R.id.parent_teacher_detail_name_tv)
    protected TextView mTeacherName;

    //教师性别
    @Bind(R.id.parent_teacher_detail_gender_iv)
    protected ImageView mTeacherGender;

    //教授科目
    @Bind(R.id.parent_teacher_detail_subject_tv)
    protected TextView mTeacherSubject;

    //价格区间
    @Bind(R.id.parent_teaching_price_tv)
    protected TextView mPriceRegion;

    //教龄
    @Bind(R.id.view_teacher_level)
    protected RingProgressbar viewTeacherLevel;

    @Bind(R.id.tv_teacher_level)
    protected TextView tvTeacherLevel;

    //级别
    @Bind(R.id.view_teacher_seniority)
    protected RingProgressbar viewTeacherSeniority;

    @Bind(R.id.tv_teacher_seniority)
    protected TextView tvTeacherSeniority;

    //教授年级
    //小学
    @Bind(R.id.rl_teach_primary)
    protected RelativeLayout rlTeachPrimary;

    @Bind(R.id.fl_teach_primary)
    protected FlowLayout flTeachPrimary;

    @Bind(R.id.view_primary_line)
    protected View viewPrimaryLine;

    //初中
    @Bind(R.id.rl_teach_junior)
    protected RelativeLayout rlTeachJunior;

    @Bind(R.id.fl_teach_junior)
    protected FlowLayout flTeachJunior;

    @Bind(R.id.view_junior_line)
    protected View viewJuniorLine;

    //高中
    @Bind(R.id.rl_teach_senior)
    protected RelativeLayout rlTeachSenior;

    @Bind(R.id.fl_teach_senior)
    protected FlowLayout flTeachSenior;

    //标签
    @Bind(R.id.parent_teacher_detail_tag_fl)
    protected FlowLayout flTags;

    //教师提分榜
    @Bind(R.id.parent_teacher_detail_highscore_listview)
    protected ListView mHighScoreList;

    @Bind(R.id.hs_gallery)
    protected HorizontalScrollView hsGallery;

    //个人相册
    @Bind(R.id.gv_gallery)
    protected GridView gvGallery;

    //更多相册
    @Bind(R.id.tv_gallery_more)
    protected TextView tvGalleryMore;

    private GalleryAdapter galleryAdapter;

    //特殊成就
    @Bind(R.id.parent_teacher_detail_achievement_fl)
    protected FlowLayout mAchievement;

    //马上报名
    @Bind(R.id.parent_teacher_signup_btn)
    protected TextView tvSignUp;

    //学习中心列表
    @Bind(R.id.listview_school)
    protected ListView listviewSchool;

    //更多学习中心
    @Bind(R.id.ll_school_more)
    protected LinearLayout llSchoolMore;

    @Bind(R.id.iv_school_more)
    protected ImageView ivSchoolMore;

    @Bind(R.id.tv_school_more)
    protected TextView tvSchoolMore;

    @Bind(R.id.iv_teacher_bk)
    protected MalaImageView teacherView;

    @Bind(R.id.tv_collection)
    protected TextView tvCollection;

    @Bind(R.id.heart_button)
    protected LikeButton likeButton;

    private Drawable drawCollection;
    private Drawable drawUnCollection;

    private SchoolAdapter mSchoolAdapter;

    private boolean isShowAllSchools = false;

    private boolean teacherInfoFlag = false;
    private boolean schoolFlag = false;

    public static TeacherInfoFragment newInstance(Context context, Long teacherId, boolean isShowSchools) {
        if (teacherId == null) {
           return null;
        }
        TeacherInfoFragment fragment = new TeacherInfoFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TEACHER_ID, teacherId);
        args.putBoolean(ARG_SI_SHOW_SCHOOLS, isShowSchools);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("arguments can not been null");
        }
        mTeacherId = args.getLong(ARG_TEACHER_ID, 0);
        isShowSchools = args.getBoolean(ARG_SI_SHOW_SCHOOLS,false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_info, container, false);
        ButterKnife.bind(this, view);
        ShareSDK.initSDK(getContext());
        initViews();
        initData();
        setEvent();
        return view;
    }

    private void startProcess(){
        startProcessDialog("正在加载数据···");
    }

    private void stopProcess() {
        if (teacherInfoFlag && (!isShowSchools||(isShowSchools&&schoolFlag))) {
            stopProcessDialog();
        }
    }

    private void initViews() {
        titleBarView.setRightBackgroundResource(R.drawable.bitmap_share_white);
        mHighScoreList.setFocusable(false);

        drawCollection = getResources().getDrawable(R.drawable.ic_uncollection);
        drawCollection.setBounds(0, 0, drawCollection.getMinimumWidth(), drawCollection.getMinimumHeight());
        drawUnCollection = getResources().getDrawable(R.drawable.ic_uncollection);
        drawUnCollection.setBounds(0, 0, drawUnCollection.getMinimumWidth(), drawUnCollection.getMinimumHeight());
        listviewSchool.setFocusable(false);

        if (isShowSchools){

        }else{
            likeButton.setEnabled(false);
        }
    }

    private void setEvent() {
        tvSignUp.setOnClickListener(this);
        llSchoolMore.setOnClickListener(this);
        tvGalleryMore.setOnClickListener(this);
        scrollView.setScrollViewListener(this);
        titleBarView.setOnTitleBarClickListener(this);
        gvGallery.setOnItemClickListener(this);
        mHeadPortrait.setOnClickListener(this);
        likeButton.setOnLikeListener(this);
        BounceTouchListener bounceTouchListener = new BounceTouchListener(scrollView, R.id.layout_teacher_info_body);
        bounceTouchListener.setOnTranslateListener(new BounceTouchListener.OnTranslateListener() {
            @Override
            public void onTranslate(float translation) {
                if (translation > 0) {
                    float scale = ((2 * translation) / headerImage.getMeasuredHeight()) + 1;
                    headerImage.setScaleX(scale);
                    headerImage.setScaleY(scale);
                } else {
                    headerImage.setScaleX(1);
                    headerImage.setScaleY(1);
                }
            }
        });
        scrollView.setOnTouchListener(bounceTouchListener);
    }

    private void initData() {
        galleryAdapter = new GalleryAdapter(getContext());
        gvGallery.setAdapter(galleryAdapter);

        mAllSchools = new ArrayList<>();
        mFirstSchool = new ArrayList<>();
        mSchoolAdapter = new SchoolAdapter(getContext());
        listviewSchool.setAdapter(mSchoolAdapter);
        startProcess();
        loadData();
    }

    private void loadData() {
        //老师
        loadTeacherInfo();
        //请求教学环境信息
        if (isShowSchools){
            loadSchools();
        }
    }

    private void loadTeacherInfo() {
        if (mTeacherId == null) {
            return;
        }
        ApiExecutor.exec(new TeacherInfoFragment.LoadTeacherInfoRequest(this, mTeacherId));
    }

    private void loadSchools() {
        ApiExecutor.exec(new TeacherInfoFragment.LoadSchoolListRequest(this,mTeacherId));
    }

    private void loadSchoolListSuccess(SchoolListResult result) {
        //获取体验中心
        mAllSchools.addAll(result.getResults());
        //无数据
        if (mAllSchools.size() <= 0) {
            return;
        }

        //获取位置
        Location location = LocManager.getInstance().getLocation();
        if (location != null) {
            //排序
            LocationUtil.sortByDistance(mAllSchools, location.getLatitude(), location.getLongitude());
            mFirstSchool.clear();
            mFirstSchool.add(mAllSchools.get(0));
            tvSchoolMore.setText(
                    String.format("离您最近的社区中心 (%s)", LocationUtil.formatDistance(mAllSchools.get(0).getDistance())));
        } else {
            School school;
            for (int i = 0; i < mAllSchools.size(); i++) {
                if (mAllSchools.get(i).isCenter()) {
                    if (i == 0) {
                        break;
                    }
                    school = mAllSchools.get(i);
                    mAllSchools.set(i, mAllSchools.get(0));
                    mAllSchools.set(0, school);
                    break;
                }

            }
            mFirstSchool.add(mAllSchools.get(0));
            tvSchoolMore.setText("其他社区中心");
        }
        isShowAllSchools = false;
        mSchoolAdapter.addAll(mFirstSchool);
        mSchoolAdapter.notifyDataSetChanged();
    }


    private void loadTeacherInfoSuccess(@NonNull Teacher teacher) {
        mTeacher = teacher;
        updateUI(mTeacher);
    }

    private void updateBlurImage(final String url) {
        teacherView.loadBlurImage(url, R.drawable.core__teacher_banner);
    }

    //跟新教师详情
    private void updateUI(Teacher teacher) {
        if (teacher != null) {
            String string;
            //姓名
            string = teacher.getName();
            if (string != null) {
                mTeacherName.setText(string);
            }
            //头像
            string = teacher.getAvatar();
            mHeadPortrait.loadCircleImage(string, R.drawable.ic_default_avatar);
            updateBlurImage(string);

            //性别
            String gender = teacher.getGender();
            if (gender != null && gender.equals("m")) {
                mTeacherGender.setImageResource(R.drawable.ic_male_gender);
            } else if (gender != null && gender.equals("f")) {
                mTeacherGender.setImageResource(R.drawable.ic_female_gender);
            }

            //教授科目
            if (!teacher.getSubject().isEmpty()) {
                mTeacherSubject.setText(teacher.getSubject());
            }

            Double minPrice = teacher.getMin_price();
            Double maxPrice = teacher.getMax_price();
            String region = null;
            if (minPrice != null && maxPrice != null) {

                region = com.malalaoshi.android.utils.Number.subZeroAndDot(minPrice * 0.01d) + "-" + Number
                        .subZeroAndDot(maxPrice * 0.01d) + "元/小时";
            } else if (minPrice != null) {
                region = Number.subZeroAndDot(minPrice * 0.01d) + "元/小时";
            } else if (maxPrice != null) {
                region = Number.subZeroAndDot(maxPrice * 0.01d) + "元/小时";
            }
            if (region != null) {
                mPriceRegion.setText(region);
            }

            //教授年级
            String[] grades = mTeacher.getGrades();
            setGradeTeaching(grades);

            //分格标签
            String[] tags = mTeacher.getTags();
            if (tags != null && tags.length > 0) {
                setFlowDatas(flTags, tags, R.drawable.bg_text_tag, R.color.color_blue_5789ac);
            }

            //提分榜
            List<HighScore> highScores = new ArrayList<>();
            //第一个为空,listView第一行为标题
            highScores.add(new HighScore());
            highScores.addAll(mTeacher.getHighscore_set());
            HighScoreAdapter highScoreAdapter = new HighScoreAdapter(getContext(), highScores);
            mHighScoreList.setFocusable(true);
            mHighScoreList.setAdapter(highScoreAdapter);

            //个人相册
            loadGallery(mTeacher.getPhoto_set());
            //特殊成就
            List<Achievement> achievements = mTeacher.getAchievement_set();
            if (achievements != null && achievements.size() > 0) {
                setFlowCertDatas(mAchievement, achievements, R.drawable.item_text_bg);
            }

            //教龄级别
            Integer level = teacher.getLevel();
            if (null != level) {
                viewTeacherLevel.setProgress(level);
                tvTeacherLevel.setText("T" + level);
            }
            Integer teachAge = teacher.getTeaching_age();
            if (teachAge != null) {
                viewTeacherSeniority.setProgress(teachAge);
                tvTeacherSeniority.setText(teachAge.toString() + "年");
            }
            if (mTeacher.isFavorite()) {
                likeButton.setLiked(true);
                setCollectionStatus(true);
            } else {
                setCollectionStatus(false);
            }

            if (mTeacher.isPublished()) {
                tvSignUp.setEnabled(true);
            } else {
                tvSignUp.setEnabled(false);
                tvSignUp.setText("已下架");
            }

        }
    }

    private void setGradeTeaching(String[] grades) {
        //数据处理
        int count = 0;
        List<List<String>> gradeList = Grade.getGradesByGroup(grades);

        if (gradeList != null && gradeList.get(0) != null && gradeList.get(0).size() > 0) {
            setFlowDatas(flTeachPrimary, gradeList.get(0).toArray(new String[gradeList.get(0).size()]),
                    R.drawable.bg_text_primary, R.color.color_red_e25c5c);
        } else {
            rlTeachPrimary.setVisibility(View.GONE);
            count++;
        }

        if (gradeList != null && gradeList.get(1) != null && gradeList.get(1).size() > 0) {
            setFlowDatas(flTeachJunior, gradeList.get(1).toArray(new String[gradeList.get(1).size()]),
                    R.drawable.bg_text_junior, R.color.color_blue_2b7bb4);
        } else {
            rlTeachJunior.setVisibility(View.GONE);
            count++;
        }

        if (gradeList != null && gradeList.get(2) != null && gradeList.get(2).size() > 0) {
            setFlowDatas(flTeachSenior, gradeList.get(2).toArray(new String[gradeList.get(2).size()]),
                    R.drawable.bg_text_senior, R.color.color_green_259746);
        } else {
            rlTeachSenior.setVisibility(View.GONE);
            count++;
        }

        if (count <= 1) {
            viewPrimaryLine.setVisibility(View.GONE);
            viewJuniorLine.setVisibility(View.GONE);
            return;
        } else if (count == 2) {
            if (gradeList.get(0).size() > 0) {
                viewJuniorLine.setVisibility(View.GONE);
                return;
            } else if (gradeList.get(2).size() > 0) {
                viewPrimaryLine.setVisibility(View.GONE);
                return;
            }
        }
    }


    private void setFlowCertDatas(FlowLayout flowlayout, final List<Achievement> datas, int drawable) {
        flowlayout.setFocusable(false);
        flowlayout.removeAllViews();

        for (int i = 0; datas != null && i < datas.size(); i++) {
            TextView textView = buildCertTextView(datas.get(i).getTitle());
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] imgUrl = new String[datas.size()];
                    String[] imgDes = new String[datas.size()];

                    for (int i = 0; i < datas.size(); i++) {
                        imgUrl[i] = datas.get(i).getImg();
                        imgDes[i] = datas.get(i).getTitle();
                    }
                    Intent intent = new Intent(getContext(), GalleryActivity.class);
                    intent.putExtra(GalleryActivity.GALLERY_URLS, imgUrl);
                    intent.putExtra(GalleryActivity.GALLERY_DES, imgDes);
                    intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, finalI);
                    startActivity(intent);
                    StatReporter.specialCertPage();
                }
            });
            flowlayout.addView(textView, i);
        }
    }

    private TextView buildCertTextView(String title) {
        TextView textView = new TextView(getContext());

        int leftPadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_left_padding);
        int rightPadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_right_padding);
        int margin = getResources().getDimensionPixelSize(R.dimen.flow_textview_left_margin);
        int height = getResources().getDimensionPixelSize(R.dimen.flow_textview_height);
        int drawablePadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_spacing);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, height);

        layoutParams.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(layoutParams);
        int topPadding = textView.getPaddingTop();
        int bottomPadding = textView.getPaddingBottom();
        textView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_certificate_icon);
        // 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null, null, drawable, null);
        textView.setCompoundDrawablePadding(drawablePadding);
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(getResources().getColor(R.color.color_red_ef8f1d));
        textView.setBackground(getResources().getDrawable(R.drawable.item_text_bg));
        return textView;
    }

    private void setFlowDatas(FlowLayout flowlayout, String[] datas, int bgDrawableId, int colorId) {
        flowlayout.setFocusable(false);
        flowlayout.removeAllViews();
        for (int i = 0; datas != null && i < datas.length; i++) {
            TextView textView = buildFlowTextView(datas[i], bgDrawableId);
            textView.setTextColor(getResources().getColor(colorId));
            flowlayout.addView(textView, i);
        }
    }

    private TextView buildFlowTextView(String data, int bgDrawableId) {
        TextView textView = new TextView(getContext());
        int leftPadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_left_padding);
        int rightPadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_right_padding);
        int margin = getResources().getDimensionPixelSize(R.dimen.flow_textview_left_margin);
        int height = getResources().getDimensionPixelSize(R.dimen.flow_textview_height);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, height);

        layoutParams.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(layoutParams);
        int topPadding = textView.getPaddingTop();
        int bottomPadding = textView.getPaddingBottom();
        textView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

        textView.setText(data);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(getResources().getColor(R.color.color_red_ef8f1d));
        textView.setBackground(getResources().getDrawable(bgDrawableId));
        return textView;
    }


    void loadGallery(String[] gallery) {
        if (gallery == null || gallery.length <= 0) {
            hsGallery.setVisibility(View.GONE);
            return;
        }
        galleryAdapter.addAll(Arrays.asList(gallery));
        gvGallery.setFocusable(true);
        MeasureGallery(gvGallery, galleryAdapter);
        gvGallery.setVerticalScrollBarEnabled(true);
        gvGallery.setAdapter(galleryAdapter);
    }

    private void MeasureGallery(GridView gvGallery, GalleryAdapter galleryAdapter) {

        int childCount = galleryAdapter.getCount();
        int galleryWidth = getResources().getDimensionPixelSize(R.dimen.grallery_width);
        int galleryHeight = getResources().getDimensionPixelSize(R.dimen.grallery_height);
        int galleryHorizontalSpacing = getResources().getDimensionPixelSize(R.dimen.grallery_horizontal_spacing);
        int gridViewWidth = 0;
        if (childCount > 0) {
            gridViewWidth = (galleryWidth + galleryHorizontalSpacing) * childCount - galleryHorizontalSpacing;
            if (childCount == 1) {
                gridViewWidth -= galleryHorizontalSpacing;
            }
        }
        ViewGroup.LayoutParams params = gvGallery.getLayoutParams();
        params.width = gridViewWidth;
        params.height = galleryHeight;
        gvGallery.setLayoutParams(params);   //重点
        gvGallery.setStretchMode(GridView.NO_STRETCH);
        gvGallery.setNumColumns(childCount);   //重点
    }

    private void requestError() {
        Toast.makeText(getContext(), "网络请求失败!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parent_teacher_signup_btn:
                //
                if (mTeacher.isPublished()) {
                    signUp();
                } else {
                    MiscUtil.toast("该老师已经下架!");
                }
                break;
            case R.id.tv_gallery_more:
                //查看更多照片
                Intent intent = new Intent(getContext(), GalleryPreviewActivity.class);
                intent.putExtra(GalleryPreviewActivity.GALLERY_URLS, mTeacher.getPhoto_set());
                startActivity(intent);
                break;
            case R.id.ll_school_more:
                //显示更多教学中心
                changeSchoolsShow();
                StatReporter.moreSchool();
                break;
            case R.id.parent_teacher_detail_head_portrait:
                onClickTeacherAvatar();
                break;
        }
    }

    private void onClickTeacherAvatar() {
        if (mTeacher != null && !EmptyUtils.isEmpty(mTeacher.getAvatar())) {
            Intent intent = new Intent(getContext(), GalleryActivity.class);
            intent.putExtra(GalleryActivity.GALLERY_URLS, new String[]{mTeacher.getAvatar()});
            startActivity(intent);
        }
    }

    private void onCollection() {
        if (UserManager.getInstance().isLogin()) {
            if (mTeacher.isFavorite()) {
                onCancelCollectTeacher(mTeacher.getId());
                setCollectionStatus(false);
            } else {
                onCollectTeacher(mTeacher.getId());
                setCollectionStatus(true);
            }
        } else {
            //跳转登录页
            startSmsActivity();
            setCollectionStatus(false);
        }
    }

    void setCollectionStatus(boolean isCollect) {
        if (isCollect) {
            tvCollection.setText("已收藏");
        } else {
            likeButton.setLiked(false);
            tvCollection.setText("收藏");
        }
    }

    //收藏教师
    private void onCollectTeacher(Long id) {
        ApiExecutor.exec(new TeacherInfoFragment.CollectTeacherRequest(this, mTeacher.getId()));

    }

    //取消收藏老师
    private void onCancelCollectTeacher(Long id) {
        ApiExecutor.exec(new TeacherInfoFragment.CancelCollectTeacherRequest(this, mTeacher.getId()));
    }

    private void changeSchoolsShow() {

        if (!isShowAllSchools) {
            ivSchoolMore.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_up));
            tvSchoolMore.setText("收起");
            mSchoolAdapter.clear();
            mSchoolAdapter.addAll(mAllSchools);
            mSchoolAdapter.notifyDataSetChanged();
        } else {
            if (mAllSchools == null || mAllSchools.size() <= 0) {
                return;
            }
            Double dis = mAllSchools.get(0).getDistance();
            if (dis != null && dis >= 0) {
                tvSchoolMore.setText(String.format("离您最近的社区中心 (%s)", LocationUtil.formatDistance(dis)));
            } else {
                tvSchoolMore.setText("其他社区中心");
            }
            ivSchoolMore.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_down));
            mSchoolAdapter.clear();
            mSchoolAdapter.addAll(mFirstSchool);
            mSchoolAdapter.notifyDataSetChanged();
        }
        isShowAllSchools = !isShowAllSchools;
    }

    private void signUp() {
        StatReporter.soonRoll();
        //判断是否登录
        if (UserManager.getInstance().isLogin()) {
            //跳转至报名页
            startCourseConfirmActivity();
        } else {
            //跳转登录页
            startSmsActivityRes();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == LoginActivity.RESULT_CODE_LOGIN_SUCCESS) {
                //跳转到课程购买页
                startCourseConfirmActivity();
            }
        }
    }

    private void showWxShare() {
        if (mTeacher==null) return;
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setTitle("我在麻辣老师发现了一位好老师!");
        StringBuilder strText = new StringBuilder(mTeacher.getName()+","+mTeacher.getSubject()+"老师");
        for (int i=0;mTeacher.getTags()!=null&&i<mTeacher.getTags().length;i++){
            strText.append(","+mTeacher.getTags()[i]);
        }
        strText.append("!");
        oks.setText(strText.toString());   //短语说明
        oks.setImageUrl(mTeacher.getAvatar());  //教师头像
        String host = String.format(getString(R.string.api_host)+"/wechat/teacher/?teacherid=%d",mTeacher.getId());
        oks.setUrl(host);        //教师url
        // 启动分享GUI
        oks.show(getContext());
    }

    //启动登录页
    private void startSmsActivityRes() {
        Intent intent = new Intent();
        intent.setClass(getContext(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    private void startSmsActivity() {
        Intent intent = new Intent();
        intent.setClass(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    //启动购买课程页
    private void startCourseConfirmActivity() {
        Subject subject = Subject.getSubjectIdByName(mTeacher.getSubject());
        if (mTeacher != null && mTeacher.getId() != null && subject != null) {
            Long schoolId = UserManager.getInstance().getSchoolId();
            CourseConfirmActivity.launch(getContext(), mTeacher.getId(), mTeacher.getName(), mTeacher.getAvatar(), subject,schoolId);
        }
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {
        //最大上滑距离
        int maxOffset = headerImage.getMeasuredHeight() - titleBarView.getMeasuredHeight();
        //开始变色位置
        int startOffset = maxOffset / 2;
        if (y > startOffset && y < maxOffset - 10) {  //开始变色
            int ratio = (int) (255 * ((float) (y - startOffset) / (float) (maxOffset - startOffset + 10)));
            titleBarView.setLeftImageDrawable(getResources().getDrawable(R.drawable.core__back_btn));
            titleBarView.setRightBackgroundResource(R.drawable.bitmap_share_black);
            titleBarView.setBackgroundColor(Color.argb(ratio, 255, 255, 255));
            viewLine.setAlpha(0);
            titleBarView.setTitle("");
        } else if (y >= maxOffset - 10) {        //白色背景
            titleBarView.setLeftImageDrawable(getResources().getDrawable(R.drawable.core__back_btn));
            titleBarView.setRightBackgroundResource(R.drawable.bitmap_share_black);
            titleBarView.setBackgroundColor(Color.argb(255, 255, 255, 255));
            viewLine.setAlpha(1);
            if (mTeacher != null) {
                titleBarView.setTitle(mTeacher.getName());
            }
        } else {                            //无背景
            titleBarView.setLeftImageDrawable(getResources().getDrawable(R.drawable.core__white_btn));
            titleBarView.setRightBackgroundResource(R.drawable.bitmap_share_white);
            titleBarView.setBackgroundColor(Color.argb(0, 255, 255, 255));
            viewLine.setAlpha(0);
            titleBarView.setTitle("");
        }
    }

    @Override
    public void onTitleLeftClick() {
        getActivity().finish();
    }

    @Override
    public void onTitleRightClick() {
        showWxShare();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //查看更多照片
        Intent intent = new Intent(getContext(), GalleryActivity.class);
        intent.putExtra(GalleryActivity.GALLERY_URLS, mTeacher.getPhoto_set());
        intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, position);
        startActivity(intent);
    }


    private void onCollectFailed() {
        MiscUtil.toast("收藏失败");
        setCollectionStatus(false);
    }

    private void onCollectSuccess(DoneModel response) {
        if (response != null && response.getTeacher() == mTeacher.getId()) {
            mTeacher.setFavorite(true);
            return;
        }
        onCollectFailed();
    }

    private void onCancelCollcetFailed() {
        MiscUtil.toast("取消失败");
        setCollectionStatus(true);
    }

    private void onCancelCollcetSuccess(DoneModel response) {
        if (response != null && response.isOk()) {
            mTeacher.setFavorite(false);
            return;
        }
        onCancelCollcetFailed();
    }

    @Override
    public void liked(LikeButton likeButton) {
        onCollection();
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        onCollection();
    }


    private static final class LoadTeacherInfoRequest extends BaseApiContext<TeacherInfoFragment, Teacher> {

        private long teacherId;

        public LoadTeacherInfoRequest(TeacherInfoFragment teacherInfoFragment, long teacherId) {
            super(teacherInfoFragment);
            this.teacherId = teacherId;
        }

        @Override
        public Teacher request() throws Exception {
            return new TeacherInfoApi().get(teacherId);
        }

        @Override
        public void onApiSuccess(@NonNull Teacher response) {
            get().loadTeacherInfoSuccess(response);
        }

        @Override
        public void onApiFinished() {
            get().teacherInfoFlag = true;
            get().stopProcess();
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().requestError();
        }
    }

    private static final class LoadSchoolListRequest extends BaseApiContext<TeacherInfoFragment, SchoolListResult> {

        private long teacherId;
        public LoadSchoolListRequest(TeacherInfoFragment teacherInfoFragment,long teacherId) {
            super(teacherInfoFragment);
            this.teacherId = teacherId;
        }

        @Override
        public SchoolListResult request() throws Exception {
            return new SchoolListApi().get(teacherId);
        }

        @Override
        public void onApiSuccess(@NonNull SchoolListResult response) {
            if (response.getResults() != null) {
                get().loadSchoolListSuccess(response);
            }
        }

        @Override
        public void onApiFinished() {
            get().schoolFlag = true;
            get().stopProcess();
        }

    }

    private static final class CollectTeacherRequest extends BaseApiContext<TeacherInfoFragment, DoneModel> {
        private Long id;

        public CollectTeacherRequest(TeacherInfoFragment teacherInfoFragment, Long id) {
            super(teacherInfoFragment);
            this.id = id;
        }

        @Override
        public DoneModel request() throws Exception {
            return new CollectTeacherApi().post(id);
        }

        @Override
        public void onApiStarted() {
            super.onApiStarted();
            get().tvCollection.setOnClickListener(null);
        }

        @Override
        public void onApiSuccess(@NonNull DoneModel response) {
            get().onCollectSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            super.onApiFailure(exception);
            get().onCollectFailed();
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().tvCollection.setOnClickListener(get());
        }
    }

    private static final class CancelCollectTeacherRequest extends BaseApiContext<TeacherInfoFragment, DoneModel> {
        private Long id;

        public CancelCollectTeacherRequest(TeacherInfoFragment teacherInfoFragment, Long id) {
            super(teacherInfoFragment);
            this.id = id;
        }

        @Override
        public DoneModel request() throws Exception {
            return new CancelCollectTeacherApi().delete(id);
        }

        @Override
        public void onApiStarted() {
            super.onApiStarted();
            get().likeButton.setOnLikeListener(null);
        }

        @Override
        public void onApiSuccess(@NonNull DoneModel response) {
            get().onCancelCollcetSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            super.onApiFailure(exception);
            get().onCancelCollcetFailed();
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().likeButton.setOnLikeListener(get());
        }
    }

    @Override
    public String getStatName() {
        return "老师详情页";
    }
}
