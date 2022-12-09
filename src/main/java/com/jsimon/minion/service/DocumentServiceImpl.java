package com.jsimon.minion.service;

import com.jsimon.minion.model.Document;
import io.minio.*;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Service
public class DocumentServiceImpl {

    @Autowired
    private MinioClient minio;

    @Value("${minio.bucket.name}")
    private String bucketName;

    public List<Document> getListObjects() {
        List<Document> objects = new ArrayList<>();
        try {
            Iterable<Result<Item>> result = minio.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(true)
                    .build());
            for (Result<Item> item : result) {
                objects.add(Document.builder()
                        .title(item.get().objectName())
                        .description(item.get().objectName())
                        .filename(item.get().objectName())
                        .size(item.get().size())
                        .url(getPreSignedUrl(item.get().objectName()))
                        .active(true)
                        .build());
            }
            return objects;
        } catch (Exception e) {
            log.error("Error en Lista", e);
        }
        return objects;
    }

    public InputStream getObject(String filename) {
        InputStream stream;
        try {
            stream = minio.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build());
        } catch (Exception e) {
            log.error("Ocurrió un error al obtener objetos de lista de minio: ", e);
            return null;
        }
        return stream;
    }

    public Document uploadFile(Document request) {
        try {
            minio.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(request.getFile().getOriginalFilename())
                    .stream(request.getFile().getInputStream(), request.getFile().getSize(), -1)
                    .build());
        } catch (Exception e) {
            log.error("Ocurrió un error al cargar el archivo:", e);
        }
        return Document.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .size(request.getFile().getSize())
                .url(getPreSignedUrl(request.getFile().getOriginalFilename()))
                .filename(request.getFile().getOriginalFilename())
                .active(true)
                .build();
    }

    private String getPreSignedUrl(String filename) {
        return "http://localhost:9090/file/".concat(filename);
    }

}
