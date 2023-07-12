package repository;
import exception.RepositoryException;
import model.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GameDBRepository implements IGameDBRepository{

    private static final Logger logger= LogManager.getLogger(GameDBRepository.class.getName());
    private Session session;


    @Autowired
    public GameDBRepository() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public Game add(Game entity) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        Long id = (Long) session.save(entity);
        entity.setId(id);
        transaction.commit();
        logger.traceExit();
        return entity;
    }

    @Override
    public void delete(Game entity) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        session.delete(entity);
        transaction.commit();
        logger.traceExit();
    }

    @Override
    public void update(Game entity) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        session.update(entity);
        transaction.commit();
        logger.traceExit();
    }

    @Override
    public Game findById(Long id) throws RepositoryException {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        Game entity = session.get(Game.class, id);
        transaction.commit();
        logger.traceExit();
        return entity;
    }

    @Override
    public Iterable<Game> getAll() {
        logger.traceEntry();
        Query query = session.createQuery("from Game");
        List<Game> entities = query.list();
        logger.traceExit();
        return entities;
    }


    public Game findBy(String parameter) throws RepositoryException {
        logger.traceEntry();
        Query query = session.createQuery("from Game where id = :p");
        query.setParameter("p", parameter);
        Game entity = (Game) query.uniqueResult();
        if (entity == null) {
            logger.error("No CLASSTR found!");
            throw new RepositoryException("No CLASSTR found!");
        }
        logger.traceExit();
        return entity;
    }

}