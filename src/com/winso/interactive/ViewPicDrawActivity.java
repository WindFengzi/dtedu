package com.winso.interactive;

import com.winso.comm_library.app.TNAppContext;
import com.winso.interactive.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ViewPicDrawActivity extends BaseActivity {

	private ImageView tViewImage;
	private String sPic;
	//float fImageWidth = 100;
	//float fImageHeight = 100;
	float fBmpWidth = 0;
	float fBmpHeight = 0;
    float scaleWidth = 1;  
    float scaleHeight =1 ;
    
    float fScreenWidth = 0;
    float fScreenHeight = 0;
    
    
	DrawView mDrawView = null;
	
	String sInDrawInfo = new String("");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pic_draw);

		Intent it = getIntent();

		sInDrawInfo = it.getStringExtra("draw_info"); 
		sPic = it.getStringExtra("pic_path");

		tViewImage = (ImageView) findViewById(R.id.t_view_pic);

		
		fScreenWidth = this.getWindowManager().getDefaultDisplay().getWidth(); 
		fScreenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
		 
		//tViewImage.setMinZoom(1);
		//tViewImage.setMaxZoom(1);
		
		Bitmap bmpDefaultPic = BitmapFactory.decodeFile(sPic, null);
		if ( bmpDefaultPic == null )
		{
			TNAppContext.popMsg(ViewPicDrawActivity.this, "", sPic+"不存在");
			
			finish();
			return;
		}
		fBmpWidth = bmpDefaultPic.getWidth();
		fBmpHeight = bmpDefaultPic.getHeight();
		
		if ( fScreenWidth > 0 )
			scaleWidth=((float)fBmpWidth)/fScreenWidth;  
	    
		if ( fScreenHeight > 0 )
			scaleHeight=((float)fBmpHeight)/fScreenHeight;

	    
	    Matrix matrix=new Matrix();  
        matrix.postScale(1/scaleWidth,1/scaleHeight);  
          
        Bitmap newBitmap=Bitmap.createBitmap(bmpDefaultPic, 0, 0, (int)fBmpWidth,(int)fBmpHeight, matrix, true);  
        tViewImage.setImageBitmap(newBitmap);  
        
        
        
	   //tViewImage.setImageBitmap(bmpDefaultPic);

		
		// 设置返回按扭
		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent it = getIntent();
		
				it.putExtra("draw_info", mDrawView.getDraw());
				
				setResult(Activity.RESULT_OK, it);
				finish();
			}
		});

		//float fx = getResources().getDisplayMetrics().density;
	
		
		
		//fImageWidth = tViewImage.getDrawable().getIntrinsicWidth() * fx;
		//fImageHeight = tViewImage.getDrawable().getIntrinsicHeight() * fx;

		
		//RectF rc = tViewImage.getZoomedRect();

		initDraw();

		//
		Button btClear = (Button) findViewById(R.id.bt_clear_draw_c);
		btClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processClear();
			}
		});
	}


	void processClear() {
		mDrawView.removeAll();
		mDrawView.invalidate();
	}

	public void initDraw() {
		RelativeLayout ly = (RelativeLayout) findViewById(R.id.id_pic_root);
		mDrawView = new DrawView(this);
		
		mDrawView.fImageWidth = fScreenWidth;
		mDrawView.fImageHeight = fScreenHeight;
		
		mDrawView.fScaleHeight = fScreenHeight;
		mDrawView.fScaleWidth = fScreenWidth;

		//mDrawView.layout(0,0, (int)fScreenWidth, (int)fScreenHeight);
		
		mDrawView.setMinimumHeight((int) fScreenHeight);
		mDrawView.setMinimumWidth((int) fScreenWidth);
		// 通知view组件重绘
		
		//设置点
		//sInDrawInfo
	
		//
		String[] vsAllPoints = sInDrawInfo.split(";", -1);
		for(int i=0;i<vsAllPoints.length;i++)
		{
			String[] vsSub = vsAllPoints[i].split(",", -1);
			
			if ( vsSub.length < 2 ) continue;
			
			mDrawView.addPointOut(Float.parseFloat(vsSub[0]), Float.parseFloat(vsSub[1]));
			
		}
		
		
		mDrawView.invalidate();
		ly.addView(mDrawView);

		ly.invalidate();
	}

//	private void processDraw() {
//		mDrawView.addPoint(200, 300);
//		mDrawView.invalidate();
//
//	}
}
