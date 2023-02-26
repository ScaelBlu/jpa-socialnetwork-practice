package socialnetwork;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GroupDaoTest {

    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("socialnetwork");
    private GroupDao gDao = new GroupDao(factory);
    private UserDao uDao = new UserDao(factory);

    @Test
    public void testSaveToGroupAndListMembers() {
     Group group = new Group("BikerFanatics", "A group for real riders", false);
     User user = new User("crazybiker49", "bike", Category.FREE);
     User user2 = new User("justbike33", "asdf", Category.FREE);
     User user3 = new User("rideordie10", "0000", Category.PREMIUM);

     uDao.saveUser(user);
     uDao.saveUser(user2);
     uDao.saveUser(user3);

     gDao.saveGroup(group);

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
        User user = new User("crazybiker49", "bike", Category.FREE);
        User user2 = new User("justbike33", "asdf", Category.FREE);
        User user3 = new User("rideordie10", "0000", Category.PREMIUM);

        uDao.saveUser(user);
        uDao.saveUser(user2);
        uDao.saveUser(user3);

        gDao.saveGroup(group);

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
}