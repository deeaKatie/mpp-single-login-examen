package services;

import dto.ActionDTO;
import dto.GameDTO;
import dto.ListItemsDTO;
import model.User;

public interface IServices {
    GameDTO checkLogIn(User user, IObserver client) throws ServiceException;
    void logout(User user) throws ServiceException;
    ListItemsDTO getData(User user) throws ServiceException;
    GameDTO madeAction(ActionDTO action) throws ServiceException;

}
