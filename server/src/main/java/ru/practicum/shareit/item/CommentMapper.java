package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Collection<CommentDto> toCommentDtoCollection(Collection<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return null;
        }
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    public static Comment toComment(CommentDto commentDto, User author, Item item, LocalDateTime created) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(created)
                .build();
    }
}