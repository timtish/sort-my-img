package ru.timtish.vyazanie.dto;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/** дерево тегов */
@Data
public class Tag {

    Long id;
    String title;
    String description;
    List<Tag> childs;
    Long parentId;

    public void addChild(Tag tag) {
        tag.setParentId(parentId);
        if (childs == null) childs = new LinkedList<>();
        childs.add(tag);
    }

    public void setId(Number id) {
        this.id = id == null ? null : id.longValue();
    }

}
