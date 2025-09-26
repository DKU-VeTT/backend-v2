package kr.ac.dankook.VettDiagnosisService.service;

import kr.ac.dankook.VettDiagnosisService.error.ErrorCode;
import kr.ac.dankook.VettDiagnosisService.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final GridFsTemplate gridFsTemplate;

    private ObjectId uploadFile(MultipartFile file) {
        var meta = new Document("contentType", file.getContentType());
        try (var in = file.getInputStream()){
            return gridFsTemplate.store(in,file.getOriginalFilename(),file.getContentType(),meta);
        }catch (IOException e){
            throw new CustomException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }
    public List<String> uploadFilesAndGetIds(List<MultipartFile> file){
        return file.stream().map(item -> {
            ObjectId obi = uploadFile(item);
            return obi.toHexString();
        }).toList();
    }

    public GridFsResource getFile(ObjectId id){
        var file = gridFsTemplate.findOne(getQueryById(id));
        return gridFsTemplate.getResource(file);
    }

    public void deleteFile(ObjectId id){
        gridFsTemplate.delete(getQueryById(id));
    }

    private Query getQueryById(ObjectId id){
        return new Query(Criteria.where("_id").is(id));
    }
}
