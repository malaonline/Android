package com.malalaoshi.android.entity;

import android.content.res.Resources;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by donald on 2017/5/19.
 */

public class WrongTopicSamples {
    public static void getSamples(List<WrongTopic> wrongTopics) {
        Resources resources = MalaApplication.getInstance().getResources();
        String[] groupTitles = resources.getStringArray(R.array.topic_group_title);
        String[] groupDes = resources.getStringArray(R.array.topic_group_description);
        String[] questionTitles = resources.getStringArray(R.array.topic_question_title);
        int[] questionSolution = resources.getIntArray(R.array.topic_question_solution);
        String[] questionExplanation = resources.getStringArray(R.array.topic_question_explanation);

        String[] options1 = resources.getStringArray(R.array.topic_question_options_1);
        String[] options2 = resources.getStringArray(R.array.topic_question_options_2);
        String[] options3 = resources.getStringArray(R.array.topic_question_options_3);
        String[] options4 = resources.getStringArray(R.array.topic_question_options_4);
        String[] options5 = resources.getStringArray(R.array.topic_question_options_5);
        String[][] options = new String[][]{options1, options2, options3, options4, options5};

        for (int i = 0; i < 5; i++) {
            WrongTopic topic = new WrongTopic();
            WrongTopic.QuestionGroup questionGroup = new WrongTopic.QuestionGroup();
            WrongTopic.Question question = new WrongTopic.Question();
            questionGroup.setTitle(groupTitles[i]);
            questionGroup.setDescription(groupDes[i]);
            question.setTitle(questionTitles[i]);
            question.setSolution(questionSolution[i]);
            question.setExplanation(questionExplanation[i]);
            question.setOptions(getOptions(options[i]));
            topic.setQuestion_group(questionGroup);
            topic.setQuestion(question);
            wrongTopics.add(topic);
        }
    }

    private static List<TopicAnswer> getOptions(String[] options) {
        ArrayList<TopicAnswer> topicAnswers = new ArrayList<>();
        for (int j = 0; j < 4; j++) {
            TopicAnswer topicAnswer = new TopicAnswer();
            topicAnswer.setText(options[j]);
            topicAnswer.setId(65+j);
            topicAnswers.add(topicAnswer);
        }
        return topicAnswers;
    }
}
