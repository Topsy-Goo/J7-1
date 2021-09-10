package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.Role;

@Repository
public interface RoleRepo extends CrudRepository<Role, Integer>
{
    Role findByName (String name);
}
