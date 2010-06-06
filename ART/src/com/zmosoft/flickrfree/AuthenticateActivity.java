/*
 package com.zmosoft.flickrfree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AuthenticateActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	m_auth_prefs = getSharedPreferences("Auth",0);
    	m_fail_msg = "";
		setResult(Activity.RESULT_CANCELED);

        setContentView(R.layout.authenticate);
        
		((Button)findViewById(R.id.btnAuthenticate)).setEnabled(checkAuthCode());

		((Button)findViewById(R.id.btnAuthenticate)).setOnClickListener(this);
        ((Button)findViewById(R.id.btnGetCode)).setOnClickListener(this);
        
        ((EditText)findViewById(R.id.authnum1)).addTextChangedListener(
        		new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
						if (s.toString().length() == 3) {
							((EditText)findViewById(R.id.authnum2)).requestFocus();
						}
						((Button)findViewById(R.id.btnAuthenticate)).setEnabled(checkAuthCode());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}
        			
        		}
        );

        ((EditText)findViewById(R.id.authnum2)).addTextChangedListener(
        		new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
						if (s.toString().length() == 3) {
							((EditText)findViewById(R.id.authnum3)).requestFocus();
						}
						((Button)findViewById(R.id.btnAuthenticate)).setEnabled(checkAuthCode());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}
        			
        		}
        );

        ((EditText)findViewById(R.id.authnum3)).addTextChangedListener(
        		new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
						((Button)findViewById(R.id.btnAuthenticate)).setEnabled(checkAuthCode());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}
        			
        		}
        );
    }
    
    public boolean checkAuthCode() {
		return (((EditText)findViewById(R.id.authnum1)).getText().toString().length() == 3
				&& ((EditText)findViewById(R.id.authnum2)).getText().toString().length() == 3
				&& ((EditText)findViewById(R.id.authnum3)).getText().toString().length() == 3);
    }
    
    public void onClick(View v) {
    	if (v.getId() == R.id.btnAuthenticate) {
    		String miniToken;
    		miniToken = ((EditText)findViewById(R.id.authnum1)).getText().toString()
    					+ "-" + ((EditText)findViewById(R.id.authnum2)).getText().toString()
    					+ "-" + ((EditText)findViewById(R.id.authnum3)).getText().toString();
    		
			JSONObject json_obj = APICalls.getFullToken(miniToken);
			try {
				// Check that authentication was successful
				if (json_obj.getString("stat").equals("ok")) {
					// Retrieve the username and fullname from the object.
					String username = JSONParser.getString(json_obj, "auth/user/username");
					String fullname = JSONParser.getString(json_obj, "auth/user/fullname");
					
					// Get the "Auth" Shared preferences object to save authentication information
					m_auth_prefs = getSharedPreferences("Auth",0);
					
					// Get the editor for auth_prefs
					SharedPreferences.Editor auth_prefs_editor = m_auth_prefs.edit();
					
					// Save all of the current authentication information. This will be the default account
					// the next time the app is started.
					auth_prefs_editor.putString("full_token", JSONParser.getString(json_obj, "auth/token/_content"));
					auth_prefs_editor.putString("perms", JSONParser.getString(json_obj, "auth/perms/_content"));
					auth_prefs_editor.putString("nsid", JSONParser.getString(json_obj, "auth/user/nsid"));
					auth_prefs_editor.putString("username", username);
					auth_prefs_editor.putString("realname", fullname);
					auth_prefs_editor.putString("displayname", fullname.equals("") ? username : fullname + " (" + username + ")");
					
					// Save the entire JSON Authentication object under the username so it can be retrieved
					// when switching accounts.
					auth_prefs_editor.putString("FlickrUsername_" + username, json_obj.toString());
					
					// Attempt to save all changes to Shared Preferences. If successful, set result to RESULT_OK.
					if (auth_prefs_editor.commit()) {
						setResult(Activity.RESULT_OK);
					}
               	 	setResult(AUTH_SUCCESS);
					finish();
				}
				else {
					m_fail_msg = JSONParser.getString(json_obj, "message");
					if (m_fail_msg == null) {
						m_fail_msg = "Unknown Error";
					}
					showDialog(DIALOG_ERR);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	else if (v.getId() == R.id.btnGetCode) {
    		startActivity(new Intent(Intent.ACTION_VIEW,
    				Uri.parse(getResources().getString(R.string.auth_url))));
    	}
    }
    
    protected Dialog onCreateDialog(int id) {
		Dialog err_dialog = null;
		
		AlertDialog.Builder builder;
    	switch(id) {
    	case DIALOG_ERR:
    		builder = new AlertDialog.Builder(this);
			builder.setMessage(m_fail_msg)
			       .setTitle(R.string.ttlerror)
			       .setIcon(android.R.drawable.ic_dialog_alert)
		           .setPositiveButton("Help", new DialogInterface.OnClickListener() {
		                             public void onClick(DialogInterface dialog, int id) {
		                            	 m_fail_msg = "";
		                            	 showDialog(DIALOG_HELP);
		                             }
		            })
		           .setNegativeButton("Close", new DialogInterface.OnClickListener() {
		                             public void onClick(DialogInterface dialog, int id) {
		                            	 m_fail_msg = "";
		                            	 AuthenticateActivity.this.setResult(AUTH_ERR);
		                            	 AuthenticateActivity.this.finish();
		                             }
		            });
			err_dialog = builder.create();
			break;
    	case DIALOG_HELP:
    		builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.msgauthhelp)
			       .setTitle(R.string.ttlhelp)
			       .setIcon(android.R.drawable.ic_dialog_info)
		           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		                             public void onClick(DialogInterface dialog, int id) {
		                            	 startActivity(new Intent(Intent.ACTION_VIEW,
		                         				Uri.parse(GlobalResources.m_EDITPERMS_URL)));
		                             }
		            })
		           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		                             public void onClick(DialogInterface dialog, int id) {
		                            	 AuthenticateActivity.this.finish();
		                             }
		            });
			err_dialog = builder.create();
    		break;
    	}

		return err_dialog;
    }
    
    public static void SetActiveUser(SharedPreferences prefs, String username) {
    	try {
			SharedPreferences.Editor prefs_editor = prefs.edit();
			String user_obj_str = username.equals("") ? "" : prefs.getString("FlickrUsername_" + username, "");

			if (user_obj_str.equals("")) {
				AuthenticateActivity.LogOut(prefs);
			}
			else {
				JSONObject user_obj = new JSONObject(user_obj_str);
				
				// Retrieve the full name from the object.
				String fullname = user_obj.getJSONObject("auth").getJSONObject("user").getString("fullname");

				// Save all of the current authentication information. This will be the default account
				// the next time the app is started.
				prefs_editor.putString("full_token", JSONParser.getString(user_obj, "auth/token/_content"));
				prefs_editor.putString("perms", JSONParser.getString(user_obj, "auth/perms/_content"));
				prefs_editor.putString("nsid", JSONParser.getString(user_obj, "auth/user/nsid"));
				prefs_editor.putString("username", username);
				prefs_editor.putString("realname", fullname);
				prefs_editor.putString("displayname", fullname.equals("") ? username : fullname + " (" + username + ")");
				prefs_editor.commit();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    public static void RemoveUser(SharedPreferences prefs, String username) {
		// Get the editor for prefs
		SharedPreferences.Editor prefs_editor = prefs.edit();
		
		prefs_editor.remove("FlickrUsername_" + username);
		prefs_editor.commit();
		if (prefs.getString("username", "").equals(username)) {
			AuthenticateActivity.LogOut(prefs);
		}
    }
    
    public static void LogOut(SharedPreferences prefs) {
		// Get the editor for prefs
		SharedPreferences.Editor prefs_editor = prefs.edit();
		
		prefs_editor.remove("full_token");
		prefs_editor.remove("perms");
		prefs_editor.remove("nsid");
		prefs_editor.remove("username");
		prefs_editor.remove("realname");
		prefs_editor.remove("displayname");
		prefs_editor.commit();
    }
    
    public static void ExportAuth(SharedPreferences auth_prefs, String path) {
    	Map<String, ?> m = auth_prefs.getAll();
		try {
			File f = new File(path);
			if (!f.exists()) {
				f.createNewFile();
			}
			FileOutputStream of = new FileOutputStream(f);
	    	for (String key : m.keySet()) {
	    		if (key.contains("FlickrUsername_")) {
	    			new PrintStream(of).println(key + " : " + m.get(key).toString());
	    		}
	    	}
	    	of.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void ImportAuth(SharedPreferences auth_prefs, String path) {
		try {
			BufferedReader read_buf = new BufferedReader(new FileReader(path));
			SharedPreferences.Editor auth_prefs_edit = auth_prefs.edit();
			String s = read_buf.readLine();
			String[] parsed = null;
			while (s != null) {
				parsed = s.split(" : ", 2);
				if (parsed.length == 2 && parsed[0].contains("FlickrUsername_")) {
					auth_prefs_edit.putString(parsed[0], parsed[1]);
				}
				s = read_buf.readLine();
			}
			auth_prefs_edit.commit();
			read_buf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	SharedPreferences m_auth_prefs;

	String m_fail_msg;
	
    static final int DIALOG_ERR = 1;
    static final int DIALOG_HELP = 2;
    static final public int AUTH_ERR = 3;
    static final public int AUTH_SUCCESS = 4;
}
*/