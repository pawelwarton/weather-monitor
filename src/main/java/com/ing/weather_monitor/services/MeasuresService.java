package com.ing.weather_monitor.services;

import com.ing.weather_monitor.api.measures.Measure;
import com.ing.weather_monitor.dao.MeasureRepository;
import com.ing.weather_monitor.mappers.MeasureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeasuresService {
    private final CityService cityService;
    private final MeasureRepository measureRepository;
    private final MeasureMapper measureMapper;

    public void saveMeasure(Measure measure) {
        // Co jeżeli api.europeancitiesdictionary.info nie zna danego miasta?
        // Pomiar powinien zostać zapisany mimo to? Aktualnie poleci wyjątek i pomiar nie zostanie zapisany.
        //
        // Co jeżeli api.europeancitiesdictionary.info nie będzie dostępny,
        // kiedy measure-monitor będzie przetwarzać pomiar z miastem, którego nie ma w bazie danych?
        // Pomiar powinien zostać zapisany mimo to? Aktualnie poleci wyjątek i pomiar nie zostanie zapisany.

        cityService.fetchAndSaveCityIfUnknown(measure.cityId());

        var entity = measureMapper.toEntity(measure);
        measureRepository.save(entity);
    }
}
