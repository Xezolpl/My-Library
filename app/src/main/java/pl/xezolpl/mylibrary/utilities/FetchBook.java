package pl.xezolpl.mylibrary.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.SelectCoverActivity;
import pl.xezolpl.mylibrary.adapters.CoversRevViewAdapter;

/**
 * AsyncTask implementation that opens a network connection and
 * query's the Book Service API.
 */
public class FetchBook extends AsyncTask<String, Void, String> {
    private static final String TAG = "FetchBook";

    private CoversRevViewAdapter adapter;
    private List<String> covers = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    private int coversToSearch;

    public FetchBook(CoversRevViewAdapter adapter, Activity activity, int coversToSearch) {
        this.adapter = adapter;
        this.activity = activity;
        this.coversToSearch = coversToSearch;
    }

    /**
     * Makes the Books API call off of the UI thread.
     *
     * @param params String array containing the search data.
     * @return Returns the JSON string from the Books API or
     * null if the connection failed.
     */
    @Override
    protected String doInBackground(String... params) {

        // Get the search string
        String queryString = params[0];


        // Set up variables for the try block that need to be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        // Attempt to query the Books API.
        try {
            // Base URI for the Books API.
            final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";

            final String QUERY_PARAM = "q"; // Parameter for the search string.

            final String MAX_RESULTS = "maxResults"; // Parameter that limits search results.
            final String PRINT_TYPE = "printType"; // Parameter to filter by print type.

            // Build up your query URI, limiting results to 10 items and printed books.
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, String.valueOf(coversToSearch))
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();

            URL requestURL = new URL(builtURI.toString());

            // Open the network connection.
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Read the response string into a StringBuilder.
            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // but it does make debugging a *lot* easier if you print out the completed buffer for debugging.
                String nLine = line + "\n";
                builder.append(nLine);
            }

            if (builder.length() == 0) {
                // Stream was empty.  No point in parsing.
                // return null;
                return null;
            }
            bookJSONString = builder.toString();

            // Catch errors.
        } catch (FileNotFoundException e) {
            activity.runOnUiThread(() -> {
                if (coversToSearch == 10) {
                    Toast.makeText(activity, activity.getString(R.string.no_covers_found), Toast.LENGTH_LONG).show();
                    adapter.setBookCovers(new ArrayList<>());
                } else {
                    Toast.makeText(activity, activity.getString(R.string.no_more_covers), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the connections.
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        // Return the raw response.
        return bookJSONString;
    }

    /**
     * Handles the results on the UI thread. Gets the information from
     * the JSON and updates the Views.
     *
     * @param s Result from the doInBackground method containing the raw JSON response,
     *          or null if it failed.
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(s);
            // Get the JSONArray of book items.
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            // Initialize iterator and results fields.
            int i = 0;
            String url;

            // Look for results in the items array, exiting when both the title and author
            // are found or when all items have been checked.
            while (i < itemsArray.length()) {
                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    url = imageLinks.getString("smallThumbnail");
                    if (url != null) {
                        covers.add(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Move to the next item.
                i++;
            }
            activity.runOnUiThread(() -> {
                adapter.setBookCovers(covers);
                ((SelectCoverActivity)activity).setMoreCoversBtnVisible(true);
            });
        } catch (Exception e) {

            activity.runOnUiThread(() -> {
                if (coversToSearch == 10) {
                    Toast.makeText(activity, activity.getString(R.string.no_covers_found), Toast.LENGTH_LONG).show();
                    adapter.setBookCovers(new ArrayList<>());
                    ((SelectCoverActivity)activity).setMoreCoversBtnVisible(false);

                }
            });

            Log.e(TAG, "onPostExecute: ", e);
        }

    }
}
