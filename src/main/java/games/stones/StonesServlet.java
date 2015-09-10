package games.stones;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.ScoreDAO;
import entity.User;

@WebServlet("/stones")
public class StonesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Inject
	private User user;
	@Inject
	private ScoreDAO scoreDAO;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		Field fieldStone;
		boolean solved = false;

		startNewGame(request);

		if (request.getParameter("field") != null || request.getSession().getAttribute("fieldStone") == null) {
			fieldStone = new Field(8, 8);
		} else {
			fieldStone = (Field) request.getSession().getAttribute("fieldStone");
		}

		request.getSession().setAttribute("fieldStone", fieldStone);

		move(request, fieldStone);

		solved = checkIfSolved(fieldStone);

		generateStones(out, fieldStone);

		if (solved) {
			out.println("<h1 align=\"center\">Vyhral si</h1><br>");
			long time = fieldStone.getTime();
			out.printf("<h1 align=\"center\">Your time is: %ss</h1><br>", time);
			String playerName = user.getName();
			String gameName = "stones";
			if (playerName != null) {
				scoreDAO.saveScore(gameName, playerName, time);
			}
		}

	}

	private void generateStones(PrintWriter out, Field fieldStone) {
		out.println("<div align=\"center\">");

		for (int row = 0; row < fieldStone.getRowCount(); row++) {
			for (int column = 0; column < fieldStone.getColumnCount(); column++) {

				if (fieldStone.getTile(row, column) instanceof Stone) {
					int value = ((Stone) fieldStone.getTile(row, column)).getValue();
					out.printf(
							"<a style=\"text-align:center;box-align: center;text-decoration:none;background-image: url(resources/gfx/stone.png);display:inline-block;width: 40px;height: 40px;\" href=\"?row=%s&column=%s\" ><font color=\"white\"><b>%s</b></font></a>",
							row, column, value);
				}
				if (fieldStone.getTile(row, column) instanceof Clue) {
					out.printf(
							"<a style=\" text-decoration:none;display:inline-block;width: 40px;height: 40px;\" href=\"stones.jsf\" >&nbsp</a>");
				}
			}
			out.printf("<br>");
		}
		out.println("</div>");
	}

	private boolean checkIfSolved(Field fieldStone) {
		boolean solved = false;
		if (fieldStone.isSolved(fieldStone)) {
			solved = true;
		}
		return solved;
	}

	private void move(HttpServletRequest request, Field fieldStone) {

		try {
			int r = Integer.parseInt(request.getParameter("row"));
			int c = Integer.parseInt(request.getParameter("column"));

			if (r != 0 && fieldStone.getTile(r - 1, c) instanceof Clue) {
				fieldStone.moveDown(r, c);
			} else if (r != fieldStone.getRowCount() - 1 && fieldStone.getTile(r + 1, c) instanceof Clue) {
				fieldStone.moveUp(r, c);
			} else if (c != 0 && fieldStone.getTile(r, c - 1) instanceof Clue) {
				fieldStone.moveRight(r, c);
			} else if (c != fieldStone.getColumnCount() - 1 && fieldStone.getTile(r, c + 1) instanceof Clue) {
				fieldStone.moveLeft(r, c);

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private void startNewGame(HttpServletRequest request) {
		try {
			if (Integer.parseInt(request.getParameter("fieldStone")) == 1) {
				request.getSession().setAttribute("fieldStone", null);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
