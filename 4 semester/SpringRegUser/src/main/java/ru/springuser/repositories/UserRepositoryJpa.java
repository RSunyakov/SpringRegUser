package ru.springuser.repositories;

import org.springframework.stereotype.Repository;
import ru.springuser.model.User;

@Repository
public interface UserRepositoryJpa extends CrudRepository<User, Long> {
}
