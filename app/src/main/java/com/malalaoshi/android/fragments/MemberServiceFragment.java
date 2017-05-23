package com.malalaoshi.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.MemberActivity;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.view.RefreshHeaderEffectView;
import com.malalaoshi.android.core.view.ShadowHelper;
import com.malalaoshi.android.entity.Report;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.network.api.LearningReportApi;
import com.malalaoshi.android.network.result.ReportListResult;
import com.malalaoshi.android.report.ReportActivity;
import com.malalaoshi.android.utils.AuthUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/5/16.
 * 会员专享
 */

public class MemberServiceFragment extends BaseFragment implements View.OnClickListener {

//    AnimationDrawable refreshAnimation = null;

    @Bind(R.id.ll_refresh_refreshing)
    protected LinearLayout llRefreshRefreshing;

    @Bind(R.id.iv_refresh_refreshing)
    protected ImageView ivRefreshRefreshing;

    @Bind(R.id.tv_refresh_refreshing)
    protected TextView tvRefreshRefreshing;

    @Bind(R.id.sub_non_learning_report)
    protected View rlNonLearningReport;

    @Bind(R.id.sub_learning_report)
    protected RelativeLayout rlLearningReport;

    @Bind(R.id.tv_subject)
    protected TextView tvSubject;

    @Bind(R.id.tv_answer_number)
    protected TextView tvAnswerNumber;

    @Bind(R.id.tv_correct_rate)
    protected TextView tvCorrectRate;

    @Bind(R.id.tv_open_learning_report)
    protected TextView tvOpenLearReport;

    @Bind(R.id.tv_report_prompt)
    protected TextView tvReportPrompt;

    @Bind(R.id.tv_report_submit)
    protected TextView tvReportSubmit;
    @Bind(R.id.ptr_member_rooter)
    PtrClassicFrameLayout mPtrMemberRooter;

    enum EnumReportStatus {
        LOGIN, LOGIN_FAILED, NOT_SIGN_IN, NOT_SIGN_UP, EMPTY_REPORT, REPORT
    }

    private Subject subject;
    private EnumReportStatus reportStatus = EnumReportStatus.LOGIN;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_service, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrMemberRooter.autoRefresh();
            }
        }, 100);
        initData();
        setEvent();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    private void setEvent() {

    }

    private void initData() {
        loadData();
    }

    private void initView() {
//        refreshAnimation = (AnimationDrawable) ivRefreshRefreshing.getDrawable();
        RefreshHeaderEffectView headerView = new RefreshHeaderEffectView(getContext());
        mPtrMemberRooter.setHeaderView(headerView);
        mPtrMemberRooter.addPtrUIHandler(headerView);
        mPtrMemberRooter.setKeepHeaderWhenRefresh(true);
        mPtrMemberRooter.setPullToRefresh(false);
        //这个会引起自动刷新刷新两次
        //refreshLayout.setEnabledNextPtrAtOnce(true);
        mPtrMemberRooter.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                MemberServiceFragment.this.onRefreshBegin();
            }
        });
        ShadowHelper.setDrawShadow(mContext, rlNonLearningReport);
//        new CrazyShadow.Builder()
//                .setContext(mContext)
//                .setDirection(CrazyShadowDirection.ALL)
//                .setShadowRadius(DensityUtil.dip2px(mContext, 4))
//                .setCorner(DensityUtil.dip2px(mContext, 4))
//                .setBaseShadowColor(getResources().getColor(R.color.core__shadow_bg_blue))
//                .setImpl(CrazyShadow.IMPL_WRAP)
//                .action(rlNonLearningReport);
    }

    private void onRefreshBegin() {
    }

    private void loadData() {
        if (!UserManager.getInstance().isLogin()) {
            showNotSignInView();
        } else {
            showLoadingView();
            ApiExecutor.exec(new FetchReportRequest(this, 0));
        }
    }

    private void loadDataBackground() {
        if (!UserManager.getInstance().isLogin()) {
            showNotSignInView();
        } else {
            ApiExecutor.exec(new FetchReportRequest(this, 1));
        }
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_LOGOUT_SUCCESS:
            case BusEvent.BUS_EVENT_LOGIN_SUCCESS:
            case BusEvent.BUS_EVENT_PAY_SUCCESS:
                reloadData();
                break;
            case BusEvent.BUS_EVENT_BACKGROUND_LOAD_REPORT_DATA:
                loadDataBackground();
                break;
        }
    }

    @OnClick(R.id.tv_report_submit)//登录或查看样本
    public void onClickNoReport() {
        if (reportStatus == EnumReportStatus.NOT_SIGN_IN) {
            openLoginActivity();
        } else if (reportStatus == EnumReportStatus.NOT_SIGN_UP) {
            openSampleReport();
        } else if (reportStatus == EnumReportStatus.EMPTY_REPORT) {
            openSampleReport();
        }
    }

    @OnClick(R.id.tv_open_learning_report)//查看样本
    public void onClickOpenReport() {
        if (reportStatus == EnumReportStatus.REPORT) {
            openLearningReport();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_refresh_refreshing) {
            reloadData();
            llRefreshRefreshing.setOnClickListener(null);
        }
    }

    //查看学习报告
    private void openLearningReport() {
        openSampleReport();
        /*不再调用快乐学接口 只显示报告样本
        if (subject != null) {
            ReportActivity.launch(getActivity(), subject.getId());
        } else {
            openSampleReport();
        }
        */
    }

    //查看学习报告样本
    private void openSampleReport() {
        ReportActivity.launch(getActivity(), -1);
    }

    //登录
    private void openLoginActivity() {
        AuthUtils.redirectLoginActivity(getContext());
    }

    //重新加载
    private void reloadData() {
        loadData();
    }

    private void showLoadingView() {
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.VISIBLE);
        llRefreshRefreshing.setOnClickListener(null);
//        ivRefreshRefreshing.setImageDrawable(refreshAnimation);
//        refreshAnimation.start();
        tvRefreshRefreshing.setText("正在加载数据···");
        reportStatus = EnumReportStatus.LOGIN;
    }

    private void showLoadFailedView() {
//        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.VISIBLE);
        llRefreshRefreshing.setOnClickListener(this);
        ivRefreshRefreshing.setImageDrawable(getResources().getDrawable(R.drawable.ic_wrong_topic));
        tvRefreshRefreshing.setText("加载失败,点击重试!");
        reportStatus = EnumReportStatus.LOGIN_FAILED;
    }

    private void showNotSignInView() {
        mPtrMemberRooter.refreshComplete();
//        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.VISIBLE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.GONE);
        tvReportPrompt.setText("登录可查看专属学习报告哦");
        tvReportSubmit.setText("登录");
        reportStatus = EnumReportStatus.NOT_SIGN_IN;
    }

    private void showEmptyReportView() {
//        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.VISIBLE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.GONE);
        tvReportPrompt.setText("学习报告目前只支持数学科目");
        tvReportSubmit.setText("查看学习报告样本");
        reportStatus = EnumReportStatus.EMPTY_REPORT;
    }

    private void showReportView(Report report) {
//        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.VISIBLE);
        llRefreshRefreshing.setVisibility(View.GONE);
        subject = Subject.getSubjectById(report.getSubject_id());
        if (subject != null) {
            tvSubject.setText(subject.getName());
        } else {
            tvSubject.setText("");
        }
        tvAnswerNumber.setText(report.getTotal_nums() + "");
        int rate = 0;
        if (report.getTotal_nums() > 0) {
            rate = report.getRight_nums() * 100 / report.getTotal_nums();
        }
        tvCorrectRate.setText(rate + "%");
        reportStatus = EnumReportStatus.REPORT;
    }

    private void dealResponse(ReportListResult response) {
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
                //update ui
                showReportView(report);
            } else {
                showEmptyReportView();
            }
        } else {
            showEmptyReportView();
        }
    }


    private static final class FetchReportRequest extends BaseApiContext<MemberServiceFragment, ReportListResult> {
        private int requestType;  //1:后台更新,获取数据才更新UI

        public FetchReportRequest(MemberServiceFragment memberServiceFragment, int requestType) {
            super(memberServiceFragment);
            this.requestType = requestType;
        }

        @Override
        public ReportListResult request() throws Exception {
            return new LearningReportApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull ReportListResult response) {
            get().dealResponse(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            if (requestType != 1) {
                get().showLoadFailedView();
            }
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
        }
    }

    /*************************
     * 会员专享
     *************************/
    @OnClick(R.id.tv_with_read)//自习陪读
    public void onClickWithRead() {
        openMemberServiceAvtivity(0);
    }

    @OnClick(R.id.tv_learning_report)//学习报告
    public void onClickLearningReport() {
        openMemberServiceAvtivity(1);
        //startActivity(new Intent(getActivity(), ReportActivity.class));
    }

    @OnClick(R.id.tv_counseling)//心理辅导
    public void onClickCounseling() {
        openMemberServiceAvtivity(2);
    }

    @OnClick(R.id.tv_lectures)//特色讲座
    public void onClickLectures() {
        openMemberServiceAvtivity(3);
    }

    @OnClick(R.id.tv_exam_explain)//考前串讲
    public void onClickExamExplain() {
        openMemberServiceAvtivity(4);
    }

    @OnClick(R.id.tv_mistake)//错题本
    public void onClickMistake() {
        openMemberServiceAvtivity(5);
    }

    @OnClick(R.id.tv_spps_evaluation)//SPPS测评
    public void onClickSppsEvaluation() {
        openMemberServiceAvtivity(6);
    }

    @OnClick(R.id.tv_expect_more)//敬请期待
    public void onClickExpectMore() {
        openMemberServiceAvtivity(7);
    }

    private void openMemberServiceAvtivity(int position) {
        Intent intent = new Intent(getContext(), MemberActivity.class);
        intent.putExtra(MemberActivity.EXTRA_CURRETN_POSITION, position);
        startActivity(intent);
    }

    @Override
    public String getStatName() {
        return "会员专享";
    }

}
