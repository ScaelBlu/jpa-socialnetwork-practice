package socialnetwork;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class CommentDao {

    private EntityManagerFactory factory;

    public CommentDao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void saveComment(long userId, long postId, Comment comment) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            User user = manager.getReference(User.class, userId);
            Post post = manager.getReference(Post.class, postId);
            manager.persist(comment);
            post.addComment(user, comment);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public List<Comment> listCommentsOfUser(long userId) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createNamedQuery("userComments", Comment.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            manager.close();
        }
    }

    public List<Comment> listCommentsUnderPostsByUser(long ownerId, long writerId) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT comment FROM User user JOIN user.posts posts JOIN posts.comments comment WHERE comment.user.id = :writerId AND user.id = :ownerId ORDER BY comment.commentDate"
                    ,Comment.class)
                    .setParameter("writerId", writerId)
                    .setParameter("ownerId", ownerId)
                    .getResultList();
        } finally {
            manager.close();
        }
    }

    public List<Comment> listCommentsWithPaging(int firstIndex, int max) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT c FROM Comment c", Comment.class)
                    .setFirstResult(firstIndex)
                    .setMaxResults(max)
                    .getResultList();
        } finally {
            manager.close();
        }
    }
}
