package ibf2022.tfip.csf.workshop36.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import ibf2022.tfip.csf.workshop36.model.Post;
import ibf2022.tfip.csf.workshop36.service.FileUploadService;
import ibf2022.tfip.csf.workshop36.service.S3Service;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Controller
public class FileUploadController {
    @Autowired
    private S3Service s3Svc;

    @Autowired
    private FileUploadService fSvc;

    private static final String BASE64_PREFIX = "data:image/png;base64,";

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> upload(
        @RequestPart MultipartFile file,
        @RequestPart String title,
        @RequestPart String complain
    ){
        String key = "";
        try{
            key = s3Svc.upload(file, title, complain);
            this.fSvc.upload(file, title, complain);
        }catch(IOException | SQLException ex){
            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body((ex.getMessage()));
        }
        JsonObject payload = Json.createObjectBuilder()
            .add("imageKey", key)
            .build();

        return ResponseEntity.ok(payload.toString());
    }

    @GetMapping(path ="/get-image/{postId}")
    public ResponseEntity<String> retrieveImage(@PathVariable Integer postId){
        Optional<Post> r = this.fSvc.getPostById(postId);
        Post p = r.get();

        String encodedString = Base64.getEncoder().encodeToString(p.getImage());

        JsonObject payload = Json.createObjectBuilder()
            .add("image", BASE64_PREFIX + encodedString)
            .build();

        return ResponseEntity.ok(payload.toString());
    }
}
