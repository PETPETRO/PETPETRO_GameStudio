package controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Transient;

import database.ScoreDAO;
import entity.Score;

@Named
public class ScoreController {

	@Inject
	ScoreDAO scoreDao;

	public List<Score> bestPlayers(String gameName) {
		return scoreDao.bestPlayers(gameName);
	}

}
