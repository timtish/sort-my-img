package ru.timtish.vyazanie.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;
import ru.timtish.vyazanie.dto.Tag;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@RestController("tag")
public class TagController {

    protected TreeMap<Long, Tag> tags;
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

    @GetMapping
    public List<Tag> rootList() {
        return tags.values().stream().filter(f -> f.getParentId() == null).collect(Collectors.toList());
    }

    public Tag get(Long id) {
        return tags.get(id);
    }

    @PostConstruct
    public void load() {
        TreeMap<Long, Tag> tags = new TreeMap<>();
        try (InputStream in = getClass().getResourceAsStream("/data/tags.yml")) {
            if (in == null) {
                log.warn("tags.yml not found");
                return;
            }
            new Yaml().loadAll(new InputStreamReader(in))
                    .forEach(list -> ((List<Map<String, Object>>) list)
                            .forEach(obj -> mapTag(obj, tags)));
            if (this.tags != null) this.tags.clear();
            this.tags = tags;
            this.maxId = tags.keySet().stream().mapToLong(v -> v).max().orElse(1000);
            log.info("Load {} Tags", tags.size());
        } catch (Exception e) {
            log.error("Failed load Tags.yml", e);
        }
    }

    protected Tag mapTag(Map<String, Object> obj, Map<Long, Tag> tags) {
        try {
            final Tag tag = new Tag();
            tag.setId((Number) obj.get("id"));
            tag.setTitle((String) obj.get("link"));
            tag.setDescription((String) obj.get("path"));
            Collection<Map<String, Object>> childs = (Collection<Map<String, Object>>) obj.get("child");
            if (childs != null) tag.setChilds(childs.stream().map(f -> {
                Tag child = mapTag(f, tags);
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
        if (tags == null) return;
        File f = new File("data/tags.yml.tmp");
        try (FileWriter out = new FileWriter(f)) {
            new Yaml().dump(tags, out);
            File dst = new File("data/tags.yml");
            dst.renameTo(new File("data/bkp/tags.yml" + System.currentTimeMillis()));
            f.renameTo(dst);
            log.info("Save {} Tags", tags.size());
        } catch (Exception e) {
            log.error("Failed save Tags.yml", e);
        }
    }

}
