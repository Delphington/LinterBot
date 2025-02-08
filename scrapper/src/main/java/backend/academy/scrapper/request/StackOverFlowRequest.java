package backend.academy.scrapper.request;

public record StackOverFlowRequest (
    Long number,        // ID вопроса
    String order,      // "desc" или "asc"
    String sort,       // "activity", "votes" и т.д.
    String site,       // "stackoverflow"
    String filter      // "withbody" для получения last_edit_date
){
}
