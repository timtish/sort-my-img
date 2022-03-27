package ru.timtish.vyazanie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.timtish.vyazanie.dto.Filter;
import ru.timtish.vyazanie.dto.Item;
import ru.timtish.vyazanie.rest.ItemController;

import java.util.Collection;

@SpringBootTest
public class TestLoadSave {

    @Autowired ItemController items;

    @Test
    public void load() {
        Collection<Item> list = items.find(new Filter(), 10, 0);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertFalse(list.iterator().next().getTags().isEmpty());
    }

    @Test
    public void save() {
        items.save();
    }

}
