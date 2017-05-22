package com.malalaoshi.android.ui.widgets;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.entity.LayoutStatusEnum;
import com.malalaoshi.android.entity.Report;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.listener.EntranceClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.malalaoshi.android.entity.LayoutStatusEnum.EMPTY;
import static com.malalaoshi.android.entity.LayoutStatusEnum.ERROR;
import static com.malalaoshi.android.entity.LayoutStatusEnum.LOADING;
import static com.malalaoshi.android.entity.LayoutStatusEnum.NORMAL;

/**
 * Created by donald on 2017/5/12.
 */

public class LearningReportEntranceView extends FrameLayout implements View.OnClickListener {

    TextView mTvReportPrompt;
    TextView mTvReportSubmit;
    @Bind(R.id.tv_answer_number)
    TextView mTvAnswerNumber;
    @Bind(R.id.tv_correct_rate)
    TextView mTvCorrectRate;
    @Bind(R.id.ll_report)
    LinearLayout mLlReport;
    @Bind(R.id.tv_open_learning_report)
    TextView mTvOpenLearningReport;
    @Bind(R.id.tv_subject)
    TextView mTvSubject;
    private View mReportErrorView;
    private View mReportEntranceView;
    private LayoutStatusEnum mCurrentStatus = NORMAL;
    private EntranceClickListener mListener;
    private ProgressBar mPbRetryLoading;

    public LearningReportEntranceView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LearningReportEntranceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LearningReportEntranceView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LayoutParams layoutParams = (LayoutParams) mReportErrorView.getLayoutParams();
        layoutParams.topMargin = MiscUtil.dp2px(15);
        mReportErrorView.setLayoutParams(layoutParams);
    }

    private void init(Context context) {
        mReportErrorView = View.inflate(context, R.layout.view_wrong_topic_error, null);
        mReportEntranceView = View.inflate(context, R.layout.view_learning_report_entrance, null);
        mTvReportPrompt = ButterKnife.findById(mReportErrorView, R.id.tv_topic_prompt);
        mTvReportSubmit = ButterKnife.findById(mReportErrorView, R.id.tv_topic_submit);
        mPbRetryLoading = ButterKnife.findById(mReportErrorView, R.id.pb_retry_loading);
        ButterKnife.findById(mReportErrorView, R.id.ll_topic_submit).setOnClickListener(this);
        ButterKnife.findById(mReportEntranceView, R.id.tv_open_learning_report).setOnClickListener(this);
        mTvReportPrompt.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_leaning_report_icon), null, null);
        addView(mReportEntranceView);
        addView(mReportErrorView);
    }

    public void setLayout(LayoutStatusEnum status) {
        mCurrentStatus = status;

        switch (status) {
            case LOGOUT:
                setVisibility(GONE);
                break;
            case EMPTY:
                setVisibility(VISIBLE);
                mTvReportPrompt.setText(R.string.report_only_support_math);
                mTvReportSubmit.setText(R.string.look_the_report_samples);
                mTvReportPrompt.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_leaning_report_icon), null, null);
                mReportEntranceView.setVisibility(GONE);
                mReportErrorView.setVisibility(VISIBLE);
                mPbRetryLoading.setVisibility(GONE);
                mTvReportSubmit.setEnabled(true);
                break;
            case ERROR:
                setVisibility(VISIBLE);
                mTvReportPrompt.setText(R.string.learning_report_fetch_fail);
                mTvReportSubmit.setText(R.string.retry_fetch);
                mTvReportPrompt.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_leaning_report_icon_error), null, null);
                mReportEntranceView.setVisibility(GONE);
                mReportErrorView.setVisibility(VISIBLE);
                mPbRetryLoading.setVisibility(GONE);
                mTvReportSubmit.setEnabled(true);
                break;
            case LOADING:
                mTvReportSubmit.setText(R.string.loading);
                mPbRetryLoading.setVisibility(VISIBLE);
                mTvReportSubmit.setEnabled(false);
                break;
            case NORMAL:
                setVisibility(VISIBLE);
                mTvReportPrompt.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_leaning_report_icon), null, null);
                mReportEntranceView.setVisibility(VISIBLE);
                mReportErrorView.setVisibility(GONE);
                break;
        }
    }

    public void setClickListener(EntranceClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) return;
        switch (v.getId()){
            case R.id.tv_open_learning_report:
                mListener.lookSample();
                break;
            case R.id.ll_topic_submit:
                if (mCurrentStatus == EMPTY) {
                    mListener.lookSample();
                } else if (mCurrentStatus == ERROR) {
                    setLayout(LOADING);
                    mListener.retry();
                }
                break;
        }

    }

    public LayoutStatusEnum getCurrentStatus() {
        return mCurrentStatus;
    }

    public void setReport(Report report) {
        Subject subject = Subject.getSubjectById(report.getSubject_id());
        if (subject != null) {
            mTvSubject.setText(subject.getName());
        } else {
            mTvSubject.setText("");
            mTvAnswerNumber.setText(report.getTotal_nums() + "");
            int rate = 0;
            if (report.getTotal_nums() > 0) {
                rate = report.getRight_nums() * 100 / report.getTotal_nums();
            }
            mTvCorrectRate.setText(rate + "%");
        }
    }
}
