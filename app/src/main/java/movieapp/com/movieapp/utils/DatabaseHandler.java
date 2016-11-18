package movieapp.com.movieapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import movieapp.com.movieapp.movies.details.Movie;

/**
 * Created by Ahmed on 9/5/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies_favorites";
    private static final String TABLE_Movies = "movies";

    private static final String KEY_ID = "id";
    private static final String KEY_MOVIE_TITLE = "title";
    private static final String KEY_POSTER_URL = "poster_url";
    private static final String KEY_MOVIE_LENGTH = "length";
    private static final String KEY_MOVIE_DATE = "date";
    private static final String KEY_MOVIE_RATE = "rate";
    private static final String KEY_MOVIE_VOTE = "vote";
    private static final String KEY_MOVIE_INFO = "info";

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + TABLE_Movies + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_MOVIE_TITLE + " TEXT,"
                + KEY_POSTER_URL + " TEXT,"
                + KEY_MOVIE_LENGTH + " TEXT,"
                + KEY_MOVIE_RATE + " TEXT,"
                + KEY_MOVIE_VOTE + " TEXT,"
                + KEY_MOVIE_INFO + " TEXT,"
                + KEY_MOVIE_DATE + " TEXT"
                + ")";
        db.execSQL(CREATE_MOVIES_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Movies);

        // Create tables again
        onCreate(db);
    }

    public void addMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, movie.getId());
        values.put(KEY_MOVIE_TITLE, movie.getTitle());
        values.put(KEY_POSTER_URL, movie.getPoster_path());
        values.put(KEY_MOVIE_LENGTH, movie.getVote_count());
        values.put(KEY_MOVIE_DATE, movie.getRelease_date());
        values.put(KEY_MOVIE_RATE, movie.getPopularity());
        values.put(KEY_MOVIE_VOTE, movie.getVote_average());
        values.put(KEY_MOVIE_INFO, movie.getOverview());

        // Inserting Row
        db.insert(TABLE_Movies, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> contactList = new ArrayList<Movie>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_Movies;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(0)));
                movie.setTitle(cursor.getString(1));
                movie.setPoster_path(cursor.getString(2));
                movie.setVote_count(Integer.parseInt((cursor.getString(3))));
                movie.setRelease_date(cursor.getString(7));
                movie.setPopularity(Double.parseDouble((cursor.getString(4))));
                movie.setVote_average(Double.parseDouble((cursor.getString(5))));
                movie.setOverview(cursor.getString(6));

                // Adding contact to list
                contactList.add(movie);
            } while (cursor.moveToNext());
        }
        // return contact list
        return contactList;
    }

    // Deleting single contact
    public void deleteMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Movies, KEY_ID + " = ?",
                new String[] { String.valueOf(movie.getId()) });
        db.close();
    }

    public boolean isEmpty(){
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_Movies;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int countItems = cursor.getInt(0);
        Log.i("db count","" + countItems);
        db.close();
        if(countItems > 0)
        return false;
        return true;
    }

}
