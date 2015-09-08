package games.guessnumber;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import database.CommentDAO;
import database.ScoreDAO;
import entity.Comment;
import entity.User;

@Named
@SessionScoped
public class UserNumberBean implements Serializable {

	@Inject
	User user;
	@Inject
	ScoreDAO scoreDAO;
	@Inject
	CommentDAO commentDAO;
	private static final long serialVersionUID = 5443351151396868724L;
	Integer randomInt = null;
	private Integer userNumber = null;
	String response = null;
	private int maximum = 10;
	private int minimum = 0;
	private long startTime;

	public UserNumberBean() {
		Random randomGR = new Random();
		randomInt = new Integer(randomGR.nextInt(maximum + 1));
		// Print number to server log
		System.out.println("Duke's number: " + randomInt);
		startTime = System.currentTimeMillis();
	}

	public void setUserNumber(Integer user_number) {
		userNumber = user_number;
	}

	public Integer getUserNumber() {
		return userNumber;
	}

	public String getResponse() {
		if ((userNumber == null) || (userNumber.compareTo(randomInt) != 0)) {
			if (userNumber != null && userNumber > randomInt) {
				return "Sorry, " + userNumber + " is incorrect. Number is smaller.";
			}

			if (userNumber != null && userNumber < randomInt) {
				return "Sorry, " + userNumber + " is incorrect. Number is larger.";
			} else {
				return "Think number.";
			}

		} else {

			randomInt = new Integer(new Random().nextInt(maximum + 1));

			String playerName = user.getName();
			String gameName = "guessNumber";
			long time = (System.currentTimeMillis() - startTime) / 1000;
			if (playerName != null) {
				scoreDAO.saveScore(gameName, playerName, time);
			}
			startTime = System.currentTimeMillis();
			return "Yay! You got it!\n" + "Your time is: " + time + "s";

		}
	}

	public int getMaximum() {
		return (this.maximum);
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public int getMinimum() {
		return (this.minimum);
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public String getUser() {
		return user.getName();
	}

	public List<Integer> rating() {
		List<Integer> rating = new ArrayList<>();

		for (int i = 0; i <= 10; i++) {
			rating.add(i);
		}
		return rating;
	}
}
