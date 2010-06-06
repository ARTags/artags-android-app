package com.zmosoft.flickrfree;

import java.util.HashMap;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CommentLayout extends RelativeLayout {

	public CommentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		m_group_links = new HashMap<String, String>();
		m_photo_links = new HashMap<String, String>();
	}
	
	public HashMap<String, String> m_group_links;
	public HashMap<String, String> m_photo_links;
}

