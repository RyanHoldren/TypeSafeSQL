  SELECT decode('DEADBEEF', 'hex') AS {out:VARBINARY:output}
UNION ALL
  SELECT decode('4321FEED1234', 'hex')