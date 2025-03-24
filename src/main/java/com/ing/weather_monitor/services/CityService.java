package com.ing.weather_monitor.services;

import com.ing.weather_monitor.dao.CityRepository;
import com.ing.weather_monitor.external.eu_cities_dictionary.EuropeanCitiesDictionaryClient;
import com.ing.weather_monitor.mappers.CityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class CityService {
    private final Set<String> knownCityIds = ConcurrentHashMap.newKeySet();
    private final Map<String, Lock> fetchLock = new ConcurrentHashMap<>();
    private final CityRepository cityRepository;
    private final EuropeanCitiesDictionaryClient citiesClient;
    private final CityMapper cityMapper;

    public void fetchAndSaveCityIfUnknown(String cityId) {
        if (knownCityIds.contains(cityId)) {
            return;
        }
        // TODO: pobrać wszystkie miasta po wystartowania aplikacji (po raz pierwszy) aby zainicjalizować bazę
        //       i trafiać tutaj tylko jak przyjdzie żądanie z miastem, którego wcześniej w słowniku nie było
        //
        // TODO: może co jakiś czas pobierać wszystkie miasta ze słownika, aby trafiać tutaj jeszcze rzadziej?

        // Osobne locki, aby żądania po inne miasta mogły być równoległe
        var lock = fetchLock.computeIfAbsent(cityId, ignored -> new ReentrantLock());
        try {
            lock.lock();
            // Sprawdzamy ponownie, aby uniknąc lawiny żądań jak pojawią się pomiary z nowym miastem
            if (knownCityIds.contains(cityId)) {
                return;
            }

            // Na wypadek kilku instancji weather-monitor
            if (cityRepository.existsById(cityId)) {
                knownCityIds.add(cityId);
                return;
            }

            // TODO: dodac retry
            var city = citiesClient.getCityById(cityId);
            if (city == null) {
                throw new IllegalStateException("Nie znaleziono miasta o identyfikatorze " + cityId);
            }

            var entity = cityMapper.toEntity(cityId, city);
            cityRepository.save(entity);
            knownCityIds.add(cityId);
        } finally {
            lock.unlock();
            fetchLock.remove(cityId);
        }
    }
}
