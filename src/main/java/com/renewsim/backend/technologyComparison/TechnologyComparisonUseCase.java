package com.renewsim.backend.technologyComparison;

import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonRequestDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TechnologyComparisonUseCase {

    private final TechnologyComparisonService service;
    private final TechnologyComparisonMapper mapper;

    public TechnologyComparisonUseCase(TechnologyComparisonService service,
            TechnologyComparisonMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    public TechnologyComparisonResponseDTO createTechnology(TechnologyComparisonRequestDTO dto) {
        TechnologyComparison tech = mapper.toEntity(dto);
        TechnologyComparison saved = service.addTechnology(tech);
        return mapper.toResponseDTO(saved);
    }

    public void deleteTechnology(Long id) {
        service.deleteTechnology(id);
    }

    public List<TechnologyComparisonResponseDTO> filterByType(String energyType) {
        return service.getTechnologiesByEnergyType(energyType).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public TechnologyComparisonResponseDTO updateTechnology(Long id, TechnologyComparisonRequestDTO dto) {
        TechnologyComparison updatedEntity = service.updateTechnology(id, mapper.toEntity(dto));
        return mapper.toResponseDTO(updatedEntity);
    }

}
