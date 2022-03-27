package ru.timtish.vyazanie.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;
import ru.timtish.vyazanie.dto.*;
import ru.timtish.vyazanie.srote.YmlUtil;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemController {

    protected TreeMap<Long, Item> items;
    protected long maxId = 1000;

    protected final FileController filesController;
    protected final TagController tagsController;

    public Item add(String link) {
        Item file = new Item();
        file.setLink(link);
        return file;
    }

    /** add or update Item */
    public Long save(Item item) {
        if (item.getId() == null) item.setId(maxId++); // set id
        else if (item.equals(items.get(item.getId()))) return item.getId(); // skip save if equals
        if (items == null) items = new TreeMap<>();
        items.put(item.getId(), item);
        save();
        return item.getId();
    }

    public Collection<Item> find(Filter filter, long max, long page) {
        return items == null ? Collections.emptyList() : items.values().stream()
                .filter(filter)
                // todo: sort
                .skip(page * max).limit(max)
                .collect(Collectors.toList());
    }

    @PostConstruct
    public void load() {
        TreeMap<Long, Item> items = new TreeMap<>();
        try (InputStream in = getClass().getResourceAsStream("/data/items.yml")) {
            if (in == null) {
                log.warn("items.yml not found");
                return;
            }
            new Yaml().loadAll(new InputStreamReader(in))
            .forEach(list -> {
                for (Map<String, Object> obj : (Collection<Map<String, Object>>) list) try {
                    Item item = new Item();
                    item.setId((Number) obj.get("id"));
                    item.setLink((String) obj.get("link"));
                    item.setTitle((String) obj.get("title"));
                    item.setDescription((String) obj.get("description"));
                    Collection<Map<String, Object>> comments = (Collection<Map<String, Object>>) obj.get("comments");
                    if (comments != null) item.setComments(comments.stream().map(c -> {
                        Comment comment = new Comment();
                        comment.setId((Number) c.get("id"));
                        comment.setText((String) c.get("text"));
                        comment.setAuthor((String) c.get("author"));
                        comment.setHidden(Boolean.valueOf(String.valueOf(c.get("hidden"))));
                        return comment;
                    }).collect(Collectors.toList()));
                    Collection<Map<String, Object>> files = (Collection<Map<String, Object>>) obj.get("files");
                    if (files != null) item.setFiles(comments.stream().map(c -> {
                        File file = new File();
                        file.setId((Number) c.get("id"));
                        file.setPath((String) c.get("path"));
                        file.setLink((String) c.get("link"));
                        if (c.get("type") != null) file.setType(FileType.valueOf((String) c.get("type")));
                        return file;
                    }).collect(Collectors.toList()));
                    Collection<Number> tags = (Collection<Number>) obj.get("tags");
                    if (tags != null) item.setTags(tags.stream()
                            .map(c -> tagsController.get(c.longValue()))
                            .collect(Collectors.toList()));
                    items.put(item.getId(), item);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Failed item obj: " + obj, e);
                }
            });
            if (this.items != null) this.items.clear();
            this.items = items;
            this.maxId = items.keySet().stream().mapToLong(v -> v).max().orElse(1000);
            log.info("Load {} items", items.size());
        } catch (Exception e) {
            log.error("Failed load items.yml", e);
        }
    }

    //@PreDestroy
    public void save() {
        if (items != null) YmlUtil.save(items.values().stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", item.getId());
            if (item.getTitle() != null) map.put("title", item.getTitle());
            if (item.getDescription() != null) map.put("description", item.getDescription());
            if (item.getTags() != null) map.put("tags", item.getTags().stream().map(Tag::getId).collect(Collectors.toList()));
            if (item.getFiles() != null) map.put("files", item.getFiles().stream().map(file -> {
                    Map<String, Object> f = new LinkedHashMap<>();
                    f.put("id", file.getId());
                    if (file.getLink() != null) f.put("path", file.getPath());
                    if (file.getLink() != null) f.put("link", file.getLink());
                    if (file.getType() != null) f.put("type", file.getType().name());
                    return f;
                }).collect(Collectors.toList()));
            if (item.getComments() != null) map.put("comments", item.getComments().stream().map(comment -> {
                    Map<String, Object> commentMap = new LinkedHashMap();
                    commentMap.put("id", comment.getId());
                    if (comment.isHidden()) commentMap.put("hidden", true);
                    if (comment.getText() != null) commentMap.put("text", comment.getText());
                    if (comment.getAuthor() != null) commentMap.put("author", comment.getAuthor());
                    return commentMap;
                }).collect(Collectors.toList()));
            return map;
        }).collect(Collectors.toList()), "data/items.yml");
    }

}
