package com.winso.interactive;

import com.winso.interactive.R;
import com.winso.interactive.app.AppContext;
import com.winso.comm_library.icedb.SelectHelp;

import android.app.Activity;
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
public class WriteAnswerActivity extends BaseViewSaveActivity {

	private TextView txNoticeInfo;
	SelectHelp helpNotice = new SelectHelp();
	String sNoticeID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.activity_write_answer,
				null);
		setContentView(view);

		
		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("回答问题");

		// 返回
		Button fbReturn = (Button) findViewById(R.id.btn_back);
		fbReturn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		}

		);
//		Button fBSubmit = (Button) findViewById(R.id.btn_save);
//		fBSubmit.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				Intent it = getIntent();
//				
//				String sText = getEditText(R.id.ed_ev_context);
//				it.putExtra("answer_content", sText);
//				
//				
//				setResult(Activity.RESULT_OK, it);
//
//				finish();
//				
//			}
//		}
//
//		);
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_SUBMIT);
//		tvRight = (TextView) findViewById(R.id.text_right);
//		getRightText(RIGHT_SUBMIT);
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent it = getIntent();
				
				String sText = getEditText(R.id.ed_ev_context);
				it.putExtra("answer_content", sText);
				
				
				setResult(Activity.RESULT_OK, it);

				finish();
				
			}
		}

		);
		
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processLoadData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processSaveData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processAfterSaveData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processDownload(boolean bOK) {
		// TODO Auto-generated method stub
		
	}

}
