CREATE TABLE core_set (
                          id BIGSERIAL PRIMARY KEY,
                          set_name VARCHAR(255) NOT NULL,
                          qty INT NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          type VARCHAR(50),
                          faction VARCHAR(50),
                          cost INT NOT NULL ,
                          defense INT NOT NULL ,
                          role VARCHAR(50)
);

CREATE TABLE abilities (
                           id BIGSERIAL PRIMARY KEY,
                           card_id INT NOT NULL REFERENCES core_set(id),
                           type VARCHAR(50) NOT NULL,
                           value INT NOT NULL ,
                           condition VARCHAR(255)
);
