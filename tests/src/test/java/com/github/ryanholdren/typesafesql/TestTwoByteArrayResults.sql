SELECT
  decode('DEADBEEF', 'hex') AS {out:VARBINARY:firstOutput},
  decode('4321FEED1234', 'hex') AS {out:VARBINARY:secondOutput};