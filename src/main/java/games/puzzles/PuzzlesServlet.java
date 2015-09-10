package games.puzzles;

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

@WebServlet("/puzzles")
public class PuzzlesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Inject
	private User user;
	@Inject
	private ScoreDAO scoreDAO;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		Field fieldPuzzle;
		boolean solved = false;

		startNewGame(request);

		if (request.getParameter("field") != null || request.getSession().getAttribute("fieldPuzzle") == null) {
			fieldPuzzle = new Field(5, 5);

		} else {
			fieldPuzzle = (Field) request.getSession().getAttribute("fieldPuzzle");
		}

		request.getSession().setAttribute("fieldPuzzle", fieldPuzzle);

		swap(request, fieldPuzzle);

		if (fieldPuzzle.isSolved(fieldPuzzle)) {
			solved = true;

		}

		generateField(request, out, fieldPuzzle);
		printIfSolved(out, fieldPuzzle, solved);

	}

	private void printIfSolved(PrintWriter out, Field fieldPuzzle, boolean solved) {
		if (solved) {
			long time = fieldPuzzle.getTime();
			out.println("<h1 align=\"center\">Vyhral si</h1><br>");
			out.printf("<h1 align=\"center\">Your time is: %ss</h1><br>", time);
			String playerName = user.getName();
			String gameName = "puzzle";
			if (playerName != null) {
				scoreDAO.saveScore(gameName, playerName, time);
			}
		}
	}

	private void generateField(HttpServletRequest request, PrintWriter out, Field fieldPuzzle) {
		out.println("<div style=\" display: inline; height: 30%;\" align=\"center\">");

		for (int row = 0; row < fieldPuzzle.getRowCount(); row++) {
			for (int column = 0; column < fieldPuzzle.getColumnCount(); column++) {

				int value = ((Puzzle) fieldPuzzle.getTile(row, column)).getValue();
				int r1 = -1;
				int c1 = -1;
				int r2 = -1;
				int c2 = -1;
				try {
					r1 = Integer.parseInt(request.getParameter("row1"));
					c1 = Integer.parseInt(request.getParameter("column1"));
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					r2 = Integer.parseInt(request.getParameter("row2"));
					c2 = Integer.parseInt(request.getParameter("column2"));
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (r1 == -1 && c1 == -1 || r1 != -1 && c1 != -1 && r2 != -1 && c2 != -1) {

					out.printf("<a href=\"?row1=%s&column1=%s\">", row, column);
					out.printf(
							"<IMG alt=\"aston%s.jpg\" src=\"resources/aston/aston%s.jpg\" style=\"height:60px;margin-bottom:0; margin-top:0;\" />",
							value, value);
					out.printf("</a>");
				} else {

					if (Integer.parseInt(request.getParameter("row1")) == row
							&& Integer.parseInt(request.getParameter("column1")) == column) {
						out.printf("<a href=\"?row1=%s&column1=%s&row2=%s&column2=%s\">", r1, c1, row, column);
						out.printf(
								"<IMG alt=\"aston%s.jpg\" src=\"resources/aston/aston%s.jpg\" style=\"border-color: red; height:60px;margin-bottom:0; margin-top:0;\" />",
								value, value);
						out.printf("</a>");
					} else {
						out.printf("<a href=\"?row1=%s&column1=%s&row2=%s&column2=%s\">", r1, c1, row, column);
						out.printf(
								"<IMG alt=\"aston%s.jpg\" src=\"resources/aston/aston%s.jpg\" style=\"height:60px;margin-bottom:0; margin-top:0;\" />",
								value, value);
						out.printf("</a>");
					}

				}
			}
			out.println("<br/>");
		}
		out.println("</div>");
	}

	private void swap(HttpServletRequest request, Field fieldPuzzle) {

		try {
			int r1 = Integer.parseInt(request.getParameter("row1"));
			int c1 = Integer.parseInt(request.getParameter("column1"));
			int r2 = Integer.parseInt(request.getParameter("row2"));
			int c2 = Integer.parseInt(request.getParameter("column2"));

			fieldPuzzle.swapPuzzles(r1, c1, r2, c2);

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
			if (Integer.parseInt(request.getParameter("fieldPuzzle")) == 1) {
				request.getSession().setAttribute("fieldPuzzle", null);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
