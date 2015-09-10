package database;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import entity.Rating;

@Stateless
public class RatingDAO {

	@Inject
	EntityManager em;

	public void saveRating(String gameName, String playerName, int rating) {
		Rating rat = new Rating(gameName, playerName, rating);
		em.persist(rat);

	}

	public List<Rating> getRatings(String gameName) {
		return em.createQuery("select r from Rating r where r.gameName=:gameName", Rating.class)
				.setParameter("gameName", gameName).getResultList();
	}

	public String getAverageRating(String gameName) {
		if (em.createQuery("select avg(r.rating) from Rating r where r.gameName=:gameName")
				.setParameter("gameName", gameName).getSingleResult() != null) {
			return "" + em.createQuery("select avg(r.rating) from Rating r where r.gameName=:gameName")
					.setParameter("gameName", gameName).getSingleResult();
		} else
			return null;
	}

	public Rating getRatingForUser(String playerName, String gameName) {
		System.out.println(playerName + "----------------------------------");
		return em
				.createQuery("select r from Rating r where r.playerName=:playerName and r.gameName=:gameName",
						Rating.class)
				.setParameter("playerName", playerName).setParameter("gameName", gameName).getSingleResult();
	}

	public void updateUserRating(int rating, String gameName, String playerName) {
		em.createQuery("update Rating r set rating=:rating where r.playerName=:playerName and r.gameName=:gameName")
				.setParameter("rating", rating).setParameter("playerName", playerName)
				.setParameter("gameName", gameName).executeUpdate();

	}
}
