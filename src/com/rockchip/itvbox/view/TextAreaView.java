/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年4月16日 上午11:19:41  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年4月16日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.view;

import java.util.Vector;

import com.rockchip.itvbox.R;
import com.rockchip.itvbox.utils.StringUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;

public class TextAreaView extends View {

	private int mTextWidth;//文本的宽度
	private int mTextHeight; //文本的高度
	private int mPaddingTop;
	private int mPaddingLeft;
	private int mPaddingRight;
	private int mPaddingBottom;
	private float mLineSpace = 0;//行间距
	private Paint mPaint = null;
	private String mContentText = "";
	private boolean mIndentOnStart;//是否缩进
	private Vector<String> mLineStrVector;
	private int mFirstCharWidth;//第一个字符宽度
	private boolean isContentDirty;//内容是否改变
	
	       
    public TextAreaView(Context context, AttributeSet set)
    {      
        super(context,set); 

        TypedArray typedArray = context.obtainStyledAttributes(set, R.styleable.TextAreaView);
        mContentText = typedArray.getString(R.styleable.TextAreaView_text);       
        float textsize = typedArray.getDimension(R.styleable.TextAreaView_textSize, 24);
        int textcolor = typedArray.getColor(R.styleable.TextAreaView_textColor, -1442840576);
        float linespace = typedArray.getDimension(R.styleable.TextAreaView_lineSpacingExtra, 15);
        mPaddingTop = (int)typedArray.getDimension(R.styleable.TextAreaView_paddingTop, 0);
        mPaddingLeft = (int)typedArray.getDimension(R.styleable.TextAreaView_paddingLeft, 0);
        mPaddingRight = (int)typedArray.getDimension(R.styleable.TextAreaView_paddingRight, 0);
        mPaddingBottom = (int)typedArray.getDimension(R.styleable.TextAreaView_paddingBottom, 0);
        mIndentOnStart = typedArray.getBoolean(R.styleable.TextAreaView_needIndent, false);
        typedArray.recycle();
       
        mLineSpace = linespace;
       
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(textcolor);
        mPaint.setTextSize(textsize);
        isContentDirty = true;
    }
    
    @Override
    protected void onFinishInflate() {
    	super.onFinishInflate();
    }
    
    protected void invalidateTextChanged() {
    	if(!isContentDirty||!StringUtils.hasLength(mContentText)){
    		return;
    	}
    	mLineStrVector = new Vector<String>(10);
    	char schar;
        int startIndex = 0;
        int currWidth = 0;
        for (int i = 0; i < mContentText.length(); i++){
     	   schar = mContentText.charAt(i);
     	   float[] widths = new float[1];
     	   String srt = String.valueOf(schar);
     	   mPaint.getTextWidths(srt, widths);

     	   if (schar == '\n'){
     		   mLineStrVector.addElement(mContentText.substring(startIndex, i));
     		   startIndex = i + 1;
     		   currWidth = 0;
     	   }else{
     		   currWidth += (int) (Math.ceil(widths[0]));
     		   if(i==0) mFirstCharWidth = currWidth;
     		   int lineWidth = 0;
     		   if(mIndentOnStart&&mLineStrVector.size()==0){//第一行缩进处理
     			  lineWidth = mTextWidth-2*mFirstCharWidth;
     		   }else{
     			  lineWidth = mTextWidth;
     		   }
     		   if (currWidth > lineWidth){
     			   mLineStrVector.addElement(mContentText.substring(startIndex, i));
     			   startIndex = i;
     			   i--;
     			   currWidth = 0;
     		   }else{
     			   if (i == (mContentText.length() - 1)){
     				   mLineStrVector.addElement(mContentText.substring(startIndex, mContentText.length()));
     			   }
     		   }
     	   }
        }
        isContentDirty = false;
    }
    
    
    /**
     * 获取行数
     */
    public int getLineCount(){
    	return mLineStrVector.size();
    }
    
    /**
     * 获取行高
     */
    public int getLineHeight(){
    	FontMetrics fm = mPaint.getFontMetrics();
    	int fontHeight = (int) Math.ceil(fm.descent - fm.top) + (int)mLineSpace;
    	return fontHeight;
    }
 
    /**
     * 重写画布
     */
    protected void onDraw(Canvas canvas)
    { 
       //super.onDraw(canvas);      
       if(!StringUtils.hasLength(mContentText)){
    	   return;
       }
       
       //Draw
       int fontHeight = getLineHeight(); 
       int baseY = fontHeight/2+mPaddingTop;
       //canvas.setViewport(mTextWidth, mTextHeight);
       for (int i = 0; i < mLineStrVector.size(); i++){
    	   if(mIndentOnStart&&i==0){
    		   canvas.drawText(mLineStrVector.elementAt(i), mPaddingLeft+mFirstCharWidth*2, baseY+fontHeight*i, mPaint);
    	   }else{
    		   canvas.drawText(mLineStrVector.elementAt(i), mPaddingLeft, baseY+fontHeight*i, mPaint);
    	   }
       }
    } 
  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {            
        mTextWidth = measureWidth(widthMeasureSpec);
        invalidateTextChanged();
        int measuredHeight = measureHeight(heightMeasureSpec);  
        this.setMeasuredDimension(mTextWidth, measuredHeight);
        //super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, mTextWidth), MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, measuredHeight));
    } 
    
    private int measureWidth(int measureSpec){ 
        int specMode = MeasureSpec.getMode(measureSpec);          
        int specSize = MeasureSpec.getSize(measureSpec);            
         
        int result = 0;         
        if (specMode == MeasureSpec.AT_MOST){         
            result = specSize-mPaddingLeft-mPaddingRight;         
        }else if (specMode == MeasureSpec.EXACTLY){          
            result = specSize;           
        }          
        return result;         
    }
                
    private int measureHeight(int measureSpec)
    { 
        int specMode = MeasureSpec.getMode(measureSpec);         
        int specSize = MeasureSpec.getSize(measureSpec);                  
        int result = 0;         
        if (specMode == MeasureSpec.EXACTLY){          
            result = specSize;           
        }else{
        	result = Math.min(getLineCount()*getLineHeight(), specSize)+mPaddingTop+mPaddingBottom;
        }
        return result;     
    } 
    
	public void setText(String text){
		mContentText = text;
		isContentDirty = true;
		//onTextChanged();
		requestLayout();
		invalidate();
	}
}
