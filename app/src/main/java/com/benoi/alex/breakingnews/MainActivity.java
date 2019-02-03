package com.benoi.alex.breakingnews;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BreakingNews>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int FEED_NEWS_LOADER_ID = 1;
    private static final int NEWS_LOADER_ID = 1;
    private static final String GUARDIAN_URL = "http://content.guardianapis.com/search";
    private static final String GUARDIANS_TECHNOLOGY_URL = "https://www.theguardian.com/uk/technology";
    private BreakingNewsAdapter breakingNewsAdapter;
    private ListView newsListView;
    private TextView emptyStateTextView;
    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyStateTextView = findViewById(R.id.empty_state);
        loadingIndicator = findViewById(R.id.loading_indicator);
        newsListView = findViewById(R.id.list);

        breakingNewsAdapter = new BreakingNewsAdapter(this, new ArrayList<BreakingNews>());

        newsListView.setAdapter(breakingNewsAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(getString(R.string.no_internet_connection));
            emptyStateTextView.setVisibility(View.VISIBLE);
        }

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BreakingNews currentNews = breakingNewsAdapter.getItem(position);
                if (currentNews != null) {
                    String url = currentNews.getUrl();
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    if (url != null) {
                        webIntent.setData(Uri.parse(url));
                    } else {
                        webIntent.setData(Uri.parse(GUARDIANS_TECHNOLOGY_URL));
                    }
                    startActivity(webIntent);
                }
            }
        });
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_page_key)) ||
                key.equals(getString(R.string.settings_interest_key))) {
            breakingNewsAdapter.clear();

            emptyStateTextView.setVisibility(View.GONE);

            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(FEED_NEWS_LOADER_ID, null, this);
        }
    }

    public Loader<List<BreakingNews>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String pageNumber = sharedPrefs.getString(
                getString(R.string.settings_page_key),
                getString(R.string.settings_page_default));

        String yourInterested = sharedPrefs.getString(
                getString(R.string.settings_interest_key),
                getString(R.string.settings_page_default));

        Uri baseUri = Uri.parse(GUARDIAN_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("section", "technology");
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("lang", "en");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("show-fields", "byline");
        uriBuilder.appendQueryParameter("page-size", pageNumber);
        uriBuilder.appendQueryParameter("q", yourInterested);
        uriBuilder.appendQueryParameter("api-key", "478170bf-8755-43a2-847e-56eae7523b54");
        return new BreakingNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<BreakingNews>> loader, List<BreakingNews> news) {

        loadingIndicator.setVisibility(View.GONE);
        emptyStateTextView.setText(getString(R.string.no_news_found));
        newsListView.setEmptyView(emptyStateTextView);
        breakingNewsAdapter.clear();
        if (news != null && !news.isEmpty()) {
            breakingNewsAdapter.addAll(news);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<BreakingNews>> loader) {
        breakingNewsAdapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, Settings.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
