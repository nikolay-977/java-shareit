package ru.practicum.shareit.comment;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class CommentRowMapper {
    public static Comment toComment(CommentDto commentDto, User booker, Item item) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                booker,
                item,
                commentDto.getCreated()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
