package app.beermecum.com.beermecum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.beermecum.com.beermecum.data.BeerContract;

public class MyDetailView extends RelativeLayout implements LoaderManager.LoaderCallbacks<Cursor> {


    static final int COL_BEER_ROW_ID = 0;
    static final int COL_BEER_ID = 1;
    static final int COL_BEER_NAME = 2;
    static final int COL_BEER_DESCRIPTION = 3;
    static final int COL_BEER_ABV = 4;
    static final int COL_BREWERIE_ROW_ID = 5;
    static final int COL_BREWERIE_ID = 6;
    static final int COL_BREWERIE_NAME = 7;
    static final int COL_BREWERIE_URL = 8;
    static final int COL_LIKE = 9;
    private static final String[] BEER_COLUMNS = {
            BeerContract.BeerEntry.TABLE_NAME + "." + BeerContract.BeerEntry._ID,
            BeerContract.BeerEntry.TABLE_NAME + "." + BeerContract.BeerEntry.COLUMN_BEER_ID,
            BeerContract.BeerEntry.COLUMN_NAME,
            BeerContract.BeerEntry.COLUMN_DESCRIPTION,
            BeerContract.BeerEntry.COLUMN_ABV,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry._ID,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry.COLUMN_BREWERIES_ID,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry.COLUMN_NAME,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry.COLUMN_URL,
            BeerContract.LikeEntry.COLUMN_LIKE
    };
    private long rowId = -1;
    private long beerId = -1;
    private EnumLike actualState = EnumLike.NOT_YET;

    private TextView mBeerNameTextView;
    private TextView mBeerDescriptionTextView;
    private TextView mBrewerieNameTextView;
    private TextView mBeerAbvTextView;

    private ImageView detailBeerIcon;
    private ImageView detailBrewerieIcon;


    private Button mButtonLike;
    private Button mButtonDislike;

    public MyDetailView(Context context) {
        super(context);
    }

    public MyDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        init();
    }

    private void init() {
        this.mBeerNameTextView = (TextView) findViewById(R.id.detail_beer_name);
        this.mBeerDescriptionTextView = (TextView) findViewById(R.id.detail_beer_description);
        this.mBrewerieNameTextView = (TextView) findViewById(R.id.detail_breweries_name);
        this.mBeerAbvTextView = (TextView) findViewById(R.id.detail_beer_abv);
        this.detailBrewerieIcon = (ImageView) findViewById(R.id.detail_breweries_icon);
        this.detailBeerIcon = (ImageView) findViewById(R.id.detail_beer_icon);

        this.mButtonLike = (Button) findViewById(R.id.buttonLike);
        mButtonLike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToLike();
            }
        });

        this.mButtonDislike = (Button) findViewById(R.id.buttonDislike);
        mButtonDislike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToDisLike();
            }
        });

    }

    private void changeToLike() {
        if (actualState == EnumLike.LIKE) {
            changeOpinion(EnumLike.NOT_YET);
        } else {
            changeOpinion(EnumLike.LIKE);
        }
    }

    private void changeToDisLike() {
        if (actualState == EnumLike.DISLIKE) {
            changeOpinion(EnumLike.NOT_YET);
        } else {
            changeOpinion(EnumLike.DISLIKE);
        }
    }

    private void changeOpinion(EnumLike opinion) {
        ContentValues likeValue = new ContentValues();

        likeValue.put(BeerContract.LikeEntry.COLUMN_BEER_ID, beerId);
        likeValue.put(BeerContract.LikeEntry.COLUMN_LIKE, opinion.toInt());
        getContext().getContentResolver().insert(BeerContract.LikeEntry.CONTENT_URI, likeValue);

        actualState = opinion;
        updateButton();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri weatherForLocationUri = BeerContract.BeerEntry.buildBeerUri(rowId);
        return new CursorLoader(
                this.getContext(),
                weatherForLocationUri,
                BEER_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateDetails(data);
    }


    private void updateButton() {
        if (actualState == EnumLike.LIKE) {
            mButtonLike.setBackgroundResource(R.drawable.ic_like);
            mButtonDislike.setBackgroundResource(R.drawable.ic_dislike_off);
        } else if (actualState == EnumLike.DISLIKE) {
            mButtonLike.setBackgroundResource(R.drawable.ic_like_off);
            mButtonDislike.setBackgroundResource(R.drawable.ic_dislike);
        } else {
            mButtonLike.setBackgroundResource(R.drawable.ic_like_off);
            mButtonDislike.setBackgroundResource(R.drawable.ic_dislike_off);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void updateDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {

            beerId = data.getLong(COL_BEER_ID);

            String beerName = data.getString(COL_BEER_NAME);
            mBeerNameTextView.setText(beerName);

            String beerDescription = data.getString(COL_BEER_DESCRIPTION);
            mBeerDescriptionTextView.setText(beerDescription);

            String brewerieName = data.getString(COL_BREWERIE_NAME);
            mBrewerieNameTextView.setText(brewerieName);

            String abvText = data.getString(COL_BEER_ABV);
            mBeerAbvTextView.setText(abvText);

            this.detailBrewerieIcon.setBackgroundResource(R.drawable.breweries_ico);
            this.detailBeerIcon.setBackgroundResource(R.drawable.beerr_icon);

            if (data.isNull(COL_LIKE)) {
                actualState = EnumLike.NOT_YET;
            } else {
                actualState = EnumLike.fromInt(data.getInt(COL_LIKE));
            }
            updateButton();
        }
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }
}
