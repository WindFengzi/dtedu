package com.winso.interactive;

import com.winso.interactive.app.UIHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EachReviewActivity extends BaseViewSaveActivity {

	String sStudentID = "";
	String sStudentName = "";
	String sItemID="",sItemName="";  //项目编号和名称
	String sGradeID="",sGradeName="";  //项目编号和名称
	int   iEVType = 0;  //代表普通  ,1代表组
	String sGroupID = "0";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_each_review);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("互评");

		// 设置返回按扭
		fbSave = (Button) findViewById(R.id.btn_save);
		// fbSave = (Button) findViewById(R.id.btn_save);
		// fbSave.setVisibility(View.INVISIBLE);
//		tvRight = (TextView) findViewById(R.id.text_right);
//		getRightText(RIGHT_SUBMIT);
		getRightChangeBtn(RIGHT_SUBMIT);
		fbSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if ( sStudentID.length() <=0  || sItemID.length() <= 0 || sGradeID.length() <=0 )
				{
					UIHelper.showMsg(EachReviewActivity.this, "", "请选择好学生、项目及评分后再保存");
					return;
				}

				
				processSaveData();

			}
		});

		// 选择老师
		Button btSelectStudent = (Button) findViewById(R.id.bt_select_student);
		btSelectStudent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//
				Intent intent = new Intent(v.getContext(),
						SelectStudentActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_SELECT_STUDENT);
			}
		});

		// 选择项目
		Button btSelectItem = (Button) findViewById(R.id.bt_select_item);
		btSelectItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//
				Intent intent = new Intent(v.getContext(),
						SelectItemActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_SELECT_ITEM);
			}
		});
		
		//选择评分
		Button btSelectGrade = (Button) findViewById(R.id.bt_select_grade);
		btSelectGrade.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//
				if ( sItemID.length() <= 0 )
					return;
				
				Intent intent = new Intent(v.getContext(),
						SelectItemGradeActivity.class);
				intent.putExtra("item_id",sItemID);
				startActivityForResult(intent, UIHelper.INTENT_SELECT_GRADE);
			}
		});
		
		//SelectItemGradeActivity

		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
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

		if (requestCode == UIHelper.INTENT_SELECT_STUDENT) {
			sStudentID = data.getStringExtra("title_id");
			sStudentName = data.getStringExtra("title_right");

			setButtonText(R.id.bt_select_student, sStudentName);
			return;
		}
		else if (requestCode == UIHelper.INTENT_SELECT_ITEM) {
			sItemID = data.getStringExtra("title_id");
			sItemName = data.getStringExtra("title_right");

			setButtonText(R.id.bt_select_item, sItemName);
			return;
		}
		else if (requestCode == UIHelper.INTENT_SELECT_GRADE) {
			sGradeID = data.getStringExtra("title_id");
			sGradeName = data.getStringExtra("title_right");

			setButtonText(R.id.bt_select_grade, sGradeName);
			return;
		}
		
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub

		//public int insertEResult(int iItemID,int iEVType,int iGradeID,String sEVInfo,String sBeUserID,String sBeGroupID) 
		
	}

	@Override
	protected void processLoadData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processSaveData() {
		// TODO Auto-generated method stub
		
		//判断是否已经存在
		if ( appContext.isExistERSult(Integer.valueOf(sItemID)) )
		{
			UIHelper.showMsg(this, "", "本项目该同学已经评价过");
			return;
		}
		
		if ( appContext.insertEResult(Integer.valueOf(sItemID),
				iEVType, Integer.valueOf(sGradeID),
				this.getEditText(R.id.ed_ev_context), 
				sStudentID, sGroupID) > 0 )
		{
			
			Toast.makeText(getApplicationContext(), "评价成功", Toast.LENGTH_SHORT)
			.show();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "评价失败，请重试", Toast.LENGTH_SHORT)
			.show();
		}
		
		
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
