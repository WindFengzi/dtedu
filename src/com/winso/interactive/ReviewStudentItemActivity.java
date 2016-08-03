package com.winso.interactive;

import com.winso.interactive.BaseListRefreshViewActivity.GetDataTask;
import com.winso.interactive.app.ActionSelectAdapter;
import com.winso.interactive.app.AppContext;
import com.winso.interactive.app.UIHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewStudentItemActivity extends
		BaseSimpleListRefreshViewActivity {

	ActionSelectAdapter mAdapter;
	
	String sCurItemID;
	String sUserID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_student);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);

		Intent it = getIntent();

		sUserID = it.getStringExtra("title_id");
		
		String sName = "学生评价(" + it.getStringExtra("title_right") + ")";

		vTitle.setText(sName);

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
//		tvRight = (TextView) findViewById(R.id.text_right);
//		getRightText(RIGHT_NULL);
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_NULL);
		o_Picture_ID = R.drawable.teacher_logo;
		initListView();

		new GetDataTask().execute();
	}

	// 加载视图
	// 加载中间的视图
	protected void reLoadView() {
		mHelpValue = appContext.getItemList_student(sUserID);
	}

	
	//处理当前的评价项
	protected void processSaveData(String sItemID, int iEVType, int iGradeID,
			String sStudentID, String sGroupID) {
		// TODO Auto-generated method stub

		// 判断是否已经存在
		if (appContext.isExistERSult(Integer.valueOf(sItemID))) {
			// UIHelper.showMsg(this, "", "本项目该同学已经评价过");
			appContext.removeERSult(Integer.parseInt(sItemID));

			// 删除先
			// return;
		}

		if (appContext.insertEResult(Integer.valueOf(sItemID), iEVType,
				iGradeID, this.getEditText(R.id.ed_ev_context), sStudentID,
				sGroupID) > 0) {

			Toast.makeText(getApplicationContext(), "评价成功", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(getApplicationContext(), "评价失败，请重试",
					Toast.LENGTH_SHORT).show();
		}

		appContext.bNeedRefreshMainReview = true;
	}

	// 处理返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		if (requestCode == UIHelper.INTENT_SELECT_ITEM) {

			String sSelGradeID = data.getStringExtra("title_id");
			
			processSaveData(sCurItemID,0,Integer.valueOf(sSelGradeID),sUserID,"0");
			new GetDataTask().execute();
			return;
		}

	}

	protected void processClickView(String sTitleID, String sTitleLeft,
			String sTitleRight) {
		// Intent it = getIntent();

		sCurItemID = sTitleID;
		// setResult(Activity.RESULT_OK, it);

		Intent intent = new Intent(ReviewStudentItemActivity.this,
				SelectItemGradeActivity.class);

		intent.putExtra("item_id", sTitleID);
		intent.putExtra("title_right", sTitleLeft);

		startActivityForResult(intent, UIHelper.INTENT_SELECT_ITEM);

		// finish();
	}

}
