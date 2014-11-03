package com.aisino.plugin.push;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;

//定义错误返回类型
enum ErrorPush {
	NET_ERROR, // 网络连接失败或者服务器没有返回数据
	MESSAHE_ERROR, // 方法传入的数据解析出错
};

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends BroadcastReceiver {
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();

	public static final String METHOD_INIT_WORK = "method_bind";
	public static final String METHOD_SET_TAG = "method_set_tags";
	public static final String METHOD_DEL_TAG = "method_del_tags";
	public static final String METHOD_LIST_TAGS = "method_listtags";
	// final String
	// urlString="http://192.168.3.175/MServer/updateEmpChannelInfo.search";
	//private static final int RESULT_OK = 0;
	private static String USER_ID = null;
	private static String APP_ID = null;
	private static String CHANNEL_ID = null;
	private static String UNAME = null;
	private static String urlString = null;

	AlertDialog.Builder builder;
	private static Class mainClazz=null;
	private static String className=null;
	/**
	 * @param context
	 *            Context
	 * @param intent
	 *            接收的intent
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {

		Log.d(TAG, ">>> Receive intent: \r\n" + intent);

		//System.out.println("urlString = " + urlString);
		
		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			// 获取消息内容
			String message = intent.getExtras().getString(
					PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			// 消息的用户自定义内容读取方式
			Log.i(TAG, "onMessage: " + message);
			// 自定义内容的json串
			Log.d(TAG,
					"EXTRA_EXTRA = "
							+ intent.getStringExtra(PushConstants.EXTRA_EXTRA));

		} 
		else if (intent.getAction().equals("com.baidu.android.pushservice.action.USERNAME")) {
			urlString = intent.getStringExtra("serverurl");
			String username = intent.getStringExtra("username");
			UNAME = username;
			className=intent.getStringExtra("clazzName");
			Log.d(TAG, "className=" + className);
		} 
		else if (intent.getAction().equals("com.baidu.android.pushservice.action.TAGS")) {
			final String paraString = String.format(
					"action=getTagInfo&appid=%s", APP_ID);
			// String recvMessageString=sendPost(urlString, paraString);

            if (urlString.contains("http")) {
                new Thread() {
                    @Override
                    public void run() {
                        // 你要执行的方法
                        String recvMessageString = HttpRequest.sendPost(urlString,
                                paraString);
                        Log.d(TAG, "callback : " + recvMessageString);
                        if (recvMessageString != "") {
                            // 返回给JS调用
                            PushDemoActivity.cbContext.success(recvMessageString);
                        }
                    }
                }.start();
            }
		} 
		else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			// 处理绑定等方法的返回数据
			// PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到

			// 获取方法
			final String method = intent
					.getStringExtra(PushConstants.EXTRA_METHOD);
			// 方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			// 绑定失败的原因有多种，如网络原因，或access token过期。
			// 请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			// 可以通过限制重试次数，或者在其他时机重新调用来解决。
			int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE,
					PushConstants.ERROR_SUCCESS);
			String content = "";
			if (intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null) {
				// 返回内容
				content = new String(
						intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			}

			// 用户在此自定义处理消息,以下代码为demo界面展示用
			Log.d(TAG, "onMessage: method : " + method);
			Log.d(TAG, "onMessage: result : " + errorCode);
			Log.d(TAG, "onMessage: content : " + content);

			// 对应调用方法的处理
			if (method.equalsIgnoreCase(METHOD_SET_TAG)) {
				if (errorCode == 0) {
					JSONObject initJsonObject = getJSONObject(content);
					JSONObject responseObject = null;
					try {
						responseObject = initJsonObject
								.getJSONObject("response_params");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String paramJson = null;
					String paramPost = "";
					try {
						paramJson = responseObject.getString("details");
						JSONArray ja;
						ja = new JSONArray(paramJson);
						for (int i = 0; i < ja.length(); i++) {
							JSONObject iObj = ja.getJSONObject(i);
							String feeTypeId = iObj.getString("tag");
							if (paramPost == "") {
								paramPost = feeTypeId;
							} else {
								paramPost = paramPost + "," + feeTypeId;
							}

						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						PushDemoActivity.cbContext
								.error(ErrorPush.MESSAHE_ERROR.toString());
					}
                    if (urlString.contains("http")){
                        // 向服务器发送绑定参数信息
                        final String paraString = String.format(
                                "action=setTag&user_id=%s&tag=%s", USER_ID,
                                paramPost);
                        Log.d(TAG, "paramPost : " + paramPost);
                        new Thread() {
                            @Override
                            public void run() {
                                // 你要执行的方法
                                String recvMessageString = HttpRequest.sendPost(
                                        urlString, paraString);
                                Log.d(TAG, "callback : " + recvMessageString);
                                if (recvMessageString == "") {
                                    // 返回给JS调用
                                    PushDemoActivity.cbContext
                                            .error(ErrorPush.NET_ERROR.toString());
                                }
                                else
                                    PushDemoActivity.cbContext.success(0);
                            }
                        }.start();
                    }
                    else
                        PushDemoActivity.cbContext.success(errorCode);
				} else {
					PushDemoActivity.cbContext.error(errorCode);
				}

			} 
			else if (method.equalsIgnoreCase(METHOD_DEL_TAG)) {
				if (errorCode == 0) {
					JSONObject initJsonObject = getJSONObject(content);
					JSONObject responseObject = null;
					try {
						responseObject = initJsonObject
								.getJSONObject("response_params");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String paramJson = null;
					String paramPost = "";
					try {
						paramJson = responseObject.getString("details");
						JSONArray ja;
						ja = new JSONArray(paramJson);
						for (int i = 0; i < ja.length(); i++) {
							JSONObject iObj = ja.getJSONObject(i);
							String feeTypeId = iObj.getString("tag");
							if (paramPost == "") {
								paramPost = feeTypeId;
							} else {
								paramPost = paramPost + "," + feeTypeId;
							}

						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						PushDemoActivity.cbContext
								.error(ErrorPush.MESSAHE_ERROR.toString());
					}

                    if (urlString.contains("http")){
                        // 向服务器发送绑定参数信息
                        final String paraString = String.format(
                                "action=delTag&user_id=%s&tag=%s", USER_ID,
                                paramPost);
                        // String recvMessageString=sendPost(urlString, paraString);
                        new Thread() {
                            @Override
                            public void run() {
                                // 你要执行的方法
                                String recvMessageString = HttpRequest.sendPost(
                                        urlString, paraString);
                                Log.d(TAG, "callback : " + recvMessageString);
                                if (recvMessageString == "") {
                                    // 返回给JS调用
                                    PushDemoActivity.cbContext
                                            .error(ErrorPush.NET_ERROR.toString());
                                }
                                else
                                    PushDemoActivity.cbContext.success(0);
                            }
                        }.start();
                    }
                    else
                        PushDemoActivity.cbContext.success(errorCode);
				} else {
					PushDemoActivity.cbContext.error(errorCode);
				}
			} 
			else if (method.equalsIgnoreCase(METHOD_INIT_WORK)) {
				// 解析返回的数据参数
				if (errorCode == 0) {
					JSONObject initJsonObject = getJSONObject(content);
					JSONObject paramJsonObject = null;
					try {
						paramJsonObject = getJSONObject(initJsonObject
								.getString("response_params"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// 向服务器发送绑定参数信息
					try {
						USER_ID = paramJsonObject.getString("user_id");
						APP_ID = paramJsonObject.getString("appid");
						CHANNEL_ID = paramJsonObject.getString("channel_id");
						System.out.println("username = " + UNAME);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
                        final JSONObject jsonParaString = new JSONObject();
                        jsonParaString.put("userID", USER_ID);
                        jsonParaString.put("channelID", CHANNEL_ID);
                        jsonParaString.put("appID", APP_ID);

                        if(urlString.contains("http")){
                            final String paraString = String
                                    .format("user_name=%s&action=initUser&appid=%s&user_id=%s&channel_id=%s&platform=android",
                                            UNAME,
                                            paramJsonObject.getString("appid"),
                                            paramJsonObject.getString("user_id"),
                                            paramJsonObject.getString("channel_id"));
                            Log.d(TAG, "paramString : " + paraString);
                            new Thread() {
                                @Override
                                public void run() {
                                    // 你要执行的方法
                                    String ssString = HttpRequest.sendPost(
                                            urlString, paraString);
                                    Log.d(TAG, "callback : " + ssString);
                                    System.out.println(ssString);
                                    PushDemoActivity.cbContext
                                            .success(jsonParaString);

                                }
                            }.start();
                        }
                        else
                            PushDemoActivity.cbContext
                                    .success(jsonParaString);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					PushDemoActivity.cbContext.error(errorCode);
				}
			}
			else if (method.equalsIgnoreCase(METHOD_LIST_TAGS)) {
				// 列出设置的标签
				if (errorCode == 0) {
                     String TagsListCallback="";
					 JSONObject listJsonObject = getJSONObject(content);
					 JSONObject paramJsonObject=null;
                     try {
					    paramJsonObject=getJSONObject(listJsonObject.getString("response_params"));
                     }
                     catch(JSONException e) { // TODO Auto-generated catch block
					     e.printStackTrace();
                     }
                     //返回给JS调用的tags信息 String
                    JSONArray listTags;
                     try {
					    listTags = paramJsonObject.getJSONArray("groups");
                         for (int i=0; i<listTags.length();i++){
                             JSONObject target=listTags.getJSONObject(i);
                             if (TagsListCallback.length()==0)
                                TagsListCallback+=target.getString("name");
                             else
                                 TagsListCallback=TagsListCallback+","+target.getString("name");
                             Log.d(TAG,target.getString("name"));
                         }
                         PushDemoActivity.cbContext.success(TagsListCallback);
                     }
                     catch(JSONException e1)
                     { // TODO Auto-generated catch block
					    e1.printStackTrace();
					    PushDemoActivity.cbContext.error(ErrorPush.MESSAHE_ERROR.toString());
                     }
				} else {
					PushDemoActivity.cbContext.error(errorCode);
				}
			}

			/*
			 * Toast.makeText( context, "method : " + method + "\n result: " +
			 * errorCode + "\n content = " + content, Toast.LENGTH_SHORT)
			 * .show();
			 */

			
		} 
		// 可选。通知用户点击事件处理,默认进入应用程序
		else if (intent.getAction().equals(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			Log.d(TAG, "intent=" + intent.toUri(0));

			Log.d(TAG, "className=" + className);
						
			try {
				if(!className.isEmpty())
					mainClazz = Class.forName(className);
				else
					Log.d(TAG, "className isEmpty");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			};			

			Intent aIntent = new Intent();
			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			aIntent.setClass(context.getApplicationContext(), mainClazz);
		  aIntent.setAction(Intent.ACTION_MAIN);
			aIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			context.getApplicationContext().startActivity(aIntent);
		}
	}

	private String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static JSONObject getJSONObject(String str) {
		if (str == null || str.trim().length() == 0) {
			return null;
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(str);
		} catch (JSONException e) {
			e.printStackTrace(System.err);
		}
		return jsonObject;
	}

}
