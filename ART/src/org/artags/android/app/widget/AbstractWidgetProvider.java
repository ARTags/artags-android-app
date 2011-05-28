/* Copyright (c) 2010-2011 ARTags Project owners (see http://www.artags.org)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.artags.android.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;
import org.artags.android.app.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Pierre LEVY
 */
public abstract class AbstractWidgetProvider extends AppWidgetProvider
{

    private long mStartTime;
    private int mRefreshDelay = 7;
    private List<Tag> mTagList;
    private int mCurrentTagIndex;
    private boolean mRunning;
    private Context mContext;
    private AppWidgetManager mAppWidgetManager;
    private int[] mAppWidgetIds;

    abstract String getTagListUrl();

    abstract void setCurrentTag(Tag tag);

    abstract Tag getCurrentTag();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        mContext = context;
        mAppWidgetManager = appWidgetManager;
        mAppWidgetIds = appWidgetIds;

        mFetchingTask.execute();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        if (Constants.ACTION_SHOW_TAG.equals(intent.getAction()))
        {
            Log.d(Constants.LOG_TAG, "onReceive - Action : " + intent.getAction());
            showTag(context);
        }
    }

    private void showTag(Context context)
    {
        Tag tag = getCurrentTag();
        String url = Constants.URL_JSP_TAG + tag.getId();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void updateTag(Tag tag)
    {
        setCurrentTag(tag);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget);
        remoteViews.setImageViewBitmap(R.id.widget_thumbnail, tag.getBitmap());
        remoteViews.setTextViewText(R.id.widget_text, tag.getText());
        Intent active = new Intent(mContext, getClass());
        active.setAction(Constants.ACTION_SHOW_TAG);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(mContext, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_thumbnail, actionPendingIntent);
        mAppWidgetManager.updateAppWidget(mAppWidgetIds, remoteViews);
        Log.d(Constants.LOG_TAG, "Widget updated");
    }
    private AsyncTask<Void, Void, Void> mFetchingTask = new AsyncTask<Void, Void, Void>()
    {

        @Override
        protected Void doInBackground(Void... args)
        {
            mTagList = getTagList();
            for (Tag tag : mTagList)
            {
                tag.setBitmap(HttpUtils.loadBitmap(tag.getThumbnailUrl()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Log.d(Constants.LOG_TAG, "Asynchronous Fetch Task completed");
            super.onPostExecute(result);
            mCurrentTagIndex = 0;
            mHandler.removeCallbacks(mUpdateTimeTask);

            if (!mRunning && !mTagList.isEmpty())
            {
                mUpdateTimeTask.run();
                mRunning = true;
            }
        }
    };
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable()
    {

        @Override
        public void run()
        {
            Log.d(Constants.LOG_TAG, "Run update thread");

            final long start = mStartTime;
            final long millis = SystemClock.uptimeMillis() - start;
            int seconds = (int) (millis / 1000);
            final int minutes = seconds / 60;
            seconds = seconds % 60;
            updateTag(mTagList.get(mCurrentTagIndex));
            mCurrentTagIndex++;
            if (mCurrentTagIndex >= mTagList.size())
            {
                mCurrentTagIndex = 0;
            }
            mHandler.postAtTime(this, start + (((minutes * 60) + seconds + mRefreshDelay) * 1000));
        }
    };

    private List<Tag> getTagList()
    {
        List<Tag> list = new ArrayList<Tag>();
        String jsonflow = HttpUtils.getUrl(getTagListUrl());

        try
        {

            JSONTokener tokener = new JSONTokener(jsonflow);
            JSONObject json = (JSONObject) tokener.nextValue();
            JSONArray jsonTags = json.getJSONArray("tags");

            int max = (jsonTags.length() < Constants.MAX_TAGS) ? jsonTags.length() : Constants.MAX_TAGS;
            for (int i = 0; i < max; i++)
            {
                JSONObject jsonTag = jsonTags.getJSONObject(i);
                Tag tag = new Tag();
                tag.setId(jsonTag.getString("id"));
                tag.setText(jsonTag.getString("title"));
                tag.setThumbnailUrl(jsonTag.getString("imageUrl"));
                tag.setRating(jsonTag.getString("rating"));
                list.add(tag);
            }
        } catch (JSONException e)
        {
            Log.e(Constants.LOG_TAG, "JSON Parsing Error : " + e.getMessage(), e);
        }
        return list;
    }
}
