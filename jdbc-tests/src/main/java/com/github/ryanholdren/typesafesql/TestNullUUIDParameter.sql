CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

SELECT ASSERT({in:UUID:input}::UUID IS NULL);