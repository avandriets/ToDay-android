package com.gcgamecore.today.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcgamecore.today.Data.DB_Answers;
import com.gcgamecore.today.Data.DB_FavoriteThemeQuestions;
import com.gcgamecore.today.Data.DB_ThemeQuestion;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.R;
import com.gcgamecore.today.Utility.Utility;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ArchiveRecyclerViewAdapter extends OrmliteCursorRecyclerViewAdapter<DB_ThemeQuiz, ArchiveRecyclerViewAdapter.ArchiveViewHolder>{

    private final SimpleDateFormat dateFormatter;
    private final Drawable drw_status_1;
    private final Drawable drw_status_2;
    private final Drawable drw_status_3;
    private final RuntimeExceptionDao<DB_FavoriteThemeQuestions, Long> questions_favorite_dao;
    private final RuntimeExceptionDao<DB_Answers, Long> answers_dao;
    private final RuntimeExceptionDao<DB_ThemeQuestion, Long> questions_dao;
    DatabaseHelper mDatabaseHelper = null;
    int type; // 1 архив 2 ибранное 3 события

    final private ArchiveAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;

    private Typeface custom_font_regular;
    private Typeface custom_font_bold;
    private Typeface custom_font_times;

    public static interface ArchiveAdapterOnClickHandler {
        void onClick(Long pId, ArchiveViewHolder vh);
    }


    public class ArchiveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvHeader;
        public TextView tvDate;
        public TextView tvCount;
        public ImageView ivShevron;

        public ArchiveViewHolder(View itemView) {
            super(itemView);

            tvHeader    = (TextView) itemView.findViewById(R.id.themeHeader);
            tvDate      = (TextView) itemView.findViewById(R.id.themeDate);
            tvCount     = (TextView) itemView.findViewById(R.id.theme_questions);
            ivShevron = (ImageView) itemView.findViewById(R.id.img_sign);

            tvHeader.setTypeface(custom_font_bold);
            tvDate.setTypeface(custom_font_regular);
            tvCount.setTypeface(custom_font_regular);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            getCursor().moveToPosition(adapterPosition);
            int dateColumnIndex = getCursor().getColumnIndex(DB_ThemeQuiz.ID);
            mClickHandler.onClick(getCursor().getLong(dateColumnIndex), this);
        }
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        Cursor cur = super.swapCursor(newCursor);
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        return cur;
    }

    public ArchiveRecyclerViewAdapter(Context context, ArchiveAdapterOnClickHandler dh, View emptyView, DatabaseHelper mDatabaseHelper, int type) {
        super(context);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        this.mDatabaseHelper = mDatabaseHelper;
        this.type = type;

        questions_favorite_dao =  mDatabaseHelper.getFavoriteDataDao();
        answers_dao =  mDatabaseHelper.getAnswerDataDao();
        questions_dao =  mDatabaseHelper.getThemeQuizQuestionsDataDao();

        mClickHandler   = dh;
        mEmptyView      = emptyView;

        //Init fonts
        custom_font_regular = Typeface.createFromAsset(context.getAssets(), "fonts/Book Antiqua Regular.ttf");
        custom_font_bold = Typeface.createFromAsset(context.getAssets(), "fonts/Book Antiqua Bold.ttf");
        custom_font_times = Typeface.createFromAsset(context.getAssets(), "fonts/Times New Roman Cyr Italic.ttf");

        drw_status_1 = ContextCompat.getDrawable(context, R.drawable.ic_status_element1);
        drw_status_2 = ContextCompat.getDrawable(context, R.drawable.ic_status_element2);
        drw_status_3 = ContextCompat.getDrawable(context, R.drawable.ic_status_element3);
    }

    @Override
    public ArchiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ArchiveViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View vElementItem = inflater.inflate(R.layout.archive_list_item, parent, false);
        viewHolder = new ArchiveViewHolder(vElementItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArchiveViewHolder holder, DB_ThemeQuiz theme_quiz) {

        holder.tvHeader.setText(theme_quiz.getName());

        Calendar c = Calendar.getInstance();
        c.setTime(theme_quiz.getTarget_date());

        String date_of_theme = String.format("%s %d, %d  ",
                Utility.getMonthForInt(c.get(Calendar.MONTH)),
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.YEAR));

        holder.tvDate.setText( date_of_theme );

        if(type == 2) {
            long numberRecords = 0;
            try {
                numberRecords = questions_favorite_dao.queryBuilder().where().eq(DB_FavoriteThemeQuestions.THEME_ID, theme_quiz.getId()).countOf();
                holder.tvCount.setText(String.valueOf(numberRecords));
            } catch (SQLException e) {
                e.printStackTrace();
                holder.tvCount.setText("");
            }
        }
        else
            holder.tvCount.setText("");

        try {
            long numberThemeQuestions = questions_dao.queryBuilder().where().eq(DB_ThemeQuestion.THEME, theme_quiz.getId()).countOf();
            long countRightAnswers = answers_dao.queryBuilder().where().eq(DB_Answers.THEME_ID, theme_quiz.getId()).and().eq(DB_Answers.ANSWER,1) .countOf();

            long res = 0;
            if(numberThemeQuestions != 0)
                res = (long)(((float)countRightAnswers / numberThemeQuestions) * 100);

            if(res <= 69){
                holder.ivShevron.setImageDrawable(drw_status_1);
            }else if(res >= 70 && res <=89 ){
                holder.ivShevron.setImageDrawable(drw_status_2);
            }else{
                holder.ivShevron.setImageDrawable(drw_status_3);
            }

        } catch (SQLException e) {
            holder.ivShevron.setImageDrawable(drw_status_1);
            e.printStackTrace();
        }
    }
}
