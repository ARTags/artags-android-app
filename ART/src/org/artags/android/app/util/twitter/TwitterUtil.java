/* Copyright (c) 2010 ARTags project owners (see http://www.artags.org)
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
package org.artags.android.app.util.twitter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;

/**
 * Start the Twitter Application to send a tweet.
 * @author Pierre Levy
 */
public class TwitterUtil
{

    private static final String TWITTER_POST_ACTIVITY = "com.twitter.android.PostActivity";
    private static final String CONTENT_TYPE_TEXT = "text/plain";

    public static boolean send(Context context, String message)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType( CONTENT_TYPE_TEXT );
        final PackageManager pm = context.getPackageManager();
        final List activityList = pm.queryIntentActivities(intent, 0);
        int len = activityList.size();
        boolean bTwitterStarted = false;
        for (int i = 0; i < len; i++)
        {
            final ResolveInfo app = (ResolveInfo) activityList.get(i);
            if (TWITTER_POST_ACTIVITY.equals(app.activityInfo.name))
            {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                intent = new Intent(Intent.ACTION_SEND);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.setComponent(name);
                intent.putExtra(Intent.EXTRA_TEXT, message);
                context.startActivity(intent);
                bTwitterStarted = true;
                break;
            }
        }
        return bTwitterStarted;
    }
}
