package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import board.dao.BoardDAO;
import board.model.BoardVO;
import board.model.PageBoard;


@WebServlet("/")
public class MainController extends HttpServlet{

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		System.out.println(request.getRequestURI());
		String[] uris=request.getRequestURI().substring(1).split("/");
		System.out.println(uris.length);
		System.out.println("uris[0]:"+uris[0]);
		String context="/home/";
		String page="main.jsp";
		if(uris[0].equals("") || uris[0].equals("index")) {
		context="/home/";
		page="main.jsp";
		}else {
			context="/"+uris[0]+"/";
			if(uris[0].equals("home")) {
				page="main.jsp";
			}else if(uris[0].equals("intro")) {
				page="intro.jsp";
			}else if(uris[0].equals("realchat")) {
				if(request.getSession().getAttribute("id")==null) {
					//로그인이 되지 않았을 때 서비스 불가
					context="/login/";
					page="login.jsp";
				}else {
					page="realchat.jsp";	
				}
			}else if(uris[0].equals("board")) {
				if(request.getSession().getAttribute("id")==null) {
					//로그인이 되지 않았을 때 서비스 불가
					context="/login/";
					page="login.jsp";
				}else {
					//로그인이 된 상태이므로 board처리
					if(uris.length==1) {
						//페이지 요청에 관한 정보가 있는지 확인
						//만약 없을 경우 기본페이지 1페이지 설정
						int requestPage=1;
						String _reqeustPage=request.getParameter("requestPage");
						if(_reqeustPage!=null && !_reqeustPage.equals("")) {
							requestPage=Integer.parseInt(_reqeustPage);
						}
						//해당페이지에 대한 리스트와 관련된 정보를 요청
						BoardDAO dao=new BoardDAO();
						PageBoard pageboard=dao.list(requestPage);
						request.setAttribute("pageboard", pageboard);
						request.setAttribute("requestPage", requestPage);
						page="list.jsp";
					}else {
						if(uris[1].equals("list")) {
							//페이지 요청에 관한 정보가 있는지 확인
							//만약 없을 경우 기본페이지 1페이지 설정
							int requestPage=1;
							String _reqeustPage=request.getParameter("requestPage");
							if(_reqeustPage!=null && !_reqeustPage.equals("")) {
								requestPage=Integer.parseInt(_reqeustPage);
							}
							//해당페이지에 대한 리스트와 관련된 정보를 요청
							BoardDAO dao=new BoardDAO();
							PageBoard pageboard=dao.list(requestPage);
							request.setAttribute("pageboard", pageboard);
							request.setAttribute("requestPage", requestPage);
							page="list.jsp";
						}else if(uris[1].equals("read")) {
							int idx=Integer.parseInt(request.getParameter("idx"));
							int requestPage=Integer.parseInt(request.getParameter("requestPage"));
								
								BoardDAO dao=new BoardDAO();
								//글조회수 1증가
								dao.readcountUpdate(idx);
								//선택한 글에 대해 자세히 읽기
								BoardVO board=dao.select(idx);
								//반드시 결과를 forward하기 전 request에 저장하는 것 잊지말것
								request.setAttribute("board", board);
								request.setAttribute("requestPage", requestPage);
								page="read.jsp";
						}else if(uris[1].equals("update")) {
							//업데이트 폼을 데이터 전달
							String requestPage=request.getParameter("requestPage");
							int idx=Integer.parseInt(request.getParameter("idx"));
							BoardDAO dao=new BoardDAO();
							BoardVO board=dao.select(idx);
							//반드시 결과를 forward하기 전 request에 저장하는 것 잊지말것
							request.setAttribute("board", board);
							request.setAttribute("requestPage", requestPage);
							page="update.jsp";
						}else if(uris[1].equals("update.do")) {
							//업데이트 폼에서 전달 받은 데이터 업데이트
							int idx=Integer.parseInt(request.getParameter("idx"));
							String title=request.getParameter("title");
							String content=request.getParameter("content");
							BoardDAO dao=new BoardDAO();
							int result=dao.update(idx,title,content);
							
							//해당페이지에 대한 리스트와 관련된 정보를 요청
							//dao=new BoardDAO();
							int requestPage=Integer.parseInt(request.getParameter("requestPage"));
							PageBoard pageboard=dao.list(requestPage);
							request.setAttribute("pageboard", pageboard);
							request.setAttribute("requestPage", requestPage);
							page="list.jsp";
						}else if(uris[1].equals("delete")) {
							//삭제
							
							int idx=Integer.parseInt(request.getParameter("idx"));
								BoardDAO dao=new BoardDAO();
								int result=dao.delete(idx);
								int requestPage=Integer.parseInt(request.getParameter("requestPage"));
								PageBoard pageboard=dao.list(requestPage);
								request.setAttribute("pageboard", pageboard);
								request.setAttribute("requestPage", requestPage);
								page="list.jsp";
						}else if(uris[1].equals("reply")) {
							//리플폼호출
							String requestPage=request.getParameter("requestPage");
							int idx=Integer.parseInt(request.getParameter("idx"));
							int groupid=Integer.parseInt(request.getParameter("groupid"));
							int depth=Integer.parseInt(request.getParameter("depth"));
							int reorder=Integer.parseInt(request.getParameter("reOrder"));
							String title=request.getParameter("title");
							String content=request.getParameter("content");
							//많은 데이터를 한곳에 저장
							BoardVO board=new BoardVO();
							board.setIdx(idx);
							board.setGroupid(groupid);
							board.setDepth(depth);
							board.setReOrder(reorder);
							board.setTitle(title);
							board.setContent(content);
							request.setAttribute("board", board);
							request.setAttribute("requestPage", requestPage);
							page="replyForm.jsp";
						}else if(uris[1].equals("reply.do")) {
							//리플달기
							int idx=Integer.parseInt(request.getParameter("parent_idx"));
							int groupid=Integer.parseInt(request.getParameter("groupid"));
							int depth=Integer.parseInt(request.getParameter("depth"));
							int reorder=Integer.parseInt(request.getParameter("reOrder"));
							String writeid=request.getParameter("writeId");
							String title=request.getParameter("title");
							String content=request.getParameter("content");
							BoardVO board=new BoardVO();
							board.setIdx(idx);
							board.setGroupid(groupid);
							board.setDepth(depth+1);
							board.setReOrder(reorder+1);
							board.setTitle(title);
							board.setContent(content);
							board.setWriteId(writeid);
							board.setWriteName(writeid);
							//db처리
							BoardDAO dao=new BoardDAO();
							if(dao.replyInsert(board)==1) {
								System.out.println("댓글성공");
							}else {
								System.out.println("댓글실패");
							}
							//페이지 요청에 관한 정보가 있는지 확인
							//만약 없을 경우 기본페이지 1페이지 설정
							int requestPage=1;
							String _reqeustPage=request.getParameter("requestPage");
							if(_reqeustPage!=null && !_reqeustPage.equals("")) {
								requestPage=Integer.parseInt(_reqeustPage);
							}
							//해당페이지에 대한 리스트와 관련된 정보를 요청
							//dao=new BoardDAO();
							PageBoard pageboard=dao.list(requestPage);
							request.setAttribute("pageboard", pageboard);
							request.setAttribute("requestPage", requestPage);
							page="list.jsp";
						}else if(uris[1].equals("searchList")) {
							String field=request.getParameter("field");
							String search=request.getParameter("search");
							//System.out.printf("field:%s,search:%s\n",field,search);
							//페이지 요청에 관한 정보가 있는지 확인
							//만약 없을 경우 기본페이지 1페이지 설정
							int requestPage=1;
							String _reqeustPage=request.getParameter("requestPage");
							if(_reqeustPage!=null && !_reqeustPage.equals("")) {
								requestPage=Integer.parseInt(_reqeustPage);
							}
							//해당페이지에 대한 리스트와 관련된 정보를 요청
							BoardDAO dao=new BoardDAO();
							PageBoard pageboard=dao.searchList(field,search,requestPage);
							request.setAttribute("pageboard", pageboard);
							request.setAttribute("requestPage", requestPage);
							page="list.jsp";						
						}else if(uris[1].equals("insert")) {
							page="writeForm.jsp";
						}else if(uris[1].equals("insert.do")) {
							String title=request.getParameter("title");
							String content=request.getParameter("content");
							String id=request.getParameter("writeName");
							BoardVO board=new BoardVO(title,content,id,id);
							BoardDAO dao=new BoardDAO();
							dao.insert(board);
							PageBoard pageboard=dao.list(1);
							request.setAttribute("pageboard", pageboard);
							request.setAttribute("requestPage", 1);
							page="list.jsp";
						}
					
					}
				}
			}else {
				context="/home/";
				page="section.jsp";
			}
		}
		
		request.setAttribute("section", context+page);
		request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
	}
	}