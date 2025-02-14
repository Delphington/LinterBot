package backend.academy.bot.command;

public enum UserState {
    WAITING_COMMAND, //нормальное состояние
    WAITING_URL,
    WAITING_TAGS,
    WAITING_FILTERS,
    PRE_END
}
