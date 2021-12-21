package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.Measure;

import java.util.Optional;

@Repository
public interface MeasureRepo extends JpaRepository<Measure, Integer> {

    Optional<Measure> findByName (String name);
}
