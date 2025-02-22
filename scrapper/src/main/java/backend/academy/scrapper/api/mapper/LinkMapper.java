package backend.academy.scrapper.api.mapper;

import backend.academy.scrapper.api.dto.request.AddLinkRequest;
import backend.academy.scrapper.api.dto.response.LinkResponse;
import org.springframework.stereotype.Component;

//@Mapper
@Component
public class LinkMapper {

    public LinkResponse AddLinkRequestToLinkResponse(AddLinkRequest linkRequest, Long id){
        return new LinkResponse(
            id,
            linkRequest.link(),
            linkRequest.tags(),
            linkRequest.filters()
        );
    }
}
