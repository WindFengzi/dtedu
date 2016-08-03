package com.winso.interactive;

import com.winso.comm_library.TimeZoneUtil;
import com.winso.interactive.app.UIHelper;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PointActivity extends BaseViewSaveActivity {

	private String sCreateTime=""; // 创建时间
	private String sExeInfo = "";
	private EditText eInputCheck;
	private Button fBSubmit; // 签到按钮
	private TextView tvDiag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_point);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText(R.string.check_in);

		bIsNeedProgressDialog = false;
		// 设置返回按扭
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_NULL);
		eInputCheck = (EditText) findViewById(R.id.ed_point_no);
		// 點擊簽到按鈕
		fBSubmit = (Button) findViewById(R.id.bt_summit_point);
		fBSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//UIHelper.showMsg(PointActivity.this, "", sCreateTime);
				if ( sCreateTime.length() > 0 )
				{
					SetInfo("本节课已经签到，无需再签到");
					inputHintAndEnabled();
					//UIHelper.showMsg(PointActivity.this, "", "请输入校验码");
					return;					
				}

				// 判断是否一致
				String sCheckNo = appContext.getCheckNo();
				if (sCheckNo.length() <= 0) {
					SetInfo("获取校验码出错");
					return;
				}

				if (!sCheckNo.equals(getInput())) {
					SetInfo("输入的校验码不正确");
					return;
				}
								
				if (getInput().length() <= 0) {
					//UIHelper.showMsg(PointActivity.this, "", "请输入校验码");
					SetInfo("请输入校验码");
					return;
				}
							
				startSaveThread();
			}
		});

		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
//		tvDiag = (TextView) findViewById(R.id.tx_update_diag);
//		System.out.println("sExeInfo---> " + sExeInfo + "; tvDiag.getText()--->" + tvDiag.getText());
//		if (tvDiag.getText().equals("本节课已经签到，无需再签到") || tvDiag.getText() == "本节课已经签到，无需再签到") {
//			inputHintAndEnabled();
//		}
		
		startLoadDataThread();		
	}

	private String getInput() {
		return eInputCheck.getText().toString();
	}

	private void SetInfo(String sInfo)
	{
//		setTextViewText(R.id.tx_point_info,sInfo);
//		System.out.println("sInfo---> " + sInfo + ";sCreateTime--->" + sCreateTime);
		setTextViewText(R.id.tx_update_diag,sInfo);
		setTextViewText(R.id.tx_update_time,sCreateTime);
		
	}
	
	public void inputHintAndEnabled() {
		eInputCheck.setEnabled(false);
		eInputCheck.setHint(R.string.input_hint_after);
	
		fBSubmit.setEnabled(false);
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub
		if (appContext.getISLeave() )
		{
			sExeInfo = "已经请假，无需签到";
			inputHintAndEnabled();
			return;
		}
		
		sCreateTime = appContext.getIsAtandence();
		if (sCreateTime.length() > 0) {
			sExeInfo = "本节课已经签到，无需再签到";
			inputHintAndEnabled();			
		}
		else {
			sExeInfo = "请输入校验码";
		}
		
		SetInfo(sExeInfo);
	}

	@Override
	protected void processLoadData() {
		// TODO Auto-generated method stub
//		setTextViewText(R.id.tx_point_info,sExeInfo);
		setTextViewText(R.id.tx_update_diag,sExeInfo);
	}

	@Override
	protected void processSaveData() {

		if ( sCreateTime.length() > 0 )
			return;
		
		if (!appContext.insertAttendance()) {

			sExeInfo = "签到失败，可能网络异常";

		} else {

			sExeInfo = "签到成功";
//			eInputCheck.setEnabled(false);
			inputHintAndEnabled();
			
		}
		sCreateTime = TimeZoneUtil.getCurDateTime();
		UIHelper.showMsg(this, "", sExeInfo);
	}

	@Override
	protected void processAfterSaveData() {
		// TODO Auto-generated method stub
		SetInfo(sExeInfo);
		
		if  ( sExeInfo.equals("签到成功") )
		{
			inputHintAndEnabled();
			finish();
		}
	}

	@Override
	protected void processDownload(boolean bOK) {
		// TODO Auto-generated method stub
		
	}

}
