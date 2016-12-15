package com.gcgamecore.today;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.gcgamecore.today.Utility.Utility;
import com.squareup.picasso.Picasso;
import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ShareActivity extends AppCompatActivity {

    private static final int KEY_SHARE_INTENT = 10001;
    @BindView(R.id.textTheme)
    TextView headLine;

    @BindView(R.id.textQuestion)
    TextView question_text;

    @BindView(R.id.ImageBackground)
    ImageView ImageBackground;
    private long theme_id;
    private long question_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        theme_id = intent.getLongExtra(Utility.KEY_THEME_ID, -1);
        question_id = intent.getLongExtra(Utility.KEY_QUESTION_ID, -1);

        String theme_name = intent.getStringExtra(Utility.KEY_THEME_NAME);
        String theme_question = intent.getStringExtra(Utility.KEY_QUESTION);

        String backGroundImage = "";

        if(intent.hasExtra(Utility.KEY_BACKGROUND_IMAGE_URL)){
            backGroundImage = intent.getStringExtra(Utility.KEY_BACKGROUND_IMAGE_URL);
        }

        headLine.setText(theme_name);
        question_text.setText(theme_question);

        if(backGroundImage.length() > 0){
            Picasso.with(this).load(backGroundImage)
                    .into(ImageBackground);
        }

        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.topLayout);
        layout.setDrawingCacheEnabled(true);
        layout.setDrawingCacheQuality(LinearLayout.DRAWING_CACHE_QUALITY_HIGH);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                getDrawingBitmap();
            }
        });
    }

    public void getDrawingBitmap(){
        RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.topLayout);
        Bitmap b = mainLayout.getDrawingCache();

        String file_name = String.format("ToDay theme-%d q-%d.jpg", theme_id, question_id);

        File file = Utility.store(b, file_name);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("FullFileName",file.getAbsolutePath());
        setResult(Activity.RESULT_OK,resultIntent);
        finish();
    }

}
