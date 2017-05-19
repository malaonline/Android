package com.malalaoshi.android.core.usercenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.api.VerifyCodeApi;
import com.malalaoshi.android.core.usercenter.entity.AuthUser;
import com.malalaoshi.android.core.usercenter.entity.SendSms;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.core.utils.StatusBarCompat;
import com.malalaoshi.android.core.view.ShadowHelper;

/**
 * 手机号登录
 * Created by Donald on 4/6/17.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final int RESULT_CODE_LOGIN_SUCCESS = 2;
    private int mKeyboardHeight;
    private int mStatusBarHeight;
    private int mBottomStatusHeight;
    private int mStatusHeight;
    private boolean isShowKeyboard;
    private FrameLayout mLlLoginRoot;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;
//    private FrameLayout mFlContainer;
    private TextView mTvLoginTitle;
    private ImageView ivLoginTopLogo;
    private Animation mTitleHideAnima;
    private Animation mTitleShowAnima;
    private Animation mLogoHideAnima;
    private Animation mLogoShowAnima;
    private EditText mEtInputPhone;
    private EditText mEtInputCode;
    private TextView mTvGetCode;
    private TextView mtvLoginCommit;
    private TextView mTvLoginAgreement;
    private ImageView mIvLoginBack;
    private ImageView mIvLoginClearNum;
    private Handler mHandler;
    private LinearLayout mLlLoginParent;
    private FrameLayout mFlInputPhone;
    private FrameLayout mFlInputCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core__activity_login);
        StatusBarCompat.compat(this);
//        if (savedInstanceState == null) {
//            LoginFragment fragment = new LoginFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, fragment).commit();
//        }
//        mFlContainer = (FrameLayout) findViewById(R.id.fl_container);
        initView();
        initShadow();
        mStatusBarHeight = StatusBarCompat.getStatusBarHeight(this);
        mBottomStatusHeight = StatusBarCompat.getBottomStatusHeight(this);
        mStatusHeight = mStatusBarHeight + mBottomStatusHeight;
        mHandler = new Handler(Looper.getMainLooper());
        initListener();
        initAnimation();
    }

    private void initShadow() {
        ShadowHelper.setDrawShadow(this, mFlInputPhone);
        ShadowHelper.setDrawShadow(this, mFlInputCode);
//        ShadowHelper.setDrawShadow(this, 4, mtvLoginCommit);
//        new CrazyShadow.Builder()
//                .setContext(this)
//                .setDirection(CrazyShadowDirection.ALL)
//                .setShadowRadius(DensityUtil.dip2px(this, 4))
//                .setBaseShadowColor(getResources().getColor(R.color.main_color))
//                .setCorner(DensityUtil.dip2px(this, 20))
//                .setImpl(CrazyShadow.IMPL_WRAP)
//                .action(mtvLoginCommit);

    }

    private void initView() {
        mTvLoginTitle = (TextView) findViewById(R.id.tv_login_title);
        mLlLoginRoot = (FrameLayout) findViewById(R.id.ll_login_root);
        ivLoginTopLogo = (ImageView) findViewById(R.id.iv_login_top_logo);
        mEtInputPhone = (EditText) findViewById(R.id.et_input_phone);
        mEtInputCode = (EditText) findViewById(R.id.et_input_code);
        mTvGetCode = (TextView) findViewById(R.id.tv_get_code);
        mtvLoginCommit = (TextView) findViewById(R.id.tv_login_commit);
        mTvLoginAgreement = (TextView) findViewById(R.id.tv_login_agreement);
        mIvLoginBack = (ImageView) findViewById(R.id.iv_login_back);
        mIvLoginClearNum = (ImageView) findViewById(R.id.iv_login_clear_num);
        mLlLoginParent = (LinearLayout) findViewById(R.id.ll_login_parent);
        mFlInputPhone = (FrameLayout) findViewById(R.id.fl_input_phone);
        mFlInputCode = (FrameLayout) findViewById(R.id.fl_login_input_code);

    }

    private void initAnimation() {
        mTitleHideAnima = AnimationUtils.loadAnimation(this, R.anim.title_alpha_hide);
        mTitleShowAnima = AnimationUtils.loadAnimation(this, R.anim.title_alpha_show);
        mLogoHideAnima = AnimationUtils.loadAnimation(this, R.anim.login_logo_scale_hide);
        mLogoShowAnima = AnimationUtils.loadAnimation(this, R.anim.login_logo_scale_show);
    }

    private void initListener() {
        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                mLlLoginRoot.getWindowVisibleDisplayFrame(rect);
                int screenHeight = mLlLoginRoot.getRootView().getHeight();
                int heightDiff = screenHeight - (rect.bottom - rect.top);
                if (mKeyboardHeight == 0 && heightDiff > mStatusHeight) {
                    mKeyboardHeight = heightDiff - mStatusHeight;
                }
                if (isShowKeyboard) {
                    if (heightDiff <= mStatusHeight) {
                        isShowKeyboard = false;
                        onHideKeyboard();
                    }
                } else {
                    if (heightDiff > mStatusHeight) {
                        isShowKeyboard = true;
                        onShowKeyboard();
                    }
                }
            }
        };
        mLlLoginRoot.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
        mEtInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)){
                    mIvLoginClearNum.setVisibility(View.GONE);
                    mTvGetCode.setEnabled(false);
                }else {
                    mIvLoginClearNum.setVisibility(View.VISIBLE);
                    if (s.length() == 11){
                        mTvGetCode.setEnabled(true);
                    }else {
                        mTvGetCode.setEnabled(false);
                    }
                }
            }
        });

        mTvGetCode.setOnClickListener(this);
        mtvLoginCommit.setOnClickListener(this);
        mIvLoginBack.setOnClickListener(this);
        mIvLoginClearNum.setOnClickListener(this);
        mTvLoginAgreement.setOnClickListener(this);
        mLlLoginParent.setOnClickListener(this);
    }

    private void onShowKeyboard() {
        mTvLoginTitle.startAnimation(mTitleShowAnima);
        mTvLoginTitle.setVisibility(View.VISIBLE);
        ivLoginTopLogo.startAnimation(mLogoHideAnima);
        ivLoginTopLogo.setVisibility(View.GONE);


    }

    private void onHideKeyboard() {
        mTvLoginTitle.startAnimation(mTitleHideAnima);
        mTvLoginTitle.setVisibility(View.GONE);
        ivLoginTopLogo.startAnimation(mLogoShowAnima);
        ivLoginTopLogo.setVisibility(View.VISIBLE);

    }

    @Override
    protected String getStatName() {
        return "短信登录";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLlLoginRoot.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
        if (mHandler != null){
            mHandler.removeCallbacks(null);
            mHandler = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_login_back) {
            finish();
        } else if (id == R.id.iv_login_clear_num) {
            mEtInputPhone.setText("");
        } else if (id == R.id.tv_get_code) {
            mEtInputCode.requestFocus();
            getCode();
        } else if (id == R.id.tv_login_commit){
            commit();
        } else if (id == R.id.tv_login_agreement){
            startActivity(new Intent(this, UserProtocolActivity.class));
            StatReporter.userProtocol(getStatName());
        } else if (id == R.id.ll_login_parent){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()){
                imm.hideSoftInputFromWindow(mLlLoginParent.getWindowToken(), 0);
            }
        }
    }

    private void commit() {
        StatReporter.verifyCode(getStatName());
        String phone = mEtInputPhone.getText().toString();
        String code = mEtInputCode.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            MiscUtil.toast(R.string.input_right_phone);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            MiscUtil.toast(R.string.input_verification_code);
            return;
        }
        ApiExecutor.exec(new CheckVerifyCodeRequest(this, phone, code));
    }

    private void getCode() {
        StatReporter.fetchCode(getStatName());
        String phone = mEtInputPhone.getText().toString();
        // For Debug: close the phone verification
        if (TextUtils.isEmpty(phone) || !MiscUtil.isMobilePhone(phone)) {
            MiscUtil.toast(R.string.input_right_phone);
            return;
        }
        ApiExecutor.exec(new FetchVerifyCodeRequest(this, phone));
    }
    private static class FetchVerifyCodeRequest extends BaseApiContext<LoginActivity, SendSms> {

        private String phone;

        public FetchVerifyCodeRequest(LoginActivity loginActivity, String phone) {
            super(loginActivity);
            this.phone = phone;
        }

        @Override
        public SendSms request() throws Exception {
            return new VerifyCodeApi().get(phone);
        }

        @Override
        public void onApiSuccess(@NonNull SendSms sendSms) {
            if (!sendSms.isSent()) {
                get().fetchCodeFailed();
            } else {
                get().fetchSucceeded();
            }
        }
    }

    private static class CheckVerifyCodeRequest extends BaseApiContext<LoginActivity, AuthUser> {

        private String phone;
        private String code;

        public CheckVerifyCodeRequest(LoginActivity loginActivity, String phone, String code) {
            super(loginActivity);
            this.phone = phone;
            this.code = code;
        }

        @Override
        public AuthUser request() throws Exception {
            return new VerifyCodeApi().check(phone, code);
        }

        @Override
        public void onApiSuccess(@NonNull AuthUser user) {
            if (!user.isVerified()) {
                get().verifyFailed();
            } else {
                get().verifySucceeded(user);
            }
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().verifyFailed();
        }
    }

    private void verifySucceeded(AuthUser user) {
        UserManager.getInstance().login(user);
        if (user.isFirst_login()) {
            Intent intent = new Intent(this, AddStudentNameActivity.class);
            startActivity(intent);
        }
        setResult(LoginActivity.RESULT_CODE_LOGIN_SUCCESS);
        finish();
    }

    private void verifyFailed() {
        MiscUtil.toast(R.string.wrong_code);
    }

    private void fetchSucceeded() {
        MiscUtil.toast(R.string.get_code_succeed);
        mTvGetCode.setEnabled(false);
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        countdown(60);
    }

    private void fetchCodeFailed() {
        MiscUtil.toast(R.string.get_code_failed);
    }
    private void countdown(final int time) {
        if (mHandler != null) {
            mTvGetCode.setText(getString(R.string.seconds_count_down, time));
            mTvGetCode.setEnabled(false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (time < 1) {
                        mTvGetCode.setEnabled(true);
                        mTvGetCode.setText(getString(R.string.get_verification_code));
                    } else {
                        countdown(time - 1);
                    }
                }
            }, 1000);
        }
    }
}
