package com.slt.networkspeed.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.TextView;

import com.rey.material.widget.Switch;
import com.slt.networkspeed.R;



public class ClientSettingItemView extends RelativeLayout {
	private CharSequence titleCharSequence,desCharSequence;
	private TextView tv_title,tv_des;
	
	private ImageView rightIcon;

	private Switch settingSwitch;
	private View view;
	
	@SuppressLint("NewApi")
	public ClientSettingItemView(Context context, AttributeSet attrs,
								 int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initView(context,attrs);
	}

	public ClientSettingItemView(Context context, AttributeSet attrs,
								 int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context,attrs);
	}

	public ClientSettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context,attrs);
	}

	public ClientSettingItemView(Context context) {
		super(context);
		initView(context,null);
	}
	public void initView(Context context, AttributeSet attributeSet){
		View.inflate(this.getContext(), R.layout.client_seting_item_view, this);
		TypedArray typedArray = getResources().obtainAttributes(attributeSet, R.styleable.ClientSetingItem);
		titleCharSequence = typedArray.getText(R.styleable.ClientSetingItem_settingTitle);
		desCharSequence = typedArray.getText(R.styleable.ClientSetingItem_settingDes);
//		Drawable iconSrc = typedArray.getDrawable(R.styleable.ClientSetingItem_settingIcon);
		Drawable rightIconSrc = typedArray.getDrawable(R.styleable.ClientSetingItem_settingRightIcon);
		
		boolean isCheck = typedArray.getBoolean(R.styleable.ClientSetingItem_switch_Checked, false);
		boolean show_check = typedArray.getBoolean(R.styleable.ClientSetingItem_show_check, false);
		boolean show_view=typedArray.getBoolean(R.styleable.ClientSetingItem_show_view, false);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		rightIcon = (ImageView) findViewById(R.id.rightIcon);
		settingSwitch = (Switch) findViewById(R.id.settingswitch);
		view = findViewById(R.id.view);
		tv_title.setText(titleCharSequence);
		tv_des.setText(desCharSequence);
		rightIcon.setImageDrawable(rightIconSrc);
		settingSwitch.setChecked(isCheck);
		settingSwitch.setClickable(false);
		
		boolean isNullDes = (desCharSequence==null||TextUtils.isEmpty(desCharSequence.toString()));
		tv_des.setVisibility(isNullDes?View.GONE:View.VISIBLE);
		
		rightIcon.setVisibility(rightIconSrc==null?View.GONE:View.VISIBLE);
		settingSwitch.setVisibility(show_check?View.VISIBLE:View.GONE);
		view.setVisibility(show_view?View.VISIBLE:View.GONE);
		if(isNullDes){
			tv_title.setSingleLine(false);
			tv_title.setMaxLines(2);
			tv_title.setEllipsize(TruncateAt.END);
		}else{
			tv_title.setMaxLines(1);
			tv_title.setSingleLine(true);
			tv_title.setEllipsize(TruncateAt.MARQUEE);
			tv_title.setMarqueeRepeatLimit(-1);
		}
		
	}
	
	public Switch getSwitch() {
		return settingSwitch;
	}

	public TextView getTitle() {
		return tv_title;
	}

	public TextView getDescription() {
		return tv_des;
	}

	public ImageView getRightIcon() {
		return rightIcon;
	}
}
