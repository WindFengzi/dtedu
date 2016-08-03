package com.winso.interactive;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {  
  
	public float fScaleWidth = 100;
	public float fScaleHeight = 100;
	
	public float fImageWidth=0;
	public float fImageHeight=0;
	
	public int  m_iTop=82;
	
	public int iRadious = 40;
	Vector<PointF> m_vPoints = new Vector<PointF>();
	Paint m_paint = new Paint();  
    public DrawView(Context context) {  
        super(context);  
        
        //addPoint(10,20);
        
        
        m_paint.setStyle(Paint.Style.STROKE);//设置填满  
        m_paint.setAntiAlias(true);
        
        m_paint.setColor(Color.RED);// 设置红色  
       // addPoint(103,220);
    }  
  
    public String getDraw()
    {
    	String sInfo = new String("");
    	
    	 for(int i=0;i<m_vPoints.size()-1;i++)
         {
    		 if ( i != m_vPoints.size()-1 )
    			 sInfo +=  m_vPoints.get(i).x + "," + m_vPoints.get(i).y + ";";
    		 else
    			 sInfo += m_vPoints.get(i).x  + "," + m_vPoints.get(i).y  ;
         }
    	 
    	 return sInfo;
    }
    public void addPoint(float x,float y)
    {
    	if ( fScaleWidth<= 0 )
    		fScaleWidth = 1;
    	
    	if ( fScaleHeight<= 0 )
    		fScaleHeight = 1;
    	
    	
    	PointF pt = new PointF(x*100/fScaleWidth,y*100/fScaleHeight);
 
    	m_vPoints.add(pt);
    	
    }
    public void addPointOut(float x,float y)
    {
    	
    	PointF pt = new PointF(x,y);
 
    	m_vPoints.add(pt);
    	
    }
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		//27,23
		//69,58
		
		float x = event.getX();
		float y = event.getY();

		//System.out.println("坐标:" + x + "," + y);
		//if (  y<=m_iTop )
		//{
		//	return super.onTouchEvent(event);
	//	}
		//else
		addPoint(x,y);
		invalidate();

		performClick();
		
		return super.onTouchEvent(event);
	}

	@Override
	 public boolean performClick() {
	  // Calls the super implementation, which generates an AccessibilityEvent
	        // and calls the onClick() listener on the view, if any
	        super.performClick();

	        // Handle the action for the custom click here

	        return true;
	 }
    public void removeAll()
    {
    	m_vPoints.removeAllElements();
    	
    }
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        /* 
         * 方法 说明 drawRect 绘制矩形 drawCircle 绘制圆形 drawOval 绘制椭圆 drawPath 绘制任意多边形 
         * drawLine 绘制直线 drawPoin 绘制点 
         */  
        // 创建画笔  
        
       // canvas.setDrawFilter(new PaintFlagsDrawFilte(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        

  
        //p.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了  
        
        
        //canvas.drawText("画圆：", 10, 20, p);// 画文本
        
        for(int i=0;i<m_vPoints.size();i++)
        {
        	canvas.drawCircle(fScaleWidth*m_vPoints.get(i).x/100, fScaleHeight*m_vPoints.get(i).y/100, iRadious, m_paint);// 小圆  
        }
        
//        canvas.drawCircle(120, 20, 20, p);// 大圆  
//  
//        canvas.drawText("画线及弧线：", 10, 60, p);  
//        p.setColor(Color.GREEN);// 设置绿色  
//        canvas.drawLine(60, 40, 100, 40, p);// 画线  
//        canvas.drawLine(110, 40, 190, 80, p);// 斜线  
//        //画笑脸弧线  
//        p.setStyle(Paint.Style.STROKE);//设置空心  
//        RectF oval1=new RectF(150,20,180,40);  
//        canvas.drawArc(oval1, 180, 180, false, p);//小弧形  
//        oval1.set(190, 20, 220, 40);  
//        canvas.drawArc(oval1, 180, 180, false, p);//小弧形  
//        oval1.set(160, 30, 210, 60);  
//        canvas.drawArc(oval1, 0, 180, false, p);//小弧形  
//  
//        canvas.drawText("画矩形：", 10, 80, p);  
//        p.setColor(Color.GRAY);// 设置灰色  
//        p.setStyle(Paint.Style.FILL);//设置填满  
//        canvas.drawRect(60, 60, 80, 80, p);// 正方形  
//        canvas.drawRect(60, 90, 160, 100, p);// 长方形  
//  
//        canvas.drawText("画扇形和椭圆:", 10, 120, p);  
//        /* 设置渐变色 这个正方形的颜色是改变的 */  
//        Shader mShader = new LinearGradient(0, 0, 100, 100,  
//                new int[] { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,  
//                        Color.LTGRAY }, null, Shader.TileMode.REPEAT); // 一个材质,打造出一个线性梯度沿著一条线。  
//        p.setShader(mShader);  
//        // p.setColor(Color.BLUE);  
//        RectF oval2 = new RectF(60, 100, 200, 240);// 设置个新的长方形，扫描测量  
//        canvas.drawArc(oval2, 200, 130, true, p);  
//        // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，第四个参数是真的时候画扇形，是假的时候画弧线  
//        //画椭圆，把oval改一下  
//        oval2.set(210,100,250,130);  
//        canvas.drawOval(oval2, p);  
//  
//        canvas.drawText("画三角形：", 10, 200, p);  
        // 绘制这个三角形,你可以绘制任意多边形
    }
    
    
    
}

