CREATE EXTENSION "uuid-ossp";

CREATE FUNCTION ASSERT(
	must_be_true boolean
) RETURNS boolean IMMUTABLE LANGUAGE plpgsql SECURITY INVOKER AS $$
	BEGIN
		IF NOT must_be_true THEN
			RAISE EXCEPTION 'Assertion failed!';
		END IF;
		RETURN must_be_true;
	END;
$$;