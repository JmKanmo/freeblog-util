package com.service.core.error.handler;

import com.service.core.error.dto.ExceptionDto;
import com.service.util.ConstUtil;
import com.service.util.FreeBlogUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionDto> fileSizeLimitExceededHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String errorMsg = FreeBlogUtil.getErrorMessage(exception);
        model.addAttribute("error", errorMsg);
        if (errorMsg == ConstUtil.UNDEFINED_ERROR) {
            log.error("[freeblog-fileSizeLimitExceededHandler] MaxUploadSizeExceededException occurred ", exception);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionDto.builder().statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .message(String.format("Exception: %s", errorMsg))
                        .build());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> httpRequestMethodNotSupportedHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        String errorMsg = FreeBlogUtil.getErrorMessage(exception);
        if (errorMsg == ConstUtil.UNDEFINED_ERROR) {
            log.error("[freeblog-httpRequestMethodNotSupportedHandler] HttpRequestMethodNotSupportedException occurred ", exception);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionDto.builder().statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .message(String.format("Exception: %s", errorMsg))
                        .build());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionDto> exceptionHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String errorMsg = FreeBlogUtil.getErrorMessage(exception);
        model.addAttribute("error", errorMsg);
        if (errorMsg == ConstUtil.UNDEFINED_ERROR) {
            log.error("[freeblog-exceptionHandler] exception occurred ", exception);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionDto.builder().statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .message(String.format("Exception: %s", errorMsg))
                        .build());
    }
    // TODO
}
