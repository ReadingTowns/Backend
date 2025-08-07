package kr.co.readingtown.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import kr.co.readingtown.common.exception.httpError.HttpErrorCode;
import kr.co.readingtown.common.log.LogInfo;
import kr.co.readingtown.common.log.LogLevel;
import kr.co.readingtown.common.log.LogUtil;
import kr.co.readingtown.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LogUtil logUtil;

    // Custom 예외 발생 시
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<CommonResponse<Object>> handleBadRequestException(CustomException e, HttpServletRequest request){

        logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.WARN);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResponse<>(e.getErrorCodeEnum()));
    }

    // @Valid 검증 실패 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Object>> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {

        logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.WARN);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResponse<>(HttpErrorCode.VALIDATION_FAILED));
    }

    // 허용하지 않는 HTTP 메서드 요청 시
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.WARN);

        return ResponseEntity
                .status(METHOD_NOT_ALLOWED)
                .body(new CommonResponse<>(HttpErrorCode.METHOD_NOT_ALLOWED));
    }

    // 필수 요청 파라미터 누락 시
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonResponse<Object>> handleMissingParam(MissingServletRequestParameterException e, HttpServletRequest request) {

        logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.WARN);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResponse<>(HttpErrorCode.MISSING_PARAMETER));
    }

    // 요청 바디가 JSON 등으로 파싱 실패 시
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Object>> handleMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {

        logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.WARN);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResponse<>(HttpErrorCode.MESSAGE_NOT_READABLE));
    }

    // 파라미터 유효성 제약 조건 위반 시
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Object>> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {

        logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.WARN);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResponse<>(HttpErrorCode.CONSTRAINT_VIOLATION));
    }

    // 권한이 없는 요청 시
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Object>> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {

        logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.WARN);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(HttpErrorCode.UNAUTHORIZED));
    }

    // DB 제약조건 위반 등 충돌 시
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {

        logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.ERROR);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new CommonResponse<>(HttpErrorCode.DATA_CONFLICT));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<HttpErrorCode>> handleUnknownException(final Exception e, HttpServletRequest request) {

        // no static resoure
        if (e.getClass().getName().equals("org.springframework.web.servlet.resource.NoResourceFoundException")) {

            logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.WARN);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CommonResponse<>(HttpErrorCode.NOT_FOUND));
        }

        logUtil.printSystemLog(LogInfo.from(e, request), LogLevel.ERROR);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CommonResponse<>(e));
    }
}
