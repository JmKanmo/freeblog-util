package com.service.util;

import com.service.core.error.model.BlogServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

public class FreeBlogUtil {
    public static boolean isEmpty(String msg) {
        return msg == null || msg.isEmpty();
    }

    public static String getErrorMessage(Exception exception) {
        if (exception instanceof BlogServiceException) {
            return exception.getMessage();
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            return "세션이 만료되어 작업에 실패하였습니다.";
        } else if (exception instanceof DataAccessException) {
            return "데이터베이스 쿼리 수행에 실패하였습니다.";
        }
        return ConstUtil.UNDEFINED_ERROR;
    }

}
