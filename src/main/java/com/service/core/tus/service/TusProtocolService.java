package com.service.core.tus.service;

import com.service.config.tus.TusProtocolConfig;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.VideoManageException;
import com.service.util.ConstUtil;
import com.service.util.FreeBlogUtil;
import com.service.util.redis.service.RedisService;
import com.service.util.tus.TusProtocolUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.upload.UploadInfo;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TusProtocolService {
    private final TusProtocolConfig tusProtocolConfig;
    private final TusFileUploadService tusFileUploadService;
    private final RedisService redisService;

    public String tusUpload(HttpServletRequest request, HttpServletResponse response) {
        try {
            // check video-token
            if (!checkVideoToken(request.getHeader("Video-Token"))) {
                throw new VideoManageException(ServiceExceptionMessage.NOT_VERIFIED_VIDEO_TOKEN.getMessage());
            }

            // 업로드
            tusFileUploadService.process(request, response);

            // 현재 업로드 정보
            UploadInfo uploadInfo = tusFileUploadService.getUploadInfo(request.getRequestURI());

            // 완료 된 경우 파일 저장
            if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
                // 파일 저장
                Map<String, String> videoMetaData = uploadInfo.getMetadata();
                String result = createFile(tusFileUploadService.getUploadedBytes(request.getRequestURI()), videoMetaData);
                log.info("[TusProtocolService:tusUpload] file created, save path:" + result);
                // 임시 파일 삭제
                tusFileUploadService.deleteUpload(request.getRequestURI());
                return "success";
            }
        } catch (Exception e) {
            log.error("[TusProtocolService:tusUpload] exception was occurred. message=" + e);
            return "error";
        }
        return "ok";
    }

    private boolean checkVideoToken(String videoToken) {
        boolean isVerified = !FreeBlogUtil.isEmpty(videoToken) && redisService.hasKey(videoToken) && redisService.getValue(videoToken).equals(videoToken);

        if (isVerified) {
            // 토큰이 유효한 경우, video token 유효기간 갱신: 1minute
            redisService.setValueWithExpiration(videoToken, videoToken, 1);
        } else {
            if (tusProtocolConfig.getVideoTokenRecreate()) {
                redisService.setValueWithExpiration(videoToken, videoToken, 1);
                isVerified = true;
            }
        }
        return isVerified;
    }

    // 브라우저 측에서 넘겨받은 경로에 파일 저장
    private String createFile(InputStream is, Map<String, String> videoMetaData) throws Exception {
        String savePath = createFilePath(videoMetaData);
        // 파일의 경로를 Path 객체로 변환
        Path path = Paths.get(savePath);

        // 파일의 상위 디렉터리를 확인하고 없으면 생성
        if (!Files.exists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                log.error("[TusProtocolService:createFile] file save path error", e);
            }
        }

        try {
            // 파일이 존재하지 않을 시에, 파일 생성
            if (!Files.exists(path)) {
                Files.createFile(path);
                FileUtils.copyInputStreamToFile(is, new File(savePath));
            }
        } catch (IOException e) {
            log.error("[TusProtocolService:createFile] create & copy file save path error", e);
        }
        return savePath;
    }

    private String createFilePath(Map<String, String> videoMetaData) {
        try {
            String protocol = videoMetaData.get("protocol");
            String address = videoMetaData.get("address");
            String port = videoMetaData.get("port");
            String directory = videoMetaData.get("directory");
            String videoToken = videoMetaData.get("videoToken");
            String uploadType = videoMetaData.get("uploadType");
            String uploadHash = videoMetaData.get("uploadHash");
            String date = videoMetaData.get("date");
            String uploadKey = videoMetaData.get("uploadKey");
            String vodName = videoMetaData.get("vodName");
            String fileName = videoMetaData.get("filename");
            String fileType = videoMetaData.get("filetype");
            /**
             * [File Path 생성 시에, 경로]
             * ${directory}/${uploadType}/${uploadHash}/${uploadKey}/${date}/${vodName}
             * ex) /home/junmokang/jmservice/jmblog/images/520904174/1702718717479/2023-12-23/45ca4177-1278-4787-9911-b5580c7d0900.jpg
             */
            return String.format(TusProtocolUtil.VIDEO_FILE_PATH, directory, uploadType, uploadHash, uploadKey, date, vodName);
        } catch (Exception e) {
            return ConstUtil.UNDEFINED;
        }
    }
}
