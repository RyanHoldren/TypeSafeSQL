{implements:TwoOutputs}
SELECT
	{in:INTEGER:firstInput} AS {out:INTEGER:firstOutput},
	{in:VARCHAR:secondInput} AS {out:VARCHAR:secondOutput}
UNION ALL SELECT
	{in:INTEGER:thirdInput},
	{in:VARCHAR:fourthInput};