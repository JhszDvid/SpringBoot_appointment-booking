package com.jddev.crmapp.utility.db;

import com.jddev.crmapp.booking.model.BookingConfirmation;
import com.jddev.crmapp.exception.InternalErrorException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class  DbService  {

    Logger logger = LoggerFactory.getLogger(DbService.class);
    private final Map<Class<?>, JpaRepository<?, ?>> repositoryMap;

    public DbService(Map<Class<?>, JpaRepository<?, ?>> repositoryMap) {
        this.repositoryMap = repositoryMap;
    }


    public <T, ID extends Serializable, R> R executeCustomMethod(Class<? extends JpaRepository<T, ID>> repositoryClass, Function<JpaRepository<T, ID>, R> function) {

        JpaRepository<T, ID> repository = getRepository(repositoryClass);
        return function.apply(repository);
    }

    public <T, ID extends Serializable> void saveWithFlush(Class<? extends JpaRepository<T, ID>> repositoryClass, T entity) {
        try{
            JpaRepository<T, ID> repository = getRepository(repositoryClass);
            repository.saveAndFlush(entity);
        }
        catch (Exception e) {
            logger.error("DB save failed: " + e.getMessage());
            throw new InternalErrorException("Unexpected error!");
        }

    }

    public <T, ID extends Serializable> void flush(Class<? extends JpaRepository<T, ID>> repositoryClass) {
        try{
            JpaRepository<T, ID> repository = getRepository(repositoryClass);
            repository.flush();
        }
        catch (Exception e) {
            logger.error("DB flush failed: " + e.getMessage());
            throw new InternalErrorException("Unexpected error!");
        }

    }

    public <T, ID extends Serializable> void save(Class<? extends JpaRepository<T, ID>> repositoryClass, T entity) {
        try{
            JpaRepository<T, ID> repository = getRepository(repositoryClass);
            repository.save(entity);
        }
        catch (Exception e) {
            logger.error("DB save failed: " + e.getMessage());
            throw new InternalErrorException("Unexpected error!");
        }

    }
    @Transactional
    public <T, ID extends Serializable> void saveAll(Class<? extends JpaRepository<T, ID>> repositoryClass, List<T> entity) {
        try{
            JpaRepository<T, ID> repository = getRepository(repositoryClass);
            repository.saveAll(entity);
        }
        catch (Exception e) {
            logger.error("DB saveAll failed: " + e.getMessage());
            throw new InternalErrorException("Unexpected error!");
        }
    }

    public <T, ID extends Serializable> void delete(Class<? extends JpaRepository<T, ID>> repositoryClass, ID id) {
        try{
            JpaRepository<T, ID> repository = getRepository(repositoryClass);
            repository.deleteById(id);
        }
        catch (Exception e) {
            logger.error("DB delete failed: " + e.getMessage());
            throw new InternalErrorException("Unexpected error!");
        }
    }

    public <T, ID extends Serializable> void deleteAll(Class<? extends JpaRepository<T, ID>> repositoryClass, List<T> entities) {
        try{
            JpaRepository<T, ID> repository = getRepository(repositoryClass);
            repository.deleteAll(entities);
        }
        catch (Exception e) {
            logger.error("DB deleteAll failed: " + e.getMessage());
            throw new InternalErrorException("Unexpected error!");
        }
    }

    public <T, ID extends Serializable> Optional<T> findById(Class<? extends JpaRepository<T, ID>> repositoryClass, ID id) {
        try{
            JpaRepository<T, ID> repository = getRepository(repositoryClass);
            return repository.findById(id);
        }
        catch (Exception e) {
            logger.error("DB find failed: " + e.getMessage());
            throw new InternalErrorException("Unexpected error!");
        }

    }

    public <T, ID extends Serializable> Page<T> findAllPageable(Class<? extends JpaRepository<T, ID>> repositoryClass, Integer pageNo, Integer pageSize, String sortField, String sortDir) {
        try{
            JpaRepository<T, ID> repository = getRepository(repositoryClass);
            return repository.findAll(PageRequest.of(pageNo, pageSize).withSort(Sort.Direction.fromString(sortDir), sortField));
        }
        catch (Exception e) {
            logger.error("DB find failed: " + e.getMessage());
            throw new InternalErrorException("Unexpected error!");
        }

    }

    public <T, ID extends Serializable> List<T> findAll(Class<? extends JpaRepository<T, ID>> repositoryClass) {
        try{
            JpaRepository<T, ID> repository = getRepository(repositoryClass);
            return repository.findAll();
        }
        catch (Exception e) {
            logger.error("DB find failed: " + e.getMessage());
            throw new InternalErrorException("Unexpected error!");
        }

    }


    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> JpaRepository<T, ID> getRepository(Class<? extends JpaRepository<T, ID>> repositoryClass) {
        return (JpaRepository<T, ID>) repositoryMap.get(repositoryClass);
    }

}
