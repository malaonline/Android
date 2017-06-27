package com.malalaoshi.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.MemberActivity;
import com.malalaoshi.android.activitys.WrongTopicDetailActivity;
import com.malalaoshi.android.adapters.MemberServiceAdapter;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.base.OnItemClickListener;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.core.view.RefreshHeaderEffectView;
import com.malalaoshi.android.core.view.ShadowHelper;
import com.malalaoshi.android.entity.LayoutStatusEnum;
import com.malalaoshi.android.entity.MemberService;
import com.malalaoshi.android.entity.Report;
import com.malalaoshi.android.entity.TopicSubject;
import com.malalaoshi.android.entity.WrongTopic;
import com.malalaoshi.android.entity.WrongTopicSamples;
import com.malalaoshi.android.listener.EntranceClickListener;
import com.malalaoshi.android.network.api.LearningReportApi;
import com.malalaoshi.android.network.api.WrongTopicApi;
import com.malalaoshi.android.network.result.ReportListResult;
import com.malalaoshi.android.network.result.WrongTopicResult;
import com.malalaoshi.android.report.ReportActivity;
import com.malalaoshi.android.ui.dialogs.TopicPointDialg;
import com.malalaoshi.android.ui.widgets.FullyGridLayoutManager;
import com.malalaoshi.android.ui.widgets.LearningReportEntranceView;
import com.malalaoshi.android.ui.widgets.WrongTopicEntranceView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 新版会员专享页面
 * Created by donald on 2017/5/12.
 */

public class MemberExclusiveServiceFragment extends BaseFragment {

    @Bind(R.id.wtev_wrong_topic)
    WrongTopicEntranceView mWtevWrongTopic;
    @Bind(R.id.lrev_learning_report)
    LearningReportEntranceView mLrevLearningReport;
    @Bind(R.id.ll_member_parent)
    LinearLayout mLlMemberParent;
    @Bind(R.id.ptr_member_refresh)
    PtrClassicFrameLayout mPtrMemberRefresh;
    @Bind(R.id.rv_member_service_content)
    RecyclerView mRvMemberServiceContent;
    @Bind(R.id.ll_member_service_parent)
    LinearLayout mLlMemberServiceParent;
    private MemberServiceAdapter mServiceAdapter;
    private ArrayList<MemberService> mServices;
    private boolean isTopicFinished = false; //错题本是否请求成功。 false 否
    private boolean isReportFinished = false;//学习报告是否请求完成，false 否
    private FetchTopicRequest mFetchTopicRequest;
    private FetchReportRequest mFetchReportRequest;
    private boolean isShowToast = false;
    private WrongTopicResult.ExerciseMistakesBean mMistakes = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_exclusive_service, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        loadData(view);
        initEvent();
        return view;

    }

    private void loadData(View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPtrMemberRefresh != null)
                    mPtrMemberRefresh.autoRefresh();
            }
        }, 100);
        mFetchTopicRequest = new FetchTopicRequest(this, 0);
        mFetchReportRequest = new FetchReportRequest(this, 1);
    }

    private void initMemberService() {
        String[] titles = getResources().getStringArray(R.array.member_services_item);
        int[] iconIds = new int[]{R.drawable.ic_with_read, R.drawable.ic_learning_report, R.drawable.ic_counseling, R.drawable.ic_lectures,
                R.drawable.ic_exam_explain, R.drawable.ic_mistake, R.drawable.ic_spps_evaluation, R.drawable.ic_expect_more};
        mServices = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            mServices.add(new MemberService(titles[i], iconIds[i]));
        }
        mServiceAdapter.addData(mServices);
    }

    private void initEvent() {
        mPtrMemberRefresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                request();
            }
        });
        mWtevWrongTopic.setClickListener(new EntranceClickListener() {
            @Override
            public void showPoint() {
                new TopicPointDialg().show(getFragmentManager(), "TopicPointDialg");
            }

            @Override
            public void retry() {
                isShowToast = true;
                ApiExecutor.exec(mFetchTopicRequest);
            }

            @Override
            public void lookSample() {
                List<WrongTopic> wrongTopics = new ArrayList<>();
                WrongTopicSamples.getSamples(wrongTopics);
                WrongTopicDetailActivity.launch(mContext, wrongTopics.size(), 0, wrongTopics, -1);
            }
        });
        mLrevLearningReport.setClickListener(new EntranceClickListener() {
            @Override
            public void showPoint() {

            }

            @Override
            public void retry() {
                isShowToast = true;
                ApiExecutor.exec(mFetchReportRequest);
            }

            @Override
            public void lookSample() {
                ReportActivity.launch(getActivity(), -1);
            }
        });
        mServiceAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(Object o, int position) {
                Intent intent = new Intent(mContext, MemberActivity.class);
                intent.putExtra(MemberActivity.EXTRA_CURRETN_POSITION, position);
                startActivity(intent);
            }
        });
    }

    private void request() {
        if (!UserManager.getInstance().isLogin()) {
            mPtrMemberRefresh.refreshComplete();
            mWtevWrongTopic.setLayout(LayoutStatusEnum.LOGOUT);
            mLrevLearningReport.setLayout(LayoutStatusEnum.LOGOUT);
        } else {
            setLayout(LayoutStatusEnum.LOADING);
            isReportFinished = false;
            isTopicFinished = false;
            isShowToast = false;
            ApiExecutor.exec(mFetchTopicRequest);
            ApiExecutor.exec(mFetchReportRequest);
        }
    }

    private void initView() {
        RefreshHeaderEffectView headerEffectView = new RefreshHeaderEffectView(mContext);
        mPtrMemberRefresh.setHeaderView(headerEffectView);
        mPtrMemberRefresh.addPtrUIHandler(headerEffectView);
        mPtrMemberRefresh.setPullToRefresh(false);
        mPtrMemberRefresh.setKeepHeaderWhenRefresh(true);

        //设置阴影背景
        ShadowHelper.setDrawShadow(mContext, mWtevWrongTopic);
        ShadowHelper.setDrawShadow(mContext, mLrevLearningReport);
        ShadowHelper.setDrawShadow(mContext, mLlMemberServiceParent);

        FullyGridLayoutManager gridLayoutManager = new FullyGridLayoutManager(mContext, 4);
        mRvMemberServiceContent.setLayoutManager(gridLayoutManager);
        mServiceAdapter = new MemberServiceAdapter(mContext);
        mRvMemberServiceContent.setAdapter(mServiceAdapter);
        initMemberService();
    }

    public void onEventMainThread(BusEvent event) {
        int eventType = event.getEventType();
        if (eventType == BusEvent.BUS_EVENT_LOGIN_SUCCESS) {
            mLrevLearningReport.setVisibility(View.VISIBLE);
        }
        switch (eventType) {
            case BusEvent.BUS_EVENT_LOGOUT_SUCCESS:
                mPtrMemberRefresh.refreshComplete();
                mWtevWrongTopic.setLayout(LayoutStatusEnum.LOGOUT);
                mLrevLearningReport.setLayout(LayoutStatusEnum.LOGOUT);
                mMistakes = null;
                break;
            case BusEvent.BUS_EVENT_LOGIN_SUCCESS:
            case BusEvent.BUS_EVENT_PAY_SUCCESS:
                request();
                break;
            case BusEvent.BUS_EVENT_BACKGROUND_LOAD_REPORT_DATA://跳转到当前页面重新刷新数据
                loadDataBackground();
                break;
        }
    }

    private void loadDataBackground() {
        if (!UserManager.getInstance().isLogin()) {
            setLayout(LayoutStatusEnum.LOGOUT);
        } else {
            ApiExecutor.exec(new FetchReportRequest(this, 0));
            ApiExecutor.exec(new FetchTopicRequest(this, 0));
        }
    }

    private void setLayout(LayoutStatusEnum status) {
        mWtevWrongTopic.setLayout(status);
        mLrevLearningReport.setLayout(status);
    }

    private void showReportView(Report report) {
        mLrevLearningReport.setLayout(LayoutStatusEnum.NORMAL);
        mLrevLearningReport.setReport(report);

    }

    private void showTopicView(ArrayList<TopicSubject> topicSubject) {
        mWtevWrongTopic.setSubjects(topicSubject);
    }

    //------------网络请求-----------
    private static final class FetchReportRequest extends BaseApiContext<MemberExclusiveServiceFragment, ReportListResult> {

        private final int mRequestType;

        public FetchReportRequest(MemberExclusiveServiceFragment memberExclusiveServiceFragment, int requestType) {
            super(memberExclusiveServiceFragment);
            mRequestType = requestType;
        }

        @Override
        public ReportListResult request() throws Exception {
            return new LearningReportApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull ReportListResult response) {
            get().dealReport(null);
        }

        @Override
        public void onApiFailure(Exception exception) {
            if (mRequestType != 1) {
                get().mLrevLearningReport.setLayout(LayoutStatusEnum.NORMAL);
            } else {
                get().mLrevLearningReport.setLayout(LayoutStatusEnum.EMPTY);
            }
            if (get().isShowToast) {
                MiscUtil.toast(R.string.network_error);
            }
        }

        @Override
        public void onApiFinished() {
            get().isReportFinished = true;
            if (get().isTopicFinished) {
                get().mPtrMemberRefresh.refreshComplete();
            }
        }
    }

    private static final class FetchTopicRequest extends BaseApiContext<MemberExclusiveServiceFragment, WrongTopicResult> {

        private final int mRequestType;

        public FetchTopicRequest(MemberExclusiveServiceFragment memberExclusiveServiceFragment, int requestType) {
            super(memberExclusiveServiceFragment);
            mRequestType = requestType;
        }

        @Override
        public WrongTopicResult request() throws Exception {
            return new WrongTopicApi().getSubject();
        }

        @Override
        public void onApiSuccess(@NonNull WrongTopicResult response) {
            get().dealTopic(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().dealTopic(null);

        }

        @Override
        public void onApiFinished() {
            get().isTopicFinished = true;
            if (get().isReportFinished) {
                get().mPtrMemberRefresh.refreshComplete();
            }
        }
    }

    private void dealTopic(WrongTopicResult response) {
        if (response == null) {
            if (mMistakes == null) {
                mWtevWrongTopic.setLayout(LayoutStatusEnum.ERROR);
            } else {
                if (isResumed()){
                    MiscUtil.toast(R.string.network_error);
                }
            }
            return;
        }
        WrongTopicResult.ExerciseMistakesBean latestMistakes = response.getExercise_mistakes();
        if (latestMistakes != null) {
            if (mMistakes != null) {
                int total = mMistakes.getNumbers().getTotal();
                int latestTotal = latestMistakes.getNumbers().getTotal();
                if (latestTotal > total && isResumed() && getUserVisibleHint()) {
                    MiscUtil.toast("新增" + (latestTotal - total) + "题");
                }
            }
            mMistakes = latestMistakes;
            setupTopic(mMistakes);
        } else {
            if (mMistakes == null) {
                mWtevWrongTopic.setLayout(LayoutStatusEnum.ERROR);
            }
        }
    }

    private void setupTopic(WrongTopicResult.ExerciseMistakesBean mistakes) {
        String school = mistakes.getSchool();
        int length = school.length();
        if (length > 7) {
            school = school.substring(0, 5) + "...校区";
        } else if (!school.endsWith("校区")) {
            if (length > 5) {
                school = school.substring(0, 5) + "...校区";
            } else {
                school = school + "校区";
            }
        }
        mWtevWrongTopic.setStudent("Hi," + school + " " + mistakes.getStudent() + "同学：");
        WrongTopicResult.ExerciseMistakesBean.NumbersBean numbers = mistakes.getNumbers();
        if (numbers != null) {
            ArrayList<TopicSubject> topicSubjects = new ArrayList<>();
            int english = numbers.getEnglish();
            int math = numbers.getMath();
            if (english <= 0 && math <= 0) {
                mWtevWrongTopic.setLayout(LayoutStatusEnum.EMPTY);
            } else {
                topicSubjects.add(new TopicSubject("英语", english, 2));
                topicSubjects.add(new TopicSubject("数学", math, 1));
            }
            showTopicView(topicSubjects);
        } else {
            mWtevWrongTopic.setLayout(LayoutStatusEnum.EMPTY);
        }
    }


    private void dealReport(ReportListResult response) {
        if (response == null) {
            mLrevLearningReport.setLayout(LayoutStatusEnum.EMPTY);
            return;
        }
        List<Report> reports = response.getResults();
        if (reports != null && reports.size() > 0) {
            Report report = null;
            for (int i = 0; i < reports.size(); i++) {
                if (reports.get(i).isSupported() && reports.get(i).isPurchased()) {
                    report = reports.get(i);
                    break;
                }
            }
            if (report != null) {
                showReportView(report);
            } else {
                mLrevLearningReport.setLayout(LayoutStatusEnum.EMPTY);
            }
        } else {
            mLrevLearningReport.setLayout(LayoutStatusEnum.EMPTY);
        }
    }


    @Override
    public String getStatName() {
        return "会员专享";
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
