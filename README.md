# TypeSafeSQL

##Introduction
The purpose of this Maven plugin is to automatically generate Java classes from (annotated) SQL files, providing the following benefits:
* No need to interact with JDBC directly.
* Forgetting a parameter to a SQL script is compile-time error.
* TypeSafeSQL will automatically close JDBC resources, such as [Connections](https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html), [PreparedStatements](https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html), [ResultSets](https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html).
* Results of SELECT queries return type-safe [Java 8 Streams](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html). For example, if your query returns only one BIGINT column, the result of execute() will be a [LongStream](https://docs.oracle.com/javase/8/docs/api/java/util/stream/LongStream.html). If your query returns two columns, one VARCHAR named "username" and one INTEGER named "age" the result will be a [Stream\<Result\>](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html) where Result is the following:
```
	public static final class Result {

		private final String username;
		private final int age;

		private Result(
			final String username,
			final int age
		) {
			this.username = username;
			this.age = age;
		}

		public final String getUsername() {
			return username;
		}

		public final int getAge() {
			return age;
		}

	}
```

##Example
You can create an annotated SQL file in your source folder such as ```src/main/java/com/example/project/LookupUser.sql```:
```
SELECT
	{out:VARCHAR:firstName},
	{out:VARCHAR:lastName},
	{out:INTEGER:age}
FROM
	user
WHERE
	username = {VARCHAR:username};
```
Then during the build process TypeSafeSQL will automatically generate ```target/generated-sources/sql/com/example/project/LookupUser.java```, which you can use within your application to execute the SQL query in a type-safe manor:
```
import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import com.example.project.LookupUser;

public class SomeClass {
	public void doSomething() {
		LookupUser
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withUsername("tester.mctesting")
			.execute()
			.forEach(result -> {
				System.out.println(result.getFirstName());
				System.out.println(result.getLastName());
				System.out.println(result.getAge());
			});
	}
}
```

##Installation
Add the TypeSafeSQL plugin to your ```pom.xml```:
```
<plugin>
	<groupId>com.github.ryanholdren.typesafesql</groupId>
	<artifactId>plugin</artifactId>
	<version>2016-01-09</version>
	<executions>
		<execution>
			<goals>
				<goal>process-sql</goal>
				<goal>process-test-sql</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```
Also add a dependency on the TypeSafeSQL's framework classes:
```
<dependency>
	<groupId>com.github.ryanholdren.typesafesql</groupId>
	<artifactId>framework</artifactId>
	<version>2016-01-09</version>
</dependency>
```
