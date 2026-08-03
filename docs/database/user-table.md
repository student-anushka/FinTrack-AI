User Table
Column	Type	Notes
id	UUID	Primary Key
first_name	VARCHAR(100)	Required
last_name	VARCHAR(100)	Required
email	VARCHAR(255)	Unique
password	VARCHAR(255)	BCrypt hash
role	ENUM	USER / ADMIN
is_verified	BOOLEAN	Email verification
created_at	TIMESTAMP	Audit
updated_at	TIMESTAMP	Audit