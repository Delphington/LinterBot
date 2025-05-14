package backend.academy.bot.state;

public enum UserState {
    WAITING_COMMAND, // нормальное состояние
    WAITING_URL,
    WAITING_TAGS,
    WAITING_FILTERS
}
