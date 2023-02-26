package socialnetwork;

public class PostData {

    private String userName;
    private Content content;
    private int commentCount;

    public PostData(String userName, Content content, int commentCount) {
        this.userName = userName;
        this.content = content;
        this.commentCount = commentCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
