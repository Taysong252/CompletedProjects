
package cr.ac.una.vivero.services;

import java.util.List;

public interface CRUD<T> {

    public void save(T t);

    public void delete(int i);

    public List<T> getAll();

    public T getById(int i);
}