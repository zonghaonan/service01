package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @Author zhn
 * @Date 2021/4/2 16:14
 * @Version 1.0
 */
@Service
public class MediaUploadService {
    @Autowired
    MediaFileRepository mediaFileRepository;
    @Value("${xc-service-manage-media.upload-location}")
    String upload_location;
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    public  String routingkey_media_video;
    @Autowired
    RabbitTemplate rabbitTemplate;
    //得到文件所属目录路径
    private String getFileFolderPath(String fileMd5){
        return upload_location+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/";
    }
    //得到文件路径
    private String getFilePath(String fileMd5,String fileEx) {
        return upload_location+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+"."+fileEx;
    }
    //得到块文件所属目录路径
    private String getChunkFileFolderPath(String fileMd5) {
        return upload_location+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/chunk/";
    }
    /**
     *  根据文件md5得到文件路径
     *  规则：
     *  一级目录：md5的第一个字符
     *  二级目录：md5的第二个字符
     *  三级目录：md5
     *  文件名：md5+文件扩展名
     *  @param  fileMd5 文件md5值
     *  @param  fileExt 文件扩展名
     *  @return  文件路径
     */
    //文件上传前注册，检查文件是否存在
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //检查文件在磁盘上是否存在
        String fileFolderPath = getFileFolderPath(fileMd5);
        String filePath = getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        boolean exists = file.exists();
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        if(exists&&optional.isPresent()){
            //文件已存在
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //文件不存在时，检查文件所在目录是否存在，不存在则创建
        File fileFolder = new File(fileFolderPath);
        if(!fileFolder.exists()){
            fileFolder.mkdirs();
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //分块检查
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Integer chunkSize) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFile = new File(chunkFileFolderPath+chunk);
        if (chunkFile.exists()){
            //块文件存在
            return new CheckChunkResult(CommonCode.SUCCESS,true);
        }
        return new CheckChunkResult(CommonCode.SUCCESS,false);
    }

    //上传分块
    public ResponseResult uploadChunk(MultipartFile file, String fileMd5, Integer chunk) {
        //检查分块目录是否存在，不存在则创建
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件路径
        String chunkFilePath=chunkFileFolderPath+chunk;
        File chunkFileFolder = new File(chunkFileFolderPath);
        if(!chunkFileFolder.exists()){
            chunkFileFolder.mkdirs();
        }
        //上传文件的输入流
        InputStream inputStream=null;
        FileOutputStream fileOutputStream=null;
        try {
            inputStream=file.getInputStream();
            fileOutputStream=new FileOutputStream(new File(chunkFilePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //合并文件
    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt){
        //1、合并所有分块
        //得到分块文件的所属目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        //分块文件列表
        File[] files = chunkFileFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        //创建一个合并文件
        String filePath = getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);
        //执行合并
        mergeFile=mergeFile(fileList,mergeFile);
        boolean checkFileMd5 = checkFileMd5(mergeFile, fileMd5);
        if(!checkFileMd5){
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //保存到mongodb
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileOriginalName(fileName);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        //文件相对路径
        String fileRelativePath=fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/";
        mediaFile.setFilePath(fileRelativePath);
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        sendProcessVideoMsg(mediaFile.getFileId());
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //发送视频处理消息
    public ResponseResult sendProcessVideoMsg(String mediaId){
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //构建消息内容
        Map<String, String> map = new HashMap<>();
        map.put("mediaId",mediaId);
        String jsonString = JSON.toJSONString(map);
        //向MQ发送消息
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey_media_video,jsonString);
        } catch (AmqpException e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //校验文件
    private boolean checkFileMd5(File mergeFile,String md5){
        try {
            FileInputStream inputStream = new FileInputStream(mergeFile);
            String md5Hex = DigestUtils.md5Hex(inputStream);
            if(md5.equals(md5Hex)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    //执行合并
    private File mergeFile(List<File> fileList,File mergeFile){
        try {
            if(mergeFile.exists()){
                mergeFile.delete();
            }else {
                mergeFile.createNewFile();
            }
            //对块文件进行排序
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                        return 1;
                    }
                    return -1;
                }
            });
            //创建一个写对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
            byte[] bytes = new byte[1024];
            for (File chunkFile : fileList) {
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
                int len=-1;
                while ((len=raf_read.read(bytes))!=-1){
                    raf_write.write(bytes,0,len);
                }
                raf_read.close();
            }
            raf_write.close();
            return mergeFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
