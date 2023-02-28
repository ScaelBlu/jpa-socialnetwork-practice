package socialnetwork;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GroupDaoTest {

    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("socialnetwork");
    private GroupDao gDao = new GroupDao(factory);
    private UserDao uDao = new UserDao(factory);
    private PostDao pDao = new PostDao(factory);

    @Test
    public void testSaveToGroupAndListMembers() {
        Group group = new Group("BikerFanatics", "A group for real riders", false);
        gDao.saveGroup(group);

        User user = new User("crazybiker49", "bike", "crazybiker49@gmail.com", Category.FREE);
        User user2 = new User("justbike33", "asdf", "justbike33@gmail.com", Category.FREE);
        User user3 = new User("rideordie10", "0000", "rideordie10@yahoo.com", Category.PREMIUM);

        uDao.saveUsers(user, user2, user3);

        gDao.addUserToGroup(user.getId(), group.getId());
        gDao.addUserToGroup(user2.getId(), group.getId());
        gDao.addUserToGroup(user3.getId(), group.getId());

        List<User> members = gDao.listGroupMembers(group.getId());

        assertThat(members)
             .hasSize(3)
             .extracting(User::getUserName)
             .containsExactly("crazybiker49", "justbike33", "rideordie10");
    }

    @Test
    public void testRemoveUserFromGroup() {
        Group group = new Group("BikerFanatics", "A group for real riders", false);
        gDao.saveGroup(group);

        User user = new User("crazybiker49", "bike", "crazybiker49@gmail.com", Category.FREE);
        User user2 = new User("justbike33", "asdf", "justbike33@gmail.com", Category.FREE);
        User user3 = new User("rideordie10", "0000", "rideordie10@yahoo.com", Category.PREMIUM);

        uDao.saveUsers(user, user2, user3);

        gDao.addUserToGroup(user.getId(), group.getId());
        gDao.addUserToGroup(user2.getId(), group.getId());
        gDao.addUserToGroup(user3.getId(), group.getId());

        gDao.removeUserFromGroup(user2.getId(), group.getId());

        List<User> members = gDao.listGroupMembers(group.getId());

        assertThat(members)
                .hasSize(2)
                .extracting(User::getUserName)
                .containsExactly("crazybiker49", "rideordie10");
    }

    @Test
    public void testDeleteGroup() {
        Group group = new Group("BikerFanatics", "A group for real riders", false);
        gDao.saveGroup(group);

        User user = new User("crazybiker49", "bike", "crazybiker49@gmail.com", Category.FREE);
        uDao.saveUsers(user);

        gDao.addUserToGroup(user.getId(), group.getId());

        gDao.deleteGroup(group.getId());

        assertThat(uDao.findUser(user.getId())).isNotNull();
    }

    @Test
    public void testListGroupsWithNamedGraph() {
        Group group = new Group("BikerFanatics", "A group for real riders", false);
        gDao.saveGroup(group);

        User user = new User("crazybiker49", "bike", "crazybiker49@gmail.com", Category.FREE);
        User user2 = new User("justbike33", "asdf", "justbike33@gmail.com", Category.FREE);
        User user3 = new User("rideordie10", "0000", "rideordie10@yahoo.com", Category.PREMIUM);

        uDao.saveUsers(user, user2, user3);

        gDao.addUserToGroup(user.getId(), group.getId());
        gDao.addUserToGroup(user2.getId(), group.getId());
        gDao.addUserToGroup(user3.getId(), group.getId());

        pDao.savePostToUser(user.getId(), new Post(Content.TEXT));
        pDao.savePostToUser(user.getId(), new Post(Content.TEXT));
        pDao.savePostToUser(user.getId(), new Post(Content.TEXT));
        pDao.savePostToUser(user2.getId(), new Post(Content.IMAGE));
        pDao.savePostToUser(user2.getId(), new Post(Content.IMAGE));
        pDao.savePostToUser(user3.getId(), new Post(Content.VIDEO));

        List<Group> groups = gDao.listGroupsWithNamedGraph("groups-users-posts");

        assertThat(groups)
                .hasSize(1)
                .flatExtracting(Group::getUsers)
                .hasSize(3)
                .flatExtracting(User::getPosts)
                .hasSize(6);
    }

    @Test
    public void testFindGroupWithDynamicGraph() {
        Group group = new Group("BikerFanatics", "A group for real riders", false);
        gDao.saveGroup(group);

        User user = new User("superman12", "abcd", "superman12@supermail.com", Category.FREE);
        User user2 = new User("supergirl21", "1234", "supergirl21@gmail.com", Category.VIP);
        User user3 = new User("kamehame93", "admin", "gokusan93@outlook.com", Category.FREE);
        User user4 = new User("crazybiker49", "bike", "crazybiker49@gmail.com", Category.FREE);
        User user5 = new User("justbike33", "asdf", "justbike33@gmail.com",  Category.FREE);

        uDao.saveUsers(user, user2, user3, user4, user5);

        gDao.addUserToGroup(user.getId(), group.getId());
        gDao.addUserToGroup(user2.getId(), group.getId());
        gDao.addUserToGroup(user3.getId(), group.getId());

        uDao.saveFriendship(user.getId(), user2.getId());
        uDao.saveFriendship(user3.getId(), user4.getId());
        uDao.saveFriendship(user3.getId(), user5.getId());
        uDao.saveFriendship(user4.getId(), user5.getId());

        Group found = gDao.findGroupByDynamicGraph(group.getId());

        assertThat(found.getUsers())
                .hasSize(3)
                .flatExtracting(User::getFriends)
                .hasSize(4)
                .extracting(User::getUserName)
                .doesNotContain("kamehame93");
    }
}