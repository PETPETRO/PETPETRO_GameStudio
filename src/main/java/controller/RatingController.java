package controller;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.validator.internal.util.privilegedactions.NewInstance;

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

	public List<Integer> getAverageRatingInStars(String gameName) {
		List<Integer> rate = new ArrayList<>();
		int rating = 0;
		try {
			Double dou = Double.parseDouble(getAverageRating(gameName));
			rating = (int) Math.round(dou);
			;
		} catch (Exception e) {
			// TODO: handle exception
		}

		for (int i = 0; i < rating; i++) {
			rate.add(i);
		}

		return rate;
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

	public void saveRating(String gameName, int rating) {
		Rating userRating = null;
		try {
			userRating = ratingDao.getRatingForUser(user.getName(), gameName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (userRating == null) {
			ratingDao.saveRating(gameName, user.getName(), rating);
		} else {
			ratingDao.updateUserRating(rating, gameName, user.getName());
		}
		;
	}

	public List<Integer> possibleRating() {
		List<Integer> rate = new ArrayList<>();
		for (int i = 0; i <= 10; i++) {
			rate.add(i);
		}
		return rate;
	}

}
