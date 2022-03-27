package ru.timtish.vyazanie.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    Long id;
    String text;
    Date date;
    String author;
    boolean hidden;

    public void setId(Number id) {
        this.id = id == null ? null : id.longValue();
    }

}
