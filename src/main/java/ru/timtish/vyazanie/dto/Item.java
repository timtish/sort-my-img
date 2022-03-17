package ru.timtish.vyazanie.dto;

import lombok.Data;

import javax.xml.stream.events.Comment;
import java.io.File;
import java.util.Collection;
import java.util.List;

@Data
public class Item {
    Long id;
    String name;
    String description;
    Collection<Tag> tags;
    List<File> files;
    List<Comment> comments;
}
