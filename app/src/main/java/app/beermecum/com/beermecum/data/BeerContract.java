/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.beermecum.com.beermecum.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class BeerContract {

    public static final String CONTENT_AUTHORITY = "app.beermecum.com.beermecum";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_BEER = "beer";
    public static final String PATH_BREWERIES = "breweries";

    public static final String PATH_LIKES = "likes";

    /* Inner class that defines the table contents of the breweries table */
    public static final class BreweriesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BREWERIES).build();
        public static final String TABLE_NAME = "breweries";
        public static final String COLUMN_BREWERIES_ID = "breweries_id";
        public static final String COLUMN_NAME = "breweries_name";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BREWERIES;
        public static final String COLUMN_URL = "url";

        public static Uri buildBreweriesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getBreweriesIdFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BREWERIES;


    }

    public static final class LikeEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIKES).build();
        public static final String TABLE_NAME = "likes";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIKES;
        public static final String COLUMN_BEER_ID = "beer_id";
        public static final String COLUMN_LIKE = "like";

        public static Uri buildLikeByBeerId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getLikeIdFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }


    }

    public static final class BeerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BEER).build();
        public static final String TABLE_NAME = "beer";
        public static final String COLUMN_BEER_ID = "beer_id";
        public static final String COLUMN_BREWERIES_KEY = "breweries";
        public static final String COLUMN_NAME = "beer_name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEER;
        public static final String COLUMN_ABV = "abv";

        public static Uri buildBeerUri(long beerId) {
            return ContentUris.withAppendedId(CONTENT_URI, beerId);
        }

        public static Uri buildBeerById(String beerId) {
            return CONTENT_URI.buildUpon().appendPath(beerId).build();
        }

        public static Uri buildBeerBreweries(String breweriesId) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_BEER).appendPath(PATH_BREWERIES).appendPath(breweriesId).build();
        }

        public static String getBeerIdFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEER;

        public static String getBreweriesIdFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }


    }
}
