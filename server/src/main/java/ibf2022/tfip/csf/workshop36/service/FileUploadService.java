package ibf2022.tfip.csf.workshop36.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ibf2022.tfip.csf.workshop36.model.Post;
import ibf2022.tfip.csf.workshop36.repository.FileUploadRepository;

@Service
public class FileUploadService {
    
    @Autowired
    private FileUploadRepository repo;

    public void upload(MultipartFile file, String title, String complain) throws SQLException, IOException {
        repo.upload(file, title, complain);
    }

    public Optional<Post> getPostById(Integer postId){
        return repo.getPostById(postId);
    }
}
