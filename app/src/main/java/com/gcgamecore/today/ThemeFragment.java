package com.gcgamecore.today;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Utility.Utility;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThemeFragment extends BaseFragment {

    @BindView(R.id.background_image)
    ImageView background_image;

    @BindView(R.id.headLine)
    TextView headLine;

    @BindView(R.id.leadText)
    TextView leadText;


    private static final String LOG_TAG = ThemeFragment.class.getSimpleName();
    private long theme_id;
    private DB_ThemeQuiz current_theme = null;

    public ThemeFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.theme_preview_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);


        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Book Antiqua Regular.ttf");
        headLine.setTypeface(custom_font);
        leadText.setTypeface(custom_font);

        Bundle arguments = getArguments();

        if (arguments != null) {
            theme_id = arguments.getLong(MainActivity.KEY_POINT_ID);
        } else {
            theme_id = -1;
        }

        current_theme = mDatabaseHelper.getThemeQuizDataDao().queryForId(theme_id);

        initTheme();

        return rootView;
    }

    private void initTheme() {

        headLine.setText(current_theme.getName());
        leadText.setText(current_theme.getDescription());

        if (current_theme.getTheme_image() != null) {
            Picasso.with(getContext()).load(Utility.BASE_URL + current_theme.getTheme_image())
                    .into(background_image);
        }

    }
}
