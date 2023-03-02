package socialnetwork;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "user-friends-friends",
    attributeNodes = @NamedAttributeNode(value = "friends", subgraph = "friendsSub"),
    subgraphs = @NamedSubgraph(name = "friendsSub", attributeNodes = @NamedAttributeNode("friends"))
)
@Entity
@Table(name = "users")
@SecondaryTable(name = "personal_data", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id"))
public class User {

    @Id
    @GeneratedValue(generator = "id_gen")
    @TableGenerator(name = "id_gen",
            table = "id_table",
            pkColumnName = "table_name",
            pkColumnValue = "users",
            valueColumnName = "next_id",
            allocationSize = 10)
    private Long id;

    @Column(name = "user_name", unique = true)
    private String userName;

    private String password;

    @Column(name = "email_address", length = 100)
    private String emailAddress;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Embedded
    private PersonalData personalData;

    @Column(name = "reg_at")
    private LocalDate registrationDate;

    @OneToMany(targetEntity = Post.class, mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(targetEntity = Comment.class, mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(targetEntity = Group.class)
    @JoinTable(name = "users_to_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new HashSet<>();

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "users_to_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private Set<User> friends = new HashSet<>();

    public User() {
    }

    public User(String userName, String password, String emailAddress) {
        this.userName = userName;
        this.password = password;
        this.emailAddress = emailAddress;
        this.category = Category.FREE;
    }

    public User(String userName, String password, String emailAddress, PersonalData personalData) {
        this(userName, password, emailAddress);
        this.personalData = personalData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public PersonalData getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    @PrePersist
    public void setRegistrationDate() {
        if(TimeMachine.isSet()) {
            this.registrationDate = TimeMachine.now().toLocalDate();
        } else {
            this.registrationDate = LocalDate.now();
        }
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void addPost(Post post) {
        post.setUser(this);
        posts.add(post);
    }

    public void addFriend(User user) {
        friends.add(user);
        user.getFriends().add(this);
    }
}
