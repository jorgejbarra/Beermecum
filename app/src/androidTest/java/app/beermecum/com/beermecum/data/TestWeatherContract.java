package app.beermecum.com.beermecum.data;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestWeatherContract extends AndroidTestCase {

    private static final String TEST_WEATHER_WITH_BREWERIES = "123213";

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildWeatherLocation() {
        Uri locationUri = BeerContract.BeerEntry.buildBeerBreweries(TEST_WEATHER_WITH_BREWERIES);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                TEST_WEATHER_WITH_BREWERIES, locationUri.getLastPathSegment());
        assertEquals("Error: Uri doesn't match",
                locationUri.toString(),
                "content://app.beermecum.com.beermecum/beer/breweries/123213");
    }
}
