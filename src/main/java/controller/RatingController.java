package controller;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import database.RatingDAO;
import entity.Rating;
import entity.User;

@Named
@RequestScoped
public class RatingController {

	@Inject
	RatingDAO ratingDao;
	@Inject
	Rating rating;
	@Inject
	User user;

	private int currentRating;

	public List<Rating> Ratings(String gameName) {
		return ratingDao.getRatings(gameName);
	}

	public String getAverageRating(String gameName) {
		String rating = ratingDao.getAverageRating(gameName);
		if (rating != null) {
			return ratingDao.getAverageRating(gameName);
		} else
			return "-";

	}

	public int getCurrentRating() {
		return currentRating;
	}

	public void setCurrentRating(int currentRating) {
		this.currentRating = currentRating;
	}

	public String saveRating(String gameName, String path) {
		Rating userRating = null;
		try {
			userRating = ratingDao.getRatingForUser(user.getName(), gameName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (userRating == null) {
			ratingDao.saveRating(gameName, user.getName(), currentRating);
		} else {
			ratingDao.updateUserRating(currentRating, gameName, user.getName());
		}
		return path;
	}

}
