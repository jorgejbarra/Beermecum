package app.beermecum.com.beermecum;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.beermecum.com.beermecum.data.BeerContract;

/**
 * {@link BeerAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class BeerAdapter extends CursorAdapter {

    static final int COL_BEER_ID = 1;
    static final int COL_BEER_NAME = 2;
    static final int COL_BEER_ABV = 4;
    static final int COL_BREWERIE_NAME = 7;

    private static final String[] BEER_COLUMNS = {
            BeerContract.BeerEntry.TABLE_NAME + "." + BeerContract.BeerEntry._ID,
            BeerContract.BeerEntry.TABLE_NAME + "." + BeerContract.BeerEntry.COLUMN_BEER_ID,
            BeerContract.BeerEntry.COLUMN_NAME,
            BeerContract.BeerEntry.COLUMN_DESCRIPTION,
            BeerContract.BeerEntry.COLUMN_ABV,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry._ID,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry.COLUMN_BREWERIES_ID,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry.COLUMN_NAME,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry.COLUMN_URL
    };


    public static class ViewHolder {
        public final TextView beerNameView;
        public final TextView brewerieNameView;
        public final TextView abvTempView;

        public ViewHolder(View view) {
            beerNameView = (TextView) view.findViewById(R.id.list_item_beer_name);
            brewerieNameView = (TextView) view.findViewById(R.id.list_item_breweries_name);
            abvTempView = (TextView) view.findViewById(R.id.list_item_beer_abv);
        }
    }

    public BeerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static String[] getBeerColumns() {
        return BEER_COLUMNS;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_beer, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read date from cursor
        String beerName = cursor.getString(COL_BEER_NAME);
        viewHolder.beerNameView.setText(beerName);

        String brewerieName = cursor.getString(COL_BREWERIE_NAME);
        viewHolder.brewerieNameView.setText(brewerieName);

        double beerAbv = cursor.getDouble(COL_BEER_ABV);
        viewHolder.abvTempView.setText(Double.toString(beerAbv));
    }


}