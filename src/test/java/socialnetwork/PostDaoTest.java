package socialnetwork;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostDaoTest {

    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("socialnetwork");
    private PostDao pDao = new PostDao(factory);
    private UserDao uDao = new UserDao(factory);
    private CommentDao cDao = new CommentDao(factory);

    @Test
    public void testSavePostToUser() {
        TimeMachine.set(LocalDateTime.parse("2023-02-25T15:00"));
        User user = new User("superman12", "abcd", "superman12@supermail.com", Category.FREE);
        uDao.saveUsers(user);

        TimeMachine.set(LocalDateTime.parse("2023-02-26T15:30"));
        pDao.savePostToUser(user.getId(), new Post(Content.TEXT));
        TimeMachine.set(LocalDateTime.parse("2023-02-28T18:30"));
        pDao.savePostToUser(user.getId(), new Post(Content.IMAGE));
        TimeMachine.set(LocalDateTime.parse("2023-02-26T10:20"));
        pDao.savePostToUser(user.getId(), new Post(Content.VIDEO));

        List<Post> posts = pDao.listAllPostsOfUser(user.getId());

        assertThat(posts)
                .hasSize(3)
                .extracting(Post::getContent)
                .containsExactly(Content.VIDEO, Content.TEXT, Content.IMAGE);
        TimeMachine.clear();
    }

    @Test
    public void testListPostsOfUser() {
        TimeMachine.set(LocalDateTime.parse("2023-02-25T15:00"));
        User user = new User("superman12", "abcd", "superman12@supermail.com", Category.FREE);
        uDao.saveUsers(user);

        TimeMachine.set(LocalDateTime.parse("2023-02-26T15:30"));
        pDao.savePostToUser(user.getId(), new Post(Content.TEXT));
        TimeMachine.set(LocalDateTime.parse("2023-02-28T18:30"));
        pDao.savePostToUser(user.getId(), new Post(Content.IMAGE));
        TimeMachine.set(LocalDateTime.parse("2023-02-26T10:20"));
        pDao.savePostToUser(user.getId(), new Post(Content.VIDEO));

        List<Post> posts = pDao.listPostsOfUser(user.getId(),
                p -> p.getPostDate().isBefore(LocalDateTime.parse("2023-02-27T00:00")));

        assertThat(posts)
                .hasSize(2)
                .extracting(Post::getContent)
                .containsExactly(Content.TEXT, Content.VIDEO);
        TimeMachine.clear();
    }

    @Test
    public void testDeleteAllPostsByUserId() {
        User user = new User("superman12", "abcd", "superman12@supermail.com", Category.FREE);
        User user2 = new User("supergirl21", "1234", "supergirl21@gmail.com", Category.VIP);
        uDao.saveUsers(user, user2);

        Post post1 = new Post(Content.TEXT);
        Post post2 = new Post(Content.IMAGE);

        pDao.savePostToUser(user.getId(), post1);
        pDao.savePostToUser(user.getId(), post2);

        cDao.saveComment(user2.getId(), post1.getId(), new Comment("This is only the beginning of a beautiful friendship"));
        cDao.saveComment(user.getId(), post1.getId(), new Comment("I hope so :)"));

        pDao.deleteAllPostsByUserId(user.getId());

        List<Post> posts = pDao.listPostsOfUser(user.getId(), p -> true);

        assertTrue(posts.isEmpty());
    }

    @Test
    public void testListPostDataByUserId() {
        User user = new User("superman12", "abcd", "superman12@supermail.com", Category.FREE);
        User user2 = new User("supergirl21", "1234", "supergirl21@gmail.com", Category.VIP);
        User user3 = new User("kamehame93", "admin", "gokusan93@outlook.com", Category.FREE);
        uDao.saveUsers(user, user2, user3);

        Post post1 = new Post(Content.TEXT);
        Post post2 = new Post(Content.IMAGE);
        Post post3 = new Post(Content.VIDEO);

        pDao.savePostToUser(user.getId(), post1);
        pDao.savePostToUser(user.getId(), post2);
        pDao.savePostToUser(user.getId(), post3);

        cDao.saveComment(user2.getId(), post1.getId(), new Comment("This is only the beginning of a beautiful friendship"));
        cDao.saveComment(user.getId(), post1.getId(), new Comment("I hope so :)"));
        cDao.saveComment(user3.getId(), post2.getId(), new Comment("Amazing photo!"));

        List<PostData> postData = pDao.listPostDataByUserId(user.getId());

        assertThat(postData)
                .hasSize(3)
                .extracting(PostData::getUserName, PostData::getCommentCount, PostData::getContent)
                .containsExactly(tuple("superman12", 2, Content.TEXT),
                        tuple("superman12", 1, Content.IMAGE),
                        tuple("superman12", 0, Content.VIDEO));
    }
}