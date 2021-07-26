package board.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import board.model.BoardVO;
import board.model.PageBoard;

public class BoardDAO {
	Connection conn;
	public BoardDAO() {
		//데이터베이스 접속
	try {
	Class.forName("oracle.jdbc.driver.OracleDriver");
	conn=DriverManager.getConnection(
	"jdbc:oracle:thin:@localhost:1521:xe","testuser","1111");

	if(conn==null) {System.out.println("DB접속에 실패");}
	System.out.println("DB접속 성공");
	
	}catch(Exception e) { }
	
}

public int insert(BoardVO board) {
	PreparedStatement pstmt=null;
	int result=0;
	String sql=null;
	try {
		sql="insert into board values(";
		sql+="board_idx_seq.nextval,";
		sql+="?,?,0,"; //title, content
		sql+="board_groupid_seq.nextval,0,0,";
		sql+="0,";
		sql+="?,?,sysdate";//id, name
		sql+=")";
		pstmt=conn.prepareStatement(sql);
		pstmt.setString(1, board.getTitle());
		pstmt.setString(2, board.getContent());
		pstmt.setString(3, board.getWriteId()); //id
		pstmt.setString(4, board.getWriteName()); //name
		
		result=pstmt.executeUpdate();
		
		if(result>0) {
			System.out.println("sql 글 입력 성공");
		}else {
			System.out.println("sql 글 입력 실패");
		}
		pstmt.close();
		//conn.close();
		
	}catch(Exception e) { }
	return result;
}

public PageBoard list(int requestPage) {
	PageBoard pageboard=null; //페이지 정보와 리스트를 담는 객체
	
	//페이지 관련 정보사항
	                  	//요청 페이지 인자전달(requestPage)
	int articleCount=0;//전체 글의수
	int countPerPage=10; //한페이지 몇개의 글을 표시할지 여부(갯수)
	int totalPage=0;  //전체 페이지 =전체글의수/한페이지의 수
	int beginPage=0;  //시작 페이지-요청페이지를 기준으로 표시될 페이지의 시작번호
	int endPage=0;    //마지막 페이지-요청페이지를 기준으로 표시될 페이지의 마지막번호
	int firstRow=0;   //시작 글번호 -요청페이지를 기준으로 처음 표시될 글 번호
	int endRow=0;     //끝 글번호 -요청페이지를 기준으로 처음 표시될 마지막 글 번호
	List<BoardVO> list=new ArrayList<BoardVO>(); //페이지에 대한 10개 글 리스트
	
	String sql=null;
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	try {
	//페이지에 대한 정보
	//1.전체 게시물 수 구하기(articleCount)
	sql="select count(*) from board";
	pstmt=conn.prepareStatement(sql);
	rs=pstmt.executeQuery();
	if(rs.next()) {
		articleCount=rs.getInt(1); 
	}else {
		articleCount=0;
	}
	//System.out.println("게시글수:"+articleCount);
	
	//2.전체페이지 수(totalPage)
	totalPage=articleCount/countPerPage; //소수제외
	if(articleCount%countPerPage>0) { //만약 35/10=3.5=4page 
		totalPage++;
	}
	//System.out.println("전체페이지:"+totalPage);
	
	//3.요청한 페이지에 대한 시작 글번호, 끝 글번호(firstRow, endRow)
	firstRow=(requestPage-1)*countPerPage+1;
	endRow=firstRow+countPerPage-1;
	//System.out.printf("firstRow:%d, endRow:%d\n",firstRow,endRow);
	
	//추가사항
	//endrow번호가 articleCount보다 큰경우 endrow는 atricleCount번호로 변경
	
	//4.시작페이지번호, 끝페이지 번호(beginPage, endPage)
	//5페이지를 기준으로 끊는다고 가정
	//예 3page요청 beginPage=요청한페이지(3)/5=0+1 endpage=beginpage+4
	//예 7page요청 beginPage=요청한페이지(7)/5=1=(1*5)+1 endpage=beginpage+4=10
	//예 14page요청 beginPage=요청한페이지(14)/5=2=(2*5)+1 endpage=beginpage+4=15
	
	beginPage=((requestPage-1)/5)*5+1;
	endPage=beginPage+4;
	
 	if(beginPage<1) {beginPage=1;}
	if(endPage>totalPage) {endPage=totalPage;}
	
	//System.out.printf("beginPage:%d, endPage:%d\n",beginPage,endPage);
	
	//5.페이지에 해당하는 리스트(firstRow, endRow)
	sql="select idx, title, content, readcount, groupid, depth, re_order, isdel, write_id, write_name, write_day from (" + 
			"select rownum rnum, idx, title, content, readcount, groupid, depth, re_order, isdel, write_id, write_name, write_day from (" + 
			"select * from board a order by a.groupid desc, a.re_order asc) where rownum <= ?" + 
			") where rnum >= ?"; 
	pstmt=conn.prepareStatement(sql);
	pstmt.setInt(1, endRow); //10큰수
	pstmt.setInt(2, firstRow);//1작은수
	rs=pstmt.executeQuery();
	//6.DB의 리스트를 board객체에 담아 전송
	while(rs.next()) {
		//System.out.println("check");
		BoardVO board=new BoardVO();
		board.setIdx(rs.getInt("idx"));
		board.setTitle(rs.getString("title"));
		board.setContent(rs.getString("content"));
		board.setReadcount(rs.getInt("readcount"));
		board.setGroupid(rs.getInt("groupid"));
		board.setDepth(rs.getInt("depth"));
		board.setReOrder(rs.getInt("re_order"));
		board.setIsdel(rs.getInt("isdel"));
		board.setWriteId(rs.getString("write_id"));
		board.setWriteName(rs.getString("write_name"));
		board.setWriteDay(rs.getDate("write_day"));
		list.add(board);
	}
	pageboard=new PageBoard(list, requestPage, 
			articleCount, totalPage, 
			firstRow, endRow, beginPage, endPage);
	//System.out.println(pageboard.getList().toString());
	rs.close();
	pstmt.close();
	conn.close();
	
	}catch(Exception e){
		
	}
	return pageboard;
	
	
}

public List select() {
	List<BoardVO> list=new ArrayList<BoardVO>();
	String sql="select * from board";
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	try{
	pstmt=conn.prepareStatement(sql);
	rs=pstmt.executeQuery();
	while(rs.next()) {
		BoardVO board=new BoardVO();
		board.setIdx(rs.getInt("idx"));
		board.setTitle(rs.getString("title"));
		board.setContent(rs.getString("content"));
		board.setReadcount(rs.getInt("readcount"));
		board.setGroupid(rs.getInt("groupid"));
		board.setDepth(rs.getInt("depth"));
		board.setReOrder(rs.getInt("re_order"));
		board.setIsdel(rs.getInt("isdel"));
		board.setWriteId(rs.getString("write_id"));
		board.setWriteName(rs.getString("write_name"));
		board.setWriteDay(rs.getDate("write_day"));
		list.add(board);
	}
	}catch(Exception e) {}
	
	return list;
	
}
public BoardVO select(int idx) {
	BoardVO board=null;
	String sql="select * from board where idx=?";
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	try{
	pstmt=conn.prepareStatement(sql);
	pstmt.setInt(1, idx);
	rs=pstmt.executeQuery();
	if(rs.next()) {
		board=new BoardVO();
		board.setIdx(rs.getInt("idx"));
		board.setTitle(rs.getString("title"));
		board.setContent(rs.getString("content"));
		board.setReadcount(rs.getInt("readcount"));
		board.setGroupid(rs.getInt("groupid"));
		board.setDepth(rs.getInt("depth"));
		board.setReOrder(rs.getInt("re_order"));
		board.setIsdel(rs.getInt("isdel"));
		board.setWriteId(rs.getString("write_id"));
		board.setWriteName(rs.getString("write_name"));
		board.setWriteDay(rs.getDate("write_day"));
	}
	rs.close();
	pstmt.close();
	conn.close();
	}catch(Exception e) {}
	return board;
}
public int delete(int idx) {
	int result=0;
	String sql="delete from board where idx=?";
	PreparedStatement pstmt=null;
	try{
	pstmt=conn.prepareStatement(sql);
	pstmt.setInt(1, idx);
	result=pstmt.executeUpdate();
	}catch(Exception e) { }
	return result;
}

public int update(int idx, String title, String content) {
int result=0;
String sql="update board set title=?,content=? where idx=?";
PreparedStatement pstmt=null;
try{
pstmt=conn.prepareStatement(sql);
pstmt.setString(1, title);
pstmt.setString(2, content);
pstmt.setInt(3, idx);
result=pstmt.executeUpdate();
}catch(Exception e) { }
return result;
}

public int replyInsert(BoardVO board) {
	//1번째 부모글이 있는지 여부 확인
	if(!parentCheck(board.getIdx())) {
		System.out.println("부모글확인실패!");
		return 0;	
	}
	//2번째 같은 그룹 다른 댓글에 대해 depth를 1증가
	reply_before_update(board.getGroupid(), board.getReOrder()-1);//-1한 값 확인
	//3번째 리플입력처리
	PreparedStatement pstmt=null;
	int result=0;
	String sql=null;
	try {
		sql="insert into board values(";
		sql+="board_idx_seq.nextval,";
		sql+="?,?,0,"; //title, content,조회수
		sql+="?,?,?,";//groupid,depth,reorder
		sql+="0,";
		sql+="?,?,sysdate";//id, name
		sql+=")";
		pstmt=conn.prepareStatement(sql);
		pstmt.setString(1, board.getTitle());
		pstmt.setString(2, board.getContent());
		pstmt.setInt(3, board.getGroupid());
		pstmt.setInt(4, board.getDepth());
		pstmt.setInt(5, board.getReOrder());
		pstmt.setString(6, board.getWriteId()); //id
		pstmt.setString(7, board.getWriteName()); //name
		
		result=pstmt.executeUpdate();
		
		if(result>0) {
			System.out.println("sql 댓글 입력 성공");
		}else {
			System.out.println("sql 댓글 입력 실패");
		}
		pstmt.close();
		//conn.close();
		
	}catch(Exception e) {e.printStackTrace();}
	return result;
}

//부모글이 있는지 여부 확인 함수
public boolean parentCheck(int idx) {
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	String sql="select * from board where idx=?";
	try {
	pstmt=conn.prepareStatement(sql);
	pstmt.setInt(1, idx);
	rs=pstmt.executeQuery();
	if(rs.next()) {
		rs.close();
		pstmt.close();
		return true;
	}else {
		rs.close();
		pstmt.close();
		return false;
	}
	}catch(Exception e) {}
	return false;
}
//리플의 depth를 증가하는 함수
public void reply_before_update(int groupid,int reOrder) {
	PreparedStatement pstmt=null;
	String sql="update board set re_order=re_order+1 where groupid=? and re_order>?";
	try {
	pstmt=conn.prepareStatement(sql);
	pstmt.setInt(1, groupid);
	pstmt.setInt(2, reOrder);
	int result=pstmt.executeUpdate();
	if(result<1) {
		System.out.println("reply전 업데이트할 사항이 없음");
	}else {
		System.out.println("reply전 업데이트 했음");
	}
	pstmt.close();
	}catch(Exception e) {e.printStackTrace();}
}

public int readcountUpdate(int idx) {
	int result=0;
	String sql="update board set readcount=readcount+1 where idx=?";
	PreparedStatement pstmt=null;
	try{
	pstmt=conn.prepareStatement(sql);
	pstmt.setInt(1, idx);
	result=pstmt.executeUpdate();
	}catch(Exception e) { }
	return result;
	
}

public PageBoard searchList(String field, String search, int requestPage) {
PageBoard pageboard=null; //페이지 정보와 리스트를 담는 객체
	
	//페이지 관련 정보사항
	                  	//요청 페이지 인자전달(requestPage)
	int articleCount=0;//전체 글의수
	int countPerPage=10; //한페이지 몇개의 글을 표시할지 여부(갯수)
	int totalPage=0;  //전체 페이지 =전체글의수/한페이지의 수
	int beginPage=0;  //시작 페이지-요청페이지를 기준으로 표시될 페이지의 시작번호
	int endPage=0;    //마지막 페이지-요청페이지를 기준으로 표시될 페이지의 마지막번호
	int firstRow=0;   //시작 글번호 -요청페이지를 기준으로 처음 표시될 글 번호
	int endRow=0;     //끝 글번호 -요청페이지를 기준으로 처음 표시될 마지막 글 번호
	List<BoardVO> list=new ArrayList<BoardVO>(); //페이지에 대한 10개 글 리스트
	
	String sql=null;
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	try {
	//페이지에 대한 정보
	//1.전체 게시물 수 구하기(articleCount)
	sql="select count(*) from board where "+field+" like '%"+search+"%'";
	pstmt=conn.prepareStatement(sql);
	rs=pstmt.executeQuery();
	if(rs.next()) {
		articleCount=rs.getInt(1); 
	}else {
		articleCount=0;
	}
	//System.out.println("검색 게시글수:"+articleCount);
	
	//2.전체페이지 수(totalPage)
	totalPage=articleCount/countPerPage; //소수제외
	if(articleCount%countPerPage>0) { //만약 35/10=3.5=4page 
		totalPage++;
	}
	//System.out.println("전체페이지:"+totalPage);
	
	//3.요청한 페이지에 대한 시작 글번호, 끝 글번호(firstRow, endRow)
	firstRow=(requestPage-1)*countPerPage+1;
	endRow=firstRow+countPerPage-1;
	//System.out.printf("firstRow:%d, endRow:%d\n",firstRow,endRow);
	
	//추가사항
	//endrow번호가 articleCount보다 큰경우 endrow는 atricleCount번호로 변경
	
	//4.시작페이지번호, 끝페이지 번호(beginPage, endPage)
	//5페이지를 기준으로 끊는다고 가정
	//예 3page요청 beginPage=요청한페이지(3)/5=0+1 endpage=beginpage+4
	//예 7page요청 beginPage=요청한페이지(7)/5=1=(1*5)+1 endpage=beginpage+4=10
	//예 14page요청 beginPage=요청한페이지(14)/5=2=(2*5)+1 endpage=beginpage+4=15
	
	beginPage=(requestPage/5)*5+1;
	endPage=beginPage+4;
	
 	if(beginPage<1) {beginPage=1;}
	if(endPage>totalPage) {endPage=totalPage;}
	
	//System.out.printf("beginPage:%d, endPage:%d\n",beginPage,endPage);
	
	//5.페이지에 해당하는 리스트(firstRow, endRow)
	sql="select idx, title, content, readcount, groupid, depth, re_order, isdel, write_id, write_name, write_day from (" + 
			"select rownum rnum, idx, title, content, readcount, groupid, depth, re_order, isdel, write_id, write_name, write_day from (" + 
			"select * from board a  where "+field+" like '%"+search+"%' order by a.groupid desc, a.re_order asc) where rownum <= ?" + 
			") where rnum >= ?"; 
	pstmt=conn.prepareStatement(sql);
	pstmt.setInt(1, endRow); //10큰수
	pstmt.setInt(2, firstRow);//1작은수
	rs=pstmt.executeQuery();
	//6.DB의 리스트를 board객체에 담아 전송
	while(rs.next()) {
		//System.out.println("check");
		BoardVO board=new BoardVO();
		board.setIdx(rs.getInt("idx"));
		board.setTitle(rs.getString("title"));
		board.setContent(rs.getString("content"));
		board.setReadcount(rs.getInt("readcount"));
		board.setGroupid(rs.getInt("groupid"));
		board.setDepth(rs.getInt("depth"));
		board.setReOrder(rs.getInt("re_order"));
		board.setIsdel(rs.getInt("isdel"));
		board.setWriteId(rs.getString("write_id"));
		board.setWriteName(rs.getString("write_name"));
		board.setWriteDay(rs.getDate("write_day"));
		list.add(board);
	}
	pageboard=new PageBoard(list, requestPage, 
			articleCount, totalPage, 
			firstRow, endRow, beginPage, endPage);
	//System.out.println(pageboard.getList().toString());
	rs.close();
	pstmt.close();
	conn.close();
	
	}catch(Exception e){
		
	}
	return pageboard;
	
}
}