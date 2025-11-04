package kr.ac.dankook.VettAIRecordService.service;

import kr.ac.dankook.VettAIRecordService.error.ErrorCode;
import kr.ac.dankook.VettAIRecordService.error.exception.CustomException;
import kr.ac.dankook.VettAIRecordService.log.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final GridFsTemplate gridFsTemplate;

    public String uploadFile(MultipartFile file) {
        try (var in = file.getInputStream()) {
            var contentType = Optional.of(file.getContentType()).orElse("application/octet-stream");
            var meta = new org.bson.Document("contentType", contentType);
            return gridFsTemplate
                    .store(in, file.getOriginalFilename(), contentType, meta)
                    .toHexString();
        } catch (Exception e) {
            log.error("{}, CLASS={}, METHOD={}, FILENAME={}, TYPE={}, ERROR={}",
                    LogMessage.FILE_PROCESSING_ERROR, "StorageService", "uploadFile",
                    file.getOriginalFilename(), file.getContentType(), e.getMessage());
            throw new CustomException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    public GridFsResource getFile(ObjectId id){
        var file = gridFsTemplate.findOne(getQueryById(id));
        return gridFsTemplate.getResource(file);
    }

    public void deleteFile(ObjectId id){
        try{
            gridFsTemplate.delete(getQueryById(id));
        }catch (Exception e){
            log.error("{}, CLASS={}, METHOD={}, ID={}, ERROR={}",
                    LogMessage.FILE_PROCESSING_ERROR, "StorageService", "deleteFile",
                    id.toHexString(), e.getMessage());
            throw new CustomException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    private Query getQueryById(ObjectId id){
        return new Query(Criteria.where("_id").is(id));
    }
}
