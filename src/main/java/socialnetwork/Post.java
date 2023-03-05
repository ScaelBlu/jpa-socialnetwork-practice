package socialnetwork;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(generator = "id_gen")
    @TableGenerator(name = "id_gen",
            table = "id_table",
            pkColumnName = "table_name",
            pkColumnValue = "posts",
            valueColumnName = "next_id",
            allocationSize = 10)
    private Long id;

    @Column(name = "posted_at")
    private LocalDateTime postDate;

    @Enumerated(EnumType.STRING)
    private Content content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(targetEntity = Comment.class, mappedBy = "post", cascade =  CascadeType.REMOVE)
    private Set<Comment> comments = new HashSet<>();

    public Post() {
    }

    public Post(Content content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    @PrePersist
    public void setPostDate() {
        if(TimeMachine.isSet()) {
            this.postDate = TimeMachine.now();
        } else {
            this.postDate = LocalDateTime.now();
        }
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void addComment(User user, Comment comment) {
        comment.setUser(user);
        comment.setPost(this);
        user.getComments().add(comment);
        this.comments.add(comment);
    }
}
