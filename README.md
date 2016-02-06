Add the TypeSafeSQL plugin to your ```pom.xml```:
```
<plugin>
	<groupId>com.github.ryanholdren.typesafesql</groupId>
	<artifactId>plugin</artifactId>
	<version>2016-01-22</version>
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
	<version>2016-01-22</version>
</dependency>
```
Create an annotated ```.sql``` file in your source folder. For example, ```src/main/java/com/example/project/LookupUser.sql```:
```
SELECT
	{out:VARCHAR:firstName},
	{out:VARCHAR:lastName},
	{out:INTEGER:age}
FROM
	user
WHERE
	username = {in:VARCHAR:username};
```
The TypeSafeSQL plugin will find any ```.sql``` files in your sources directory(ies) at during the build process and extract any columns and/or parameters it finds. In the ```LookupUser.sql``` example above, the plugin will determine that the query requires one parameter (a ```String``` named ```username```) and returns a result containing three columns:
* A ```String``` named ```firstName```
* A ```String``` named ```lastName```
* An ```int``` named ```age```
These annotations are removed during the build process, and don't actually get sent to the database. The query above, for example, would be executed as the following:
```
SELECT
	firstName,
	lastName,
	age
FROM
	user
WHERE
	username = ?;
```
Once it knows the names and types of every column and parameter, TypeSafeSQL will automatically generate a class for each SQL file. For the ```LookupUser.sql``` example above, TypeSafeSQL would generated ```target/generated-sources/sql/com/example/project/LookupUser.java```. These classes can be used within your applicated to execute their corresponding SQL in a type-safe manor. For example:
```
import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import com.example.project.LookupUser;

public class SomeClass {
	public void doSomething() {
		LookupUser
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withUsername("tester.mctesting")
			.execute(stream -> stream.forEach(result -> {
				System.out.println(result.getFirstName());
				System.out.println(result.getLastName());
				System.out.println(result.getAge());
			}));
	}
}
``` 
Some things to note about these classes:
* You start using these classes by calling their ```using(Connection connection, ConnectionHandling handling)``` method.
* You must then provide a value for each parameter, in the order they appear in your SQL file.
	* You may not skip any parameter.
	* You must provide parameters in their expected type.
	* Forgetting to do either of these will result in a glorious compile-time error, saving you many hours of debugging.
* You do not need to worry about closing the [Connection](https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html), the [PreparedStatement](https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html), or the  [ResultSet](https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html); it will be done for you. 




, providing the following benefits:
* Forgetting a parameter of a SQL script is compile-time error.
* TypeSafeSQL will make sure JDBC resources, such as 
* Results of SELECT queries are returned as type-safe [Java 8 Streams](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html). For example, if your query returns only one BIGINT column, the result will be a [LongStream](https://docs.oracle.com/javase/8/docs/api/java/util/stream/LongStream.html). Where as if your query returns two columns, one VARCHAR named "username" and one INTEGER named "age" the result will be a [Stream\<Result\>](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html) where Result is an automatically generated class that looks like this:
```
	public final class Result {

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

