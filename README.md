# TypeSafeSQL

##Introduction
The purpose of this simple Maven plugin is to create Java classes for each of your SQL files for the purposes of type-safety.

##Example
If you have a SQL file in your source folder such as ```src/main/java/com/example/project/FindNameOfUser.sql```:
```
SELECT
  name
FROM
  user
WHERE
  username = {VARCHAR:username};
```

TypeSafeSQL will automatically create ```target/generated-sources/sql/com/example/project/FindNameOfUser.java``` as part of the build process, which can be used like so:

```
import com.example.project.FindNameOfUser;

public class SomeClass {
  public void doSomething() {
    try (
      final Connection connection = ...
    ) {
      FindNameOfUser
  			.using(connection)
  			.withUsername("tester.mctesting")
  			.execute(results -> {
  				if (results.next()) {
  				  return results.getString("name");
  				} else {
  				  return null;
  				}
  			});
    }
  }
}
```
