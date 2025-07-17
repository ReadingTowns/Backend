package kr.readingtown.backend.global.exception;

import kr.readingtown.backend.global.exception.httpError.HttpErrorCode;
import kr.readingtown.backend.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleUnknownException(final Exception e) {

        log.error("[INTERNAL_SERVER_ERROR]", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CommonResponse<>(HttpErrorCode.INTERNAL_SERVER_ERROR));
    }
}
