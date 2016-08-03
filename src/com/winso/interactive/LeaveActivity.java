package com.winso.interactive;

import com.winso.comm_library.DateTimePickDialogUtil;
import com.winso.interactive.app.UIHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LeaveActivity extends BaseViewSaveActivity {

	private String sTeacherID = "", sTeacherName = "", sFromTime = "",
			sToTime = "";

	private Button btSelectFromDate, btSelectToDate,btSelectTecacher;

	int iOK = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leave);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("请假");

		// 点击添加请假
		fbSave = (Button) findViewById(R.id.btn_save);
//		// fbSave = (Button) findViewById(R.id.btn_save);
//		// fbSave.setVisibility(View.INVISIBLE);
		getRightChangeBtn(RIGHT_SUBMIT);
		fbSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				startSaveThread();
				
				

			}
		});
		// 点击添加请假
//		tvRight = (TextView) findViewById(R.id.text_right);
//		getRightText(RIGHT_SUBMIT);
//		tvRight.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				startSaveThread();
//			}
//		});
		// 设置返回按扭
		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 选择老师
		btSelectTecacher = (Button) findViewById(R.id.bt_select_student);
		btSelectTecacher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//
				Intent intent = new Intent(v.getContext(),SelectTeacherActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_SELECT_TEACHER);
			}
		});

		// 选择老师
		btSelectFromDate = (Button) findViewById(R.id.bt_select_from_time);
		btSelectFromDate.setText("");
		btSelectFromDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String sInitDate = btSelectFromDate.getText().toString();
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						LeaveActivity.this, sInitDate);
				dateTimePicKDialog.dateTimePicKDialogButton(btSelectFromDate);

			}
		});
		// 选择老师
		btSelectToDate = (Button) findViewById(R.id.bt_select_grade);
		btSelectToDate.setText("");
		btSelectToDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String sInitDate = btSelectToDate.getText().toString();

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						LeaveActivity.this, sInitDate);
				dateTimePicKDialog.dateTimePicKDialogButton(btSelectToDate);
			}
		});

	}

	// bt_select_teacher

	// 处理返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		if (requestCode == UIHelper.INTENT_SELECT_TEACHER) {

			sTeacherID = data.getStringExtra("title_id");
			sTeacherName = data.getStringExtra("title_right");

			setButtonText(R.id.bt_select_student, sTeacherName);

			return;
		} else if (requestCode == UIHelper.INTENT_SELECT_DATE_TIME_FROM_TIME) {

			sFromTime = data.getStringExtra("title_right");
			setButtonText(R.id.bt_select_from_time, sFromTime);

			return;
		} else if (requestCode == UIHelper.INTENT_SELECT_DATE_TIME_TO_TIME) {

			sToTime = data.getStringExtra("title_right");
			setButtonText(R.id.bt_select_grade, sToTime);

			return;
		}
	}

	// 保存
	private int ProcessSave() {
		String sFromDate = btSelectFromDate.getText().toString();
		if (sFromDate.length() <= 0) {
			UIHelper.showMsg(this, "", "请选择开始时间");
			return -9;
		}

		String sToDate = btSelectToDate.getText().toString();
		if (sToDate.length() <= 0) {
			UIHelper.showMsg(this, "", "请选择结束时间");
			return -9;
		}

		// String sTeacherID = getButtonText(R.id.bt_select_teacher);
		if (sTeacherID.length() <= 0) {
			UIHelper.showMsg(this, "", "请选择请假教师");
			return -9;
		}

		String sContext = getEditText(R.id.ed_ev_context);
		if (sContext.length() <= 0) {
			UIHelper.showMsg(this, "", "请输入请假原因");
			return -9;
		}
		
		//判断时间是不能一样的
		
		if (sFromDate.equals(sToDate) ) {
			UIHelper.showMsg(this, "", "输入的开始与结束时间不能一样");
			return -9;
		}
		if (sFromDate.compareTo(sToDate) > 0  ) {
			UIHelper.showMsg(this, "", "输入的开始时间必须小于结束时间");
			return -9;
		}
		
		boolean bok =  appContext.insertLeave(sTeacherID, sContext,
				DateTimePickDialogUtil.convertToEnDateTime(sFromDate),
				DateTimePickDialogUtil.convertToEnDateTime(sToDate));
		
		if ( bok )
			return 1;
		
		return -1;
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
		iOK = ProcessSave();
		
		
	}

	@Override
	protected void processAfterSaveData() {
		// TODO Auto-generated method stub
		if (iOK >= 0 ) {
			Intent it = getIntent();
			//it.putExtra("title_id", sTitleID);
			setResult(Activity.RESULT_OK, it);
			
			finish();
		} 
		else if ( iOK == -9 )
		{
			
		}
		else 
		{
			UIHelper.showMsg(LeaveActivity.this, "", "保存失败");
		}
	}

	@Override
	protected void processDownload(boolean bOK) {
		// TODO Auto-generated method stub
		
	}

}
