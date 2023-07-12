package dto;

import model.HasId;
import model.Question;
import model.Response;
import model.User;


public class ActionDTO implements HasId<Long> {

    private User user;
    private Question question;
    private Response response;


    public ActionDTO() {
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }
}
