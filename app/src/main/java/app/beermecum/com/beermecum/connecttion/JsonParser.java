package app.beermecum.com.beermecum.connecttion;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import app.beermecum.com.beermecum.data.BeerContract;

public class JsonParser {

    public JsonParser() {
    }

    public Vector<ContentValues> getBreweriesDataFromJson(String breweriesString)
            throws JSONException {

        // Breweries information
        final String OWM_BREWERIES_ID = "id";
        final String OWM_BREWERIES_NAME = "name";
        final String OWM_BREWERIES_URL = "url";

        final String OWM_LIST = "breweries";


        JSONObject forecastJson = new JSONObject(breweriesString);
        JSONArray breweriesArray = forecastJson.getJSONArray(OWM_LIST);

        Vector<ContentValues> breweriesVector = new Vector<>(breweriesArray.length());

        for (int i = 0; i < breweriesArray.length(); i++) {

            JSONObject breweriesJSONObject = breweriesArray.getJSONObject(i);

            long breweriesId = breweriesJSONObject.getLong(OWM_BREWERIES_ID);
            String breweriesName = breweriesJSONObject.getString(OWM_BREWERIES_NAME);
            String breweriesUrl = breweriesJSONObject.getString(OWM_BREWERIES_URL);

            ContentValues weatherValues = new ContentValues();
            weatherValues.put(BeerContract.BreweriesEntry.COLUMN_BREWERIES_ID, breweriesId);
            weatherValues.put(BeerContract.BreweriesEntry.COLUMN_NAME, breweriesName);
            weatherValues.put(BeerContract.BreweriesEntry.COLUMN_URL, breweriesUrl);

            breweriesVector.add(weatherValues);
        }


        return breweriesVector;
    }

    public Vector<ContentValues> getBeerDataFromJson(String beerJsonStr)
            throws JSONException {

        // beer information
        final String OWM_BEER_ID = "id";
        final String OWM_BEER_NAME = "name";
        final String OWM_BEER_DESCRIPTION = "description";
        final String OWM_BEER_ABV = "abv";

        final String OWM_BREWERIES_ID = "id";
        final String OWM_BREWERIES = "brewery";

        final String OWM_LIST = "beers";


        JSONObject forecastJson = new JSONObject(beerJsonStr);
        JSONArray beerArray = forecastJson.getJSONArray(OWM_LIST);

        Vector<ContentValues> beerVector = new Vector<ContentValues>(beerArray.length());

        for (int i = 0; i < beerArray.length(); i++) {

            JSONObject breweriesJSONObject = beerArray.getJSONObject(i);

            long beerId = breweriesJSONObject.getLong(OWM_BEER_ID);
            String beerName = breweriesJSONObject.getString(OWM_BEER_NAME);
            String beerDescription = breweriesJSONObject.getString(OWM_BEER_DESCRIPTION);
            double beerAbv = breweriesJSONObject.getDouble(OWM_BEER_ABV);

            JSONObject brewery = breweriesJSONObject.getJSONObject(OWM_BREWERIES);
            long breweryId = brewery.getLong(OWM_BREWERIES_ID);

            ContentValues weatherValues = new ContentValues();
            weatherValues.put(BeerContract.BeerEntry.COLUMN_BEER_ID, beerId);
            weatherValues.put(BeerContract.BeerEntry.COLUMN_NAME, beerName);
            weatherValues.put(BeerContract.BeerEntry.COLUMN_DESCRIPTION, beerDescription);
            weatherValues.put(BeerContract.BeerEntry.COLUMN_ABV, beerAbv);
            weatherValues.put(BeerContract.BeerEntry.COLUMN_BREWERIES_KEY, breweryId);

            beerVector.add(weatherValues);
        }


        return beerVector;
    }
}