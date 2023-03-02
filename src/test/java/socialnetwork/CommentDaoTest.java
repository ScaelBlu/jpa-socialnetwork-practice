package socialnetwork;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class CommentDaoTest {

    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("socialnetwork");
    private CommentDao cDao = new CommentDao(factory);
    private UserDao uDao = new UserDao(factory);
    private PostDao pDao = new PostDao(factory);

    @Test
    public void testSaveAndListUserComments() {
        User user = new User("superman12", "abcd", "superman12@supermail.com");
        User user2 = new User("supergirl21", "1234", "supergirl21@gmail.com");

        uDao.saveUsers(user, user2);

        TimeMachine.set(LocalDateTime.parse("2023-02-26T15:30"));
        Post post1 = new Post(Content.TEXT);
        pDao.savePostToUser(user.getId(), post1);
        TimeMachine.set(LocalDateTime.parse("2023-02-28T18:30"));
        Post post2 = new Post(Content.IMAGE);
        pDao.savePostToUser(user.getId(), post2);

        TimeMachine.set(LocalDateTime.parse("2023-02-26T15:35"));
        cDao.saveComment(user2.getId(), post1.getId(), new Comment("This is only the beginning of a beautiful friendship"));

        assertThat(cDao.listCommentsOfUser(user2.getId()))
                .hasSize(1)
                .extracting(Comment::getCommentText)
                .containsExactly("This is only the beginning of a beautiful friendship");
        TimeMachine.clear();
    }

    @Test
    public void testListCommentsUnderPostsByUser() {
        User user = new User("superman12", "abcd", "superman12@supermail.com");
        User user2 = new User("supergirl21", "1234", "supergirl21@gmail.com");
        User user3 = new User("kamehame93", "admin", "gokusan93@outlook.com");

        uDao.saveUsers(user, user2, user3);

        Post post1 = new Post(Content.TEXT);
        Post post2 = new Post(Content.IMAGE);
        Post post3 = new Post(Content.VIDEO);

        pDao.savePostToUser(user.getId(), post1);
        pDao.savePostToUser(user.getId(), post2);
        pDao.savePostToUser(user2.getId(), post3);

        cDao.saveComment(user2.getId(), post1.getId(), new Comment("This is only the beginning of a beautiful friendship"));
        cDao.saveComment(user.getId(), post1.getId(), new Comment("I hope so :)"));
        cDao.saveComment(user2.getId(), post2.getId(), new Comment("What's that thing on the picture?"));
        cDao.saveComment(user3.getId(), post2.getId(), new Comment("I have seen this somewhere before!"));
        cDao.saveComment(user3.getId(), post3.getId(), new Comment("I love this song so much <3"));

        List<Comment> comments = cDao.listCommentsUnderPostsByUser(user.getId(), user2.getId());

        assertThat(comments)
                .hasSize(2)
                .extracting(c -> c.getUser().getUserName(), Comment::getCommentText, c -> c.getPost().getContent())
                .containsOnly(tuple("supergirl21", "This is only the beginning of a beautiful friendship", Content.TEXT),
                        tuple("supergirl21", "What's that thing on the picture?", Content.IMAGE));
    }

    @Test
    public void testListCommentsWithPaging() {
        User user = new User("superman12", "abcd", "superman12@supermail.com");
        User user2 = new User("supergirl21", "1234", "supergirl21@gmail.com");

        uDao.saveUsers(user, user2);

        Post post1 = new Post(Content.TEXT);
        Post post2 = new Post(Content.IMAGE);

        pDao.savePostToUser(user.getId(), post1);
        pDao.savePostToUser(user2.getId(), post2);

        cDao.saveComment(user2.getId(), post1.getId(), new Comment("This is only the beginning of a beautiful friendship"));
        cDao.saveComment(user.getId(), post1.getId(), new Comment("I hope so :)"));
        cDao.saveComment(user.getId(), post2.getId(), new Comment("What's that thing on the picture?"));
        cDao.saveComment(user2.getId(), post2.getId(), new Comment("I dunno, but I like it"));
        cDao.saveComment(user.getId(), post2.getId(), new Comment("LMAO :'D"));

        List<Comment> comments = cDao.listCommentsWithPaging(1, 3);

        assertThat(comments)
                .hasSize(3)
                .extracting(Comment::getCommentText)
                .containsExactly("I hope so :)",
                        "What's that thing on the picture?",
                        "I dunno, but I like it");
    }
}