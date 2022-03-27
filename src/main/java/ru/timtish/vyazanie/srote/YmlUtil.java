package ru.timtish.vyazanie.srote;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileWriter;
import java.util.Collection;

@Slf4j
public class YmlUtil {

    protected static final Representer REPRESENTER = new Representer() {
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            if (propertyValue == null) return null; // if value of property is null, ignore it.
            else return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        }
    };

    public static void save(Collection<?> values, String fileName) {
        if (values == null) return;
        java.io.File tmp = new java.io.File(fileName + ".tmp");
        tmp.getParentFile().mkdirs();
        try (FileWriter out = new FileWriter(tmp)) {
            final DumperOptions options = new DumperOptions();
            //options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
            final Yaml yaml = new Yaml(REPRESENTER, options);
            yaml.dump(values.iterator(), out);
            java.io.File dst = new java.io.File(fileName);
            java.io.File bkp = new java.io.File(tmp.getParent() + "/bkp/" + dst.getName() + "." + System.currentTimeMillis());
            bkp.getParentFile().mkdirs();
            dst.renameTo(bkp);
            tmp.renameTo(dst);
            log.info("Save {} {}", values.size(), fileName);
        } catch (Exception e) {
            log.error("Failed save " + fileName, e);
        }
    }
}
