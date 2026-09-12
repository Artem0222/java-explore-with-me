package ru.practicum.main.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.dto.location.LocationDto;
import ru.practicum.main.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto(Location location);

    @Mapping(target = "id", ignore = true)
    Location toEntity(LocationDto locationDto);
}