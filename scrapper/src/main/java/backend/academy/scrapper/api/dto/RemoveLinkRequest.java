package backend.academy.scrapper.api.dto;


import java.net.URI;

public record RemoveLinkRequest(
    URI link
) {
}
