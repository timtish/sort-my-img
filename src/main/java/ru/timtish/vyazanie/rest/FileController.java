package ru.timtish.vyazanie.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;
import ru.timtish.vyazanie.dto.File;
import ru.timtish.vyazanie.dto.FileType;
import ru.timtish.vyazanie.store.YmlUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    protected TreeMap<Long, File> files = new TreeMap<>();
    protected long maxId = 1000;

    @GetMapping("init")
    public File add(String link) {
        File file = new File();
        file.setLink(link);
        return file;
    }

    @PostMapping
    public Long save(Long itemId, File file) {
        if (file.getId() == null) file.setId(maxId++); // set id
        else if (file.equals(files.get(file.getId()))) return file.getId(); // skip save if equals
        files.put(file.getId(), file);
        save();
        return file.getId();
    }

    @GetMapping("find")
    public Collection<File> find(String name) {
        return files.values();
    }

    @PostConstruct
    public void load() {
        TreeMap<Long, File> files = new TreeMap<>();
        try (InputStream in = new FileInputStream("data/files.yml")) {
            new Yaml().loadAll(new InputStreamReader(in))
            .forEach(list -> {
                for (Map<String, Object> obj : (Collection<Map<String, Object>>) list) try {
                    File file = new File();
                    file.setId((Long) obj.get("id"));
                    file.setLink((String) obj.get("link"));
                    file.setPath((String) obj.get("path"));
                    file.setType(FileType.valueOf((String) obj.get("type")));
                    files.put(file.getId(), file);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Failed file obj: " + obj, e);
                }
            });
            if (this.files != null) this.files.clear();
            this.files = files;
            log.info("Load {} files", files.size());
        } catch (Exception e) {
            log.error("Failed load files.yml", e);
        }
    }

    @PreDestroy
    public void save() {
        if (!ObjectUtils.isEmpty(files)) YmlUtil.save(files.values(), "data/files.yml");
    }

}
