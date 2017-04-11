package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.LiveCourseInfoActivity;
import com.malalaoshi.android.activitys.LiveCourseIntroduceActivity;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.utils.CalendarUtils;
import com.malalaoshi.android.utils.Number;
import com.malalaoshi.android.utils.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/10/13.
 */

public class LiveCourseAdapter extends BaseRecycleAdapter<LiveCourseAdapter.ViewHolder, LiveCourse> {
    private final int ITEM_TYPE_HEAD = 0;
    private final int ITEM_TYPE_ITEM = 1;
    public  LiveCourseAdapter(Context context) {
        super(context);
    }

    @Override
    public LiveCourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LiveCourseAdapter.ViewHolder holder = null;
        View view = null;
        if (viewType==ITEM_TYPE_HEAD){
            view = LayoutInflater.from(context).inflate(R.layout.view_banner_item, parent, false);
            holder = new BannerViewHolder(view);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.liveclass_list_item, parent, false);
            holder = new ItemViewHolder(view);
        }
        view.setTag(holder);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return ITEM_TYPE_HEAD;
        }else{
            return ITEM_TYPE_ITEM;
        }
        //return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(LiveCourseAdapter.ViewHolder holder, int position) {
        LiveCourse liveClass = null;
        switch (getItemViewType(position)) {
            case ITEM_TYPE_HEAD:

                break;
            case ITEM_TYPE_ITEM:
                liveClass = getItem(position-1);
                break;
        }

        holder.update(liveClass, position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount()+1;
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
        protected abstract void update(LiveCourse liveClass, int position);
    }

    public class ItemViewHolder extends ViewHolder implements View.OnClickListener {

        private int[][] colors = new int[][]{{R.color.color_orange_fbef64, R.color.color_orange_f5a623, R.color.color_green_7ed321, R.color.color_purple_a560ff},
                {R.color.color_red_e26254,R.color.color_white_ffffff,R.color.color_white_ffffff,R.color.color_white_ffffff}};
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

        protected void update(LiveCourse liveClass, int position) {
            view.setOnClickListener(this);
            this.liveClass = liveClass;
            String imgUrl = liveClass.getLecturer_avatar();
            ivLecturerAvator.loadCircleImage(imgUrl, R.drawable.ic_default_teacher_avatar);
            imgUrl = liveClass.getAssistant_avatar();
            ivAssistAvator.loadCircleImage(imgUrl, R.drawable.ic_default_teacher_avatar);
            setClassType(tvClassType, liveClass.getRoom_capacity()+"人班",position);

            tvLecturerName.setText(liveClass.getLecturer_name());
            tvLectureHonorary.setText(liveClass.getLecturer_title());
            tvAssistName.setText("助教:"+liveClass.getAssistant_name());
            tvLiveClass.setText(liveClass.getCourse_name());

            tvClassTime.setText(CalendarUtils.formatMonthAndDay(liveClass.getCourse_start())+"—"+CalendarUtils.formatMonthAndDay(liveClass.getCourse_end()));
            tvGrade.setText(liveClass.getCourse_grade());

            if (liveClass.getCourse_fee() != null) {
                String str1 = String.format("￥%s",Number.subZeroAndDot(liveClass.getCourse_fee().doubleValue() * 0.01d));
                String str2 = String.format("%d次",liveClass.getCourse_lessons());
                StringUtil.setHumpText(tvTotalPrice.getContext(),tvTotalPrice,str1,R.style.LiveCoursePriceStyle,str2,R.style.LiveCourseStuNum);
            }
        }

        private void setClassType(TextView tvClassType, String s, int position) {
            int bgcolor = tvClassType.getResources().getColor(colors[0][position%colors[0].length]);
            int textColor = tvClassType.getResources().getColor(colors[1][position%colors[1].length]);
            tvClassType.setTextColor(textColor);
            tvClassType.setBackgroundColor(bgcolor);
            tvClassType.setText(s);
        }

        @Override
        public void onClick(View v) {
            if (liveClass!=null&&liveClass.getId()!=null) {
                LiveCourseInfoActivity.open(this.view.getContext(), liveClass);
            }
        }
    }


    public class BannerViewHolder extends ViewHolder implements View.OnClickListener {

        ImageView ivBanner;
        public BannerViewHolder(View itemView) {
            super(itemView);
            ivBanner = (ImageView) itemView;
        }
        protected void update(LiveCourse liveClass, int position){
            ivBanner.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            LiveCourseIntroduceActivity.launch(view.getContext());
        }
    }
}
