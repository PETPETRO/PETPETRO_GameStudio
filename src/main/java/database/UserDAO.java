package database;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import entity.User;

@Stateless
public class UserDAO {

	@Inject
	EntityManager em;

	public void saveUser(String name, String password) {
		User user = new User(name, password);
		System.out.println(user);

		em.persist(user);

	}

	public User findUserByPass(User user) {
		System.out.println(user);
		return em.createQuery("select s from User s where s.password=:password", User.class)
				.setParameter("password", user.getPassword()).getSingleResult();
	}

	public User findUserByName(User user) {
		System.out.println(user);
		return em.createQuery("select s from User s where s.name=:name", User.class)
				.setParameter("name", user.getName().toLowerCase()).getSingleResult();
	}

	public User findUser(User user) {
		System.out.println(user);
		return em.createQuery("select s from User s where s.name=:name and s.password=:password", User.class)
				.setParameter("password", user.getPassword()).setParameter("name", user.getName()).getSingleResult();
	}

}
