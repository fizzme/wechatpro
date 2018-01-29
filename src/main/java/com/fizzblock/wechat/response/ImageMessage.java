package com.fizzblock.wechat.response;


/**
 * 图片消息回复
 * @author glen
 *
 */
public class ImageMessage extends BaseMessage{

	private Image Image;

	public Image getImage() {
		return Image;
	}

	public void setImage(Image image) {
		Image = image;
	}
	
	
}
