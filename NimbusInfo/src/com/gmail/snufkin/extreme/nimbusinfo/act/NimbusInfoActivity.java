/**
 Nimbus Info(JPN)
 Copyright (C) 2012  Tohru Mashiko
 <a href="http://www.opensource.org/licenses/mit-license.php">The MIT License</a>
 クラス名：NimbusInfoActivity
 内容：雨雲情報
 特記事項：特になし
 更新履歴(バージョン情報/年月日/氏名)：
          0.1/2012.11.15/T.Mashiko
          0.2/2012.11.16/T.Mashiko
          0.3/2012.11.18/T.Mashiko
          0.4/2012.11.20/T.Mashiko メモリリークとVM aborting対策
          0.5/2012.11.21/T.Mashiko メモリリークとVM aborting対策
          0.6/2012.11.22/T.Mashiko メモリリークとVM aborting対策
*/
package com.gmail.snufkin.extreme.nimbusinfo.act;

import com.gmail.snufkin.extreme.nimbusinfo.R;
import com.gmail.snufkin.extreme.nimbusinfo.lib.NetWorkUtil;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class NimbusInfoActivity extends Activity {
	//メイン画面のレイアウト
	private RelativeLayout rlObj;
	//YOLP WebView
	private WebView wvObj;
	//プログレスダイアログ
	private ProgressDialog pdObj;
	//カスタムWebViewクライアント
	private DialogClient dlgClient;
	//GeoClient
	private GeoClient gcObj;
	//アラートダイアログビルダー
	private AlertDialog.Builder adbObj;
	//アラートダイアログ
	private AlertDialog adObj;

	/**
	 * onCreate
	 * @param savedInstanceState バンドル
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("DEBUG", "NimbusInfoActivity onCreate Start");
		super.onCreate(savedInstanceState);
		//Windowのタイトルを変更するメソッド
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//画面描画処理
		setContentView(R.layout.nimbusinfo);
		//ネットワーク導通確認処理
		if(NetWorkUtil.isOffline(this.getApplicationContext())) {
			//オフラインの場合
			Log.d("DEBUG","NimbusInfoActivity onCreate Offline");
			//タイトル画面にエラーダイアログ表示
			showDialog(this, R.string.network_if_err1, R.string.network_if_err2);
		} else {
			//オンラインの場合
			Log.d("DEBUG","NimbusInfoActivity onCreate Online");
			//プログレスダイアログの実体化
			pdObj = new ProgressDialog(this);
			//プログレスダイアログのスタイル設定
			pdObj.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			//プログレスダイアログのメッセージ設定
			pdObj.setMessage(getString(R.string.load_msg));
			//途中での停止不可設定
			pdObj.setCancelable(false);
			//レイアウト取得
			rlObj = (RelativeLayout) findViewById(R.id.nimbusinfo);
			//WebViewの実体化
			wvObj = new WebView(this);
			//JavaScript 有効化
			wvObj.getSettings().setJavaScriptEnabled(true);
			//Geolocation API有効化
			wvObj.getSettings().setGeolocationEnabled(true);
			//カスタムWebViewクライアントを生成
			dlgClient = new DialogClient();
			//生成したインスタンスをwebviewにセット
			wvObj.setWebViewClient(dlgClient);
			//GeoClientインスタンスを生成
			gcObj = new GeoClient();
			//生成したインスタンスをwebviewにセット
			wvObj.setWebChromeClient(gcObj);
			//HTML読み込み
			wvObj.loadUrl("file:///android_asset/main.html");
			//WebView描画処理
			ViewGroup.LayoutParams vglpObj = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT); 
			rlObj.addView(wvObj, vglpObj);

			//●WebViewとEditTextViewが混在する場合に以下のコードが必要となる場合があるが、
			//画面回転時にonDestroy()にてWebViewのDestroy()を実行した直後にonCreateが呼び出されると
			//Android 2.3.3にて以下のエラーが発生する頻度が高い為、未処理とする
			//Class lookup Ljava/util/HashMap; attempted while exception Ljava/lang/NullPointerException; pending
			//VM aborting
			//wvObj.requestFocus(View.FOCUS_DOWN);
			
		}
		Log.d("DEBUG", "NimbusInfoActivity onCreate End");
	}

	/**
	 * 戻るボタンで終了確認ダイアログ表示
	 *
	 * @param kEvent キーイベント情報 
	 * @return 処理を行った場合はtrue
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent kEvent) {
		Log.d("DEBUG", "NimbusInfoActivity dispatchKeyEvent Start");
		//キー押下されたことを確認
		if (kEvent.getAction() == KeyEvent.ACTION_DOWN) {
			//戻るボタンが押されたか確認
			if (kEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				//終了確認yes/noダイアログの表示
				showYesNoDialog(this, R.string.mes1_dialog, R.string.mes2_dialog, new DialogInterface.OnClickListener() {
					//クリック時に呼ばれる
					public void onClick(DialogInterface dialog,
						int whith) {
						if (whith == DialogInterface.BUTTON_POSITIVE) {
							//アプリケーション終了
							endActivity();
						} else if (whith == DialogInterface.BUTTON_NEGATIVE) {
							//戻る
							return;
						}
					}
				});
			}
		}
		Log.d("DEBUG", "NimbusInfoActivity dispatchKeyEvent End");
		return super.dispatchKeyEvent(kEvent);
	}

	/**
	 * エラー発生時ダイアログの表示
	 *
	 * @param context コンテキスト
	 * @param titleMsg タイトルメッセージ定数情報
	 * @param mainMsg メッセージ定数情報
	 */
	private void showDialog(Context context, int titleMsg, int mainMsg) {
		Log.d("DEBUG", "NimbusInfoActivity showDialog Start");
		adbObj = new AlertDialog.Builder(context);
		adbObj.setTitle(titleMsg);
		adbObj.setMessage(mainMsg);
		adbObj.setPositiveButton(R.string.yes_btn, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				//Yesボタンが押された時の処理
				//エラー発生時のActivity終了処理
				endActivity();
			}});
		adObj = adbObj.create();
		//途中での停止不可設定
		adObj.setCancelable(false);
		adObj.show();
		Log.d("DEBUG", "NimbusInfoActivity showDialog End");
	}

	/**
	 * 終了確認yes/noダイアログの表示
	 *
	 * @param context コンテキスト
	 * @param titleMsg タイトルメッセージ定数情報
	 * @param mainMsg メッセージ定数情報
	 * @param listener リスナー情報 
	 */
	private void showYesNoDialog(Context context, int titleMsg, int mainMsg, DialogInterface.OnClickListener listener) {
		Log.d("DEBUG", "NimbusInfoActivity showYesNoDialog Start");
		adbObj = new AlertDialog.Builder(context);
		adbObj.setTitle(titleMsg);
		adbObj.setMessage(mainMsg);
		adbObj.setPositiveButton(R.string.yes_btn, listener);
		adbObj.setNegativeButton(R.string.no_btn, listener);
		adObj = adbObj.create();
		adObj.show();
		Log.d("DEBUG", "NimbusInfoActivity showYesNoDialog End");
	}

	/**
	 * Activity終了処理
	 *
	 */
	private void endActivity() {
		Log.d("DEBUG", "NimbusInfoActivity endActivity Start");
		//自アクティビティの終了
		finish();
		Log.d("DEBUG", "NimbusInfoActivity endActivity End");
	}
	
	/**
	 * onPause
	 * Activityポーズ処理
	 */
	@Override
	public void onPause() {
		Log.d("DEBUG", "NimbusInfoActivity onPause Start");
		super.onPause();
		// ダイアログの終了処理
		if(adObj != null) {
			adObj.dismiss();
			adObj = null;
			adbObj = null;
		}
		Log.d("DEBUG", "NimbusInfoActivity onPause End");
	}

	/**
	 * onDestroy
	 * Activityクローズ処理
	 */
	@Override
	public void onDestroy(){
		Log.d("DEBUG", "NimbusInfoActivity onDestroy Start");
		super.onDestroy();
		//ダイアログオブジェクト開放処理
		if (pdObj != null && pdObj.isShowing()) {
			pdObj.dismiss();
		}
		pdObj = null;
		//WebViewオブジェクト開放処理
		if(wvObj != null) {
			wvObj.stopLoading();
			wvObj.clearCache(true);
			wvObj.destroyDrawingCache();
			wvObj.setWebChromeClient(null);
			wvObj.setWebViewClient(null);
			dlgClient = null;
			gcObj = null;
			this.unregisterForContextMenu(wvObj);
			//WebView.destroy() called while still attached!エラー対策
			rlObj.removeView(wvObj);
			wvObj.removeAllViews();
			//WebViewのdestroy()を実施しないとActivity has leaked IntentReceiver.XXX.XXX.XXX.browsermanagement.Errorが発生し、
			//BrowserManagementに対してunregisterReceiver()を実行せよと指摘される。
			wvObj.destroy();
			wvObj = null;
		}
		Log.d("DEBUG", "NimbusInfoActivity onDestroy End");
	}

	/**
	 * WebViewClientを継承したカスタムDialogClient
	 *
	 */
	class DialogClient extends WebViewClient {
		@Override
		public void onPageFinished(final WebView view, final String url) {
			if (pdObj != null && pdObj.isShowing()) {
				pdObj.dismiss();
			}
		};
		@Override
		public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
			if (pdObj != null && pdObj.isShowing() == false) {
				pdObj.show();
			}
		};
	};

	/**
	 * WebChromeClientを継承したカスタムGeoClient
	 * 
	 */
	class GeoClient extends WebChromeClient {
		@Override
		public void onGeolocationPermissionsShowPrompt(
			String origin, Callback callback) {
				//onGeolocationPermissionsShowPromptを継承してプロンプトを表示させずにGeoロケーションを許可する
				super.onGeolocationPermissionsShowPrompt(origin, callback);
				callback.invoke(origin, true, false);
			}
		@Override
		public void onProgressChanged(WebView view, int progress) {
			if(pdObj != null) {
				pdObj.setProgress(progress);
			}
		}
	}
}