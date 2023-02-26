package socialnetwork;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(generator = "id_gen")
    @TableGenerator(name = "id_gen",
            table = "id_table",
            pkColumnName = "table_name",
            pkColumnValue = "groups",
            valueColumnName = "next_id",
            allocationSize = 10)
    private Long id;

    @Column(name = "group_name")
    private String name;

    @Column(name = "descr")
    private String description;

    @ManyToMany(targetEntity = User.class, mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    @Column(name = "is_private")
    private boolean privateGroup;

    public Group() {
    }

    public Group(String name, String description, boolean privateGroup) {
        this.name = name;
        this.description = description;
        this.privateGroup = privateGroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public boolean isPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(boolean privateGroup) {
        this.privateGroup = privateGroup;
    }

    public void addUser(User user) {
        users.add(user);
        user.getGroups().add(this);
    }
}
