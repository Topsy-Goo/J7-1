package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.Role;

import java.util.Optional;

@Repository
public interface RoleRepo extends CrudRepository<Role, Integer> {

    Optional<Role> findByName (String name);
}
