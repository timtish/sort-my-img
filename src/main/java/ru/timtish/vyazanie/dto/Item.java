package ru.timtish.vyazanie.dto;

import lombok.Data;

import java.util.Collection;
import java.util.List;

/** элемент коллекции */
@Data
public class Item {

    Long id;
    String title;
    String description;
    String link;
    boolean hidden;
    Collection<Tag> tags;
    List<File> files;
    List<Comment> comments;

    public void setId(Number id) {
        this.id = id == null ? null : id.longValue();
    }

}
