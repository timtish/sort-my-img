package ru.timtish.vyazanie.dto;

import lombok.Data;

import java.util.Collection;

@Data
public class Filter {
    String searchText;
    Collection<Long> tagIds;
}
