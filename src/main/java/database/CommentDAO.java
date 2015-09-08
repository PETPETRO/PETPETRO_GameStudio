package database;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import entity.Comment;

@Stateless
public class CommentDAO {

	@Inject
	EntityManager em;

	public void saveComment(String gameName, String playerName, String comment) {
		Comment com = new Comment(gameName, playerName, comment);
		em.persist(com);

	}

	public List<Comment> getComments(String gameName) {
		return em.createQuery("select c from Comment c where c.gameName=:gameName", Comment.class)
				.setParameter("gameName", gameName).getResultList();
	}

}
