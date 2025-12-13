package mate.academy.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import mate.academy.dao.UserDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class UserDaoImpl implements UserDao {
    @Override
    public User add(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't add User: " + user + " to db", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> from = query.from(User.class);
            Predicate email1 = cb.equal(from.get("email"), email);
            List<User> userList = session.createQuery(query.where(email1)).getResultList();
            if (userList.size() > 1) {
                throw new DataProcessingException("There is a problem in DB, two users "
                        + "with same email: " + email);
            }
            if (userList.isEmpty()) {
                throw new DataProcessingException("There is no user with email: "
                        + email + "in DB");
            }
            return Optional.of(userList.get(0));
        } catch (Exception e) {
            throw new DataProcessingException("Can't find User by email: " + email + " in DB", e);
        }
    }
}
