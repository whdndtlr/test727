package board.model;

import java.util.Collections;
import java.util.List;

public class PageBoard {

	private List<BoardVO> list;
	private int requestPage;
	private int totalCount;
	private int totalPage;
	private int firstRow;	
	private int endRow;
	private int beginPage;
	private int endPage;
	
	public PageBoard() {
		this(Collections.<BoardVO> emptyList(), 0, 0, 0, 0, 0, 0, 0);
	}

	public PageBoard(List<BoardVO> list, int requestPage, int totalCount, int totalPage, int startRow, int endRow,int beginPage,int endPage) {
		this.list = list;
		this.requestPage = requestPage;
		this.totalCount = totalCount;
		this.totalPage = totalPage;
		this.firstRow = startRow;
		this.endRow = endRow;
		this.beginPage = beginPage;
		this.endPage = endPage;
	}

	public List<BoardVO> getList() {
		return list;
	}

	public void setList(List<BoardVO> list) {
		this.list = list;
	}

	public int getRequestPage() {
		return requestPage;
	}

	public void setRequestPage(int requestPage) {
		this.requestPage = requestPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getBeginPage() {
		return beginPage;
	}

	public void setBeginPage(int beginPage) {
		this.beginPage = beginPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}
	public boolean isHasBoard() {
		return !list.isEmpty();
	}

	
}

