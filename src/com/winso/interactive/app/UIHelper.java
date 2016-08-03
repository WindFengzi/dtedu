package com.winso.interactive.app;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.winso.interactive.SettingActivity;
import com.winso.interactive.AboutActivity;
import com.winso.interactive.LoginActivity;
import com.winso.interactive.R;
import com.winso.interactive.ViewPicActivity;
import com.winso.interactive.ViewPicDrawActivity;

public class UIHelper {
	//
	public static final int INTENT_VIEW_MAIN_POINT = 1000;
	public static final int INTENT_VIEW_MAIN_ANSWER = 1001;
	public static final int INTENT_VIEW_MAIN_REVIEW_EACHOTHER = 1002;
	public static final int INTENT_MAIN_LEAVE = 1003;
	public static final int INTENT_ADD_LEAVE = 1004;
	public static final int INTENT_VIEW_LEAVE = 1004;
	
	public static final int INTENT_SELECT_TEACHER = 1005;  //选择老师Intent
	public static final int INTENT_SELECT_TEACHER_MAIN = 1006;  //请假首页
	public static final int INTENT_WRITE_ANSWER = 1007;  //回答
	public static final int INTENT_EACH_REVIEW=1008;//互评
	public static final int INTENT_SELECT_STUDENT = 1009;  //选择学生Intent
	public static final int INTENT_SELECT_ITEM = 1010; //选择项目
	public static final int INTENT_SELECT_GRADE = 1011; //选择评级
	
	public static final int INTENT_DRAW_PIC = 1012; //画图
	public static final int INTENT_ITEM_REVIEW_STUDENT=1013;//评价单个学生
	
	public static final int INTENT_SELECT_DATE_TIME = 2000; //选择时间和日期
	public static final int INTENT_SELECT_DATE_TIME_FROM_TIME = 2001; //选择开始时间和日期
	public static final int INTENT_SELECT_DATE_TIME_TO_TIME = 2002; //选择时间和日期
	
	
	
	
	public static void showMsg(Context ac, String title, String msg) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(ac);

		alertDialog.setTitle(title);
		alertDialog.setIcon(R.drawable.ic_launcher);

		alertDialog.setMessage(msg);

		alertDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});

		alertDialog.show();

	}

	
	/**
	 * 加载关于同步对话�?.
	 */
	public static void openViewPic(Context ct,String sPicPath) {
		Intent intent = new Intent(ct, ViewPicDrawActivity.class);
		
		intent.putExtra("pic_path", sPicPath);
		
		ct.startActivity(intent);
	}
	
	/**
	 * 加载设置对话框.
	 */
	public static void openSetting(Context ct) {
		Intent intent = new Intent(ct, SettingActivity.class);
		ct.startActivity(intent);
	}

	/**
	 * 加载关于对话框.
	 */
	public static void openAbout(Context ct) {
		Intent intent = new Intent(ct, AboutActivity.class);
		ct.startActivity(intent);
	}
	
	/**
	 * 退出登录
	 * @author hzx
	 * @date 2016/07/29
	 * */
	public static void exitLogin(Context ct) {
		Intent intent = new Intent(ct, LoginActivity.class);
		ct.startActivity(intent);
	}
	
}
