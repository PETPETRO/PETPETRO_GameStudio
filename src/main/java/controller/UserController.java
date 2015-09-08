package controller;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import database.UserDAO;
import entity.User;
import java.io.Serializable;

@Named
@SessionScoped
public class UserController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean logged = false;

	@Inject
	UserDAO userDao;
	@Inject
	User user;

	/**
	 * @return the logged
	 */
	public boolean isLogged() {
		return logged;
	}

	/**
	 * @param logged
	 *            the logged to set
	 */
	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public String registerUser() {

		// User user = new User(name, password);

		if (this.equals(null)) {
			return "registration.jsf";
		} else {

			User findUserByName = null;
			try {
				findUserByName = userDao.findUserByName(user);

			} catch (Exception e) {
				// TODO: handle exception
			}

			if (findUserByName == null) {
				userDao.saveUser(user.getName(), user.getPassword());
				setLogged(true);
				return "gameStudio.jsf";
			} else
				return "registration.jsf";

		}
	}

	public String loggUser() {
		if (this.equals(null)) {
			return "login.jsf";
		} else {
			User findUser = null;
			try {
				findUser = userDao.findUser(user);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (findUser != null) {
				setLogged(true);
				return "gameStudio.jsf";
			} else
				return "login.jsf";

		}
	}

	public String loggOut() {
		user.setName(null);
		user.setPassword(null);
		setLogged(false);
		return "gameStudio.jsf";

	}

}
