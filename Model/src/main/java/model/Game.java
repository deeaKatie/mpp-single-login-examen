package model;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Game implements HasId<Long> {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    @OneToOne
    private User player;
    @OneToMany
    @JoinTable(name = "answers", //name of join table
            joinColumns = @JoinColumn(name = "id_q"), // param of left object
            inverseJoinColumns = @JoinColumn(name = "id")) // param of right object
    @MapKeyJoinColumn(name = "answers_q_r") //name of join column in the join table
    private Map<Question, Response> answers;
    private int noOfAnswersQ1;
    private int noOfAnswersQ2;
    private int noOfAnswersQ3;
    private int noOfAnswersQ4;

    private String state;

    public Game() {
        noOfAnswersQ1 = 0;
        noOfAnswersQ2 = 0;
        noOfAnswersQ3 = 0;
        noOfAnswersQ4 = 0;
        state = "OnGoing";
        answers = new HashMap<>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addAnswer(Question question, Response response){
        answers.put(question, response);
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public Map<Question, Response> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Question, Response> answers) {
        this.answers = answers;
    }

    public int getNoOfAnswersQ1() {
        return noOfAnswersQ1;
    }

    public void setNoOfAnswersQ1(int noOfAnswersQ1) {
        this.noOfAnswersQ1 = noOfAnswersQ1;
    }

    public int getNoOfAnswersQ2() {
        return noOfAnswersQ2;
    }

    public void setNoOfAnswersQ2(int noOfAnswersQ2) {
        this.noOfAnswersQ2 = noOfAnswersQ2;
    }

    public int getNoOfAnswersQ3() {
        return noOfAnswersQ3;
    }

    public void setNoOfAnswersQ3(int noOfAnswersQ3) {
        this.noOfAnswersQ3 = noOfAnswersQ3;
    }

    public int getNoOfAnswersQ4() {
        return noOfAnswersQ4;
    }

    public void setNoOfAnswersQ4(int noOfAnswersQ4) {
        this.noOfAnswersQ4 = noOfAnswersQ4;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id=" + id +", player=" + player.getUsername() ;
    }
}
