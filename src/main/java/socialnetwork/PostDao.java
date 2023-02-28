package socialnetwork;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PostDao {

    private EntityManagerFactory factory;

    public PostDao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void savePostToUser(long userId, Post post) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user = manager.getReference(User.class, userId);
            user.addPost(post);
            manager.persist(post);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public List<Post> listAllPostsOfUser(long userId) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT post FROM Post post WHERE post.user.id = :id ORDER BY post.postDate",
                    Post.class)
                    .setParameter("id", userId)
                    .getResultList();
        } finally {
            manager.close();
        }
    }

    public List<Post> listPostsOfUser(long userId, Predicate<Post> predicate) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT post FROM Post post WHERE post.user.id = :id",
                            Post.class)
                    .setParameter("id", userId)
                    .getResultStream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        } finally {
            manager.close();
        }
    }

    public void deleteAllPostsByUserId(long userId) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user = manager.getReference(User.class, userId);
            user.getPosts().clear();
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public List<PostData> listPostDataByUserId(long userId) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT new socialnetwork.PostData(post.user.userName, post.content, SIZE(post.comments)) " +
                                            "FROM Post post WHERE post.user.id = :userId",
                    PostData.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            manager.close();
        }
    }
}
