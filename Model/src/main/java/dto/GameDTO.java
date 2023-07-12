package dto;

import model.HasId;
import model.Question;
import model.User;

public class GameDTO implements HasId<Long> {

    private Question question;
    private User user;
    private int noPoints;
    private ListItemsDTO items;
    private boolean answer;
    private boolean won;

    public GameDTO() {
        noPoints = -1;
        answer = false;
        won = false;
    }

    public GameDTO(Question question, ListItemsDTO items) {
        noPoints = -1;
        this.question = question;
        this.items = items;
        answer = false;
        won = false;
    }

    public boolean getAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public int getNoPoints() {
        return noPoints;
    }

    public void setNoPoints(int noPoints) {
        this.noPoints = noPoints;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ListItemsDTO getItems() {
        return items;
    }

    public void setItems(ListItemsDTO items) {
        this.items = items;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }
}
