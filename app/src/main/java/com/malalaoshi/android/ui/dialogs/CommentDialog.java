package com.malalaoshi.android.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.entity.Comment;
import com.malalaoshi.android.network.Constants;
import com.malalaoshi.android.network.api.PostCommentApi;
import com.malalaoshi.android.ui.widgets.DoubleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by donald on 2017/6/29.
 */

public class CommentDialog extends BaseDialog {
    private static String ARGS_DIALOG_COMMENT_TYPE = "comment type";

    private static String ARGS_DIALOG_TEACHER_NAME = "teacher name";
    private static String ARGS_DIALOG_TEACHER_AVATAR = "teacher avatar";

    private static String ARGS_DIALOG_LECTURER_NAME = "lecturer name";
    private static String ARGS_DIALOG_LECTURER_AVATAR = "lecturer avatar";

    private static String ARGS_DIALOG_ASSIST_NAME = "assist name";
    private static String ARGS_DIALOG_ASSIST_AVATAR = "assist avatar";

    private static String ARGS_DIALOG_COURSE_NAME = "course name";
    private static String ARGS_DIALOG_COMMENT = "comment";
    private static String ARGS_DIALOG_TIMESLOT = "timeslot";
    @Bind(R.id.div_comment_dialog_avatar)
    DoubleImageView mDivCommentDialogAvatar;
    @Bind(R.id.tv_comment_dialog_teacher_course)
    TextView mTvCommentDialogTeacherCourse;
    @Bind(R.id.rb_comment_dialog_score)
    RatingBar mRbCommentDialogScore;
    @Bind(R.id.et_comment_dialog_input)
    EditText mEtCommentDialogInput;
    @Bind(R.id.tv_comment_dialog_commit)
    TextView mTvCommentDialogCommit;
    @Bind(R.id.iv_comment_dialog_close)
    ImageView mIvCommentDialogClose;
    private int mCommentType;
    private String mTeacherName;
    private String mTeacherAvatar;
    private String mLeactureAvatar;
    private String mLeactureName;
    private String mAssistantAvatar;
    private String mAssistantName;
    private String mCourseName;
    private Comment mComment;
    private long mTimeslot;
    private OnCommentResultListener mResultListener;

    public CommentDialog(Context context) {
        super(context);
    }

    public CommentDialog(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            mCommentType = bundle.getInt(ARGS_DIALOG_COMMENT_TYPE);
            if (mCommentType == 0) {
                mTeacherName = bundle.getString(ARGS_DIALOG_TEACHER_NAME, "");
                mTeacherAvatar = bundle.getString(ARGS_DIALOG_TEACHER_AVATAR, "");
            } else if (mCommentType == 1) {
                mLeactureAvatar = bundle.getString(ARGS_DIALOG_LECTURER_AVATAR, "");
                mLeactureName = bundle.getString(ARGS_DIALOG_LECTURER_NAME, "");
                mAssistantAvatar = bundle.getString(ARGS_DIALOG_ASSIST_AVATAR, "");
                mAssistantName = bundle.getString(ARGS_DIALOG_ASSIST_NAME, "");
            }
            mCourseName = bundle.getString(ARGS_DIALOG_COURSE_NAME, "");
            mComment = bundle.getParcelable(ARGS_DIALOG_COMMENT);
            mTimeslot = bundle.getLong(ARGS_DIALOG_TIMESLOT, 0L);
        }
        initView();
    }

    private void initView() {
        setCancelable(false);
        if (mCommentType == 0)
            mDivCommentDialogAvatar.loadImg(mTeacherAvatar, "", DoubleImageView.LOAD_SIGNLE_BIG);
        else if (mCommentType == 1)
            mDivCommentDialogAvatar.loadImg(mLeactureAvatar, mAssistantAvatar, DoubleImageView.LOAD_DOUBLE);
        if (mComment != null) {
            StatReporter.commentPage(true);
            updateUI(mComment);
            mRbCommentDialogScore.setIsIndicator(true);
            mEtCommentDialogInput.setFocusableInTouchMode(false);
            mEtCommentDialogInput.setCursorVisible(false);
            mTvCommentDialogCommit.setText("查看评价");
        } else {
            StatReporter.commentPage(false);
            mTvCommentDialogCommit.setText("提交");
            mRbCommentDialogScore.setIsIndicator(false);
            mEtCommentDialogInput.setFocusableInTouchMode(true);
            mEtCommentDialogInput.setCursorVisible(true);

        }
        mTvCommentDialogTeacherCourse.setText(mCourseName);
        mRbCommentDialogScore.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (mComment == null){
                    if (fromUser && rating > 0 && mEtCommentDialogInput.getText().length() > 0){
                        mTvCommentDialogCommit.setEnabled(true);
                    }else {
                        mTvCommentDialogCommit.setEnabled(false);
                    }
                }
            }
        });
        mEtCommentDialogInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mComment == null){
                    if (s.length() > 0 && mRbCommentDialogScore.getRating() > 0){
                        mTvCommentDialogCommit.setEnabled(true);
                    }else {
                        mTvCommentDialogCommit.setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    protected View getView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_comment_v2, null);
        ButterKnife.bind(this, view);
        return view;
    }

    private void updateUI(Comment comment) {

        if (comment != null) {
            mRbCommentDialogScore.setRating(comment.getScore());
            mEtCommentDialogInput.setText(comment.getContent());
        } else {
            mRbCommentDialogScore.setRating(0);
            mEtCommentDialogInput.setText("");
        }
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }


    public static CommentDialog newInstance(Context context, String lecturerName, String lecturerAvatarUrl, String assistName, String assistAvatarUrl, String courseName,
                                            Long timeslot, Comment comment) {
        Bundle args = new Bundle();
        args.putInt(ARGS_DIALOG_COMMENT_TYPE, 1);
        args.putString(ARGS_DIALOG_LECTURER_NAME, lecturerName);
        args.putString(ARGS_DIALOG_LECTURER_AVATAR, lecturerAvatarUrl);
        args.putString(ARGS_DIALOG_ASSIST_NAME, assistName);
        args.putString(ARGS_DIALOG_ASSIST_AVATAR, assistAvatarUrl);
        args.putString(ARGS_DIALOG_COURSE_NAME, courseName);
        args.putParcelable(ARGS_DIALOG_COMMENT, comment);
        args.putLong(ARGS_DIALOG_TIMESLOT, timeslot);
        //        f.setArguments(args);
        CommentDialog f = new CommentDialog(context, args);
        return f;
    }

    public void setOnCommentResultListener(OnCommentResultListener listener) {
        mResultListener = listener;
    }

    public static CommentDialog newInstance(Context context, String teacherName, String teacherAvatarUrl, String courseName,
                                            Long timeslot, Comment comment) {
        Bundle args = new Bundle();
        args.putInt(ARGS_DIALOG_COMMENT_TYPE, 0);
        args.putString(ARGS_DIALOG_TEACHER_NAME, teacherName);
        args.putString(ARGS_DIALOG_TEACHER_AVATAR, teacherAvatarUrl);
        args.putString(ARGS_DIALOG_COURSE_NAME, courseName);
        args.putParcelable(ARGS_DIALOG_COMMENT, comment);
        args.putLong(ARGS_DIALOG_TIMESLOT, timeslot);
        //        f.setArguments(args);
        CommentDialog f = new CommentDialog(context, args);
        return f;
    }

    @OnClick({R.id.tv_comment_dialog_commit, R.id.iv_comment_dialog_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_comment_dialog_commit:
                commit();
                dismiss();
                break;
            case R.id.iv_comment_dialog_close:
                dismiss();
                break;
        }
    }

    private void commit() {
        StatReporter.commentSubmit();

        if (mComment != null) {
            dismiss();
            return;
        }
        float score = mRbCommentDialogScore.getRating();
        if (score == 0.0) {
            MiscUtil.toast(R.string.rate_the_course);
            return;
        }
        String content = mEtCommentDialogInput.getText().toString();
        if (TextUtils.isEmpty(content)) {
            MiscUtil.toast(R.string.write_few_reviews);
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.TIMESLOT, mTimeslot);
            json.put(Constants.SCORE, score);
            json.put(Constants.CONTENT, content);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        ApiExecutor.exec(new PostCommentRequest(this, json.toString()));
    }

    public interface OnCommentResultListener {
        void onSuccess(Comment comment);
    }

    private static final class PostCommentRequest extends BaseApiContext<CommentDialog, Comment> {

        private String body;

        public PostCommentRequest(CommentDialog commentDialog, String body) {
            super(commentDialog);
            this.body = body;
        }

        @Override
        public Comment request() throws Exception {
            return new PostCommentApi().post(body);
        }

        @Override
        public void onApiSuccess(@NonNull Comment response) {
            get().commentSucceed(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().commentFailed();
        }
    }

    private void commentFailed() {
        MiscUtil.toast(R.string.comment_failed);
    }

    private void commentSucceed(Comment response) {
        MiscUtil.toast(R.string.comment_succeed);
        if (mResultListener != null)
            mResultListener.onSuccess(response);
        dismiss();
    }
}
