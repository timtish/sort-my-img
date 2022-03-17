package ru.timtish.vyazanie.dto;

import lombok.Data;

import java.util.List;

@Data
public class Tag {
    Long id;
    String name;
    String description;
    List<Tag> child;
}
