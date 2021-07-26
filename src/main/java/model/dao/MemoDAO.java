package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import board.model.BoardVO;
import memo.model.MemoVO;

public class MemoDAO {

	Connection conn;
	
	public MemoDAO() {
		//데이터베이스 접속
		try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		conn=DriverManager.getConnection(
		"jdbc:oracle:thin:@localhost:1521:xe","testuser","1111");

		if(conn==null) {System.out.println("DB접속에 실패");}
		System.out.println("DB접속 성공");
		
		}catch(Exception e) { 
			e.printStackTrace();
		}
	}
	
	public int insert(MemoVO memo) {
		PreparedStatement pstmt=null;
		int result=0;
		String sql=null;
		try {
			sql="insert into memo values((select max(id)+1 from memo), ?, ?, sysdate)";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, memo.getTitle());
			pstmt.setString(1, memo.getContent());
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
	
	public List select() {
		List<MemoVO> list=new ArrayList<MemoVO>();
		String sql="select * from memo";
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try{
		pstmt=conn.prepareStatement(sql);
		rs=pstmt.executeQuery();
		while(rs.next()) {
			MemoVO memo=new MemoVO();
			memo.setId(rs.getInt("id"));
			memo.setTitle(rs.getString("title"));
			memo.setContent(rs.getString("content"));
			memo.setWdate(rs.getDate("wdate"));
			list.add(memo);
		}
		}catch(Exception e) {}
		
		return list;
	}
	
	public int update(String title, String content) {
		
		return 0;
	}
	
	public int delete() {
		return 0;
	}
}
