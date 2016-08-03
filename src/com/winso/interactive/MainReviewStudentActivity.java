package com.winso.interactive;

import com.winso.interactive.app.ActionSelectAdapter;
import com.winso.interactive.app.UIHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainReviewStudentActivity extends BaseSimpleListRefreshViewActivity {
	
	ActionSelectAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_student);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("学生互评");

		// 返回
		Button fbReturn = (Button) findViewById(R.id.btn_back);
		fbReturn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		}

		);

		// 保存
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_NULL);
//		tvRight = (TextView) findViewById(R.id.text_right);
//		getRightText(RIGHT_NULL);
//		fbSave.setVisibility(View.INVISIBLE);
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ProcessSave("");

			}
		}

		);

		o_Picture_ID = R.drawable.teacher_logo;
		initListView();
		
		new GetDataTask().execute();
	}
	// 加载视图
	// 加载中间的视图
	protected void reLoadView()  {
		mHelpValue = appContext.getStudentList_review();
	}
	
	// 处理返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if ( appContext.bNeedRefreshMainReview )
		{
			appContext.bNeedRefreshMainReview  = false;
			new GetDataTask().execute();
		}
		
		if (resultCode != RESULT_OK)
			return;

		if (requestCode == UIHelper.INTENT_ITEM_REVIEW_STUDENT) {


			return;
		}

	}
	
	protected void processClickView(String sTitleID,String sTitleLeft,String sTitleRight)
	{
		Intent it = getIntent();
		
		
		//setResult(Activity.RESULT_OK, it);

		
		Intent intent = new Intent(MainReviewStudentActivity.this,
				ReviewStudentItemActivity.class);
		
		intent.putExtra("title_id", sTitleID);
		intent.putExtra("title_right", sTitleLeft);

		
		startActivityForResult(intent, UIHelper.INTENT_ITEM_REVIEW_STUDENT);
		
		
		//finish();
	}

}
