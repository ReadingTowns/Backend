package kr.co.readingtown.member.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.readingtown.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoLocationService implements LocationService {

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String resolveTown(BigDecimal longitude, BigDecimal latitude) {
        if (latitude == null || longitude == null){
            throw new MemberException.TownResolvedFailed();
        }

        // 1) 행정구역 코드 API (권장)
        String town = coord2region(longitude, latitude); // x=lon, y=lat
        if (!town.isBlank()) return town;

        // 2) 주소 API로 폴백
        town = coord2address(longitude, latitude);
        if (!town.isBlank()) return town;

        // 둘 다 실패 → 예외
        throw new MemberException.TownResolvedFailed();
    }

    private String coord2region(BigDecimal lon, BigDecimal lat) {
        try {
            String url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x={x}&y={y}";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            ResponseEntity<String> resp = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class,
                    lon.toPlainString(), lat.toPlainString()
            );

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) return "";

            JsonNode docs = objectMapper.readTree(resp.getBody()).path("documents");
            if (!docs.isArray() || docs.size() == 0) return "";

            // H(행정동) 우선, 없으면 첫 번째
            JsonNode selected = null;
            for (JsonNode d : docs) {
                if ("H".equalsIgnoreCase(d.path("region_type").asText())) {
                    selected = d; break;
                }
            }
            if (selected == null) selected = docs.get(0);

            String depth1 = selected.path("region_1depth_name").asText("");
            String depth2 = selected.path("region_2depth_name").asText("");
            String depth3 = selected.path("region_3depth_name").asText("");
            
            // 시 구 동 조합
            StringBuilder fullAddress = new StringBuilder();
            if (!depth1.isBlank()) fullAddress.append(depth1);
            if (!depth2.isBlank()) {
                if (fullAddress.length() > 0) fullAddress.append(" ");
                fullAddress.append(depth2);
            }
            if (!depth3.isBlank()) {
                if (fullAddress.length() > 0) fullAddress.append(" ");
                fullAddress.append(depth3);
            }
            
            return fullAddress.toString();
        } catch (Exception e) {
            log.warn("[KAKAO coord2region] request failed: {}", e.toString());
            return "";
        }
    }

    private String coord2address(BigDecimal lon, BigDecimal lat) {
        try {
            String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x={x}&y={y}";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            ResponseEntity<String> resp = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class,
                    lon.toPlainString(), lat.toPlainString()
            );
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) return "";

            JsonNode docs = objectMapper.readTree(resp.getBody()).path("documents");
            if (!docs.isArray() || docs.size() == 0) return "";

            JsonNode addr = docs.get(0).path("address");
            String depth1 = addr.path("region_1depth_name").asText("");
            String depth2 = addr.path("region_2depth_name").asText("");
            String depth3 = addr.path("region_3depth_name").asText("");
            
            // 시 구 동 조합
            StringBuilder fullAddress = new StringBuilder();
            if (!depth1.isBlank()) fullAddress.append(depth1);
            if (!depth2.isBlank()) {
                if (fullAddress.length() > 0) fullAddress.append(" ");
                fullAddress.append(depth2);
            }
            if (!depth3.isBlank()) {
                if (fullAddress.length() > 0) fullAddress.append(" ");
                fullAddress.append(depth3);
            }
            
            return fullAddress.toString();
        } catch (Exception e) {
            log.warn("[KAKAO coord2address] request failed: {}", e.toString());
            return "";
        }
    }
}