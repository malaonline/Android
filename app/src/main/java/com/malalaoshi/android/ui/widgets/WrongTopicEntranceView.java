package com.malalaoshi.android.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapters.TopicSubjectsAdapter;
import com.malalaoshi.android.core.usercenter.LoginActivity;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.entity.LayoutStatusEnum;
import com.malalaoshi.android.entity.TopicSubject;
import com.malalaoshi.android.listener.EntranceClickListener;
import com.malalaoshi.android.ui.decoration.DividerItemDecoration;

import java.util.List;

import butterknife.ButterKnife;

import static com.malalaoshi.android.entity.LayoutStatusEnum.EMPTY;
import static com.malalaoshi.android.entity.LayoutStatusEnum.ERROR;
import static com.malalaoshi.android.entity.LayoutStatusEnum.LOADING;
import static com.malalaoshi.android.entity.LayoutStatusEnum.LOGOUT;
import static com.malalaoshi.android.entity.LayoutStatusEnum.NORMAL;

/**
 * 错题本入口界面
 * Created by donald on 2017/5/8.
 */

public class WrongTopicEntranceView extends FrameLayout implements View.OnClickListener {

    private View mErrorView;
    private View mEntranceView;
    private TextView mTvTopicPrompt;
    private TextView mTvTopicSubmit;
    private TextView mTvTopicStudentName;
    private TextView mTvTopicPoint;
    private RecyclerView mRvTopicSubjects;
    private LayoutStatusEnum mCurrentStatus = LOGOUT;
//    private TextView mTvTopicErrorPoint;
    private TopicSubjectsAdapter mAdapter;
    private EntranceClickListener mListener;
    private ProgressBar mPbRetryLoading;
    private Context mContext;
    private LinearLayout mLlTopicSubmit;
    //    private TextView mTvTopicPoint1;

    public WrongTopicEntranceView(@NonNull Context context) {
        super(context);
        mContext = context;
        init(mContext);
    }

    public WrongTopicEntranceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public WrongTopicEntranceView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.layout_wrong_topic_entrance, this);
        mErrorView = ButterKnife.findById(view,R.id.ll_wrong_topic_error_rooter);
        mEntranceView = ButterKnife.findById(view, R.id.ll_wrong_topic_entrance_rooter);
        mTvTopicPrompt = (TextView) view.findViewById(R.id.tv_topic_prompt);
        mTvTopicSubmit = (TextView) view.findViewById(R.id.tv_topic_submit);
        mPbRetryLoading = (ProgressBar) view.findViewById(R.id.pb_retry_loading);

        mTvTopicStudentName = (TextView) view.findViewById(R.id.tv_topic_student_name);
        mRvTopicSubjects = (RecyclerView) view.findViewById(R.id.rv_topic_subjects);

        mTvTopicPoint = ButterKnife.findById(view, R.id.tv_wrong_topic_point);
        mLlTopicSubmit = ButterKnife.findById(view, R.id.ll_topic_submit);

        mRvTopicSubjects.setLayoutManager(new FullyGridLayoutManager(context, 2));
        mAdapter = new TopicSubjectsAdapter(context);
        mRvTopicSubjects.setAdapter(mAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(context, LinearLayoutManager.VERTICAL, 1, MiscUtil.getColor(R.color.color_gray_979797));
        divider.setMargin(0, MiscUtil.dp2px(25), 0, MiscUtil.dp2px(75));
        mRvTopicSubjects.addItemDecoration(divider);
        setEvent();

    }

    private void setEvent() {
        mLlTopicSubmit.setOnClickListener(this);
        mTvTopicPoint.setOnClickListener(this);
    }

    public void setLayout(LayoutStatusEnum status){
        mCurrentStatus = status;
        switch (status){
            case LOGOUT:
                mTvTopicPrompt.setText(R.string.login_to_look_topic_report);
                mTvTopicSubmit.setText(R.string.login_immeditely);
                mTvTopicPoint.setVisibility(GONE);
                setErrorStatus();
                break;
            case EMPTY:
                mTvTopicPrompt.setText(R.string.your_topic_will_show_here);
                mTvTopicSubmit.setText(R.string.look_over_the_sample_of_topic);
                setErrorStatus();
                mTvTopicPoint.setVisibility(VISIBLE);
                break;
            case ERROR:
                mTvTopicPrompt.setText(R.string.wrong_topics_fetch_fail);
                mTvTopicSubmit.setText(R.string.retry_fetch);
                mTvTopicPrompt.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_wrong_topic_error),null, null);
                mPbRetryLoading.setVisibility(GONE);
                mEntranceView.setVisibility(GONE);
                mErrorView.setVisibility(VISIBLE);
                mTvTopicSubmit.setEnabled(true);
                mTvTopicPoint.setVisibility(VISIBLE);
                break;
            case LOADING:
                mTvTopicPrompt.setText(R.string.wrong_topics_fetch_fail);
                mTvTopicSubmit.setText(R.string.loading);
                mTvTopicSubmit.setEnabled(false);
                mPbRetryLoading.setVisibility(VISIBLE);
                break;
            case NORMAL:
                mEntranceView.setVisibility(VISIBLE);
                mErrorView.setVisibility(GONE);
                mTvTopicPrompt.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_wrong_topic),null, null);
                mTvTopicPoint.setVisibility(VISIBLE);
                break;
        }
    }
    public void setErrorStatus(){
        mTvTopicSubmit.setEnabled(true);
        mPbRetryLoading.setVisibility(GONE);
        mEntranceView.setVisibility(GONE);
        mErrorView.setVisibility(VISIBLE);
        mTvTopicPrompt.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_wrong_topic),null, null);
    }

    public void setSubjects(List<TopicSubject> subjects){
        if (subjects == null || subjects.size() == 0){
            setLayout(EMPTY);
        }else {
            setLayout(NORMAL);
            mAdapter.resetData(subjects);
        }
    }
    public void setClickListener(EntranceClickListener listener){
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) return;
        switch (v.getId()){
            case R.id.tv_wrong_topic_point:
                mListener.showPoint();
                break;
            case R.id.ll_topic_submit:
                if (mCurrentStatus == EMPTY){
                    mListener.lookSample();
                }else if (mCurrentStatus == ERROR){
                    setLayout(LOADING);
                    mListener.retry();
                }else if (mCurrentStatus == LOGOUT){
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
        }
    }
    public void setStudent(String student){
        mTvTopicStudentName.setText(student);
    }
}
