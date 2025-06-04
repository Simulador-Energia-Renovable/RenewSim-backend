USE renewsim;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

-- Inserta al usuario daniel@gmail.com con el hash de contraseña ya generado
INSERT INTO users (username, password)
VALUES ('daniel@gmail.com', '$2a$10$yCX5HLegGdaT6l5Pc.RPK.oeqc.qXCwwRPaOr1tQ2EvBEW3ebK4/G');




