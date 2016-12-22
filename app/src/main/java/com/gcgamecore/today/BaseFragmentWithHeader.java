package com.gcgamecore.today;

import android.widget.TextView;
import com.gcgamecore.today.Data.DB_Answers;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import java.sql.SQLException;
import butterknife.BindView;


public class BaseFragmentWithHeader extends BaseFragment {

    @BindView(R.id.textQuestionCountCaption)
    protected TextView textQuestionCountCaption;

    @BindView(R.id.idQuestionsCount)
    protected TextView idQuestionsCount;

    @BindView(R.id.textPercentCaption)
    protected TextView textPercentCaption;

    @BindView(R.id.idPercentQuestions)
    protected TextView idPercentQuestions;

    protected void InitHeader() {

        textQuestionCountCaption.setTypeface(custom_font_regular);
        idQuestionsCount.setTypeface(custom_font_regular);
        textPercentCaption.setTypeface(custom_font_regular);
        idPercentQuestions.setTypeface(custom_font_regular);

        RuntimeExceptionDao<DB_Answers, Long> daoAnswers = mDatabaseHelper.getAnswerDataDao();
        long numQuestions = 0;
        long numRightAnswers = 0;


        numQuestions = daoAnswers.countOf();
        idQuestionsCount.setText(String.valueOf(numQuestions));


        long res = 0;
        try {
            numRightAnswers = daoAnswers.queryBuilder().where().eq(DB_Answers.ANSWER,1).countOf();

            if(numQuestions != 0)
                res = (long)(((float)numRightAnswers / numQuestions) * 100);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        idPercentQuestions.setText(String.valueOf(res)+"%" );

//        try {
//            // return the orders with the sum of their amounts per account
//            GenericRawResults<String[]> rawResults = daoAnswers.queryRaw("select sum(answer) from answers");
//            for (String[] resultArray : rawResults) {
//                System.out.println("Account-id " + resultArray[0] + " has "
//                        + resultArray[1] + " total orders");
//            }
//            rawResults.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}
