package games.stones;

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

@WebServlet("/stones")
public class StonesServlet extends HttpServlet {
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
		Field fieldStone;
		boolean solved = false;

		startNewGame(request);

		if (request.getSession().getAttribute("fieldStone") == null) {
			fieldStone = new Field(3, 3);
		} else {
			fieldStone = (Field) request.getSession().getAttribute("fieldStone");
		}

		request.getSession().setAttribute("fieldStone", fieldStone);

		move(request, fieldStone);

		out.println("<html>");
		out.println("<head>");
		out.println("<title>Stones</title>");
		out.println("</head>");
		out.println(
				"<body style=\"background-image: url(resources/gfx/gameStudio.jpg); background-position: center; background-repeat: no-repeat; height: 100%;\">");

		out.println("<div align=\"center\">");
		out.printf(
				"<a  href=\"?fieldStone=%d\"><img src=\"resources/gfx/newGame.png\" alt=\"newGame.png\"style=\"border: 0\" /></a>",
				1);
		out.println("</div><br>");

		solved = checkIfSolved(fieldStone);

		generateStones(out, fieldStone);

		out.println("<br><div align=\"center\">");
		out.printf(
				"<a href=\"http://localhost:8080/gameStudio/gameStudio.xhtml\"><img src=\"resources/gfx/gameStudio.png\" alt=\"gameStudio.png\"style=\"border: 0\" /></a>");
		out.println("</div>");

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

		out.printf("</div>");
		out.printf("<div align=\"center\">");
		out.printf(
				"<a href=\"http://localhost:8080/gameStudio/bestTimesStones.xhtml\"  style=\"color: black;\">Best Score</a>");

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
							"<a style=\" text-decoration:none;display:inline-block;width: 40px;height: 40px;\" href=\"stones\" >&nbsp</a>");
				}
			}
			out.printf("<br>");
		}
		out.println("</div>");
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
		comments = commentDAO.getComments("stones");
		out.println("<div style=\" width: 100%;\" align=\"left\">");
		for (Comment comment : comments) {
			out.printf("<FONT color=\"white\">%s:</FONT>  %s <br/>", comment.getPlayerName(), comment.getComment());
		}
		out.printf("</div>");
	}

	private void saveComment(HttpServletRequest request) {
		if (request.getParameter("comment") != null) {
			commentDAO.saveComment("stones", user.getName(), request.getParameter("comment"));
		}
	}

	private void saveRating(HttpServletRequest request) {
		if (request.getParameter("rating") != null) {
			Rating userRating = null;
			try {
				userRating = ratingDao.getRatingForUser(user.getName(), "stones");
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (userRating == null) {
				ratingDao.saveRating("stones", user.getName(), Integer.parseInt(request.getParameter("rating")));
			} else {
				ratingDao.updateUserRating(Integer.parseInt(request.getParameter("rating")), "stones", user.getName());
			}

		}
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
