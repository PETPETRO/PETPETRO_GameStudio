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
	private CommentDAO commentDao;
	@Inject
	private User user;
	@Inject
	private Comment comment;

	private String actualComment;

	public String saveComment(String gameName, String path) {
		commentDao.saveComment(gameName, user.getName(), actualComment);
		return path;
	}

	public List<Comment> getAllComments(String gameName) {
		return commentDao.getComments(gameName);
	}

	public String getActualComment() {
		return actualComment;
	}

	public void setActualComment(String actualComment) {
		this.actualComment = actualComment;
	}
}
