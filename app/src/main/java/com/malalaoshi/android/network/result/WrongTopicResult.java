package com.malalaoshi.android.network.result;

/**
 * 错题本科目
 * Created by donald on 2017/5/15.
 */

public class WrongTopicResult {

    /**
     * tocomment_num : 8
     * unpaid_num : 1
     * exercise_mistakes : {"numbers":{"total":10,"english":5,"math":5},"school":"北京正通店","student":"王小二"}
     */

    private int tocomment_num;
    private int unpaid_num;
    private ExerciseMistakesBean exercise_mistakes;

    public int getTocomment_num() {
        return tocomment_num;
    }

    public void setTocomment_num(int tocomment_num) {
        this.tocomment_num = tocomment_num;
    }

    public int getUnpaid_num() {
        return unpaid_num;
    }

    public void setUnpaid_num(int unpaid_num) {
        this.unpaid_num = unpaid_num;
    }

    public ExerciseMistakesBean getExercise_mistakes() {
        return exercise_mistakes;
    }

    public void setExercise_mistakes(ExerciseMistakesBean exercise_mistakes) {
        this.exercise_mistakes = exercise_mistakes;
    }

    public static class ExerciseMistakesBean {
        /**
         * numbers : {"total":10,"english":5,"math":5}
         * school : 北京正通店
         * student : 王小二
         */

        private NumbersBean numbers;
        private String school;
        private String student;

        public NumbersBean getNumbers() {
            return numbers;
        }

        public void setNumbers(NumbersBean numbers) {
            this.numbers = numbers;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getStudent() {
            return student;
        }

        public void setStudent(String student) {
            this.student = student;
        }

        public static class NumbersBean {
            /**
             * total : 10
             * english : 5
             * math : 5
             */

            private int total;
            private int english;
            private int math;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getEnglish() {
                return english;
            }

            public void setEnglish(int english) {
                this.english = english;
            }

            public int getMath() {
                return math;
            }

            public void setMath(int math) {
                this.math = math;
            }
        }
    }
}
