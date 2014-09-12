package com.pujoy.charminder.views;

import java.util.Random;

import com.pujoy.charminder.R;
import com.pujoy.charminder.base.FloatingBase;
import com.pujoy.charminder.base.FunctionWrapper;
import com.pujoy.charminder.base.PopDialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import static com.pujoy.charminder.MainActivity.con;
import static com.pujoy.charminder.MainActivity.timerThread;
import static com.pujoy.charminder.MainActivity.NUM_CIRCLE_ITEMS;

public class Charmy extends FloatingBase implements OnTouchListener, OnClickListener{
	RelativeLayout mainView;
	ImageView ivIcon;
	ImageView ivIconCenter;
	public Bubble bubble;
	MainCircle mainCircle;
	private float fOldMouseX;
	private float fOldMouseY;
	private float fX;
	private float fY;
	private boolean bBeingRemoved;

	@Override
	protected void onInitialize(){
		layoutParams.setWidth((int) dpToPx(48));
		layoutParams.setHeight((int) dpToPx(48)); 
		layoutParams.setX((int) (getScreenWidth() - layoutParams.getWidth()));
		layoutParams.setY((int) (getScreenHeight()*0.75 - layoutParams.getHeight())); 
		mainView = new RelativeLayout(con);
		mainView.setOnClickListener(this);
		mainView.setOnTouchListener(this);
		ivIcon = new ImageView(con);
		ivIcon.setImageResource(R.drawable.charmy);
		ivIcon.setOnClickListener(this);
		ivIcon.setOnTouchListener(this);
		mainView.addView(ivIcon);
		ivIconCenter = new ImageView(con);
		ivIconCenter.setImageResource(R.drawable.charmy_center);
		ivIconCenter.setOnClickListener(this);
		ivIconCenter.setOnTouchListener(this);
		mainView.addView(ivIconCenter);
		bubble = new Bubble();
		mainCircle = new MainCircle();	
	}

	
	@Override
	protected void onCreate(){
		addView(mainView, layoutParams); 
		bBeingRemoved = false;
	}
	
	@Override
	public void remove(){
		if (!bCreated){
			return;
		}
		bBeingRemoved = true;
		ivIcon.setPivotX(ivIcon.getWidth()/2);
		ivIcon.setPivotY(ivIcon.getHeight()/2);
		ValueAnimator aRotation = ObjectAnimator.ofFloat(ivIcon, "rotation",
				0, 3600/2);
		aRotation.setDuration(3000);
		aRotation.addListener(new Animator.AnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            	removeView(mainView);
            	if(bubble.isCreated()){
        			bubble.remove();
        		}
            	bCreated = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

			@Override
			public void onAnimationCancel(Animator animation) {
				removeView(mainView);
				if(bubble.isCreated()){
					bubble.remove();
				}
				bCreated = false;
			}
        });
		aRotation.start();
		
	}
	
	@Override
	protected void onRemove(){
	}
	
	@Override
	protected void onUpdateLayout(){ 
		bubble.iIconPositionX = layoutParams.getX();
		bubble.iIconPositionY = layoutParams.getY();
		bubble.onUpdateLayout();
	}
	
	public void PushBubble(String BubbleText){
		bubble.setText(BubbleText);
		bubble.create();
	}
	
	public void Update(){
		updateViewLayout(mainView, layoutParams);
		bubble.Update();
	}
	
	@Override
	public void onClick(View v){
		if (bBeingRemoved)
			return;
		if(bubble.isCreated()){
			bubble.remove();
		}
		Babble();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (bBeingRemoved)
			return false;
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(bubble.isCreated()){
				bubble.remove();
			}
			mainCircle.create();
			fOldMouseX = event.getRawX();
			fOldMouseY = event.getRawY();
			fX = layoutParams.getX();
			fY = layoutParams.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			fX += event.getRawX() - fOldMouseX;
			fY += event.getRawY() - fOldMouseY;
			layoutParams.setX((int) (fX));
			layoutParams.setY((int) (fY)); 
			fOldMouseX = event.getRawX();
			fOldMouseY = event.getRawY();
			onUpdateLayout();
			Update();	
			for(int i=0; i < NUM_CIRCLE_ITEMS; i++){
				if(mainCircle.isPointInsideItem(event.getRawX(), event.getRawY(), i)){
					mainCircle.Hover(i);
			    	mainCircle.tvCircleDescription.setBackgroundColor(android.graphics.Color.argb(192, 48, 78, 98));
					break;
				}else{
					if (i == NUM_CIRCLE_ITEMS - 1){
						mainCircle.tvCircleDescription.setText("");
						mainCircle.tvCircleDescription.setBackgroundColor(android.graphics.Color.argb(
								0, 48, 78, 98));
						mainCircle.updateOldHoverItem();
					}
				
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mainCircle.remove();
			timerThread.moveIconToCorner();
			for(int i=0;i<NUM_CIRCLE_ITEMS;i++){
				if(mainCircle.isPointInsideItem(event.getRawX(), event.getRawY(), i)){
					switch(i){
					case 0:
					{
						Timer1 timer1 = new Timer1();
						timer1.create();
						break;
					}
						
					case 1:
					{
						Timer2 timer2 = new Timer2();
						timer2.create();
						break;
					}
					case 2:
					{
						Timer3 timer3 = new Timer3();
						timer3.create();
						break;
					}
					case 3:
						//GoToActivity(Timer4.class);
						break;
					case 4:
						//GoToActivity(Settings.class);
						break;
					case 5:
						//GoToActivity(RemindersList.class);
						break;
					case 6:
					{
						PopDialog dialog = new PopDialog(con.getResources().getString(R.string.onExit_content, 
								con.getResources().getString(R.string.app_name)),
								con.getResources().getString(R.string.onExit_title),
							new FunctionWrapper(){
								@Override
								public void function() {
									remove();
									PushBubble(con.getResources().getString(R.string.b_exit));
								}
							},
							new FunctionWrapper(){
								@Override
								public void function() {
									remove();
									PushBubble(con.getResources().getString(R.string.b_exit_completely));
									timerThread = null;
								}
							});
						dialog.setOkText(con.getResources().getString(R.string.yes));
						dialog.setCancelText(con.getResources().getString(R.string.no));
						dialog.create();
					}
					}
					break;
				}else{
		//			if (i==NUM_CIRCLE_ITEMS-1)
		//				tvCircleDescription.setText("");
				}
			}
			break;
		}
		return false;
	}
	
	public void Babble(){
		String[] babble_text = con.getResources().getStringArray(R.array.babble_bubble);
		Random r = new Random();
		PushBubble(babble_text[r.nextInt(babble_text.length)]);
	}

	public boolean MoveToCorner() {
		int toLeft = layoutParams.getX();
		int toRight = getScreenWidth() - layoutParams.getX() - layoutParams.getWidth();
		int toTop = layoutParams.getY();
		int toBottom = getScreenHeight() - layoutParams.getY() - layoutParams.getHeight();
		final int speed = (int) dpToPx(8);
		float retationSpeed = 12;
		boolean r = false;
		if (Math.min(toLeft, toRight) <= Math.min(toTop, toBottom)){
			if(toLeft < toRight){
				if(layoutParams.getX() != 0){
					layoutParams.setX(layoutParams.getX() - speed);
					retationSpeed = -retationSpeed;
					r = true;
				}
			}else{
				if(layoutParams.getX() != getScreenWidth() - layoutParams.getWidth()){
					layoutParams.setX(layoutParams.getX() + speed);
					r = true;
				}
			}
		}else{
			if(toTop < toBottom){
				if(layoutParams.getY() != 0){
					layoutParams.setY(layoutParams.getY() - speed);
					retationSpeed = -retationSpeed;
					r = true;
				}
			}else{
				if(layoutParams.getY() != getScreenHeight() - layoutParams.getHeight()){
					layoutParams.setY(layoutParams.getY() + speed);
					r = true;
				}
			}
		}
		if(r){
			ivIcon.setPivotX(ivIcon.getWidth()/2);
			ivIcon.setPivotY(ivIcon.getHeight()/2);
			ivIcon.setRotation(ivIcon.getRotation() + retationSpeed);
			onUpdateLayout();
			Update();	
		}
		return r;
	}
}
