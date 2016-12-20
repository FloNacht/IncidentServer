package com.storage;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * <h1>Storage Service Interface</h1> 
 * This interface defines all file storing (up- & download) related methods.
 * 
 * @author Florian Nachtigall
 * @version 1.0
 * @since 2016-12-20
 */
@Component
public interface StorageService {

    void init();

    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
    
}