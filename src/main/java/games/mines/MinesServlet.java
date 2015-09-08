package games.mines;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.CommentDAO;
import database.RatingDAO;
import database.ScoreDAO;
import entity.Comment;
import entity.Rating;
import entity.User;
import games.mines.Tile.State;

@WebServlet("/mines")
public class MinesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Inject
	User user;
	@Inject
	ScoreDAO scoreDAO;
	@Inject
	RatingDAO ratingDao;
	@Inject
	CommentDAO commentDAO;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		response.setContentType("text/html");
		Field field;
		boolean solved = false;
		boolean failed = false;

		startNewGame(request);

		if (request.getSession().getAttribute("field") == null) {
			field = new Field(5, 5, 1);
		} else {
			field = (Field) request.getSession().getAttribute("field");
		}

		request.getSession().setAttribute("field", field);

		markTile(request, field);

		openTile(request, field);

		out.println("<html>");
		out.println("<head>");
		out.println("<title> Minesweeper</title>");
		out.println("</head>");
		out.println(
				"<body style=\"background-image: url(resources/gfx/gameStudio.jpg); background-position: center; background-repeat: no-repeat; height: 100%;\">");

		out.println("<div align=\"center\">");
		out.printf(
				"<a  href=\"?field=%d\"><img src=\"resources/gfx/newGame.png\" alt=\"newGame.png\"style=\"border: 0\" /></a>",
				1);
		out.println("</div><br>");

		out.println("<div align=\"center\">");
		out.printf("<a href=\"?mark=%d\"><img src=\"resources/gfx/markicon.png\"  s></a>", 1);
		out.printf("<a href=\"mines\"><img src=\"resources/gfx/closeicon.png\"  s></a>");
		out.println("</div><br>");

		solved = checkIfSolved(field, solved);

		failed = checkIfFailed(field, solved, failed);

		out.println("<div align=\"center\">");

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

		out.println("<br><div align=\"center\">");
		out.printf(
				"<a href=\"http://localhost:8080/gameStudio/gameStudio.xhtml\"><img src=\"resources/gfx/gameStudio.png\" alt=\"gameStudio.png\"style=\"border: 0\" /></a>");
		out.println("</div>");

		if (solved) {
			out.println("<h1 align=\"center\">Vyhral si</h1><br>");
			long time = field.getTime();
			out.printf("<h1 align=\"center\">Your time is: %ss</h1><br>", time);
			String playerName = user.getName();
			String gameName = "minesweeper";
			if (playerName != null) {
				scoreDAO.saveScore(gameName, playerName, time);
			}

		}

		if (failed)
			out.println("<h1 align=\"center\">Prehral si</h1><br>");

		out.printf("</div>");
		out.printf("<div align=\"center\">");
		out.printf(
				"<a href=\"http://localhost:8080/gameStudio/bestTimesMinesweeper.xhtml\"  style=\"color: black;\" >Best Score</a>");
		out.printf("</div>");

		out.printf("</div>");
		out.println("<div style=\" width: 100%;\" align=\"left\">");
		addRating(out);
		addComment(out);
		out.printf("</div>");
		saveComment(request);
		printComments(out);
		out.println(
				"<footer style=\"text-align: center; background: transparent;\"class=\"navbar navbar-fixed-bottom\"><p style=\"color: black\">&#xA9; Created by Peter Petrovaj.</p></footer>");
		out.println("</body>");
		out.println("</html>");

		saveRating(request);

	}

	private void addComment(PrintWriter out) {
		if (user.getName() != null) {

			out.printf("<form>");
			out.printf("Comment:");
			out.printf(" <input type=\"text\" name=\"comment\">");
			out.printf("</form>");

		}
	}

	private void printComments(PrintWriter out) {
		List<Comment> comments = new ArrayList<>();
		comments = commentDAO.getComments("minesweeper");
		out.println("<div style=\" width: 100%;\" align=\"left\">");
		for (Comment comment : comments) {
			out.printf("<FONT color=\"white\">%s:</FONT>  %s <br/>", comment.getPlayerName(), comment.getComment());
		}
		out.printf("</div>");
	}

	private void saveComment(HttpServletRequest request) {
		if (request.getParameter("comment") != null) {
			commentDAO.saveComment("minesweeper", user.getName(), request.getParameter("comment"));
		}
	}

	private void addRating(PrintWriter out) {

		if (user.getName() != null) {
			out.printf("Rating:");

			out.printf("<form>");

			out.printf("<select name=\"rating\">");
			for (int i = 0; i <= 10; i++) {
				out.printf("<option  value=\"%s\">%s</option >", i, i, i);
			}

			out.printf("</select>");
			out.printf("<input type=\"submit\" value=\"Add rating\">");
			out.printf("</form>");
		}

	}

	private void saveRating(HttpServletRequest request) {
		if (request.getParameter("rating") != null) {
			System.out.println(request.getParameter("rating"));

			Rating userRating = null;
			try {
				userRating = ratingDao.getRatingForUser(user.getName(), "minesweeper");
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userRating == null) {
				ratingDao.saveRating("minesweeper", user.getName(), Integer.parseInt(request.getParameter("rating")));
			} else {
				ratingDao.updateUserRating(Integer.parseInt(request.getParameter("rating")), "minesweeper",
						user.getName());
			}

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
			int m = Integer.parseInt(request.getParameter("mark"));

			field.markTile(r, c);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private boolean checkIfFailed(Field field, boolean solved, boolean failed) {
		if (!solved) {
			if (field.getState().equals(GameState.FAILED)) {
				for (int row = 0; row < field.getRowCount(); row++) {
					for (int column = 0; column < field.getColumnCount(); column++) {
						field.openTile(row, column);
						failed = true;
					}
				}
			}
		}
		return failed;
	}

	private boolean checkIfSolved(Field field, boolean solved) {
		if (field.getState().equals(GameState.SOLVED)) {
			for (int row = 0; row < field.getRowCount(); row++) {
				for (int column = 0; column < field.getColumnCount(); column++) {
					field.openTile(row, column);
					solved = true;
				}
			}
		}
		return solved;
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
