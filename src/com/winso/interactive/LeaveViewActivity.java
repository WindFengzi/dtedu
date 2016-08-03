package com.winso.interactive;

import com.winso.comm_library.icedb.SelectHelp;
import com.winso.interactive.R;
import com.winso.interactive.app.UIHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LeaveViewActivity extends BaseViewSaveActivity {

	SelectHelp mHelpTeachers=null;
	private String sLeaveID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leave_view);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("请假单");

		// 设置返回按扭
//		Button fbSave = (Button) findViewById(R.id.btn_save);
//		fbSave.setVisibility(View.INVISIBLE);

		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Intent it = getIntent();
		sLeaveID = it.getStringExtra("title_id");

		// et.setKeyListener(null);
		EditText ed = (EditText)findViewById(R.id.ed_ev_context);
		if ( ed != null)
		{
			ed.setKeyListener(null);
		}
		startLoadDataThread();
	}

	@Override
	protected void loadData() {

		mHelpGetData = appContext.getLeaveDeatail(sLeaveID);

		String sID1,sID2;
		
		if ( mHelpGetData.size() <= 0 )
		{
			return;
		}
		sID1 = mHelpGetData.valueStringByName(0, "teacher_id");
		sID2 = mHelpGetData.valueStringByName(0, "review_teacher_id");
		
		mHelpTeachers = appContext.getReviewTeacher(sID1,sID2);
		
	}

	@Override
	protected void processLoadData() {

		if (mHelpGetData == null)
			return;

		if (mHelpGetData.size() <= 0)
			return;

		int iReviewType = Integer.valueOf(mHelpGetData.valueStringByName(0, "review_type"));
		
		if ( iReviewType == 1  )
		{
			setButtonText(R.id.bt_review_type,"审批通过");
		}
		else if ( iReviewType == 0 )
		{
			setButtonText(R.id.bt_review_type,"待审批");
		}
		else
		{
			setButtonText(R.id.bt_review_type,"审批不通过");
		}
		setEditText(R.id.ed_ev_context,mHelpGetData.valueStringByName(0, "content"));
		setButtonText(R.id.bt_select_item,mHelpGetData.valueStringByName(0, "from_time"));
		//UIHelper.showMsg(this, "", mHelpGetData.valueStringByName(0, "from_time"));
		
		setButtonText(R.id.bt_select_grade,mHelpGetData.valueStringByName(0, "to_time"));
		setButtonText(R.id.bt_review_time,mHelpGetData.valueStringByName(0, "review_time"));
		
		if ( mHelpTeachers == null )
		{
			return;
		}
		if ( mHelpTeachers.size() <= 0 )
			return;
		
		for(int i=0;i<mHelpTeachers.size();i++)
		{
			String sID = mHelpTeachers.valueStringByName(i, "user_id");
			
			
			if ( mHelpGetData.valueStringByName(0, "teacher_id").equals(sID) )
			{
				setButtonText(R.id.bt_select_student,mHelpGetData.valueStringByName(0, "teacher_name"));
			}
			
			if ( mHelpGetData.valueStringByName(0, "review_teacher_id").equals(sID) )
			{
				setButtonText(R.id.bt_review_teacher,mHelpTeachers.valueStringByName(i, "review_teacher_name"));
			}
		}
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
