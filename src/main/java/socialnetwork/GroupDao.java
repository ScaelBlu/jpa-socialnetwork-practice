package socialnetwork;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Set;

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

    public List<Group> listAllGroups() {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT g FROM Group g", Group.class)
                    .getResultList();
        } finally {
            manager.close();
        }
    }
    public void deleteGroup(long groupId) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            Group group = manager.getReference(Group.class, groupId);
            group.getUsers().forEach(u -> u.getGroups().remove(group));
            manager.remove(group);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public List<Group> listGroupsWithNamedGraph(String graphName) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT g FROM Group g",
                    Group.class)
                    .setHint("javax.persistence.fetchgraph", manager.getEntityGraph(graphName))
                    .getResultList();
        } finally {
            manager.close();
        }
    }

    public Group findGroupByDynamicGraph(long groupId) {
        EntityManager manager = factory.createEntityManager();
        try {
            EntityGraph<Group> graph = manager.createEntityGraph(Group.class);
            graph.addSubgraph("users").addAttributeNodes("friends");
            return manager.createQuery("SELECT g FROM Group g WHERE g.id = :groupId",
                    Group.class)
                    .setParameter("groupId", groupId)
                    .setHint("javax.persistence.loadgraph", graph)
                    .getSingleResult();
        } finally {
            manager.close();
        }
    }
}
