CREATE TABLE Users (
                         user_id    SERIAL      PRIMARY KEY,
                         username      VARCHAR     NOT NULL UNIQUE ,
                         password       VARCHAR     NOT NULL
);

CREATE TABLE Models (
                          model_id      SERIAL      PRIMARY KEY,
                          model_name      VARCHAR     NOT NULL Unique,
                          username         VARCHAR       NOT NULL,
                          FOREIGN KEY (username)
                              REFERENCES Users (username)
);

CREATE TABLE Attributes (
                       attribute_id      SERIAL      PRIMARY KEY,
                       model_id      INTEGER     NOT NULL,
                       linesOfCode         INTEGER       NOT NULL,
                       linesOfCodeNoBlanks         INTEGER       NOT NULL,
                       numberOfFields         INTEGER       NOT NULL,
                       numberOfMethods         INTEGER       NOT NULL,
                       averageLinesPerMethod         INTEGER       NOT NULL,
                       maxCyclomaticComplexity         INTEGER       NOT NULL,
                       inheritanceDepth         INTEGER       NOT NULL,
                       numberOfAssociations         INTEGER       NOT NULL,
                       numberOfImports         INTEGER       NOT NULL,
                       FOREIGN KEY (model_id)
                           REFERENCES Models (model_id)
);