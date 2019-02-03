package com.benoi.alex.breakingnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class BreakingNewsLoader extends AsyncTaskLoader<List<BreakingNews>> {

    private String url;

    BreakingNewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<BreakingNews> loadInBackground() {
        if (url == null) {
            return null;
        }
        return QueryUtils.fetchNewsData(url, getContext());
    }
}
