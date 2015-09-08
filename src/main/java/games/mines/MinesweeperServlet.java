package games.mines;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import games.mines.Tile.State;




@WebServlet("/minesweeper")
public class MinesweeperServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Field field = new Field(5, 5, 5);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		response.setContentType("text/html");

		request.getSession().setAttribute("field", field);

		// START OF JAVASCRIPT
		out.println("<script> ");

		// function changeMine
		out.println("function changeMine(x,y)");
		out.println("{");
		out.println("var img =document.getElementById(\"image\"+x+y);");
		out.println("img.src=\"resources/gfx/mine.png\";");
		out.println("window.alert(\"Prehral si !!!\");");
		out.println("return false;");
		out.println("}");

		// function mark
		out.println("function mark(x,y)");
		out.println("{");
		out.println("var img =document.getElementById(\"image\"+x+y);");
		out.println("img.src=\"resources/gfx/mark.png\";");
		out.println("return false;");
		out.println("}");

		// function changeClue
		out.println("var i=0;");
		out.println("function changeClue(x,y,value)");
		out.println("{");
		out.println("var img =document.getElementById(\"image\"+x+y);");
		out.println("img.src=\"resources/gfx/\"+value+\".png\";");
		out.println("i=i+1;");
		out.println("if(i==" + ((field.getColumnCount() * field.getRowCount()) - field.getMineCount()) + "){");
		out.println("window.alert(\"Vyhral si !!!\");");
		out.println("}");

		// out.printf("for (var rowOffset = -1; rowOffset <= 1; rowOffset++)
		// {");
		// out.printf("var actRow = x + rowOffset;");
		// out.printf("if (actRow >= 0 && actRow <" + field.getRowCount() + " )
		// {");
		// out.printf("for (var columnOffset = -1; columnOffset <=
		// 1;columnOffset++) {");
		// out.printf("var actColumn = y + columnOffset;");
		// out.printf("if (actColumn >= 0 && actColumn < " +
		// field.getColumnCount() + ") {");
		// out.printf("changeClue(actRow, actColumn,
		// ((Clue)field.getTile(actRow, actColumn)).getValue());");
		// out.printf("}");
		// out.printf("}");
		// out.printf("}");
		// out.printf("}");

		out.println("return false;");
		out.println("}");

		out.println("</script>");
		// END OF JAVASCRIPT

		out.println("<html>");
		out.println("<head>");
		out.println("<title> Minesweeper</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1 align=\"center\">Minesweeper</h1><br>");
		out.println("<div align=\"center\">");

		for (int row = 0; row < field.getRowCount(); row++) {
			for (int column = 0; column < field.getColumnCount(); column++) {
				if (field.getTile(row, column).getState().equals(State.CLOSED)) {

					if (field.getTile(row, column) instanceof Mine) {
						out.printf("<img id=\"image" + row + column + "\""
								+ " src=\"resources/gfx/close.png\" onclick=\"changeMine(" + row + "," + column
								+ ")\";\" oncontextmenu=mark(" + row + "," + column + ") >");
					}
					if (field.getTile(row, column) instanceof Clue) {
						int value = ((Clue) field.getTile(row, column)).getValue();
						out.printf("<img id=\"image" + row + column + "\""
								+ " src=\"resources/gfx/close.png\" onclick=\"changeClue(" + row + "," + column + ","
								+ value + ")\";\" oncontextmenu=mark(" + row + "," + column + ")>");
					}
				}
			}
			out.printf("<br>");
		}
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
