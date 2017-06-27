package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hitomi.cslibrary.draw.RoundRectShadowDrawable;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.LiveCourseDetailActivity;
import com.malalaoshi.android.activitys.LiveCourseInfoActivity;
import com.malalaoshi.android.activitys.LiveCourseIntroduceActivity;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.core.view.ShadowHelper;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.ui.widgets.CircleImageView;
import com.malalaoshi.android.utils.CalendarUtils;
import com.malalaoshi.android.utils.Number;
import com.malalaoshi.android.utils.StringUtil;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/10/13.
 */

public class LiveCourseAdapter extends BaseRecycleAdapter<LiveCourseAdapter.ViewHolder, LiveCourse> {
    private final int ITEM_TYPE_HEAD = 0;
    private final int ITEM_TYPE_ITEM = 1;


    public LiveCourseAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder holder = null;
        View view = null;
        if (viewType == ITEM_TYPE_HEAD) {
            view = LayoutInflater.from(mContext).inflate(R.layout.view_banner_item, parent, false);
            holder = new BannerViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_live_course, parent, false);
            holder = new CourseViewHolder(view);
        }
        view.setTag(holder);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEAD;
        } else {
            return ITEM_TYPE_ITEM;
        }
        //return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LiveCourse liveClass = null;
        switch (getItemViewType(position)) {
            case ITEM_TYPE_HEAD:

                break;
            case ITEM_TYPE_ITEM:
                liveClass = getItem(position - 1);
                break;
        }

        holder.update(mContext, liveClass, position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void update(Context context, LiveCourse liveClass, int position);
    }

    public class ItemViewHolder extends ViewHolder implements View.OnClickListener {

        private int[][] colors = new int[][]{{R.color.color_orange_fbef64, R.color.color_orange_f5a623, R.color.color_green_7ed321, R.color.color_purple_a560ff},
                {R.color.color_red_e26254, R.color.color_white_ffffff, R.color.color_white_ffffff, R.color.color_white_ffffff}};
        @Bind(R.id.iv_lecturer_avator)
        protected MalaImageView ivLecturerAvator;
        @Bind(R.id.iv_assist_avator)
        protected MalaImageView ivAssistAvator;
        @Bind(R.id.tv_class_type)
        protected TextView tvClassType;
        @Bind(R.id.tv_lecturer_name)
        protected TextView tvLecturerName;
        @Bind(R.id.tv_lecture_honorary)
        protected TextView tvLectureHonorary;
        @Bind(R.id.tv_assist_name)
        protected TextView tvAssistName;
        @Bind(R.id.tv_live_course)
        protected TextView tvLiveClass;
        @Bind(R.id.tv_class_time)
        protected TextView tvClassTime;
        @Bind(R.id.tv_grade)
        protected TextView tvGrade;
        @Bind(R.id.tv_total_price)
        protected TextView tvTotalPrice;

        protected LiveCourse liveClass;
        protected View view;

        protected ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        protected void update(Context context, LiveCourse liveClass, int position) {
            view.setOnClickListener(this);
            this.liveClass = liveClass;
            String imgUrl = liveClass.getLecturer_avatar();
            ivLecturerAvator.loadCircleImage(imgUrl, R.drawable.ic_default_avatar);
            imgUrl = liveClass.getAssistant_avatar();
            ivAssistAvator.loadCircleImage(imgUrl, R.drawable.ic_default_avatar);
            setClassType(tvClassType, liveClass.getRoom_capacity() + "人班", position);

            tvLecturerName.setText(liveClass.getLecturer_name());
            tvLectureHonorary.setText(liveClass.getLecturer_title());
            tvAssistName.setText("助教:" + liveClass.getAssistant_name());
            tvLiveClass.setText(liveClass.getCourse_name());

            tvClassTime.setText(CalendarUtils.formatMonthAndDay(liveClass.getCourse_start()) + "—" + CalendarUtils.formatMonthAndDay(liveClass.getCourse_end()));
            tvGrade.setText(liveClass.getCourse_grade());

            if (liveClass.getCourse_fee() != null) {
                String str1 = String.format("￥%s", Number.subZeroAndDot(liveClass.getCourse_fee().doubleValue() * 0.01d));
                String str2 = String.format("%d次", liveClass.getCourse_lessons());
                StringUtil.setHumpText(tvTotalPrice.getContext(), tvTotalPrice, str1, R.style.LiveCoursePriceStyle, str2, R.style.LiveCourseStuNum);
            }
        }

        private void setClassType(TextView tvClassType, String s, int position) {
            int bgcolor = tvClassType.getResources().getColor(colors[0][position % colors[0].length]);
            int textColor = tvClassType.getResources().getColor(colors[1][position % colors[1].length]);
            tvClassType.setTextColor(textColor);
            tvClassType.setBackgroundColor(bgcolor);
            tvClassType.setText(s);
        }

        @Override
        public void onClick(View v) {
            if (liveClass != null && liveClass.getId() != null) {
                LiveCourseInfoActivity.open(this.view.getContext(), liveClass);
            }
        }
    }

    public class CourseViewHolder extends ViewHolder implements View.OnClickListener {
        @Bind(R.id.tv_course_subject)
        TextView mTvCourseSubject;
        @Bind(R.id.tv_course_season)
        TextView mTvCourseSeason;
        @Bind(R.id.tv_course_title)
        TextView mTvCourseTitle;
        @Bind(R.id.tv_course_subtitle)
        TextView mTvCourseSubtitle;
        @Bind(R.id.tv_course_status)
        TextView mTvCourseStatus;
        @Bind(R.id.iv_course_teacher_avatar)
        CircleImageView mIvCourseTeacherAvatar;
        @Bind(R.id.tv_course_teacher_name)
        TextView mTvCourseTeacherName;
        @Bind(R.id.iv_course_assistant_avatar)
        CircleImageView mIvCourseAssistantAvatar;
        @Bind(R.id.tv_course_assistant_name)
        TextView mTvCourseAssistantName;
        @Bind(R.id.tv_course_class_time)
        TextView mTvCourseClassTime;
        @Bind(R.id.tv_course_student_grade)
        TextView mTvCourseStudentGrade;
        @Bind(R.id.tv_course_price)
        TextView mTvCoursePrice;
        @Bind(R.id.tv_course_teacher_title)
        TextView mTvCourseTeacherTitle;
        @Bind(R.id.ll_course_info)
        LinearLayout mLlCourseInfo;
        private LiveCourse mLiveClass;

        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void update(Context context, LiveCourse liveClass, int position) {
            if (liveClass == null) return;
            itemView.setOnClickListener(this);
            mLiveClass = liveClass;
            //            ShadowHelper.setWrapShadow(context, itemView, 0);
            String courseName = liveClass.getCourse_name();
            String[] courseNames = courseName.split("\\(");
            mTvCourseTitle.setText(courseNames[0]);
            if (courseNames.length >= 2) {
                mTvCourseSubtitle.setText(TextUtils.isEmpty(courseNames[1]) ? "" : "(" + courseNames[1]);
            }else {
                mTvCourseSubtitle.setText("");
            }
            mTvCourseTeacherName.setText(liveClass.getLecturer_name());
            mTvCourseTeacherTitle.setText(liveClass.getLecturer_title());
            mIvCourseTeacherAvatar.loadImage(liveClass.getLecturer_avatar(), R.drawable.ic_default_avatar);
            mIvCourseAssistantAvatar.loadImage(liveClass.getAssistant_avatar(), R.drawable.ic_default_avatar);
            mTvCourseAssistantName.setText(String.format("助教 %s", liveClass.getAssistant_name()));
            mTvCourseClassTime.setText(CalendarUtils.formatMonthAndDay(liveClass.getCourse_start()) + "-"
                    + CalendarUtils.formatMonthAndDay(liveClass.getCourse_end()));
            mTvCourseStudentGrade.setText(liveClass.getCourse_grade());
            if (liveClass.getCourse_fee() != null) {
                String str1 = String.format("¥%s", Number.subZeroAndDot(liveClass.getCourse_fee().doubleValue() * 0.01d));
                String str2 = String.format("%d次", liveClass.getCourse_lessons());
                StringUtil.setHumpText(mContext, mTvCoursePrice, str1, R.style.LiveCoursePriceStyle, str2, R.style.LiveCourseStuNum);
            }

            setLabel(liveClass);

            if (!(mLlCourseInfo.getBackground() instanceof RoundRectShadowDrawable)) {
                ShadowHelper.setDrawShadow(mContext, mLlCourseInfo, 0);
            }
        }

        private void setLabel(LiveCourse liveClass) {
            String courseSubject = liveClass.getCourse_subject();
            if (!TextUtils.isEmpty(courseSubject)) {
                mTvCourseSubject.setText(courseSubject.charAt(0) + "");
                if ("英语".equals(courseSubject)) {
                    mTvCourseSubject.setTextColor(MiscUtil.getColor(R.color.color_green_99d03b));
                    mTvCourseSubject.setBackgroundResource(R.drawable.shape_green_frame_bg);
                } else if ("数学".equals(courseSubject)) {
                    mTvCourseSubject.setTextColor(MiscUtil.getColor(R.color.color_purple_b790ff));
                    mTvCourseSubject.setBackgroundResource(R.drawable.shape_purple_frame_bg);
                }
            }
            long currentTime = System.currentTimeMillis() / 1000;
            Long courseStart = liveClass.getCourse_start();
            int courseMonth = CalendarUtils.getMonth(courseStart);
            switch (courseMonth){
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        mTvCourseSeason.setText("春季班");
                        break;
                    case 7:
                    case 8:
                        mTvCourseSeason.setText("暑假班");
                        break;
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        mTvCourseSeason.setText("秋季班");
                        break;
                    case 1:
                    case 2:
                        mTvCourseSeason.setText("寒假班");
                        break;
                    default:
            }

            if (courseStart < currentTime){
                mTvCourseStatus.setText("开课中");
                mTvCourseStatus.setBackgroundResource(R.drawable.bg_in_class);
            }else if (courseStart > currentTime && courseStart < getNextSeasonTime(2)){
                mTvCourseStatus.setText("火热报名");
                mTvCourseStatus.setBackgroundResource(R.drawable.bg_hot_registration);
            }else{
                mTvCourseStatus.setText("预报中");
                mTvCourseStatus.setBackgroundResource(R.drawable.bg_forecasting);
            }
        }

        private long getNextSeasonTime(int next) {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH)+ 1;
            int year = calendar.get(Calendar.YEAR);
            switch (month){
                case 3:
                case 4:
                case 5:
                case 6:
                    if (next == 1)
                        return CalendarUtils.getSecondTime(year+"-07-01");
                    else if (next == 2)
                        return CalendarUtils.getSecondTime(year+"-09-01");
                case 7:
                case 8:
                    if (next == 1)
                        return CalendarUtils.getSecondTime(year+"-09-01");
                    else if (next == 2)
                        return CalendarUtils.getSecondTime((year+1)+"-01-01");
                case 9:
                case 10:
                case 11:
                case 12:
                    if (next == 1)
                        return CalendarUtils.getSecondTime((year+1)+"-01-01");
                    else if (next == 2)
                        return CalendarUtils.getSecondTime((year+1)+"-03-01");
                case 1:
                case 2:
                    if (next == 1)
                        return CalendarUtils.getSecondTime((year+1)+"-03-01");
                    else if (next ==2)
                        return CalendarUtils.getSecondTime((year+1)+"-07-01");
                default:
                    return -1;
            }
        }

        @Override
        public void onClick(View v) {
            if (mLiveClass != null && mLiveClass.getId() != null) {
//                LiveCourseInfoActivity.open(this.itemView.getContext(), mLiveClass);
                LiveCourseDetailActivity.launch(itemView.getContext(), mLiveClass);
            }
        }
    }


    public class BannerViewHolder extends ViewHolder implements View.OnClickListener {

        ImageView ivBanner;

        public BannerViewHolder(View itemView) {
            super(itemView);
            ivBanner = (ImageView) itemView;
        }

        protected void update(Context context, LiveCourse liveClass, int position) {
            ivBanner.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            LiveCourseIntroduceActivity.launch(view.getContext());
        }
    }
}
