package controller;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import database.CommentDAO;
import entity.User;
import entity.*;

@Named
@RequestScoped
public class CommentController {
	@Inject
	Comment comment;
	@Inject
	CommentDAO commentDao;
	@Inject
	User user;

	public String saveComment(String comment, String gameName, String path) {
		commentDao.saveComment(gameName, user.getName(), comment);
		return path;
	}

	public List<Comment> getAllComments(String gameName) {

		return commentDao.getComments(gameName);
	}
}
