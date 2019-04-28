package com.etar.purifier.modules.file;


import entity.common.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import utils.ResultCode;

/**
 * 文件太大异常
 *
 * @author hzh
 * @date 2018/10/17
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String SIZE_ERROR = "Maximum upload size";

    /**
     * 处理上传异常
     *
     * @param t 异常
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Result> handleAll(Throwable t) {
        Result result = new Result();
        logger.error("=>" + t.getMessage());
        if (t.getMessage().contains(SIZE_ERROR)) {
            result.error(ResultCode.IMG_SIZE_ERROR);
        } else {
            result.error(ResultCode.IMG_SAVE_ERROR);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

}
