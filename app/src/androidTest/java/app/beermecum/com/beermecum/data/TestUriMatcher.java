package app.beermecum.com.beermecum.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    private static final int TEST_BREWERIES_ID = 10;

    private static final Uri TEST_BEER_DIR = BeerContract.BeerEntry.CONTENT_URI;
    private static final Uri TEST_BEER_BY_ID = BeerContract.BeerEntry.buildBeerBreweries(Integer.toString(TEST_BREWERIES_ID));
    private static final Uri TEST_WEATHER_WITH_BREWERIES_DIR = BeerContract.BeerEntry.buildBeerBreweries(Integer.toString(TEST_BREWERIES_ID));
    private static final Uri TEST_BREWERIES_DIR = BeerContract.BreweriesEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = BeerProvider.buildUriMatcher();

        assertEquals("Error: The TEST_BEER_BY_ID was matched incorrectly.",
                testMatcher.match(TEST_BEER_BY_ID), BeerProvider.BEER_FOR_ID);
        assertEquals("Error: The TEST_BEER_DIR was matched incorrectly.",
                testMatcher.match(TEST_BEER_DIR), BeerProvider.BEER);
        assertEquals("Error: The TEST_WEATHER_WITH_BREWERIES_DIR URI was matched incorrectly.",
                testMatcher.match(TEST_WEATHER_WITH_BREWERIES_DIR), BeerProvider.BEER_WITH_BREWERIES);
        assertEquals("Error: The TEST_BREWERIES_DIR URI was matched incorrectly.",
                testMatcher.match(TEST_BREWERIES_DIR), BeerProvider.BREWERIES);
    }
}
