package com.gcgamecore.today;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Utility.Utility;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ThemeFragment extends BaseFragmentWithHeader {

    @BindView(R.id.background_image)
    ImageView background_image;

    @BindView(R.id.headLine)
    TextView headLine;

    @BindView(R.id.leadText)
    TextView leadText;

    @BindView(R.id.themeIntroduction)
    View themeIntroduction;

    @BindView(R.id.emptyTheme)
    View emptyTheme;

    @BindView(R.id.textViewEmpty)
    TextView textViewEmpty;

    public interface Callback {
        void onStartGame(Long themeId);
    }

    private static final String LOG_TAG = ThemeFragment.class.getSimpleName();
    private long theme_id;
    private DB_ThemeQuiz current_theme = null;

    public ThemeFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.theme_preview_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        headLine.setTypeface(custom_font_bold);
        leadText.setTypeface(custom_font_regular);
        textViewEmpty.setTypeface(custom_font_regular);

        Bundle arguments = getArguments();

        if (arguments != null) {
            theme_id = arguments.getLong(MainActivity.KEY_POINT_ID);
            current_theme = mDatabaseHelper.getThemeQuizDataDao().queryForId(theme_id);
        } else {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);


            Date today_date = c.getTime();

            List<DB_ThemeQuiz> theme_list = mDatabaseHelper.getThemeQuizDataDao().queryForEq(DB_ThemeQuiz.TARGET_DATE, today_date);
            DB_ThemeQuiz main_theme = null;
            for (DB_ThemeQuiz cc : theme_list) {
                current_theme = cc;

                if (cc.getMain_theme()) {
                    main_theme = cc;
                }
            }

            if(main_theme != null){
                current_theme = main_theme;
            }

            if(current_theme != null)
                theme_id = current_theme.getId();
        }


        initTheme();
        InitHeader();

        return rootView;
    }

    private void initTheme() {

        if(current_theme != null) {
            headLine.setText(current_theme.getName());
            leadText.setText(current_theme.getDescription());

            if (current_theme.getTheme_image() != null) {
                Picasso.with(getContext()).load(Utility.BASE_URL + current_theme.getTheme_image())
                        .placeholder(R.drawable.ic_oval_placeholder)
                        .error(R.drawable.ic_oval_placeholder)
                        .into(background_image);
            }

            themeIntroduction.setVisibility(View.VISIBLE);
            emptyTheme.setVisibility(View.GONE);
        }else{
            themeIntroduction.setVisibility(View.GONE);
            emptyTheme.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.start_game_button)
    public void startGame(ImageButton button) {
        ((Callback) getActivity()).onStartGame(theme_id);
    }
}
