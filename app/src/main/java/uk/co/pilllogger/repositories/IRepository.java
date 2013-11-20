package uk.co.pilllogger.repositories;

import java.util.List;

/**
 * Created by alex on 14/11/2013.
 */
public interface IRepository<T> {
    long insert(T data);
    void update(T data);
    void delete(T data);
    T get(int id);
    List<T> getAll();
}
