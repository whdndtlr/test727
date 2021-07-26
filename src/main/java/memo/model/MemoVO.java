package memo.model;

import java.sql.Date;

public class MemoVO {

	int id;
	String title;
	String content;
	Date wdate;
	
	public MemoVO() {
		
	}

	public MemoVO(int id, String title, String content, Date wdate) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.wdate = wdate;
	}

	
	
	public MemoVO(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getWdate() {
		return wdate;
	}

	public void setWdate(Date wdate) {
		this.wdate = wdate;
	}

	@Override
	public String toString() {
		return "MemoVO [id=" + id + ", title=" + title + ", content=" + content + ", wdate=" + wdate + "]";
	}
	
	
}
