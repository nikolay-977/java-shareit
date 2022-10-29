package ru.practicum.shareit.server.comment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.user.User;

@Component
public class CommentRowMapper {
    public Comment toComment(CommentDto commentDto, User booker, Item item) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                booker,
                item,
                commentDto.getCreated()
        );
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
