package com.tjbaobao.ledhelper.entity.enums;

import com.tjbaobao.ledhelper.R;

public enum EditToolsEnum {
	Paint(0,R.drawable.anim_edit_tool_plant_on,R.drawable.anim_edit_tool_plant_off,"paint"),
	Eraser(1,R.drawable.anim_edit_tool_eraser_on,R.drawable.anim_edit_tool_eraser_off,"eraser"),
	Revoke(2,R.drawable.anim_edit_tool_revoke,R.drawable.anim_edit_tool_revoke,"revoke"),
	Forward(3,R.drawable.anim_edit_tool_forward,R.drawable.anim_edit_tool_forward,"forward");
	
	public int id ;
	public int imgOnId ,imgOffId;
	public String tag ;
	EditToolsEnum(int id, int imgOnId, int imgOffId, String tag) {
		this.id = id;
		this.imgOnId = imgOnId;
		this.imgOffId = imgOffId;
		this.tag = tag;
	}
	
}
