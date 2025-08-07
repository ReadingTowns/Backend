package kr.co.readingtown.common.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogUtil {

    public void printSystemLog(LogInfo info, LogLevel level) {

        StringBuilder sb = new StringBuilder();

        // 1. 요청 메서드 타입
        sb.append("METHOD=").append(info.method()).append(" | ");

        // 2.요청 URI
        sb.append("REQUEST_URI=").append(info.uri()).append(" | ");

        // 3. 오류 메시지
        sb.append("MESSAGE=").append(info.message()).append(" | ");

        // 4. 오류 발생 위치
        sb.append("LOCATION=").append(info.location());

        // 5. 로그 출력
        print(level, sb.toString());
    }

    private void print(final LogLevel level, final String logDetail) {
        switch (level) {
            case TRACE -> log.trace(logDetail);
            case DEBUG -> log.debug(logDetail);
            case WARN -> log.warn(logDetail);
            case ERROR -> log.error(logDetail);
            default -> log.info(logDetail);
        }
    }
}
