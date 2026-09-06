package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.UpdateEventAdminRequest;
import ru.practicum.main.dto.event.UpdateEventUserRequest;
import ru.practicum.main.model.Category;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.Location;
import ru.practicum.main.model.User;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class})
public interface EventMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    EventFullDto toFullDto(Event event);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    EventShortDto toShortDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "categoryFromId")
    @Mapping(target = "initiator", source = "userId", qualifiedByName = "userFromId")
    @Mapping(target = "location", source = "newEventDto.location")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "views", constant = "0L")
    @Mapping(target = "publishedOn", ignore = true)
    Event toEntity(NewEventDto newEventDto, Long userId, Long categoryId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    void updateEntityFromUserRequest(UpdateEventUserRequest request, @MappingTarget Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    void updateEntityFromAdminRequest(UpdateEventAdminRequest request, @MappingTarget Event event);

    @Named("categoryFromId")
    default Category categoryFromId(Long categoryId) {
        return Category.builder().id(categoryId).build();
    }

    @Named("userFromId")
    default User userFromId(Long userId) {
        return User.builder().id(userId).build();
    }

    @Named("locationFromDto")
    default Location locationFromDto(ru.practicum.main.dto.location.LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}