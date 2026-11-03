CREATE USER identity_user WITH PASSWORD 'identity_password';
CREATE DATABASE identity_db OWNER identity_user;
GRANT ALL PRIVILEGES ON DATABASE identity_db TO identity_user;

CREATE USER word_user WITH PASSWORD 'word_password';
CREATE DATABASE word_db OWNER word_user;
GRANT ALL PRIVILEGES ON DATABASE word_db TO word_user;

CREATE USER notification_user WITH PASSWORD 'notification_password';
CREATE DATABASE notification_db OWNER notification_user;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO notification_user;