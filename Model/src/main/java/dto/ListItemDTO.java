package dto;

import model.Game;
import model.HasId;
import model.User;

public class ListItemDTO implements HasId<Long> {

    private Game game;

    public ListItemDTO() {
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }

    @Override
    public String toString() {
        return "G= " + game;
    }
}
