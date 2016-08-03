package com.winso.interactive;

import com.winso.interactive.app.UIHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainLeaveActivity extends BaseListRefreshViewActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_leave_main_list);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("请假历史");

		// 添加新请假按钮
		fbSave = (Button) findViewById(R.id.btn_save);

		getRightChangeBtn(RIGHT_ADD);
		fbSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), LeaveActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_ADD_LEAVE);
			}
		});

		// 返回按钮
		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// o_Picture_ID = R.drawable.my_list_big; // 用于图片的编号
		 
		initListView();
		// 加载
		new GetDataTask().execute();
	}

	// 加载视图
	// 加载中间的视图
	protected void reLoadView() {

		// SelectHelp rtnValue = appContext.getBreakLastView();
		mHelpValue = appContext.getLeaveHis();

	}

	protected void processClickView(String sTitleID, String sTitleLeft,
			String sTitleRight, String sContent) {
		Intent intent = new Intent(this, LeaveViewActivity.class);
		intent.putExtra("title_id", sTitleID);
		intent.putExtra("title_content", sContent);

		intent.putExtra("title_right", sTitleRight);
		intent.putExtra("title_left", sTitleLeft);

		startActivityForResult(intent, UIHelper.INTENT_VIEW_LEAVE);

	}
	
	// 处理返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		if (requestCode == UIHelper.INTENT_VIEW_LEAVE) {
			new GetDataTask().execute();
			
			return;
		}
		
		
	}
	

}
