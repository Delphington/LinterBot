CREATE TABLE IF NOT EXISTS filter_list (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    tg_chat_id BIGINT REFERENCES tg_chats(id) ON DELETE CASCADE,
    filter TEXT NOT NULL
);
