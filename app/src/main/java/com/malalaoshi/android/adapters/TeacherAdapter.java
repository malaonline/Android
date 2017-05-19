package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.TeacherInfoActivity;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.utils.Number;
import com.malalaoshi.android.utils.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;


public class TeacherAdapter extends BaseRecycleAdapter<TeacherAdapter.ViewHolder, Teacher> {


    public TeacherAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.teacher_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.teacher_list_item_name)
        protected TextView name;

        @Bind(R.id.teacher_list_item_level)
        protected TextView level;

        @Bind(R.id.teacher_list_item_avater)
        protected MalaImageView avatar;

        @Bind(R.id.teacher_list_item_price)
        protected TextView price;

        @Bind(R.id.teacher_list_item_tags)
        protected TextView tags;

        @Bind(R.id.teacher_list_item_subjects)
        protected TextView subjects;

        protected Teacher teacher;

        protected View view;

        protected ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        protected void update(Teacher teacher) {
            view.setOnClickListener(this);
            this.teacher = teacher;
            level.setText("T" + teacher.getLevel());
            name.setText(teacher.getName());
            String sub = teacher.getSubject();
            String gradeStr = teacher.getGrades_shortname();
            if (gradeStr != null && !gradeStr.equals("") && sub != null && !sub.equals("")) {
                subjects.setText(gradeStr + " · " + sub);
            } else {
                if (gradeStr == null || gradeStr.equals("")) {
                    subjects.setText(sub);
                } else if (sub == null || sub.equals("")) {
                    subjects.setText(gradeStr);
                }
            }
            String tagStr = StringUtil.join(teacher.getTags());
            if (!EmptyUtils.isEmpty(tagStr)) {
                tags.setText(tagStr);
            }
            String imgUrl = teacher.getAvatar();
            avatar.loadCircleImage(imgUrl, R.drawable.ic_default_avatar);

            String priceRange = "价格异常";
            Double minPrice = teacher.getMin_price();
            Double maxPrice = teacher.getMax_price();
            if (minPrice != null && maxPrice != null) {
                priceRange = Number.subZeroAndDot(minPrice * 0.01d) + "-" + Number.subZeroAndDot(maxPrice * 0.01d);
            }
            price.setText(priceRange);

        }

        @Override
        public void onClick(View v) {
            TeacherInfoActivity.open(this.view.getContext(), teacher != null ? teacher.getId() : null);
        }
    }

}
