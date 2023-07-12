package rpcprotocol;

import dto.ActionDTO;
import dto.GameDTO;
import dto.ListItemsDTO;
import dto.UpdateDTO;
import model.Game;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesRpcProxy implements IServices {
    private String host;
    private int port;
    private IObserver client;
    private User loggedUser;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.qresponses = new LinkedBlockingQueue<>();
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    private void initializeConnection() throws ServiceException {
        try {
            this.connection = new Socket(this.host, this.port);
            this.output = new ObjectOutputStream(this.connection.getOutputStream());
            this.output.flush();
            this.input = new ObjectInputStream(this.connection.getInputStream());
            this.finished = false;
            this.startReader();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
    private void closeConnection() {
        this.finished = true;

        try {
            this.input.close();
            this.output.close();
            this.connection.close();
            this.client = null;
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void sendRequest(Request request) throws ServiceException {
        try {
            this.output.writeObject(request);
            this.output.flush();
        } catch (IOException ex) {
            throw new ServiceException("Error sending object " + ex);
        }
    }

    private Response readResponse() throws ServiceException {
        Response response = null;
        try {
            response = (Response)this.qresponses.take();
            System.out.println("d");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return response;
    }

    private class ReaderThread implements Runnable {
        private ReaderThread() {
        }
        public void run() {
            while(!ServicesRpcProxy.this.finished) {
                try {
                    Object response;
                    synchronized (input){
                        response = ServicesRpcProxy.this.input.readObject();
                    }

                    System.out.println("response received " + response);
                    if (ServicesRpcProxy.this.isUpdate((Response)response)) {
                        ServicesRpcProxy.this.handleUpdate((Response)response);
                    } else {
                        try {
                            ServicesRpcProxy.this.qresponses.put((Response)response);
                        } catch (InterruptedException var3) {
                            var3.printStackTrace();
                        }
                    }
                } catch (IOException var4) {
                    System.out.println("Reading error " + var4);
                } catch (ClassNotFoundException var5) {
                    System.out.println("Reading error " + var5);
                }
            }

        }
    }

    private void handleUpdate(Response response) {
        System.out.println("PROXY -> handleUpdate");
        System.out.println("RESPONSE -> " + response);
        if (response.type() == ResponseType.UPDATE_DATA) {
            client.update((UpdateDTO) response.data());
        }
    }
    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.UPDATE_DATA;
    }

    @Override
    public GameDTO checkLogIn(User user, IObserver client) throws ServiceException {
        System.out.println("PROXY -> checkLogIn");

        initializeConnection();
        Request req = (new Request.Builder()).type(RequestType.LOGIN).data(user).build();
        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.OK) {
            this.client = client;
            GameDTO gameDTO = (GameDTO)response.data();
            user.setId(gameDTO.getUser().getId());
            loggedUser = gameDTO.getUser();
            return gameDTO;

        } else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new ServiceException(err);
        }
        throw new ServiceException("No User found");
    }

    @Override
    public void logout(User user) throws ServiceException {
        System.out.println("PROXY -> logout");
        Request req = (new Request.Builder()).type(RequestType.LOGOUT).data(user).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();

        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
    }

    @Override
    public ListItemsDTO getData(User user) throws ServiceException {
        System.out.println("PROXY -> getData");
        Request req = (new Request.Builder()).type(RequestType.GET_DATA).data(user).build();
        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.OK) {
            System.out.println("PROXY -> response OK");
            return (ListItemsDTO) response.data();
        } else if (response.type() == ResponseType.ERROR) {
            throw new ServiceException("Error " + response.data().toString());
        }

        return null;
    }


    @Override
    public GameDTO madeAction(ActionDTO action) throws ServiceException {
        System.out.println("PROXY -> madeAction");
        Request req = (new Request.Builder()).type(RequestType.MADE_ACTION).data(action).build();
        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.OK) {
            System.out.println("PROXY -> response OK");
            return (GameDTO) response.data();
        } else if (response.type() == ResponseType.ERROR) {
            throw new ServiceException("Error " + response.data().toString());
        }
        return null;
    }



}
