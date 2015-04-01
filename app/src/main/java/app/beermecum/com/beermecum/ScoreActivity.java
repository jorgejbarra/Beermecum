package app.beermecum.com.beermecum;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.beermecum.com.beermecum.data.BeerContract;


public class ScoreActivity extends ActionBarActivity {


    public static final int POINT_FOR_BEER = 175;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        int countBeer = getTotalBeer();

        int countLike = getNumOfOpinion();

        ProgressBar mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        mProgress.setMax(countBeer * POINT_FOR_BEER);
        mProgress.setProgress(countLike * POINT_FOR_BEER);

        TextView pointDisplay = (TextView) findViewById(R.id.pointDisplay);
        pointDisplay.setText(String.valueOf("Congratulations, You have drunk " + countLike + " different beers"));

        TextView progressDisplay = (TextView) findViewById(R.id.progressDisplay);
        progressDisplay.setText(String.valueOf(countLike * 175) + " Point!");
    }

    private int getTotalBeer() {
        Cursor countCursorBeer = getContentResolver().query(BeerContract.BeerEntry.CONTENT_URI,
                new String[]{"count(*) AS count"},
                null,
                null,
                null);

        countCursorBeer.moveToFirst();
        int countcBeer = countCursorBeer.getInt(0);
        countCursorBeer.close();
        return countcBeer;
    }

    private int getNumOfOpinion() {
        Cursor countCursorLike = getContentResolver().query(BeerContract.LikeEntry.CONTENT_URI,
                new String[]{"count(*) AS count"},
                null,
                null,
                null);

        countCursorLike.moveToFirst();
        int countLike = countCursorLike.getInt(0);
        countCursorLike.close();
        return countLike;
    }
}
