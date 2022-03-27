package ru.timtish.vyazanie.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;
import ru.timtish.vyazanie.dto.File;
import ru.timtish.vyazanie.dto.FileType;
import ru.timtish.vyazanie.srote.YmlUtil;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@RestController
public class FileController {

    protected TreeMap<Long, File> files;
    protected long maxId = 1000;

    public File add(String link) {
        File file = new File();
        file.setLink(link);
        return file;
    }

    public Long save(Long itemId, File file) {
        if (file.getId() == null) file.setId(maxId++); // set id
        else if (file.equals(files.get(file.getId()))) return file.getId(); // skip save if equals
        files.put(file.getId(), file);
        save();
        return file.getId();
    }

    @PostConstruct
    public void load() {
        TreeMap<Long, File> files = new TreeMap<>();
        try (InputStream in = getClass().getResourceAsStream("/data/files.yml")) {
            if (in == null) {
                log.warn("files.yml not found");
                return;
            }
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

    //@PreDestroy
    public void save() {
        if (files != null) YmlUtil.save(files.values(), "data/files.yml");
    }

}
