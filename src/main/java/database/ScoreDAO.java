package database;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import entity.Score;

@Stateless
public class ScoreDAO {

	@Inject
	EntityManager em;

	public void saveScore(String gameName, String playerName, long time) {
		Score score = new Score(gameName, playerName, time);
		em.persist(score);

	}

	public List<Score> bestPlayers(String gameName) {
		return em.createQuery("select s from Score s where s.gameName=:gameName order by s.time", Score.class)
				.setParameter("gameName", gameName).setMaxResults(10).getResultList();
	}

}
