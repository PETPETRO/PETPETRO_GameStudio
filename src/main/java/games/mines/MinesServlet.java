package games.mines;

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
import games.mines.Tile.State;

@WebServlet("/mines")
public class MinesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Inject
	private User user;
	@Inject
	private ScoreDAO scoreDAO;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		Field field;

		startNewGame(request);

		if (request.getSession().getAttribute("field") == null) {
			field = new Field(5, 5, 1);
		} else {
			field = (Field) request.getSession().getAttribute("field");
		}

		request.getSession().setAttribute("field", field);

		if (request.getParameter("mark") != null) {
			markTile(request, field);
		} else {
			openTile(request, field);
		}
		out.println("<div align=\"center\">");
		out.println("<div class=\"row col-12\" style=\"display: flex;\" >");
		out.println("<div class=\" col-12\" style=\"text-align: center;\" >");
		out.printf("<a href=\"?mark=%d\"><img src=\"resources/gfx/markicon.png\"/ ></a>", 1);
		out.printf("<a href=\"mines.jsf\"><img src=\"resources/gfx/closeicon.png\"  / ></a>");
		out.println("</div >");
		out.println("</div >");

		if (field.getState().equals(GameState.FAILED) || field.getState().equals(GameState.SOLVED)) {
			openField(field);
		}

		for (int row = 0; row < field.getRowCount(); row++) {
			for (int column = 0; column < field.getColumnCount(); column++) {

				if (field.getTile(row, column).getState().equals(State.CLOSED)) {
					int m = 0;
					try {
						m = Integer.parseInt(request.getParameter("mark"));
					} catch (Exception e) {
						// TODO: handle exception
					}

					if (m == 1) {
						out.printf("<a href=\"?row=%s&column=%s&mark=%s\"><img src=\"resources/gfx/close.png\"></a>",
								row, column, m);
					} else {
						out.printf("<a href=\"?row=%s&column=%s\"><img src=\"resources/gfx/close.png\"  s></a>", row,
								column);
					}

				}

				if (field.getTile(row, column).getState().equals(State.OPEN)) {
					if (field.getTile(row, column) instanceof Mine) {
						out.printf("<a href=\"?row=%s&column=%s\"><img src=\"resources/gfx/mine.png\"></a>", row,
								column);
					}
					if (field.getTile(row, column) instanceof Clue) {
						int value = ((Clue) field.getTile(row, column)).getValue();
						out.printf("<a href=\"?row=%s&column=%s\"><img src=\"resources/gfx/%d.png\"></a>", row, column,
								value);
					}
				}

				if (field.getTile(row, column).getState().equals(State.MARKED)) {
					out.printf("<a href=\"?row=%s&column=%s&mark=%s\"><img src=\"resources/gfx/mark.png\"></a>", row,
							column, 1);
				}
			}
			out.printf("<br>");
		}
		out.println("</div>");

		if (field.getState().equals(GameState.SOLVED)) {
			out.println("<h1 align=\"center\">Vyhral si</h1><br>");
			long time = field.getTime();
			out.printf("<h1 align=\"center\">Your time is: %ss</h1><br>", time);
			String playerName = user.getName();
			String gameName = "minesweeper";
			if (playerName != null) {
				scoreDAO.saveScore(gameName, playerName, time);
			}
		} else if (field.getState().equals(GameState.SOLVED)) {
			out.println("<h1 align=\"center\">Prehral si</h1><br>");
		}

	}

	private void openTile(HttpServletRequest request, Field field) {
		try {

			int r = Integer.parseInt(request.getParameter("row"));
			int c = Integer.parseInt(request.getParameter("column"));

			field.openTile(r, c);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void markTile(HttpServletRequest request, Field field) {
		try {
			int r = Integer.parseInt(request.getParameter("row"));
			int c = Integer.parseInt(request.getParameter("column"));
			field.markTile(r, c);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void openField(Field field) {
		for (int row = 0; row < field.getRowCount(); row++) {
			for (int column = 0; column < field.getColumnCount(); column++) {
				field.openTile(row, column);
			}
		}
	}

	private void startNewGame(HttpServletRequest request) {
		try {
			if (Integer.parseInt(request.getParameter("field")) == 1) {
				request.getSession().setAttribute("field", null);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
