package kr.co.readingtown.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record YoutubeSearchedData(
        List<Item> items
) {
    public record Item(
            Id id,
            Snippet snippet
    ) {}

    public record Id(
            String videoId
    ) {}

    public record Snippet(
            String title,
            Thumbnails thumbnails
    ) {}

    public record Thumbnails(
            @JsonProperty("default")
            ThumbnailInfo defaultThumbnail
    ) {}

    public record ThumbnailInfo(
            String url
    ) {}
}
