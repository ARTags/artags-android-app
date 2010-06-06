package com.zmosoft.flickrfree;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.Window;


public class GlobalResources {
    public static String m_EDITPERMS_URL = "http://www.flickr.com/services/auth/list.gne";
    
	public static int API_DELAY_MS = 1000;
	public static int ERROR_DELAY_MS = 1000;

	static final int ADD_ACCOUNT_REQ = 1;
	static final int MANAGE_ACCOUNTS_REQ = 2;
	static final int IMGS_PER_PAGE = 20;
    static final int NRETRIES = 10;

    private static class UpdateProgressBar implements Runnable {

    	UpdateProgressBar(Activity activity, int progress) {
    		m_activity = activity;
    		m_progress = progress;
    	}
    	
		@Override
		public void run() {
			m_activity.setProgress(m_progress);
			if (m_progress == Window.PROGRESS_END) {
				m_activity.setProgressBarIndeterminateVisibility(false);
			}
		}
    	
		Activity m_activity;
		int m_progress;
    }
    
    public enum ImgSize {
    	SMALLSQUARE(0), THUMB(1), SMALL(2), MED(3), LARGE(4), ORIG(5);
    	
    	private int m_sizenum;
    	
    	private ImgSize(int i) {
    		m_sizenum = i;
    	}
    	
    	public int getNum() {
    		return m_sizenum;
    	}

    	public void setNum(int num) {
    		m_sizenum = num;
    	}
    	
    	public String toString() {
    		if (m_sizenum == 0) {
    			return "Small Square";
    		}
    		else if (m_sizenum == 1) {
    			return "Thumb";
    		}
    		else if (m_sizenum == 2) {
    			return "Small";
    		}
    		else if (m_sizenum == 3) {
    			return "Medium";
    		}
    		else if (m_sizenum == 4) {
    			return "Large";
    		}
    		else if (m_sizenum == 5) {
    			return "Original";
    		}
    		else {
    			return "Unknown";
    		}
    	}
    }
    
    public static boolean isAppUser(Activity a, String nsid) {
    	return (nsid != "" && a.getSharedPreferences("Auth",0).getString("nsid", "").equals(nsid));
    }
    
    public static String getDisplayName(String username, String realname) {
    	String displayname = "";
    	
    	if (!realname.equals("")) {
    		displayname = realname + " (" + username + ")";
    	}
    	else {
    		displayname = username;
    	}
    	
    	return displayname;
    }
    
    public static Bitmap getBitmapFromURL(String url) throws JSONException, IOException {
        Bitmap bm = null;
        URL aURL = new URL(url);
        URLConnection conn = aURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        bm = BitmapFactory.decodeStream(bis);
        bis.close();
        is.close();

        return bm;
    }
    
    public static String getImageURL(String farm, String server, String id, String secret, ImgSize size, String extension) {
		String img_url = "http://farm" + farm + ".static.flickr.com/" + server
						+ "/" + id + "_" + secret;
		if (size == ImgSize.SMALLSQUARE) {
			img_url = img_url + "_s";
		}
		else if (size == ImgSize.THUMB) {
			img_url = img_url + "_t";
		}
		else if (size == ImgSize.SMALL) {
			img_url = img_url + "_m";			
		}
		else if (size == ImgSize.LARGE) {
			img_url = img_url + "_b";
		}
		else if (size == ImgSize.ORIG) {
			img_url = img_url + "_o";
		}
		img_url = img_url + "." + extension;
		
		return img_url;
    }

    public static void downloadImage(String url, String filename, Activity callingActivity, boolean show_progress) throws MalformedURLException, IOException {
    	String dlpath = GetDownloadDir();
    	if (!dlpath.equals("")) {
    		downloadImage(url, filename, dlpath, callingActivity, show_progress);
    	}
    }
    
    public static void downloadImage(String url, String filename, String dlpath, Activity callingActivity, boolean show_progress) throws MalformedURLException, IOException {
    	if (filename.equals("")) {
    		filename = url.substring(url.lastIndexOf("/") + 1);
    	}
		URL u = new URL(url);
		URLConnection uc = u.openConnection();
		
		if (uc == null) {
			Log.e("flickrfree", "Failed to open connection while trying to download \"" + url + "\".");
		}
		
		double contentLength = (double)uc.getContentLength();
		double contentReceived = 0;

		if (uc.getContentType() == null || !uc.getContentType().contains("image")) {
			Log.e("flickrfree", "File at URL \"" + url + "\" is not an image.");
		}

		// Check to see if download directory exists. If not, create it.
		File download = new File(dlpath);
		if (!download.exists()) {
			download.mkdir();
		}

		InputStream in = uc.getInputStream();
		File f = new File(dlpath,filename);
		FileOutputStream imgfile = new FileOutputStream(f);
		byte[] buffer = new byte[1024];
		int len1 = 0;
		int progress;
		double maxProgress = (double)(Window.PROGRESS_END);
		while ((len1 = in.read(buffer)) != -1) {
			imgfile.write(buffer,0, len1);
			contentReceived += (double)1024;
			progress = (int)(maxProgress * contentReceived / contentLength);
			if (callingActivity != null && show_progress) {
				callingActivity.runOnUiThread(new GlobalResources.UpdateProgressBar(callingActivity, progress));
			}
		}

		if (callingActivity != null && show_progress) {
			callingActivity.runOnUiThread(new GlobalResources.UpdateProgressBar(callingActivity, Window.PROGRESS_END));
		}
		
		in.close();
		imgfile.close();
    }

    public static boolean CheckDir(String dir_name) {
    	boolean result = false;
    	
        if (dir_name != null && dir_name != "") {
    		File Dir = new File(dir_name);
    		if (Dir.exists() || Dir.mkdir()) {
    			result = true;
    		}
        }
        
        return result;
    }
    
    public static String GetAppDir() {
        // Check for the app directory. If it doesn't exist, create it.
        String app_dir = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
        ? Environment.getExternalStorageDirectory().toString() + "/FlickrFree/"
        : "";
        
        return CheckDir(app_dir) ? app_dir : "";
    }
    
    public static String GetCacheDir(Activity callingActivity) {
    	//String app_dir = GetAppDir();
    	
    	// If the app directory can be found, set the cache directory to be the subdirectory
    	// "cache" in the app directory. If not, it will be the default Android cache
    	// location for this app.
        //String cache_dir = (app_dir == null) || app_dir.equals("")
        //                   ? callingActivity.getCacheDir().getAbsolutePath()
        //		           : app_dir + "cache";
        return callingActivity.getCacheDir().getAbsolutePath();

        //return CheckDir(cache_dir) ? cache_dir : "";
    }
    
    public static String GetDownloadDir() {
    	String app_dir = GetAppDir();
    	
    	// If the app directory can be found, set the cache directory to be the subdirectory
    	// "download" in the app directory. If not, there is no download path -- files cannot
    	// be downloaded.
        String dl_dir = (app_dir == null) || app_dir.equals("")
				        ? ""
				        : app_dir + "download";

        return CheckDir(dl_dir) ? dl_dir : "";
    }
    
    public static String CachedImageFilename(String url) {
		return (url.replaceAll(":", "").replace("/", ""));
    }
    
    public static boolean CacheImage(String url, Activity callingActivity, boolean show_progress) throws MalformedURLException, IOException, InterruptedException {
		String filename = CachedImageFilename(url);
		String cachedir = GetCacheDir(callingActivity);
		File img_cache = new File(cachedir + "/" + filename);
		
		// Check to see if a cached image with this name already exists. If not, then
		// download the image.
		for (int i = 0; i < NRETRIES && !(img_cache.exists()); i++) {
			if (i > 0) {
				sleep(ERROR_DELAY_MS);
				Log.e("flickrfree", "Error retrieving image from URL \"" + url + "\". Retrying.");
			}
			downloadImage(url, filename, cachedir, callingActivity, show_progress);
		}

    	return img_cache.exists();
    }
    
    public static Bitmap GetCachedImage(String url, Activity callingActivity) throws MalformedURLException, IOException, InterruptedException {
    	Bitmap b = null;
		File img_cache = new File(GetCacheDir(callingActivity)
									+ "/" + CachedImageFilename(url));
		
		if (img_cache.exists()) {
			b = BitmapFactory.decodeFile(img_cache.getAbsolutePath());
		}

    	return b;
    }
    
    public static String GetBuddyIcon(String nsid) {
    	return GetBuddyIcon(APICalls.peopleGetInfo(nsid));
    }
    
    public static String GetBuddyIcon(JSONObject userinfo) {
		int iconserver = JSONParser.getInt(userinfo, "person/iconserver");
		int iconfarm = JSONParser.getInt(userinfo, "person/iconfarm");
		String nsid = JSONParser.getString(userinfo, "person/nsid");
		String icon_url = "";
		
		if (iconserver > 0 && iconfarm > 0) {
			icon_url = "http://farm"
						+ iconfarm
						+ ".static.flickr.com/"
						+ iconserver + "/buddyicons/"
						+ nsid + ".jpg";
		}
		else {
			icon_url = "http://www.flickr.com/images/buddyicon.jpg";
		}
		
		return icon_url;
    }    

    public static double LatLongToDecimal(String val) {
    	double deg, min, sec;
    	String[] string_arr;
    	
    	string_arr = val.split(" deg ");
    	deg = Double.valueOf(string_arr[0]);
    	string_arr = string_arr[1].split("' ");
    	min = Double.valueOf(string_arr[0]);
    	String s = string_arr[1].substring(0, string_arr[1].length() - 1); 
    	sec = Double.valueOf(s);
    	
    	return LatLongToDecimal(deg, min, sec);
    }
    
    public static double LatLongToDecimal(double deg, double min, double sec) {
    	double val = (deg + (min / 60.0) + (sec / 3600.0));
    	
		return Double.valueOf((new DecimalFormat("#.#######")).format(val)).doubleValue();
    }
    
    public static void LogSharedPrefs(SharedPreferences pref) {
    	Map<String, ?> m = pref.getAll();
    	for (String key : m.keySet()) {
        	Log.i("flickrfree", "Prefs Entry: (" + key + ", " + m.get(key).toString() + ")");
    	}
    	
    }
    
    public static void sleep(long ms) {
		Thread.currentThread();
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
