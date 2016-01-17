CREATE FUNCTION ASSERT(
    in_assertion boolean
)
RETURNS boolean
IMMUTABLE
LANGUAGE plpgsql
SECURITY INVOKER
AS $$
    BEGIN
        IF NOT in_assertion THEN
            RAISE EXCEPTION 'Assertion failed!';
        END IF;

        RETURN in_assertion;
    END;
$$;