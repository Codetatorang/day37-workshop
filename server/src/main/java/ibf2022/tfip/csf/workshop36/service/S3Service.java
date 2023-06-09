package ibf2022.tfip.csf.workshop36.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    @Autowired
    private AmazonS3 s3Client;

    @Value("${DO_STORAGE_BUCKETNAME}")
    private String bucketName;

    public String upload(MultipartFile file, String title, String complain)throws IOException{
        Map<String,String> userData = new HashMap<>();
        userData.put("name", "vince");
        userData.put("uploadDateTIme", LocalDateTime.now().toString());
        userData.put("originalFilename", file.getOriginalFilename());
        userData.put("title", title);
        userData.put("complain", complain);

        //!TODO loaded content includes the base64 file
        // System.out.printf("file:%s", file.getContentType());
        System.out.println("before: >>>>>>>>>>>>>" + file.getContentType());
        
        //experimental
        // String testString = file.getContentType();
        // testString = testString.replace(";base64", "");
        
        
        ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType(testString);
        // System.out.println("after: >>>>>>>>>>>>>" + testString);
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setUserMetadata(userData);


        String key = UUID.randomUUID().toString().substring(0,  8);
        StringTokenizer tk = new StringTokenizer(file.getOriginalFilename(), "." );
        int count = 0;
        String filenameExt = "";

        while(tk.hasMoreTokens()){
            if(count == 1){
                filenameExt = tk.nextToken();
            }else{
                filenameExt = tk.nextToken();
                count++;
            }
        }

        if(filenameExt.equals("blob")){
            filenameExt = filenameExt + ".png";
        }
        PutObjectRequest putRequest  = new PutObjectRequest(bucketName, "myObject%s.%s".formatted(key, filenameExt), file.getInputStream(), metadata);
        putRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject(putRequest);

        return "myObject%s.%s".formatted(key, filenameExt);

    }
}
