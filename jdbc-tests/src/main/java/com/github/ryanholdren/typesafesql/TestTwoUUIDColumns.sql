CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

SELECT
	'0b5a63d6-f5ab-45a4-a6f1-1c37913ceb39'::UUID AS {out:UUID:firstOutput},
	'91bb1220-8136-4fea-89a9-c973f0ba2ed6'::UUID AS {out:UUID:secondOutput};