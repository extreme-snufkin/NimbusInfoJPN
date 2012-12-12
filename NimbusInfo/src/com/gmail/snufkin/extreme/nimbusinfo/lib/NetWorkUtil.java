/**
 Nimbus Info
 Copyright (C) 2012  Tohru Mashiko
 <a href="http://www.opensource.org/licenses/mit-license.php">The MIT License</a>
 クラス名：NetWorkUtil
 内容：ネットワークに関するユーティリティクラス
 特記事項：このクラスを利用するには、AndroidManifest.xmlに以下を追加する必要がある
			<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

 更新履歴(バージョン情報/年月日/氏名)：
          0.1/2012.02.20/T.Mashiko
          0.2/2012.03.12/T.Mashiko
*/

package com.gmail.snufkin.extreme.nimbusinfo.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetWorkUtil {

	/**
	 * オフラインの場合にtrueを返す
	 * <pre>
	 * Activityから利用する場合、引数にthisを渡すとメモリリークの可能性があるため、
	 * this.getApplicationContext()を渡す点に注意
	 * 例:
	 * boolean offline = NetWorkUtil.isOffline(this.getApplicationContext());
	 * </pre>
	 *
	 * @param context
	 * @return true=オフライン、false=オンライン
	 */
	public static boolean isOffline(Context context) {
		Log.d("DEBUG", "NetWorkUtil isOffline Start");
		boolean blResult = isOnline(context);
		if(blResult) {
			Log.d("DEBUG", "NetWorkUtil isOffline(1) End");
			return false;
		}
		Log.d("DEBUG", "NetWorkUtil isOffline(2) End");
		return true;
	}

	/**
	 * オンラインの場合にtrueを返す
	 * <pre>
	 * Activityから利用する場合、引数にthisを渡すとメモリリークの可能性があるため、
	 * this.getApplicationContext()を渡す点に注意
	 * 例:
	 * boolean online = NetWorkUtil.isOnline(this.getApplicationContext());
	 * </pre>
	 *
	 * @param context
	 * @return true=オンライン、false=オフライン
	 */
	private static boolean isOnline(Context context) {
		Log.d("DEBUG", "NetWorkUtil isOnline Start");
		NetworkInfo objNI = getNetworkInfo(context);
		ConnectivityManager objCM = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean blOnline = false;
		if(objNI != null) {
			blOnline = objCM.getActiveNetworkInfo().isConnected();
		}
		context = null;
		Log.d("DEBUG", "NetWorkUtil isOnline End");
		return blOnline;
	}

	/**
	 * ネットワークの情報を返す
	 * <pre>
	 * Activityから利用する場合、引数にthisを渡すとメモリリークの可能性があるため、
	 * this.getApplicationContext()を渡す点に注意
	 * 例:
	 * NetworkInfo info = NetWorkUtil.getNetworkInfo(this.getApplicationContext());
	 * </pre>
	 *
	 * @param context
	 * @return
	 */
	private static NetworkInfo getNetworkInfo(Context context) {
		Log.d("DEBUG", "NetWorkUtil getNetworkInfo Start");
		ConnectivityManager objCM = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		context = null;
		Log.d("DEBUG", "NetWorkUtil getNetworkInfo End");
		return objCM.getActiveNetworkInfo();
	}
}
