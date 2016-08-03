package com.winso.interactive;

import com.winso.interactive.app.ActionSelectAdapter;
import com.winso.interactive.app.AppContext;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SelectStudentActivity extends BaseSimpleListRefreshViewActivity {
	
	ActionSelectAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_student);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("选择教师");

		// 返回
		Button fbReturn = (Button) findViewById(R.id.btn_back);
		fbReturn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		}

		);

		// 保存
//		Button fbSave = (Button) findViewById(R.id.btn_save);
//		fbSave.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				// ProcessSave("");
//
//			}
//		}
//
//		);
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_NULL);
//		tvRight = (TextView) findViewById(R.id.text_right);
//		getRightText(RIGHT_NULL);
		
		o_Picture_ID = R.drawable.teacher_logo;
		initListView();
		
		new GetDataTask().execute();
	}
	// 加载视图
	// 加载中间的视图
	protected void reLoadView()  {
		mHelpValue = appContext.getStudentList_review();
	}
	protected void processClickView(String sTitleID,String sTitleLeft,String sTitleRight)
	{
		Intent it = getIntent();
		it.putExtra("title_id", sTitleID);
		it.putExtra("title_right", sTitleLeft);
		
		
		setResult(Activity.RESULT_OK, it);

		
		finish();
	}

}
