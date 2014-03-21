package com.aisino.plugin.push;

import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import android.R.string;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.util.Log;


public class PushDemoActivity extends  CordovaPlugin{

	 public static final String PLUGIN_NAME = "PUSHPLUGIN";
	 private static final int RESULT_OK = 0;
	 private static final String INIT_WORK="initWork";
	 private static final String STOP_WORK="stopWork";
	 private static final String IS_PUSH_ENABLED="isPushEnabled";
	 private static final String RESUME_WORK="resumeWork";
	 private static final String SET_TAGS="setTags";
	 private static final String DEL_TAGS="delTags";
	 private static final String LIST_TAGS="listTags";
	 private static final String TAGS_INFO="getTagInfo";
	 public static CallbackContext cbContext;
	 

	 public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
			Log.d(PLUGIN_NAME, "Plugin execute called with action: " + action);
			cbContext = callbackContext;  
			boolean result = false ;
			
			// Determine which action of the plugin needs to be invoked
			 
			if (action.equalsIgnoreCase(INIT_WORK)) {
				try {
					final String username= args.getString(0);
					Intent intent = new Intent(); 
			        intent.setAction("com.baidu.android.pushservice.action.USERNAME");//发出自定义广播
			        intent.putExtra("username", username);
			        cordova.getActivity().sendBroadcast(intent);
					cordova.getThreadPool().execute(new Runnable() {
			            public void run() {
			            	initWork(); 	// Thread-safe.
			            }
			        });
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return true;
					
			} else if (action.equalsIgnoreCase(STOP_WORK)) {
				
				stopWork();
				return true;
			}else if (action.equalsIgnoreCase(RESUME_WORK)) {
				
				resumeWork();
				return true;
			}else if (action.equalsIgnoreCase(IS_PUSH_ENABLED)) {
			   	
				Boolean isworkBoolean=isPushEnabled();
				if (isworkBoolean==true) {
					callbackContext.success("true");
				}else {
					callbackContext.success("false");
				}		
				return true;
			}else if (action.equalsIgnoreCase(SET_TAGS)) {
				
				try {
					final String parameters= args.getString(0);
					cordova.getThreadPool().execute(new Runnable() {
			            public void run() {
			            	setTags(parameters);	// Thread-safe.
			            }
			        });
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}else if (action.equalsIgnoreCase(DEL_TAGS)) {
				
				try {
					final String parameters= args.getString(0);
					cordova.getThreadPool().execute(new Runnable() {
			            public void run() {
			            	delTags(parameters); 	// Thread-safe.
			            }
			        });
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return true;
			}else if (action.equalsIgnoreCase(LIST_TAGS)) {	
				cordova.getThreadPool().execute(new Runnable() {
		            public void run() {
		            	getChannelList(); 	// Thread-safe.
		            }
		        });
				return true;
			}else if (action.equalsIgnoreCase(TAGS_INFO)) {				
				cordova.getThreadPool().execute(new Runnable() {
		            public void run() {
		            	getTagInfo(); 	// Thread-safe.
		            }
		        });
				return true;
			}			
			return result;
	    }
	 
	public void initWork(){
		PushManager.startWork(cordova.getActivity(),
				PushConstants.LOGIN_TYPE_API_KEY, 
				Utils.getMetaValue(cordova.getActivity(), "api_key"));	
	}
	
	public void stopWork(){
		PushManager.stopWork(cordova.getActivity());
	}
	
	public void resumeWork(){
		PushManager.resumeWork(cordova.getActivity());
	}
	
	public Boolean isPushEnabled(){
		return PushManager.isPushEnabled(cordova.getActivity());
		//返回测试结果数据
	}
	
	public void setTags(String tag){
		List<String> tagsList= getTagsList(tag);
		PushManager.setTags(cordova.getActivity(), tagsList);
	}
	
	public void delTags(String tag){
		List<String> tagsList= getTagsList(tag);
		PushManager.delTags(cordova.getActivity(), tagsList);
	}
	
	public void getChannelList(){
		
		PushManager.listTags(cordova.getActivity());
	}
	public void getTagInfo(){
		Intent intent = new Intent(); 
        intent.setAction("com.baidu.android.pushservice.action.TAGS");//发出自定义广播
        cordova.getActivity().sendBroadcast(intent);
	}

	private List<String> getTagsList(String originalText) {

		List<String> tags = new ArrayList<String>();
		int indexOfComma = originalText.indexOf(',');
		String tag;
		while (indexOfComma != -1) {
			tag = originalText.substring(0, indexOfComma);
			tags.add(tag);

			originalText = originalText.substring(indexOfComma + 1);
			indexOfComma = originalText.indexOf(',');
		}

		tags.add(originalText);
		return tags;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // 接收被调方 Activity 返回的数据
		switch (resultCode) {
			case RESULT_OK:
			String returnData = data.getExtras().getString("data");
			//返回json数据
			// this.cbContext.success(message);
			break;
		}
	}

}
