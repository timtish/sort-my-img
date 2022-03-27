package ru.timtish.vyazanie.dto;

import lombok.Data;

/** иллюстрация к элементу коллекции */
@Data
public class File {
    Long id;
    String path;
    FileType type;
    byte[] data;
    String link;

    public void setId(Number id) {
        this.id = id == null ? null : id.longValue();
    }

}
