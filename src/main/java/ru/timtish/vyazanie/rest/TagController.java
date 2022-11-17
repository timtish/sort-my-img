package ru.timtish.vyazanie.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;
import ru.timtish.vyazanie.dto.Tag;
import ru.timtish.vyazanie.store.YmlUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("tag")
public class TagController {

    protected TreeMap<Long, Tag> tags = new TreeMap<>();
    protected long maxId = 1000;

    @PostMapping
    public Tag add(String title, String description, Long parentId) {
        Tag tag = new Tag();
        tag.setId(maxId++);
        tag.setTitle(title);
        tag.setDescription(description);
        Tag parent = tags.get(parentId);
        if (parent != null) parent.addChild(tag);
        tags.put(tag.getId(), tag);
        return tag;
    }

    @GetMapping("root")
    public List<Tag> rootList() {
        return tags.values().stream().filter(f -> f.getParentId() == null).collect(Collectors.toList());
    }

    @GetMapping
    public Tag get(Long id) {
        return tags.get(id);
    }

    @PostConstruct
    public void load() {
        TreeMap<Long, Tag> tags = new TreeMap<>();
        try (InputStream in = new FileInputStream("data/tags.yml")) {
            new Yaml().loadAll(new InputStreamReader(in))
                    .forEach(list -> ((List<Map<String, Object>>) list)
                            .forEach(obj -> mapTag(obj, tags)));
            if (this.tags != null) this.tags.clear();
            this.tags = tags;
            this.maxId = tags.keySet().stream().mapToLong(v -> v).max().orElse(1000);
            log.info("Load {} tags", tags.size());
        } catch (Exception e) {
            log.error("Failed load tags.yml", e);
        }
    }

    protected Tag mapTag(Map<String, Object> obj, Map<Long, Tag> tags) {
        try {
            final Tag tag = new Tag();
            tag.setId((Number) obj.get("id"));
            tag.setTitle((String) obj.get("title"));
            tag.setDescription((String) obj.get("description"));
            Collection<Map<String, Object>> childs = (Collection<Map<String, Object>>) obj.get("childs");
            if (childs != null) tag.setChilds(childs.stream().map(childObj -> {
                Tag child = mapTag(childObj, tags);
                child.setParentId(tag.getId());
                return child;
            }).collect(Collectors.toList()));
            tags.put(tag.getId(), tag);
            return tag;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed Tag obj: " + obj, e);
        }
    }

    @PreDestroy
    public void save() {
        if (!ObjectUtils.isEmpty(tags)) YmlUtil.save(serializeTagsTree(rootList()), "data/tags.yml");
    }

    protected List<Map<String, Object>> serializeTagsTree(Collection<Tag> list) {
        return list.stream().map(tag -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", tag.getId());
            if (tag.getTitle() != null) map.put("title", tag.getTitle());
            if (tag.getDescription() != null) map.put("description", tag.getDescription());
            if (tag.getChilds() != null) map.put("childs", serializeTagsTree(tag.getChilds()));
            return map;
        }).collect(Collectors.toList());
    }

}
