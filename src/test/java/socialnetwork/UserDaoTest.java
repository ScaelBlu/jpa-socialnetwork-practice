package socialnetwork;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("socialnetwork");
    private UserDao uDao = new UserDao(factory);

    @Test
    public void testSaveUserAndFind() {
        TimeMachine.set(LocalDateTime.of(2023, 2, 23, 15, 0));
        User user = new User("superman12", "abcd", Category.FREE);
        uDao.saveUser(user);

        User found = uDao.findUserById(user.getId());

        assertNotNull(user.getId());
        assertEquals("superman12", found.getUserName());
        assertEquals(LocalDate.parse("2023-02-23"), user.getRegistrationDate());
        TimeMachine.clear();
    }

    @Test
    public void testDeleteUserById() {
        User user = new User("superman12", "abcd", Category.FREE,
                new PersonalData("Gipsz Jakab", LocalDate.parse("1975-05-15"), "Budapest"));
        uDao.saveUser(user);

        uDao.deleteUserById(user.getId());

        User found = uDao.findUserById(user.getId());

        assertNull(found);
    }

    @Test
    public void testUpdateUserPassword() {
        User user = new User("superman12", "abcd", Category.FREE);

        uDao.saveUser(user);

        assertFalse(uDao.updateUser(user.getId(), "abcd"));
        assertTrue(uDao.updateUser(user.getId(), "dcba"));

        User found = uDao.findUserById(user.getId());

        assertEquals("dcba", found.getPassword());
    }

    @Test
    public void testUpdateUserCategory() {
        User user = new User("superman12", "abcd", Category.FREE);

        uDao.saveUser(user);

        assertFalse(uDao.updateUser(user.getId(), Category.FREE));
        assertTrue(uDao.updateUser(user.getId(), Category.PREMIUM));

        User found = uDao.findUserById(user.getId());

        assertEquals(Category.PREMIUM, found.getCategory());
    }

    @Test
    public void testSaveUserWithPersonalData() {
        User user = new User("superman12", "abcd", Category.FREE,
                new PersonalData("Gipsz Jakab", LocalDate.parse("1975-05-15"), "Budapest"));

        uDao.saveUser(user);

        User found = uDao.findUserById(user.getId());

        assertThat(found)
                .extracting(User::getPersonalData)
                .extracting(PersonalData::getRealName, PersonalData::getDateOfBirth, PersonalData::getCity)
                .containsExactly("Gipsz Jakab", LocalDate.of(1975, 5, 15), "Budapest");
    }

    @Test
    public void testSaveFriendshipAndList() {
        User user = new User("superman12", "abcd", Category.FREE);
        User user2 = new User("supergirl21", "1234", Category.VIP);
        User user3 = new User("kamehame93", "admin", Category.FREE);
        User user4 = new User("crazybiker49", "bike", Category.PREMIUM);
        User user5 = new User("justbike33", "asdf", Category.FREE);

        uDao.saveUser(user);
        uDao.saveUser(user2);
        uDao.saveUser(user3);
        uDao.saveUser(user4);
        uDao.saveUser(user5);

        uDao.saveFriendship(user.getId(), user2.getId());
        uDao.saveFriendship(user.getId(), user3.getId());
        uDao.saveFriendship(user.getId(), user4.getId());
        uDao.saveFriendship(user2.getId(), user3.getId());

        List<User> friends = uDao.listFriendsOfUser(user.getId());

        assertThat(friends)
                .hasSize(3)
                .extracting(User::getUserName)
                .containsOnly("supergirl21", "kamehame93", "crazybiker49");
    }

    @Test
    public void testRemoveFriendship() {
        User user = new User("superman12", "abcd", Category.FREE);
        User user2 = new User("supergirl21", "1234", Category.VIP);
        User user3 = new User("kamehame93", "admin", Category.FREE);
        User user4 = new User("crazybiker49", "bike", Category.PREMIUM);
        User user5 = new User("justbike33", "asdf", Category.FREE);

        uDao.saveUser(user);
        uDao.saveUser(user2);
        uDao.saveUser(user3);
        uDao.saveUser(user4);
        uDao.saveUser(user5);

        uDao.saveFriendship(user.getId(), user2.getId());
        uDao.saveFriendship(user.getId(), user3.getId());
        uDao.saveFriendship(user.getId(), user4.getId());
        uDao.saveFriendship(user2.getId(), user3.getId());
        //örihari
        uDao.removeFriendship(user.getId(), user2.getId());

        List<User> friends = uDao.listFriendsOfUser(user.getId());

        assertThat(friends)
                .hasSize(2)
                .extracting(User::getUserName)
                .containsOnly("kamehame93", "crazybiker49");
    }
}