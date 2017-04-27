package com.malalaoshi.android.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.utils.DateUtils;
import com.malalaoshi.android.core.utils.DialogUtils;
import com.malalaoshi.android.dialogs.CommentDialog;
import com.malalaoshi.android.entity.Comment;
import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.ui.widgets.DoubleAvatarView;

/**
 * 评论
 * Created by tianwei on 16-6-12.
 */
public class CommentAdapter extends BaseRecycleAdapter<CommentAdapter.CommentViewHolder, Course> {

    private FragmentManager fragmentManager;

    public CommentAdapter(Context context) {
        super(context);
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_my_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        final Course course = getItem(position);
        Teacher teacher = course.getTeacher();
        Teacher lecturer = course.getLecturer();
        if (course.is_live()){
            holder.iconView.setVisibility(View.GONE);
            holder.ivLiveCourseAvator.setVisibility(View.VISIBLE);
            if (teacher!=null){
                holder.teacherView.setText(lecturer.getName());
                holder.ivLiveCourseAvator.setRightCircleImage(teacher.getAvatar(), R.drawable.ic_default_avatar);
                holder.ivLiveCourseAvator.setLeftCircleImage(lecturer.getAvatar(), R.drawable.ic_default_avatar);
            }else{
                holder.teacherView.setText("");
                holder.ivLiveCourseAvator.setRightCircleImage("", R.drawable.ic_default_avatar);
                holder.ivLiveCourseAvator.setLeftCircleImage("", R.drawable.ic_default_avatar);
            }
        }else{
            holder.iconView.setVisibility(View.VISIBLE);
            holder.ivLiveCourseAvator.setVisibility(View.GONE);
            if (teacher!=null){
                holder.teacherView.setText(teacher.getName());
                holder.iconView.loadCircleImage(teacher.getAvatar(), R.drawable.ic_default_avatar);
            }else{
                holder.teacherView.setText("");
                holder.iconView.loadCircleImage("", R.drawable.ic_default_avatar);
            }
        }

        if (course.getComment() != null) {
            setCommentedUI(holder, course);
        } else if (course.is_expired()) {
            setExpiredComment(holder);
        } else {
            setNoCommentUI(holder, course);
        }
        holder.gradeView.setText(course.getGrade() + " " + course.getSubject());
        holder.dateView.setText(formatCourseDate(course.getStart()));
        holder.timeView.setText(formatCourseTime(course.getStart(), course.getEnd()));
        holder.locationView.setText(course.getSchool());
        holder.tvAddress.setText(course.getSchool_address());
    }

    private String formatCourseDate(long ms) {
        return DateUtils.formatNoHyphenDate(ms * 1000);
    }

    private String formatCourseTime(long start, long end) {
        return DateUtils.formatHourMin(start * 1000) + "~" + DateUtils.formatHourMin(end * 1000);
    }

    private void setExpiredComment(final CommentViewHolder holder) {
        holder.teacherView.setTextColor(getColor(R.color.color_black_939393));
        Drawable drawable = holder.teacherView.getResources().getDrawable(R.drawable.ic_avatar_icon_black);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        holder.teacherView.setCompoundDrawables(drawable,null,null,null);

        holder.stateView.setText("过期");
        holder.stateView.setBackgroundResource(R.drawable.ic_comment_expired);
        holder.commentView.setBackground(null);
        holder.commentView.setText("评价已过期");
        holder.commentView.setTextColor(getColor(R.color.color_black_939393));
        holder.ratingbar.setVisibility(View.GONE);
    }

    private void setCommentedUI(final CommentViewHolder holder, final Course course) {
        holder.teacherView.setTextColor(getColor(R.color.color_blue_8fbcdd));
        Drawable drawable = holder.teacherView.getResources().getDrawable(R.drawable.ic_avatar_icon);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        holder.teacherView.setCompoundDrawables(drawable,null,null,null);

        holder.stateView.setText("已评");
        holder.stateView.setBackgroundResource(R.drawable.ic_commented);
        holder.commentView.setBackgroundResource(R.drawable.bg_blue_border_btn);
        holder.commentView.setText("查看评价");
        holder.commentView.setTextColor(getColor(R.color.color_blue_82b4d9));
        holder.ratingbar.setVisibility(View.VISIBLE);
        if (course.is_expired() && course.getComment() == null) {
            holder.ratingbar.setRating(0);
            return;
        }
        holder.ratingbar.setRating(course.getComment().getScore());
        holder.commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComment(holder, course);
            }
        });
    }

    private void setNoCommentUI(final CommentViewHolder holder, final Course course) {
        holder.teacherView.setTextColor(getColor(R.color.color_blue_8fbcdd));
        Drawable drawable = holder.teacherView.getResources().getDrawable(R.drawable.ic_avatar_icon);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        holder.teacherView.setCompoundDrawables(drawable,null,null,null);

        holder.stateView.setText("待评");
        holder.stateView.setBackgroundResource(R.drawable.ic_no_comment);
        holder.commentView.setText("去评价");
        holder.commentView.setBackgroundResource(R.drawable.bg_red_border_btn);
        holder.commentView.setTextColor(getColor(R.color.color_red_e26254));
        holder.ratingbar.setVisibility(View.GONE);
        holder.commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComment(holder, course);
            }
        });
    }

    private void openComment(final CommentViewHolder holder, final Course course) {
        if (course.is_live()){
            openLiveCourseComment(holder,course);
        }else{
            openNormalComment(holder,course);
        }
    }

    private void openNormalComment(final CommentViewHolder holder, final Course course) {
        String teacherName = course.getTeacher() == null ? "" : course.getTeacher().getName();
        String teacherIcon = course.getTeacher() == null ? "" : course.getTeacher().getAvatar();

        CommentDialog commentDialog = CommentDialog
                .newInstance(teacherName, teacherIcon, course.getSubject(), Long.valueOf(course.getId()),
                        course.getComment());
        commentDialog.SetOnCommentResultListener(new CommentDialog.OnCommentResultListener() {
            @Override
            public void onSuccess(Comment response) {
                course.setComment(response);
                setCommentedUI(holder, course);
            }
        });
        if (fragmentManager != null) {
            DialogUtils.showDialog(fragmentManager, commentDialog, "comment_dialog");
        }
    }

    private void openLiveCourseComment(final CommentViewHolder holder, final Course course) {
        String LecturerName = course.getLecturer() == null ? "" : course.getLecturer().getName();
        String LecturerIcon = course.getLecturer() == null ? "" : course.getLecturer().getAvatar();
        String assistName = course.getTeacher() == null ? "" : course.getTeacher().getName();
        String assistIcon = course.getTeacher() == null ? "" : course.getTeacher().getAvatar();

        CommentDialog commentDialog = CommentDialog
                .newInstance(LecturerName, LecturerIcon,assistName,assistIcon, course.getSubject(), Long.valueOf(course.getId()),
                        course.getComment());
        commentDialog.SetOnCommentResultListener(new CommentDialog.OnCommentResultListener() {
            @Override
            public void onSuccess(Comment response) {
                course.setComment(response);
                setCommentedUI(holder, course);
            }
        });
        if (fragmentManager != null) {
            DialogUtils.showDialog(fragmentManager, commentDialog, "comment_dialog");
        }
    }

    private int getColor(int rid) {
        return context.getResources().getColor(rid);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public static final class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView teacherView;
        private TextView gradeView;
        private TextView timeView;
        private TextView dateView;
        private TextView locationView;
        private TextView tvAddress;
        private TextView stateView;
        private MalaImageView iconView;
        private TextView commentView;
        private RatingBar ratingbar;
        private DoubleAvatarView ivLiveCourseAvator;
        public CommentViewHolder(View view) {
            super(view);
            teacherView = (TextView) view.findViewById(R.id.tv_teacher);
            gradeView = (TextView) view.findViewById(R.id.tv_grade);
            timeView = (TextView) view.findViewById(R.id.tv_time);
            dateView = (TextView) view.findViewById(R.id.tv_date);
            locationView = (TextView) view.findViewById(R.id.tv_location);
            tvAddress = (TextView) view.findViewById(R.id.tv_address);
            stateView = (TextView) view.findViewById(R.id.tv_status);
            iconView = (MalaImageView) view.findViewById(R.id.iv_icon);
            commentView = (TextView) view.findViewById(R.id.tv_comment);
            ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);
            ivLiveCourseAvator = (DoubleAvatarView) view.findViewById(R.id.iv_live_course_avator);
        }
    }
}
