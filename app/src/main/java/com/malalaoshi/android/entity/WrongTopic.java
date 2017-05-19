package com.malalaoshi.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by donald on 2017/5/10.
 */

public class WrongTopic implements Serializable{

    /**
     * id : 353
     * question_group : {"id":56,"title":"数词","description":"数词"}
     * question : {"id":101,"title":"We can see _________stars in the sky at night in the countryside.","options":[{"id":401,"text":"million of"},{"id":402,"text":"three millions of"},{"id":403,"text":"millions of"},{"id":404,"text":"three million of"}],"solution":404,"explanation":"题目解析"}
     * submit_option : 403
     * updated_at : 1494490662
     */

    private int id;
    private QuestionGroup question_group;
    private Question question;
    private int submit_option;
    private int updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QuestionGroup getQuestion_group() {
        return question_group;
    }

    public void setQuestion_group(QuestionGroup question_group) {
        this.question_group = question_group;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getSubmit_option() {
        return submit_option;
    }

    public void setSubmit_option(int submit_option) {
        this.submit_option = submit_option;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public static class QuestionGroup implements Serializable{
        /**
         * id : 56
         * title : 数词
         * description : 数词
         */

        private int id;
        private String title;
        private String description;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "QuestionGroup{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    public static class Question implements Serializable{
        /**
         * id : 101
         * title : We can see _________stars in the sky at night in the countryside.
         * options : [{"id":401,"text":"million of"},{"id":402,"text":"three millions of"},{"id":403,"text":"millions of"},{"id":404,"text":"three million of"}]
         * solution : 404
         * explanation : 题目解析
         */

        private int id;
        private String title;
        private int solution;
        private String explanation;
        private List<TopicAnswer> options;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSolution() {
            return solution;
        }

        public void setSolution(int solution) {
            this.solution = solution;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public List<TopicAnswer> getOptions() {
            return options;
        }

        public void setOptions(List<TopicAnswer> options) {
            this.options = options;
        }

        @Override
        public String toString() {
            return "Question{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", solution=" + solution +
                    ", explanation='" + explanation + '\'' +
                    ", options=" + options +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WrongTopic{" +
                "id=" + id +
                ", question_group=" + question_group +
                ", question=" + question +
                ", submit_option=" + submit_option +
                ", updated_at=" + updated_at +
                '}';
    }
}
