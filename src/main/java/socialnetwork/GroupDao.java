package socialnetwork;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class GroupDao {

    private EntityManagerFactory factory;

    public GroupDao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void saveGroup(Group group) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            manager.persist(group);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public void addUserToGroup(long userId, long groupId) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user = manager.getReference(User.class, userId);
            Group group = manager.getReference(Group.class, groupId);
            group.addUser(user);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public List<User> listGroupMembers(long groupId) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT u FROM User u JOIN u.groups g WHERE g.id = :groupId",
                            User.class)
                    .setParameter("groupId", groupId)
                    .getResultList();
        } finally {
            manager.close();
        }
    }

    public void removeUserFromGroup(long userId, long groupId) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user = manager.getReference(User.class, userId);
            Group group = manager.getReference(Group.class, groupId);
            user.getGroups().remove(group);
            group.getUsers().remove(user);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }
}
