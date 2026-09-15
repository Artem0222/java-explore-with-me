package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.model.Comment;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "event", source = "event.id")
    CommentDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "edited", ignore = true)
    @Mapping(target = "hidden", ignore = true)
    Comment toEntity(NewCommentDto newCommentDto);
}
