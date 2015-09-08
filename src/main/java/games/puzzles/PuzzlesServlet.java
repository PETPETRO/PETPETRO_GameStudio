package games.puzzles;

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

@WebServlet("/puzzles")
public class PuzzlesServlet extends HttpServlet {
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
		Field fieldPuzzle;
		boolean solved = false;

		startNewGame(request);

		if (request.getSession().getAttribute("fieldPuzzle") == null) {
			fieldPuzzle = new Field(5, 5);

		} else {
			fieldPuzzle = (Field) request.getSession().getAttribute("fieldPuzzle");
		}

		request.getSession().setAttribute("fieldPuzzle", fieldPuzzle);

		swap(request, fieldPuzzle);

		out.println("<html>");
		out.println("<head>");
		out.println("<title>Puzzles</title>");
		out.println("</head>");
		out.println(
				"<body style=\"background-image: url(resources/gfx/gameStudio.jpg); background-position: center; background-repeat: no-repeat; height: 100%;\">");

		out.println("<div style=\" width: 100%;\" align=\"center\"> ");

		out.printf(
				"<a  href=\"?fieldPuzzle=%d\"><img style=\"background-color: transparent;\" src=\"resources/gfx/newGame.png\" alt=\"newGame.png\" /></a><br/><br/> ",
				1);

		if (fieldPuzzle.isSolved(fieldPuzzle)) {
			solved = true;

		}

		out.println(
				"<div style=\" display: inline; height: 30%; margin-top: 2%; margin-bottom: 2%;\" align=\"center\">");

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
					out.printf("<IMG alt=\"aston%s.jpg\" src=\"resources/aston/aston%s.jpg\" style=\"height:60px\" />",
							value, value);
					out.printf("</a>");
				} else {

					if (Integer.parseInt(request.getParameter("row1")) == row
							&& Integer.parseInt(request.getParameter("column1")) == column) {
						out.printf("<a href=\"?row1=%s&column1=%s&row2=%s&column2=%s\">", r1, c1, row, column);
						out.printf(
								"<IMG alt=\"aston%s.jpg\" src=\"resources/aston/aston%s.jpg\" style=\"border-color: red; height:60px;\" />",
								value, value);
						out.printf("</a>");
					} else {
						out.printf("<a href=\"?row1=%s&column1=%s&row2=%s&column2=%s\">", r1, c1, row, column);
						out.printf(
								"<IMG alt=\"aston%s.jpg\" src=\"resources/aston/aston%s.jpg\" style=\"height:60px;\" />",
								value, value);
						out.printf("</a>");
					}

				}
			}
			out.println("<br/");
		}
		out.println("</div>");

		out.printf(
				"<a href=\"http://localhost:8080/gameStudio/gameStudio.xhtml\"><img src=\"resources/gfx/gameStudio.png\" alt=\"gameStudio.png\"style=\"border: 0\" /></a>");
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

		out.printf("<br/>");
		out.printf(
				"<a href=\"http://localhost:8080/gameStudio/bestTimesPuzzle.xhtml\" style=\"color: black;\">Best Score</a>");

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

	private void printComments(PrintWriter out) {
		List<Comment> comments = new ArrayList<>();
		comments = commentDAO.getComments("puzzle");
		out.println("<div style=\" width: 100%;\" align=\"left\">");
		for (Comment comment : comments) {
			out.printf("<FONT color=\"white\">%s:</FONT>  %s <br/>", comment.getPlayerName(), comment.getComment());
		}
		out.printf("</div>");
	}

	private void saveComment(HttpServletRequest request) {
		if (request.getParameter("comment") != null) {
			commentDAO.saveComment("puzzle", user.getName(), request.getParameter("comment"));
		}
	}

	private void addRating(PrintWriter out) {

		if (user.getName() != null) {
			out.printf("<form>");
			out.printf("Rating:");
			out.printf("<select name=\"rating\">");
			for (int i = 0; i <= 10; i++) {
				out.printf("<option  value=\"%s\">%s</option >", i, i, i);
			}
			out.printf("</select>");
			out.printf("<input type=\"submit\" value=\"Add rating\">");
			out.printf("</form>");
		}
	}

	private void addComment(PrintWriter out) {
		if (user.getName() != null) {
			out.printf("<form>");
			out.printf("Comment: ");
			out.printf(" <input type=\"text\" name=\"comment\">");
			out.printf("</form>");

		}
	}

	private void saveRating(HttpServletRequest request) {
		if (request.getParameter("rating") != null) {
			System.out.println(request.getParameter("rating"));

			Rating userRating = null;
			try {
				userRating = ratingDao.getRatingForUser(user.getName(), "puzzle");
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userRating == null) {
				ratingDao.saveRating("puzzle", user.getName(), Integer.parseInt(request.getParameter("rating")));
			} else {
				ratingDao.updateUserRating(Integer.parseInt(request.getParameter("rating")), "puzzle", user.getName());
			}

		}
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
