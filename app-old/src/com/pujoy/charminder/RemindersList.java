package com.pujoy.charminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class RemindersList extends Activity implements OnClickListener, OnTouchListener{

	private ArrayList<View> itemsList = new ArrayList<View>();
	private ArrayList<Boolean> itemsListExpanded = new ArrayList<Boolean>();
	private static boolean isInFront = false;
	
	
	private static final int UPDATE_REMINDING_TIME = 24;
	
	   private Handler mHandler = new Handler() {
	        @Override
			public void handleMessage(Message msg) {
	            if (msg.what == UPDATE_REMINDING_TIME && isInFront){
     			for(int i=0; i<MainActivity.reminderList.size(); i++){
     	        	TextView tvCountDown = (TextView)findViewById(720+i);
     	        	if(tvCountDown != null){
         	        	Calendar t = Calendar.getInstance();
         	        	if(t.after(MainActivity.reminderList.get(i).time_to_remind)){
         	        		tvCountDown.setText(getString(R.string.expired));
         	        	}else{
         	        		t.setTimeInMillis(MainActivity.reminderList.get(i).time_to_remind.getTimeInMillis() - t.getTimeInMillis());
         	        		tvCountDown.setText(FormatRemindingTime(t.getTimeInMillis()));
         	        	}
         	        	
     	        	}
         			}
     				if(isInFront)
     					mHandler.sendEmptyMessageDelayed(UPDATE_REMINDING_TIME, 1000);
	            }
	        }
	 };
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminderslist);
        RecreateMain();
	}
	public void onClick(View v) { 
		for(int i=0; i<MainActivity.reminderList.size(); i++)
        {
        	if(v.getId() == 520+i)
        	{
        		if(itemsListExpanded.get(i)){
                	LinearLayout loView = (LinearLayout)itemsList.get(i);
            		View vItem = (View)findViewById(620+i);
                	loView.removeView(vItem);
                	ImageView ivExpand = (ImageView)findViewById(820+i);
                	ivExpand.setImageResource(R.drawable.down_arrow);
        			itemsListExpanded.set(i,false);
        		}else{
                	LinearLayout loView = (LinearLayout)itemsList.get(i);
            		View vItem = getLayoutInflater().inflate(R.layout.reminder_item_expandable, loView, false);
                	vItem.setId(620+i);
                	loView.addView(vItem);
                	ImageView ivExpand = (ImageView)findViewById(820+i);
                	ivExpand.setImageResource(R.drawable.up_arrow);
                	TextView tvDate = (TextView)findViewById(R.id.text_date);
                	tvDate.setId(1120+i);
                	tvDate.setText(getString(R.string.date_added)+
                			FormatDate(MainActivity.reminderList.get(i).time_when_created));
                	TextView tvTime = (TextView)findViewById(R.id.text_time);
                	tvTime.setId(1220+i);
                	tvTime.setText(getString(R.string.time_added)+
                			FormatTime(MainActivity.reminderList.get(i).time_when_created));
		        	RelativeLayout rlTime = (RelativeLayout)findViewById(R.id.remindingtime_layout);
		        	rlTime.setId(1420+i);
		        	RelativeLayout rlLocation = (RelativeLayout)findViewById(R.id.event_location_layout);
		        	rlLocation.setId(1520+i);
		        	RelativeLayout rlRepeat = (RelativeLayout)findViewById(R.id.repeat_layout);
		        	rlRepeat.setId(1920+i);
		        	TextView tvRemindingtime = (TextView)findViewById(R.id.text_remindingtime);
		        	tvRemindingtime.setId(1620+i);
		        	TextView tvLocation = (TextView)findViewById(R.id.text_location);
		        	tvLocation.setId(1720+i);
		        	TextView tvRepeat = (TextView)findViewById(R.id.text_repeat);
		        	tvRepeat.setId(2020+i);
		        	LinearLayout llMainSection = (LinearLayout)findViewById(R.id.main_section_layout);
		        	llMainSection .setId(1820+i);
    	        	if(MainActivity.reminderList.get(i).type != 4)
    	        	{
    	        		llMainSection.removeView(rlTime);
    	        		llMainSection.removeView(rlLocation);
    	        		llMainSection.removeView(rlRepeat);
    	        	}else{
    	        		if(MainActivity.reminderList.get(i).location.isEmpty()){
    	        			llMainSection.removeView(rlLocation);
    	        		}else{
    	        			tvLocation.setText(getString(R.string.event_location)+MainActivity.reminderList.get(i).location);
    	        		}
    	        		if(MainActivity.reminderList.get(i).repeat>0){
    	        			String[] s = new String[] { getString(R.string.repeat_once),
    	    						getString(R.string.repeat_dayly), getString(R.string.repeat_weekly),
    	    						getString(R.string.repeat_monthly), getString(R.string.repeat_yearly)};
    	        			tvRepeat.setText(getString(R.string.repeat)+s[MainActivity.reminderList.get(i).repeat]);
    	        		}else{
        	        		llMainSection.removeView(rlRepeat);
    	        		}
    	        		tvRemindingtime.setText(getString(R.string.time_to_remind)+
    	        				FormatDate(MainActivity.reminderList.get(i).time_to_remind)+
    	        				FormatTime(MainActivity.reminderList.get(i).time_to_remind));
    	        	}
                	TextView tvContent = (TextView)findViewById(R.id.text_content);
                	tvContent.setId(1320+i);
					
					if(MainActivity.reminderList.get(i).type != 4)
    	        	{
					tvContent.setText(getString(R.string.content)+ GetDescriptionHead(MainActivity.reminderList.get(i).type) +
                			MainActivity.reminderList.get(i).note+ GetDescriptionEnd(MainActivity.reminderList.get(i).type));
    	        	}else{
    	        		if(MainActivity.reminderList.get(i).note.isEmpty()){
    	        			if(MainActivity.reminderList.get(i).repeat == -1){
    	        				tvContent.setText(getString(R.string.content)+ GetDescriptionHead(1) + FormatTime(MainActivity.reminderList.get(i).time_to_remind));
    	        			}else{
    	        				tvContent.setText(getString(R.string.content)+ GetDescriptionHead(2) + FormatDate(MainActivity.reminderList.get(i).time_to_remind)
    	        						+ FormatTime(MainActivity.reminderList.get(i).time_to_remind));
    	        			}
    	        		}else{
    	        			tvContent.setText(getString(R.string.content)+ MainActivity.reminderList.get(i).note);
    	        		}
    	        	}
                	ImageView ivStar1 = (ImageView)findViewById(R.id.reminderlist_level_star1);
                	ivStar1.setId(generateViewId());
                	ImageView ivStar2 = (ImageView)findViewById(R.id.reminderlist_level_star2);
                	ivStar2.setId(generateViewId());
                	ImageView ivStar3 = (ImageView)findViewById(R.id.reminderlist_level_star3);
                	ivStar3.setId(generateViewId());
                	ImageView ivStar4 = (ImageView)findViewById(R.id.reminderlist_level_star4);
                	ivStar4.setId(generateViewId());
                	ImageView ivStar5 = (ImageView)findViewById(R.id.reminderlist_level_star5);
                	ivStar5.setId(generateViewId());
                	if(MainActivity.reminderList.get(i).level<2)
                		ivStar2.setVisibility(View.INVISIBLE);
                	if(MainActivity.reminderList.get(i).level<3)
                		ivStar3.setVisibility(View.INVISIBLE);
                	if(MainActivity.reminderList.get(i).level<4)
                		ivStar4.setVisibility(View.INVISIBLE);
                	if(MainActivity.reminderList.get(i).level<5)
                		ivStar5.setVisibility(View.INVISIBLE);
                	itemsListExpanded.set(i,true);
        		}
        	}
        }
	}
	
	 public boolean onTouch(View v, MotionEvent motionEvent) {
 		RelativeLayout rView;
		 for(int i=0; i<MainActivity.reminderList.size(); i++)
	        {
	        	if(v.getId() == 520+i)
	        	{
	        		switch (motionEvent.getAction())
	    			{
	    			case MotionEvent.ACTION_DOWN:
	    				v.setBackgroundColor(android.graphics.Color.rgb(32, 170, 170));
	    				break;
	    			case MotionEvent.ACTION_UP:
	    				v.setBackgroundColor(android.graphics.Color.rgb(228, 242, 254));
	    				break;
	    			}
	        		System.out.println(motionEvent.getAction());
	    			return false;
	        	}
	        }
		 switch(v.getId()){
		 case R.id.reminderList_scroll:
			 if(motionEvent.getAction() == MotionEvent.ACTION_UP){
				 for(int i=0; i<MainActivity.reminderList.size(); i++)
			        {
					 rView = (RelativeLayout)findViewById(520+i);
					 rView.setBackgroundColor(android.graphics.Color.rgb(228, 242, 254));
			        }
			 }
		 
		 }
		 return false;
	}
	 private String FormatRemindingTime(long t){
		String s="";
		long diffSeconds = t / 1000 % 60;
		long diffMinutes = t / (60 * 1000) % 60;
		long diffHours = t / (60 * 60 * 1000) % 24;
		long diffDays = t / (24 * 60 * 60 * 1000);
		 if(diffDays!=0){
			 s=s+diffDays+getString(R.string.day);
		 }
		 if(diffHours!=0){
			 s=s+diffHours+getString(R.string.hour);
		 }
		 if(diffMinutes!=0){
			 s=s+diffMinutes+getString(R.string.minute);
		 }
		 if(diffSeconds!=0){
			 s=s+diffSeconds+getString(R.string.second);
		 }
		 return s;
	 } 
	 
	 private String FormatDate(Calendar t){
		 String s="";
			 s=s+t.get(Calendar.YEAR)+getString(R.string.year);
			 s=s+(t.get(Calendar.MONTH)+1)+getString(R.string.month);
			 s=s+t.get(Calendar.DAY_OF_MONTH)+getString(R.string.day);
		 return s;
	 } 
	 private String FormatTime(Calendar t){
		 String s="";
			 s=s+t.get(Calendar.HOUR_OF_DAY)+getString(R.string.hour);
			 s=s+t.get(Calendar.MINUTE)+getString(R.string.minute);
			 s=s+t.get(Calendar.SECOND)+getString(R.string.second);
		 return s;
	 } 
	 
	 public static CharSequence GetDescriptionHead(int Index){
		 switch(Index){
		 case 1:
			 return "����ʱ";
		 case 2:
			 return "��ʱ��";
		 case 3:
			 return "ÿ��Сʱ��";
		 }
		 return "";
	 }
	 
	 private CharSequence GetDescriptionEnd(int Index){
		 switch(Index){
		 case 1:
			 return "";
		 case 2:
			 return "";
		 case 3:
			 return "��";
		 }
		 return "";
	 }
	
	 @Override
	 public void onResume() {
	     super.onResume();
	     RecreateMain();
	 }

	 @Override
	 public void onPause() {
	     super.onPause();
	     isInFront = false;
	 }
	 
	 private void RecreateMain(){
	        //520+ reminder_item_layout
	        //620+ reminder_item_expandable
	        //720+ countdown_text
	        //820+ ri_expand
	        //920+ reminder_type_icon
	        //1020+ reminder_type
	        //1120+ text_date
	        //1220+ text_time
	        //1320+ text_content
		 	//1420+ remindingtime_layout
		 	//1520+ event_location_layout
		 	//1620+ text_time_to_remind
		 	//1720+ text_location
		 	//1820+ main_section_layout
		 	//1920+ repeat_layout
		 	//2020+ text_repeat
	        LinearLayout lView = (LinearLayout)findViewById(R.id.reminderList_layout);
	        TextView tvPrompt = (TextView)findViewById(R.id.reminderslist_prompt);
	        if(MainActivity.reminderList.size() == 0)
	        {
	        	tvPrompt.setText(getString(R.string.reminderslist_empty_prompt));
	        }
	        lView.removeAllViews();
	        lView.addView(tvPrompt);
	        itemsListExpanded.clear();
	        itemsList.clear();
	        for(int i=0; i<MainActivity.reminderList.size(); i++)
	        {
	        	itemsList.add(getLayoutInflater().inflate(R.layout.reminder_item, lView, false));
	        	itemsListExpanded.add(false);
	        	itemsList.get(i).setId(420+i);
	            lView.addView(itemsList.get(i));
	        	RelativeLayout rView = (RelativeLayout)findViewById(R.id.reminder_item_layout);
	        	rView.setId(520+i);
	        	rView.setOnTouchListener(this);
	        	rView.setOnClickListener(this);
	        	TextView tvCountDown = (TextView)findViewById(R.id.countdown_text);
	        	tvCountDown.setId(720+i);
	        	ImageView ivExpand = (ImageView)findViewById(R.id.ri_expand);
	        	ivExpand.setId(820+i);
	        	ImageView ivType = (ImageView)findViewById(R.id.reminder_type_icon);
	        	ivType.setId(920+i);
	        	TextView tvType = (TextView)findViewById(R.id.reminder_type);
	        	tvType.setId(1020+i);
	        	switch(MainActivity.reminderList.get(i).type){
	        	case 1:
	            	ivType.setImageResource(R.drawable.timer1_icon);
	            	tvType.setText(getString(R.string.title_timer1));
	            	break;
	        	case 2:
	            	ivType.setImageResource(R.drawable.timer2_icon);
	            	tvType.setText(getString(R.string.title_timer2));
	            	break;
	        	case 3:
	            	ivType.setImageResource(R.drawable.timer3_icon);
	            	tvType.setText(getString(R.string.title_timer3));
	            	break;
	        	case 4:
	            	ivType.setImageResource(R.drawable.timer4_icon);
	            	tvType.setText(MainActivity.reminderList.get(i).title);
	            	break;
	        	}
	        }
	        ScrollView sView = (ScrollView)findViewById(R.id.reminderList_scroll);
	        sView.setOnTouchListener(this);
	        isInFront=true;
	        mHandler.sendEmptyMessage(UPDATE_REMINDING_TIME);
	 }
	 
	 private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	 /**
	  * Generate a value suitable for use in {@link #setId(int)}.
	  * This value will not collide with ID values generated at build time by aapt for R.id.
	  *
	  * @return a generated ID value
	  */
	 public static int generateViewId() {
	     for (;;) {
	         final int result = sNextGeneratedId.get();
	         // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
	         int newValue = result + 1;
	         if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
	         if (sNextGeneratedId.compareAndSet(result, newValue)) {
	             return result;
	         }
	     }
	 }
}