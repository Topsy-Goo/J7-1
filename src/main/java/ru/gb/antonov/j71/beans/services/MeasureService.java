package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.antonov.j71.beans.errorhandlers.ResourceNotFoundException;
import ru.gb.antonov.j71.beans.repositos.MeasureRepo;
import ru.gb.antonov.j71.entities.Measure;

@Service
@RequiredArgsConstructor
public class MeasureService {

    private final MeasureRepo measureRepo;

    public Measure findByName (String measureName) {
        String errMsg = "Еденица измерения товара не найдена: " + measureName;
        return measureRepo.findByName (measureName)
                          .orElseThrow (()->new ResourceNotFoundException(errMsg));
    }
}
