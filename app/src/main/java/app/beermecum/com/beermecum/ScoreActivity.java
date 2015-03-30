package app.beermecum.com.beermecum;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.beermecum.com.beermecum.data.BeerContract;


public class ScoreActivity extends ActionBarActivity {
    private ProgressBar mProgress;
    private TextView progressDisplay;
    private TextView pointDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Cursor countCursorBeer = getContentResolver().query(BeerContract.BeerEntry.CONTENT_URI,
                new String[]{"count(*) AS count"},
                null,
                null,
                null);

        countCursorBeer.moveToFirst();
        int countcBeer = countCursorBeer.getInt(0);

        Cursor countCursorLike = getContentResolver().query(BeerContract.LikeEntry.CONTENT_URI,
                new String[]{"count(*) AS count"},
                null,
                null,
                null);

        countCursorLike.moveToFirst();
        int countLike = countCursorLike.getInt(0);

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        mProgress.setMax(countcBeer * 175);
        mProgress.setProgress(countLike * 175);

        pointDisplay = (TextView) findViewById(R.id.pointDisplay);
        pointDisplay.setText(String.valueOf("Congratulations, You have drunk " + countLike + " different beers"));

        progressDisplay = (TextView) findViewById(R.id.progressDisplay);
        progressDisplay.setText(String.valueOf(countLike * 175) + " Point!");
    }
}
