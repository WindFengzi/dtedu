package com.winso.interactive;

import com.winso.interactive.app.AppContext;
import com.winso.interactive.app.AppManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 应用程序Activity的基类
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-11-11
 */
@SuppressLint("NewApi")
public class BaseActivity extends Activity {

	// 是否允许全屏
	private boolean allowFullScreen = true;

	// 是否允许销毁
	private boolean allowDestroy = true;
	public AppContext appContext;
	private View view;

	public TextView tvRight;
	public Button fbSave;
	public final String RIGHT_SUBMIT = "sumit";
	public final String RIGHT_ADD = "add";
	public final String RIGHT_SAVE = "save";
	public final String RIGHT_NULL = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		
		super.onCreate(savedInstanceState);
		
		allowFullScreen = true;
		// 添加Activity到堆栈
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // 隐藏软件键盘输入。
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 无标题栏。
		AppManager.getAppManager().addActivity(this);

		appContext = (AppContext) getApplication();

		setContentView(R.layout.activity_main_header);
//		tvRight = (TextView) findViewById(R.id.text_right);
		fbSave = (Button) findViewById(R.id.btn_save);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

	public boolean isAllowFullScreen() {
		return allowFullScreen;
	}

	protected void onPause() {
		super.onPause();

	}

	public void getRightChangeBtn(String str) {
		Drawable drawable;
		
		if (str.equals(RIGHT_SUBMIT)) {
			drawable = getResources().getDrawable(R.drawable.btn_true);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
			fbSave.setCompoundDrawables(null, null, drawable, null);
		} else if (str.equals(RIGHT_ADD)) {
			drawable = getResources().getDrawable(R.drawable.btn_add);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
			fbSave.setCompoundDrawables(null, null, drawable, null);
		} else if (str.equals(RIGHT_SAVE)) {
			drawable = getResources().getDrawable(R.drawable.btn_true);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
			fbSave.setCompoundDrawables(null, null, drawable, null);
		}else if (str.equals(RIGHT_NULL)) {
			fbSave.setVisibility(View.INVISIBLE);
		}
		fbSave.setWidth(fbSave.getWidth());
		
		fbSave.setPadding(fbSave.getPaddingLeft(), fbSave.getPaddingTop(), fbSave.getPaddingRight(), fbSave.getPaddingBottom());
	}
	
	public void getRightText(String str) {
		if (str.equals(RIGHT_SUBMIT)) {
			tvRight.setText(R.string.submit);
		} else if (str.equals(RIGHT_ADD)) {
			tvRight.setText(R.string.add);
		} else if (str.equals(RIGHT_SAVE)) {
			tvRight.setText(R.string.save);
		}else if (str.equals(RIGHT_NULL)) {
			tvRight.setText("");
		}
	}
	
	// public void onAttachedToWindow() {
	// this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	// super.onAttachedToWindow();
	// }

	/**
	 * 设置是否可以全屏
	 * 
	 * @param allowFullScreen
	 */
	public void setAllowFullScreen(boolean allowFullScreen) {
		this.allowFullScreen = allowFullScreen;
	}

	public void setAllowDestroy(boolean allowDestroy) {
		this.allowDestroy = allowDestroy;
	}

	public void setAllowDestroy(boolean allowDestroy, View view) {
		this.allowDestroy = allowDestroy;
		this.view = view;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
			view.onKeyDown(keyCode, event);
			if (!allowDestroy) {
				return false;
			}
		} 

		return super.onKeyDown(keyCode, event);
	}

	public void setButtonText(int iID, String sText) {
		Button bt = (Button) findViewById(iID);
		if (bt != null) {
			bt.setText(sText);
		}

	}

	public void setEditText(int iID, String sText) {
		EditText bt = (EditText) findViewById(iID);
		if (bt != null) {
			bt.setText(sText);
		}

	}

	public void setTextViewText(int iID, String sText) {
		TextView tx = (TextView) findViewById(iID);
		if (tx != null) {
			tx.setText(sText);
		}

	}

	public String getButtonText(int iID) {
		Button bt = (Button) findViewById(iID);
		if (bt != null) {
			return bt.getText().toString();
		}

		return "";
	}

	public String getEditText(int iID) {
		EditText bt = (EditText) findViewById(iID);
		if (bt != null) {
			return bt.getText().toString();
		}

		return "";
	}

	public String getTextViewText(int iID) {
		TextView bt = (TextView) findViewById(iID);
		if (bt != null) {
			return bt.getText().toString();
		}

		return "";
	}

}
