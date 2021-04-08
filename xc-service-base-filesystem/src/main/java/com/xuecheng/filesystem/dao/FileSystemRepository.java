package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;
import springfox.documentation.schema.Model;

/**
 * @Author zhn
 * @Date 2021/3/27 10:19
 * @Version 1.0
 */
public interface FileSystemRepository extends MongoRepository<FileSystem, String> {
}
