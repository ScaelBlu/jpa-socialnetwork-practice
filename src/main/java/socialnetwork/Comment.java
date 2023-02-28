package socialnetwork;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@NamedQuery(name = "userComments", query = "SELECT c FROM Comment c WHERE c.user.id = :userId")
public class Comment {

    @Id
    @GeneratedValue(generator = "id_gen")
    @TableGenerator(name = "id_gen",
            table = "id_table",
            pkColumnName = "table_name",
            pkColumnValue = "comments",
            valueColumnName = "next_id",
            allocationSize = 10)
    private Long id;

    @Column(name = "commented_at")
    private LocalDateTime commentDate;

    @Column(name = "comment_text", columnDefinition = "TEXT")
    private String commentText;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Comment() {
    }

    public Comment(String commentText) {
        this.commentText = commentText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    @PrePersist
    public void setCommentDate() {
        if(TimeMachine.isSet()) {
            this.commentDate = TimeMachine.now();
        } else {
            this.commentDate = LocalDateTime.now();
        }
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
