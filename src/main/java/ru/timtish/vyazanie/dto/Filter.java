package ru.timtish.vyazanie.dto;

import lombok.Data;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Predicate;

/** ui фильтр */
@Data
public class Filter implements Predicate<Item> {

    String searchText;
    boolean searchInDescription;
    boolean searchInComments;
    boolean searchInHidden;
    Collection<Long> tagIds;

    @Override
    public boolean test(Item item) {
        if (tagIds != null && tagIds.size() > 0) {
            if (item.tags == null || item.tags.isEmpty()) return false;
            if (item.tags.stream().filter(t -> tagIds.contains(t.id)).findAny().orElse(null) == null) return false;
        }
        if (searchText != null && searchText.trim().length() > 0) {
            String txt = searchText.toLowerCase(Locale.ROOT);
            boolean contains = item.title != null && item.title.toLowerCase().contains(txt);
            if (searchInDescription && !contains && item.description != null) {
                contains = item.description.toLowerCase().contains(txt);
            }
            if (searchInComments && !contains && item.comments != null) {
                contains = (searchInHidden || !item.hidden) && item.comments.stream()
                        .filter(c -> (searchInHidden || !c.hidden) && c.text != null && c.text.toLowerCase(Locale.ROOT).contains(txt))
                        .findAny().orElse(null) == null;
            }
            if (!contains) return false;
        }
        return true;
    }
}
