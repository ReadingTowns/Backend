package kr.co.readingtown.member.client;

import kr.co.readingtown.member.dto.response.YoutubeSearchedData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "youtube-search-client",
        url = "https://www.googleapis.com/youtube/v3"
)
public interface YoutubeSearchClient {

    @GetMapping("/search")
    YoutubeSearchedData searchVideos(
            @RequestParam("part") String part,
            @RequestParam("q") String query,
            @RequestParam("type") String type,
            @RequestParam("maxResults") int maxResults,
            @RequestParam("regionCode") String regionCode,
            @RequestParam("key") String apiKey
    );
}
