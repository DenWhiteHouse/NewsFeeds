package com.example.android.newsfeeds;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Loader;
import android.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.view.View.GONE;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private TextView mErrorMessage;
    private RecyclerView mRecyclerView;
    private NewsAdapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String GUARDIAN_URL = "http://content.guardianapis.com/search";
    private static final String APIKEY = "057236e3-074c-46bb-b661-a941adbf9b04";

    // OPTION SETTINGS METHODS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Linking the temporary Views to the main Views of the activity_main*/
        mErrorMessage = (TextView) findViewById(R.id.error_TextView);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /*Checking the connection and in case creating the needed views*/
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        /*If the connection is present, the Adapter and Loader are set for the activities*/
        if (isConnected) {
            mErrorMessage.setVisibility(View.GONE);
            mRecyclerAdapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());
            mRecyclerView.setAdapter(mRecyclerAdapter);
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, MainActivity.this);
        /* If there isn't connection the Visibility of the progressBar and a message shows the NO INTERNET Status*/
        } else {
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.progress_bar);
            loadingSpinner.setVisibility(GONE);
            String message = "@string/noConnection";
            mErrorMessage.setText(message);
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    //Opens the connections on click
    public void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public static class NewsLoader extends AsyncTaskLoader<List<News>> {
        //TAG message
        private final String LOG_TAG = NewsLoader.class.getName();
        //Query URL
        private String mUrl;
        //Constructing a new NewsLoader passing the context (Main) and the URL)
        public NewsLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }
        @Override
        protected void onStartLoading() {
            forceLoad();
        }
        //Background Activity
        @Override
        public List<News> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            // Perform the network request, parse the response, and extract a list of news.
            List<News> news = Utils.fetchNewsData(mUrl);
            return news;
        }
    }

    //Create the Loader initializing the needed params to get the content from the API
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String section = sharedPrefs.getString(getString(R.string.section), getString(R.string.section));
        String listSize = sharedPrefs.getString(getString(R.string.listSize), getString(R.string.listSizeDefault));
        Uri baseUri = Uri.parse(GUARDIAN_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", section);
        uriBuilder.appendQueryParameter("page-size", listSize);
        uriBuilder.appendQueryParameter("api-key", APIKEY);
        return new NewsLoader(this, uriBuilder.toString());
    }

    // Set the activities for the END of the Loader Functions
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newses) {
        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.progress_bar);
        loadingSpinner.setVisibility(GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerAdapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());
        if (newses != null && !newses.isEmpty()) {
            mRecyclerAdapter = new NewsAdapter(MainActivity.this, newses);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }
    }

    // Resets the Loader by cleaning the Recycler
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mRecyclerAdapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());
    }
}
