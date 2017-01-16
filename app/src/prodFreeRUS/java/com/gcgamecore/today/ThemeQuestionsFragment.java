package com.gcgamecore.today;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.gcgamecore.today.Data.DB_Answers;
import com.gcgamecore.today.Data.DB_FavoriteThemeQuestions;
import com.gcgamecore.today.Data.DB_LastQuestionInTheme;
import com.gcgamecore.today.Data.DB_ThemeQuestion;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Utility.Utility;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.j256.ormlite.stmt.QueryBuilder;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ThemeQuestionsFragment extends BaseFragment {

    private static final String LOG_TAG = ThemeQuestionsFragment.class.getSimpleName();
    private static final int SHARE_ACTIVITY_REQUEST_CODE = 1002;
    private long theme_id;
    private List<DB_ThemeQuestion> question_list;
    private DB_ThemeQuiz current_theme;

    private long current_question_index = 0;
    private long last_question_index = 0;

    public long currentAnswer = -1;

    @BindView(R.id.textTheme)
    TextView headLine;

    @BindView(R.id.textQuestion)
    TextView question_text;

    @BindView(R.id.textAnswerOne)
    TextView one_answer_text;

    @BindView(R.id.textAnswerTwo)
    TextView two_answer_text;

    @BindView(R.id.answerDescription)
    TextView answerDescription;

    @BindView(R.id.layout_answer_one)
    LinearLayout one_layout_answer;

    @BindView(R.id.layout_answer_two)
    LinearLayout two_layout_answer;

    @BindView(R.id.imageAnswerOne)
    ImageView one_image_answer;
    @BindView(R.id.imageAnswerTwo)
    ImageView two_image_answer;

    @BindView(R.id.imgButtonFavorite)
    ImageButton imgButtonFavorite;

    @BindView(R.id.imgButtonShare)
    ImageButton imgButtonShare;

    @BindView(R.id.finishGameDescription)
    LinearLayout finishGameDescription;

    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    @BindView(R.id.gameLayout)
    ScrollView gameLayout;

    @BindView(R.id.finishLayout)
    RelativeLayout finishLayout;

    @BindView(R.id.textFinishMessage)
    TextView textFinishMessage;

    @BindView(R.id.ImageBackground)
    ImageView ImageBackground;

    @BindView(R.id.textResultView)
    TextView textResultView;

    @BindView(R.id.imageButtonBACK)
    Button imageButtonBACK;

    @BindView(R.id.imageButtonNEXT)
    Button imageButtonNEXT;

    @BindView(R.id.img_sign)
    ImageView img_sign;

    @BindView(R.id.topHeaderImage)
    ImageView topHeaderImage;


    Drawable drw_answerOneOriginal;
    Drawable drw_answerOneLoser;
    Drawable drw_answerOneWinner;

    Drawable drw_answerTwoOriginal;
    Drawable drw_answerTwoLoser;
    Drawable drw_answerTwoWinner;

    Drawable drw_favorite_on;
    Drawable drw_favorite_off;

    Drawable drw_status_0;
    Drawable drw_status_1;
    Drawable drw_status_2;
    Drawable drw_status_3;
    private Drawable drw_happy_face;
    private Drawable drw_sad_face;
    private Drawable drw_usual_face;
    private int clickCounter;
    private InterstitialAd mInterstitialAd;

    public interface Callback {
        void onFinishGame();
        void onAnswer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        topHeaderImage.setImageDrawable(drw_usual_face);
    }

    public ThemeQuestionsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.theme_questions_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();

        headLine.setTypeface(custom_font_bold);
        question_text.setTypeface(custom_font_regular);
        one_answer_text.setTypeface(custom_font_regular);
        two_answer_text.setTypeface(custom_font_regular);
        answerDescription.setTypeface(custom_font_regular);
        textFinishMessage.setTypeface(custom_font_regular);
        textResultView.setTypeface(custom_font_bold);

        imageButtonBACK.setTypeface(custom_font_regular);
        imageButtonNEXT.setTypeface(custom_font_regular);

        drw_answerOneOriginal = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_original);
        drw_answerOneLoser = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_loser);
        drw_answerOneWinner = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_winner);
        drw_answerTwoOriginal = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_original);
        drw_answerTwoLoser = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_loser);
        drw_answerTwoWinner = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_winner);

        drw_favorite_on = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_on);
        drw_favorite_off = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_off);

        drw_status_0 = ContextCompat.getDrawable(getContext(), R.drawable.ic_status_element0);
        drw_status_1 = ContextCompat.getDrawable(getContext(), R.drawable.ic_status_element1);
        drw_status_2 = ContextCompat.getDrawable(getContext(), R.drawable.ic_status_element2);
        drw_status_3 = ContextCompat.getDrawable(getContext(), R.drawable.ic_status_element3);

        drw_happy_face = ContextCompat.getDrawable(getContext(), R.drawable.ic_happy_face);
        drw_sad_face = ContextCompat.getDrawable(getContext(), R.drawable.ic_sad_face);
        drw_usual_face = ContextCompat.getDrawable(getContext(), R.drawable.ic_header);

        imageButtonNEXT.setCompoundDrawablesWithIntrinsicBounds(
                null
                , null
                , ContextCompat.getDrawable(getContext(), R.drawable.ic_right)
                , null);

        imageButtonBACK.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_left)
                , null
                , null
                , null);

        if (arguments != null) {
            theme_id = arguments.getLong(MainActivity.KEY_THEME_ID);

            current_theme = mDatabaseHelper.getThemeQuizDataDao().queryForId(theme_id);
        } else {
            theme_id = -1;
        }

        try {
            question_list = mDatabaseHelper.getThemeQuizQuestionsDataDao().queryBuilder()
                    .orderBy(DB_ThemeQuestion.CREATED_AT, true)
                    .where()
                    .eq(DB_ThemeQuestion.THEME, theme_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        last_question_index = 0;

        if (theme_id != -1) {
            try {
                DB_LastQuestionInTheme last_q = mDatabaseHelper.getLastQuestionDataDao().queryBuilder()
                        .where()
                        .eq(DB_LastQuestionInTheme.THEME_ID, theme_id).queryForFirst();

                if (last_q != null) {

                    for (int i = 0; i < question_list.size(); i++) {
                        if (question_list.get(i).getId() == last_q.getQuestion_id()) {
                            last_question_index = i;
                        }
                    }
                } else {
                    last_question_index = -1;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (question_list.size() > 0) {
            if ( last_question_index >= 0) {
                if (last_question_index < question_list.size() - 1)
                    current_question_index = last_question_index + 1;
                else
                    current_question_index = last_question_index;
            } else {
                last_question_index = 0;
                current_question_index = last_question_index;
            }
        }

        initHeadLine();

        if( isQuestionAnswered(question_list.get((int) current_question_index).getId(), pLanguage) /*current_question_index != question_list.size() - 1*/) {
            initOpenQuestion((int) current_question_index);
        }else{
            initNexQuestion();
        }

        gameLayout.setVisibility(View.VISIBLE);
        finishLayout.setVisibility(View.GONE);

        clickCounter = 0;

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-6013004568754454/9028854127");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        return rootView;
    }

    private void initHeadLine() {
        if (theme_id == -1) {
            return;
        }

        headLine.setText(current_theme.getName());

        if (current_theme.getTheme_image() != null) {

            Drawable targetImage = Utility.getImageFromAsset(current_theme.getTheme_background_image(), getContext());

            if(targetImage != null){
                ImageBackground.setImageDrawable(targetImage);
            }else{
                Picasso.with(getContext()).load(Utility.BASE_URL + current_theme.getTheme_background_image())
                        .into(ImageBackground);
            }
        }

        imgButtonFavorite.setVisibility(View.VISIBLE);
        imgButtonShare.setVisibility(View.VISIBLE);

        topHeaderImage.setImageDrawable(drw_usual_face);
    }

    private int getIndexQuestionInList(DB_ThemeQuestion pQuest){
        int index = -1;
        for (int i = 0; i < question_list.size(); i++) {
            if (question_list.get(i).getId() == pQuest.getId()) {
                index = i;
            }
        }
        return index;
    }

    private boolean isQuestionAnswered(long questionId, String lang) {

        DB_LastQuestionInTheme lastQuestion = null;
        try {
            lastQuestion = mDatabaseHelper.getLastQuestionDataDao().queryBuilder()
                    .where()
                    .eq(DB_LastQuestionInTheme.THEME_ID, theme_id)
                    .queryForFirst();

            DB_ThemeQuestion check_question = mDatabaseHelper.getThemeQuizQuestionsDataDao().queryBuilder()
                    .where()
                    .eq(DB_ThemeQuestion.ID, questionId)
                    .and()
                    .eq(DB_ThemeQuiz.LANGUAGE, lang).queryForFirst();

            if(check_question == null){
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lastQuestion != null) {
            long answeredQuestion = lastQuestion.getQuestion_id();

            DB_ThemeQuestion answeredQuest = mDatabaseHelper.getThemeQuizQuestionsDataDao().queryForId(answeredQuestion);
            DB_ThemeQuestion curQuest = mDatabaseHelper.getThemeQuizQuestionsDataDao().queryForId(questionId);

            int index_cur_question = getIndexQuestionInList(curQuest);
            int index_ans_question = getIndexQuestionInList(answeredQuest);

            if(index_cur_question <= index_ans_question){
                return true;
            }else{
                return false;
            }
        } else {
            return false;
        }
    }

    private void initNextPrevButtons(long position) {
        if (position == 0)
            imageButtonBACK.setVisibility(View.INVISIBLE);
        else
            imageButtonBACK.setVisibility(View.VISIBLE);

        boolean isAnswered = isQuestionAnswered(question_list.get((int) current_question_index).getId(), pLanguage);
        if( isAnswered){
            imageButtonNEXT.setVisibility(View.VISIBLE);
        }else{
            imageButtonNEXT.setVisibility(View.INVISIBLE);
        }

        if(current_question_index == question_list.size()-1 && !isAnswered){
            imageButtonNEXT.setVisibility(View.INVISIBLE);
        }

        if (position == question_list.size() - 1)
            imageButtonNEXT.setText(getString(R.string.finish));
        else
            imageButtonNEXT.setText(getString(R.string.next));
    }

    private void initNexQuestion() {
        if (theme_id == -1) {
            return;
        }

        topHeaderImage.setImageDrawable(drw_usual_face);

        initNextPrevButtons(current_question_index);

        DB_ThemeQuestion current_question = question_list.get((int) current_question_index);

        question_text.setText(current_question.getQuestion());
        one_answer_text.setText(current_question.getAnswer1());
        two_answer_text.setText(current_question.getAnswer2());
        answerDescription.setText(current_question.getDescription());

        finishGameDescription.setVisibility(View.GONE);

        one_layout_answer.setVisibility(View.VISIBLE);
        two_layout_answer.setVisibility(View.VISIBLE);

        one_layout_answer.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGray));
        two_layout_answer.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGray));

        one_image_answer.setImageDrawable(drw_answerOneOriginal);
        two_image_answer.setImageDrawable(drw_answerTwoOriginal);

        one_answer_text.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorTextGray));
        two_answer_text.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorTextGray));

        DB_FavoriteThemeQuestions isFavorite = null;
        try {
            isFavorite = mDatabaseHelper.getFavoriteDataDao().queryBuilder().where()
                    .eq(DB_FavoriteThemeQuestions.THEME_ID, theme_id)
                    .and()
                    .eq(DB_FavoriteThemeQuestions.QUESTION_ID, current_question.getId())
                    .queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (isFavorite != null)
            imgButtonFavorite.setImageDrawable(drw_favorite_on);
        else
            imgButtonFavorite.setImageDrawable(drw_favorite_off);
    }

    private void initOpenQuestion(int pQuestionNumber) {
        if (theme_id == -1) {
            return;
        }

        initNextPrevButtons(pQuestionNumber);

        DB_ThemeQuestion current_question = question_list.get(pQuestionNumber);

        question_text.setText(current_question.getQuestion());
        one_answer_text.setText(current_question.getAnswer1());
        two_answer_text.setText(current_question.getAnswer2());
        answerDescription.setText(current_question.getDescription());

        finishGameDescription.setVisibility(View.GONE);

        one_layout_answer.setVisibility(View.VISIBLE);
        two_layout_answer.setVisibility(View.VISIBLE);

        one_layout_answer.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGray));
        two_layout_answer.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGray));

        one_image_answer.setImageDrawable(drw_answerOneOriginal);
        two_image_answer.setImageDrawable(drw_answerTwoOriginal);

        one_answer_text.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorTextGray));
        two_answer_text.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorTextGray));

        DB_FavoriteThemeQuestions isFavorite = null;
        try {
            isFavorite = mDatabaseHelper.getFavoriteDataDao().queryBuilder().where()
                    .eq(DB_FavoriteThemeQuestions.THEME_ID, theme_id)
                    .and()
                    .eq(DB_FavoriteThemeQuestions.QUESTION_ID, current_question.getId())
                    .queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (isFavorite != null)
            imgButtonFavorite.setImageDrawable(drw_favorite_on);
        else
            imgButtonFavorite.setImageDrawable(drw_favorite_off);


        try {
            DB_Answers answer_state = mDatabaseHelper.getAnswerDataDao().queryBuilder().where()
                    .eq(DB_Answers.THEME_ID, theme_id)
                    .and().eq(DB_Answers.QUESTION_ID, current_question.getId())
                    .queryForFirst();

            if(answer_state != null) {
                if (answer_state.getAnswer() == 1) {
                    currentAnswer = 1;
                    ShowResult(true);
                } else {
                    currentAnswer = 2;
                    ShowResult(false);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.layout_answer_one)
    public void oneQuestionClick() {
        if (currentAnswer == -1) {
            currentAnswer = 1;
            applyAnswer();
        }
    }

    @OnClick(R.id.layout_answer_two)
    public void twoQuestionClick() {
        if (currentAnswer == -1) {
            currentAnswer = 2;
            applyAnswer();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void applyAnswer() {

        if (mInterstitialAd.isLoaded()) {

            if(clickCounter >= 3){
                mInterstitialAd.show();
                clickCounter = 0;
            }else{
                clickCounter+=1;
            }
        }

        DB_ThemeQuestion current_question = question_list.get((int) current_question_index);
        boolean flag_winner = false;
        if (currentAnswer == current_question.getRight_answer()) {
            flag_winner = true;
        }

        // Save answer
        DB_Answers current_answer;

        //ask if exist
        QueryBuilder<DB_Answers, Long> qb = mDatabaseHelper.getAnswerDataDao().queryBuilder();
        try {
            qb.where().eq(DB_Answers.THEME_ID, theme_id).and().eq(DB_Answers.QUESTION_ID, current_question.getId());
            current_answer = qb.queryForFirst();
            if (current_answer != null) {
                // tell the user to enter unique values
                if (flag_winner)
                    current_answer.setAnswer(1);
                else
                    current_answer.setAnswer(0);

                mDatabaseHelper.getAnswerDataDao().update(current_answer);
            } else {
                current_answer = new DB_Answers();
                current_answer.setTheme_id(theme_id);
                current_answer.setQuestion_id(current_question.getId());

                if (flag_winner)
                    current_answer.setAnswer(1);
                else
                    current_answer.setAnswer(0);

                mDatabaseHelper.getAnswerDataDao().create(current_answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switch ((int) currentAnswer) {
            case 1:
                gameLayout.scrollTo(0, question_text.getHeight());
                break;
            case 2:
                gameLayout.scrollTo(0, question_text.getHeight());
                break;
        }

        ShowResult(flag_winner);

        // Save last question
        try {
            DB_LastQuestionInTheme lastQuestion = mDatabaseHelper.getLastQuestionDataDao().queryBuilder()
                    .where()
                    .eq(DB_LastQuestionInTheme.THEME_ID, theme_id)
                    .queryForFirst();

            long q = current_question.getId();
//            if (current_question_index == question_list.size() - 1) {
//                //q =0;
////                if (lastQuestion != null)
////                    mDatabaseHelper.getLastQuestionDataDao().delete(lastQuestion);
//            } else {
                if (lastQuestion != null) {
                    lastQuestion.setQuestion_id(q);
                    mDatabaseHelper.getLastQuestionDataDao().createOrUpdate(lastQuestion);
                } else {
                    lastQuestion = new DB_LastQuestionInTheme();
                    lastQuestion.setTheme_id(theme_id);
                    lastQuestion.setQuestion_id(q);
                    mDatabaseHelper.getLastQuestionDataDao().createIfNotExists(lastQuestion);
                }
//            }

            last_question_index = getIndexQuestionInList(current_question);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((ThemeQuestionsFragment.Callback) getActivity()).onAnswer();

        initNextPrevButtons(current_question_index);
//        imageButtonNEXT.setVisibility(View.VISIBLE);
    }

    public void ShowResult(boolean flag_winner) {
        // Show right answer
        switch ((int) currentAnswer) {
            case 1:
                hideResultPanels(one_layout_answer, one_image_answer, drw_answerOneLoser, drw_answerOneWinner, one_answer_text, flag_winner);
                two_layout_answer.setVisibility(View.GONE);
                break;
            case 2:
                hideResultPanels(two_layout_answer, two_image_answer, drw_answerTwoLoser, drw_answerTwoWinner, two_answer_text, flag_winner);
                one_layout_answer.setVisibility(View.GONE);
                break;
        }
    }

    public void hideResultPanels(View view, ImageView img_view, Drawable drw_loser, Drawable drw_winner, TextView caption, boolean winner) {

        caption.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorWhite));
        if (winner) {
            view.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGreen));
            img_view.setImageDrawable(drw_winner);
            textResultView.setText(getString(R.string.winner_text));
            textResultView.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorGreen));
            topHeaderImage.setImageDrawable(drw_happy_face);
        } else {
            view.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorRed));
            img_view.setImageDrawable(drw_loser);
            textResultView.setText(getString(R.string.looser_text));
            textResultView.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorRed));
            topHeaderImage.setImageDrawable(drw_sad_face);
        }

        finishGameDescription.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.imgButtonFavorite)
    public void onClickAddFavorite() {
        DB_ThemeQuestion current_question = question_list.get((int) current_question_index);
        DB_FavoriteThemeQuestions favorite_item;

        QueryBuilder<DB_FavoriteThemeQuestions, Long> qb = mDatabaseHelper.getFavoriteDataDao().queryBuilder();
        try {
            qb.where().eq(DB_FavoriteThemeQuestions.THEME_ID, theme_id).and().eq(DB_FavoriteThemeQuestions.QUESTION_ID, current_question.getId());
            favorite_item = qb.queryForFirst();

            if (favorite_item == null) {

                favorite_item = new DB_FavoriteThemeQuestions();
                favorite_item.setTheme_id(theme_id);
                favorite_item.setQuestion_id(current_question.getId());

                mDatabaseHelper.getFavoriteDataDao().createIfNotExists(favorite_item);
                imgButtonFavorite.setImageDrawable(drw_favorite_on);
                Toast.makeText(getActivity(), R.string.success_add_to_fav, Toast.LENGTH_SHORT).show();
            } else {
                mDatabaseHelper.getFavoriteDataDao().delete(favorite_item);
                imgButtonFavorite.setImageDrawable(drw_favorite_off);
                Toast.makeText(getActivity(), R.string.delete_from_favorite, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.imgButtonShare)
    public void onClickShare() {

        DB_ThemeQuestion current_question = question_list.get((int) current_question_index);

        Intent newShareActivity = new Intent(getActivity(), ShareActivity.class);
        newShareActivity.putExtra(Utility.KEY_THEME_NAME, current_theme.getName());
        newShareActivity.putExtra(Utility.KEY_QUESTION, current_question.getQuestion());

        newShareActivity.putExtra(Utility.KEY_THEME_ID, current_theme.getId());
        newShareActivity.putExtra(Utility.KEY_QUESTION_ID, current_question.getId());

        if (current_theme.getTheme_image() != null) {
            newShareActivity.putExtra(Utility.KEY_BACKGROUND_IMAGE_URL, Utility.BASE_URL + current_theme.getTheme_background_image());
        }

        startActivityForResult(newShareActivity, SHARE_ACTIVITY_REQUEST_CODE);

//        DB_ThemeQuestion current_question = question_list.get((int) current_question_index);
//        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
//
//        String file_name = String.format("ToDay theme-%d q-%d.jpg", theme_id, current_question.getId());
//
//        Bitmap bmp = Utility.getScreenShot(rootView);
//        File file = Utility.store(bmp, file_name);
//        Utility.shareImage(file, getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SHARE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String newText = data.getStringExtra("FullFileName");
                File dir = new File(newText);

                Utility.shareImage(dir, getActivity());
            }
        }
    }

    private void ShowFinishScreen() {
        gameLayout.setVisibility(View.GONE);
        finishLayout.setVisibility(View.VISIBLE);

        imgButtonFavorite.setVisibility(View.INVISIBLE);
        imgButtonShare.setVisibility(View.INVISIBLE);

        long numQuestions = 0;
        long numRightAnswers = 0;

        numQuestions = question_list.size();

        long res = 0;
        try {
            numRightAnswers = mDatabaseHelper.getAnswerDataDao().queryBuilder()
                    .where()
                    .eq(DB_Answers.ANSWER, 1)
                    .and()
                    .eq(DB_Answers.THEME_ID, theme_id)
                    .countOf();

            if (numQuestions != 0)
                res = (long) (((float) numRightAnswers / numQuestions) * 100);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String finish_message = String.format(getEndGameMessage(), String.valueOf(numRightAnswers));

        if (res <= 69) {
            img_sign.setImageDrawable(drw_status_1);
        } else if (res >= 70 && res <= 89) {
            img_sign.setImageDrawable(drw_status_2);
        } else {
            img_sign.setImageDrawable(drw_status_3);
        }

        textFinishMessage.setText(finish_message);
    }

    private String getEndGameMessage() {

        return getString(R.string.finish_round_description);
    }

    @OnClick(R.id.btnFinish)
    public void onClickFinishGame() {

        try {
            DB_LastQuestionInTheme lastQuestion = mDatabaseHelper.getLastQuestionDataDao().queryBuilder()
                    .where()
                    .eq(DB_LastQuestionInTheme.THEME_ID, theme_id)
                    .queryForFirst();

            if (lastQuestion != null)
                mDatabaseHelper.getLastQuestionDataDao().delete(lastQuestion);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((ThemeQuestionsFragment.Callback) getActivity()).onFinishGame();
    }

    @OnClick(R.id.imageButtonBACK)
    protected void OnBACKClick() {

        gameLayout.scrollTo(0, 0);

        if (current_question_index > 0)
            current_question_index -= 1;

        initOpenQuestion((int) current_question_index);
    }

    @OnClick(R.id.imageButtonNEXT)
    protected void OnNEXTClick() {
        if (currentAnswer == -1)
            return;

        gameLayout.scrollTo(0, 0);

        if (current_question_index == question_list.size() - 1) {
            ShowFinishScreen();
        } else if (last_question_index > current_question_index) {
            current_question_index += 1;
            initOpenQuestion((int) current_question_index);
        } else {
            current_question_index += 1;
            currentAnswer = -1;
            initNexQuestion();
        }
    }
}
