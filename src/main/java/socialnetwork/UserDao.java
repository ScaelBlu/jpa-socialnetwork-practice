package socialnetwork;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.NoSuchElementException;

public class UserDao {

    private EntityManagerFactory factory;

    public UserDao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void saveUser(User user) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            manager.persist(user);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public void deleteUserById(long userId) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user = manager.getReference(User.class, userId);
            manager.remove(user);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public User findUserById(long id) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.find(User.class, id);
        } finally {
            manager.close();
        }
    }

    public boolean updateUser(long id, String newPassword) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user = manager.getReference(User.class, id);
            if(user.getPassword().equals(newPassword)) {
                return false;
            }
            user.setPassword(newPassword);
            manager.getTransaction().commit();
            return true;
        } finally {
            manager.close();
        }
    }

    public boolean updateUser(long id, Category newCategory) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user = manager.getReference(User.class, id);
            if(user.getCategory().equals(newCategory)) {
                return false;
            }
            user.setCategory(newCategory);
            manager.getTransaction().commit();
            return true;
        } finally {
            manager.close();
        }
    }

    public void saveFriendship(long userId1, long userId2) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user1 = manager.getReference(User.class, userId1);
            User user2 = manager.getReference(User.class, userId2);
            user1.addFriend(user2);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public List<User> listFriendsOfUser(long userId) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT friend FROM User u JOIN u.friends friend WHERE u.id = :userId",
                    User.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            manager.close();
        }
    }

    public void removeFriendship(long userId1, long userId2) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user1 = manager.getReference(User.class, userId1);
            User user2 = manager.getReference(User.class, userId2);
            user1.getFriends().remove(user2);
            user2.getFriends().remove(user1);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }
}
