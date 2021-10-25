package com.springboot.upload.controller;

import com.springboot.upload.config.CustomMultipartResolver;
import com.springboot.upload.entity.DurationEntity;
import com.springboot.upload.entity.ProgressEntity;
import com.springboot.upload.model.UploadModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UploadController {


    @PostMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(MultipartFile file, HttpServletRequest request){
        String fileName = request.getHeader("X-Upload-File");
        Map<String, Object> result = new HashMap<>();

        for (var uploadModel : CustomMultipartResolver.uploadModelList) {
            if(uploadModel.getId().indexOf(fileName) > -1){
                result.put("msg", "File with same name is already uploading");
                return result;
            }
        }

        if(CustomMultipartResolver.uploadModelList.size() >= 100){
            result.put("msg", "Max Number of Files reached");
            return result;
        }
        if (file != null && !file.isEmpty()){
            try {
                file.transferTo(new File("uploaded/"+fileName));
                result.put("code", 200);
                result.put("msg", "success");
            } catch (IOException e) {
                result.put("code", -1);
                result.put("msg", "Error！");
                e.printStackTrace();
            }
        } else {
            result.put("code", -1);
            result.put("msg", "No File Found!");
        }
        return result;
    }

    @RequestMapping("upload/progress")
    @ResponseBody
    public ProgressEntity getUploadProgress(HttpServletRequest request){
        return (ProgressEntity) request.getSession().getAttribute("uploadStatus");
    }

    @RequestMapping("upload/duration")
    @ResponseBody
    public DurationEntity getDuration(HttpServletRequest request){
        return (DurationEntity) request.getSession().getAttribute("durationStatus");
    }

}
