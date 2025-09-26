package kr.ac.dankook.VettDiagnosisService.service;

import kr.ac.dankook.VettDiagnosisService.error.ErrorCode;
import kr.ac.dankook.VettDiagnosisService.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final GridFsTemplate gridFsTemplate;

    private String uploadFile(MultipartFile file) {
        try (var in = file.getInputStream()) {
            var contentType = Optional.of(file.getContentType()).orElse("application/octet-stream");
            var meta = new org.bson.Document("contentType", contentType);
            return gridFsTemplate
                    .store(in, file.getOriginalFilename(), contentType, meta)
                    .toHexString();
        } catch (Exception e) {
            log.error("Error during upload file. name={}, type={}, ex={}",
                    file.getOriginalFilename(), file.getContentType(), e.toString());
            throw new CustomException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    public List<String> uploadFilesAndGetIds(List<MultipartFile> files) {
        List<String> uploadedIds = new ArrayList<>();
        try {
            for (MultipartFile f : files) {
                String id = uploadFile(f);
                uploadedIds.add(id);
            }
            return uploadedIds;
        } catch (Exception e) {
            deleteFiles(uploadedIds.stream().map(ObjectId::new).toList());
            throw new CustomException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    public GridFsResource getFile(ObjectId id){
        var file = gridFsTemplate.findOne(getQueryById(id));
        return gridFsTemplate.getResource(file);
    }

    public void deleteFiles(List<ObjectId> ids){
        ids.forEach(i -> {
            try{
                gridFsTemplate.delete(getQueryById(i));
            }catch (Exception e){
                log.error("Delete file failed. id={}, ex={}", i.toHexString(), e.getMessage());
            }
        });
    }

    private Query getQueryById(ObjectId id){
        return new Query(Criteria.where("_id").is(id));
    }
}
