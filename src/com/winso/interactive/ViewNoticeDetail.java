package com.winso.interactive;

import com.winso.interactive.R;
import com.winso.interactive.app.AppContext;
import com.winso.comm_library.icedb.SelectHelp;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 用于整改批阅
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-9-20
 */
public class ViewNoticeDetail extends BaseActivity {

	private TextView txNoticeInfo;
	SelectHelp helpNotice = new SelectHelp();
	String sNoticeID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.activity_view_notice,
				null);
		setContentView(view);

		Intent it = getIntent();
		sNoticeID = it.getStringExtra("notice_id");
		
		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("公告明细");

		// 返回
		Button fbReturn = (Button) findViewById(R.id.btn_back);
		fbReturn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		}

		);
		
		
	
		txNoticeInfo = (TextView) findViewById(R.id.tx_notice_content);

		new GetDataTask().execute();
	}

	private void getRemoteData() {
		helpNotice = appContext.getNoticeDetail(sNoticeID);

	}

	// 加载历史数据
	private void ProcessLoadData() {

		if ( helpNotice.size() <= 0 ) return;
		
		
		txNoticeInfo.setText(Html.fromHtml(helpNotice.valueStringByName(0, "body")));

	}

	public void initProps() {

	}

	private void ProcessSave() {

	}

	//执行获取远程数据任务
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		private String[] mStrings = { "" };

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				getRemoteData();

			} catch (Exception e) {
				e.printStackTrace();

			}

			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			super.onPostExecute(result);

			ProcessLoadData();
		}
	}

}
