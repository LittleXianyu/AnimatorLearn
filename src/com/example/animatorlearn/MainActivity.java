package com.example.animatorlearn;

import android.os.Bundle;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
private String TAG="MainActivity";
private TextView text1,text2,text3,text_sync;
private Button button1;
private ImageView image1;
private LinearLayout layout_image;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text1=(TextView)findViewById(R.id.text1);
		text2=(TextView)findViewById(R.id.text2);
		text3=(TextView)findViewById(R.id.text3);
		text_sync=(TextView)findViewById(R.id.text_sync);
		button1 =(Button)findViewById(R.id.button1);
		layout_image=(LinearLayout)findViewById(R.id.layout_image);
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//viewXYAnimator();
				viewTranslationXAnimator();
				viewRotationAnimator();
				setSyncTextAnimator();
				viewPropertyValuesHolder();
				setImageState();
				
			}
		});
		
	}
	/**
	 * 属性动画Animator是View的位置变化
	 * 一般动画Animation分为：补间动画（两个关键帧间电脑自动合成变化效果），逐帧动画（多个图片逐帧播放，类似幻灯片）
	 * -------------------------------原理
	 * Animation框架：
	 * 实现原理是每次绘制视图时View所在的ViewGroup中的drawChild函数获取该View的Animation的Transformation值，
	 	然后调用canvas.concat(transformToApply.getMatrix())，通过矩阵运算完成动画帧，如果动画没有完成，继续调用invalidate()函数，
	  	启动下次绘制来驱动动画，动画过程中的帧之间间隙时间是绘制函数所消耗的时间，可能会导致动画消耗比较多的CPU资源。
	 * Animator框架：Animator Set和ObjectAnimator配合
	   	在Animator框架中使用最多的是AnimatorSet和ObjectAnimator配合，使用ObjectAnimator进行更精细化控制（PropertyValuesHolder），
		只控制一个对象的一个属性值，多个ObjectAnimator组合到AnimatorSet形成一个动画。而且ObjectAnimator能够自动驱动，
		可以调用setFrameDelay(longframeDelay)设置动画帧之间的间隙时间，调整帧率，减少动画过程中频繁绘制界面，而在不影响动画效果的前提下减少CPU资源消耗。
	 */
	public void viewXYAnimator(){
		ObjectAnimator animX = ObjectAnimator.ofFloat(text1, "x", 0f,50f);
		ObjectAnimator animY = ObjectAnimator.ofFloat(text1, "y", 0f,50f);
		AnimatorSet animSetXY = new AnimatorSet();
		animSetXY.playTogether(animX, animY);// playTogether表示异步叠加，还有playSequentially，表示同步执行
		animSetXY.setDuration(100);
		animSetXY.start();
	}
	
	public void viewRotationAnimator(){
		//一个简单的动画，非复合动画，直接用ObjectAnimator及可start
		ObjectAnimator.ofFloat(text2, "rotation", 0F, 360F).setDuration(200).start();//360度旋转
	}
	public void viewTranslationXAnimator(){
		ObjectAnimator animX = ObjectAnimator.ofFloat(text1, "translationX", 0f,50f);//从原位置移动到X方向50px处
		ObjectAnimator animY = ObjectAnimator.ofFloat(text1, "translationY", 0f,50f);
		AnimatorSet animSetXY = new AnimatorSet();
		animSetXY.playSequentially(animX, animY);// playTogether表示异步叠加，还有playSequentially，表示同步执行
		animSetXY.setDuration(100);
		animSetXY.start();
	}
	//ObjectAnimator细化方案：PropertyValuesHolder使用，多个属性值存储，每个属性又可以细化成关键帧。
	public void viewPropertyValuesHolder(){
		Keyframe kf0 = Keyframe.ofFloat(0f, 0f);  
		Keyframe kf1 = Keyframe.ofFloat(0.1f, 180f);  
		Keyframe kf2 = Keyframe.ofFloat(0.5f, 360f);  
		Keyframe kf3 = Keyframe.ofFloat(1.0f, 720f); 
		// 用三个关键帧构造PropertyValuesHolder对象，最后装配到ObjectAnimator
		PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe(  
		                        "x", kf0, kf1, kf2,kf3);  
		PropertyValuesHolder moveX = PropertyValuesHolder.ofFloat("translationX",0f,200f,300f,0f);
		PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",1.0f,0.2f,1f);
		//ofPropertyValuesHolder是多参数的，每个PropertyValuesHolder相当于动画预设值，这些动画异步执行
		ObjectAnimator animator= ObjectAnimator.ofPropertyValuesHolder(text3, pvhX).setDuration(3000); 
		animator.start();
	}
	public void setImageState(){
		image1=new ImageView(this);
		image1.setBackgroundResource(R.drawable.sample_1);
		LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		image1.setLayoutParams(layoutParams);
		layout_image.addView(image1);
//测量方案，先利用UNSPECIFIED模式测量View实际需要的空间大小。
		int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);  
		int childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED); 
		image1.measure(childWidthSpec, childHeightSpec);
		final int measuredHeight=-image1.getMeasuredHeight();
		Log.i(TAG, "measuredHeight"+measuredHeight);
//目标效果是从上向下，逐步显示出图片，也就是左上角坐标从一个负位置到0位置，负位置大小就是MeasureHeight
		PropertyValuesHolder moveX = PropertyValuesHolder.ofFloat("translationY",measuredHeight,0f);
		ObjectAnimator animator= ObjectAnimator.ofPropertyValuesHolder(image1,moveX).setDuration(3000); 
		//该方法用来监听动画绘制过程中的每一帧的改变，通过这个方法，我们可以在动画重绘的过程中，实现自己的逻辑。
		animator.addUpdateListener(new AnimatorUpdateListener() {
			private int sum=0;
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				float i=1.0f;
//				animation.getInterpolator().getInterpolation(arg0)
				//该方法会获取每一帧的“插值”，也就是结束值与开始值之间按照时间区间分割出多个值。
				//例如，移动时从Y轴方向view从-200移动到0，会添加-200到0的很多帧代表的数值：-199，-198等等直到0
				float ll=(Float) animation.getAnimatedValue();
				Log.i(TAG,"比例:::"+ll+"sum:  "+sum++);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300,(int) (ll-measuredHeight));
				layout_image.setLayoutParams(params);
				image1.setLayoutParams(params);
				//重绘自身的位置，构成View占用空间由小变大的效果。
				image1.requestLayout();
			}
		});
		animator.setFrameDelay(40);
		animator.setInterpolator(new AccelerateInterpolator(this, null));
		animator.start();
	}
	public void setSyncTextAnimator(){
		ObjectAnimator animX = ObjectAnimator.ofFloat(text_sync, "x", 0f,200f);//这个是相当于父View的最终位置。移动到该位置后再次运行不会动。
		ObjectAnimator animscaleY = ObjectAnimator.ofFloat(text_sync, "scaleY", 1f,2f,1f);
		AnimatorSet animSetXY = new AnimatorSet();
		animSetXY.playSequentially(animX, animscaleY);// playTogether表示异步叠加，还有playSequentially，表示同步执行
		animSetXY.setDuration(2000);
		animSetXY.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
