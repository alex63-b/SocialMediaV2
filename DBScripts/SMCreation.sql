CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL
    );

-- o prietenie (id1, id2) se salveaza o singura data, cu id1 < id2
CREATE TABLE IF NOT EXISTS friendships (
    id1 BIGINT REFERENCES users (id) ON DELETE CASCADE,
    id2 BIGINT REFERENCES users (id) ON DELETE CASCADE,
    friendshipDate TIMESTAMP,
    PRIMARY KEY (id1, id2),
    CHECK (id1 < id2)
    );

SELECT id,first_name,last_name FROM friendships INNER JOIN users ON id1=users.id OR id2=users.id WHERE (id1=2 OR id2=2) AND NOT id=2 ORDER BY id;