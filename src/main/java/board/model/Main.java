package board.model;

import board.dao.BoardDAO;

public class Main {

	public static void main(String[] args) {
		new BoardDAO().list(2);

	}

}
