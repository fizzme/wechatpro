package com.fizzblock.wechat.response;

import java.util.List;

import com.fizzblock.wechat.response.BaseMessage;

public class NewsMessage extends BaseMessage {

	// 图文消息
	private int ArticleCount;
	private List<Article> Articles;//属性名称会变成xml的标签，所以注意大小写
	
	
	public int getArticleCount() {
		return ArticleCount;
	}
	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}
	public List<Article> getArticles() {
		return Articles;
	}
	public void setArticles(List<Article> articles) {
		Articles = articles;
	}

	
}
