# TypeSafeSQL
1. [Introduction](#introduction)
2. [Example](#example)
3. [Installation](#installation)

##Introduction
The purpose of this Maven plugin is to automatically generate Java classes from (annotated) SQL files, providing the following benefits:
* No need to interact with JDBC directly.
* Forgetting a parameter of a SQL script is compile-time error.
* TypeSafeSQL will automatically close JDBC resources, such as [Connections](https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html), [PreparedStatements](https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html), [ResultSets](https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html).
* Results of SELECT queries return type-safe [Java 8 Streams](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html).

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
	username = {in:VARCHAR:username} OR
	id = {in:INTEGER:id};
```
Then during Maven's build process, TypeSafeSQL will automatically generate a ```LookupUser``` class in ```target/generated-sources/sql/com/example/project/LookupUser.java```. This class can be used to easily and safely execute the SQL.

##Parameters

Each input parameter in your SQL file (e.g. ```{in:VARCHAR:username}``` and ```{in:INTEGER:id}```) will become a parameter in the [PreparedStatement](https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html) that TypeSafeSQL creates for you. The type (e.g. ```VARCHAR``` and ```INTEGER```) and name (e.g. ```username``` and ```id```) tell TypeSafeSQL what to name the method it creates for setting this parameter, and what type of object it should expect (e.g. ```withUsername(String username)``` and ```withId(int id)```). All parameters must be specified, even if the value is ```null```. Forgetting to specify the value of a parameter, is a compile-time error, catching the mistake long before it can turn into a bug. If the parameter type maps to a primitive in Java and can't be ```null``` (e.g. ```BIGINT```, ```INTEGER```, ```DOUBLE```), you can set the parameter to null by calling a ```without``` method (e.g. ```withoutId()```).

##Columns

It's important that you annotate **all** the columns that will be in the [ResultSet](https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html) of your SQL file (e.g. ```{out:VARCHAR:firstName}```, ```{out:VARCHAR:lastName}``` and ```{out:INTEGER:age}```). The number of columns and their types determine what TypeSafeSQL will return to you when you execute the query. No matter what, there are always two methods for executing the classes TypeSafeSQL generates: ```execute()``` and ```executeStream(...)```. The first method, ```execute()```, will return the result of the first row or throw a ```NoSuchElementException``` if the [ResultSet](https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html) has no rows. The second method, ```executeStream(...)```, expects you to pass it [Consumer](https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html) that will accept the result of each row.

If there is only one column in your SQL, the return type of ```execute()``` and the type variable of the [Consumer](https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html) expected by ```executeStream(...)``` will correspond to the type of the column. For example, if ```{out:VARCHAR:name}``` is the only column, then the two method will be ```String execute()``` and ```void executeStream(Consumer<String> action)```. For another example, if the ```{out:INTEGER:age}``` is the only column, then the two method signatures will be ```int execute()``` and ```void executeStream(IntConsumer action)```.

If there is more than one column in your SQL, then TypeSafeSQL will create an immutable nested class with a field for each column. For example, if there are three columns, ```{out:VARCHAR:firstName}```, ```{out:VARCHAR:lastName}``` and ```{out:INTEGER:age}``` then the nested class will look something like this:

```
public static final class Result {
		
	private final String firstName;
	private final String lastName;
	private final int age;
	
	...
	
	public final String getFirstName() {
		return firstName;
	}
	
	public final String getLastName() {
		return lastName;
	}
	
	public final int getAge() {
		return age;
	}
	
}
```

The return type of ```execute()``` as well as the argument type of the [Consumer](https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html) expected by ```executeStream(...)``` will both be the ```Result``` class, as seen above.

##Putting It All Together

```
import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import com.example.project.LookupUser;

public class SomeClass {

	public void printAllUsers() {
		LookupUser
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withUsername("tester.mctesting")
			.execute(stream -> stream.forEach(this::print));
	}

	public void printFirstUser() {
		LookupUser.Result result = LookupUser
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withUsername("tester.mctesting")
			.execute();
		print(result);
	}

	private void print(LookupUser.Result result) {
		System.out.println(result.getFirstName());
		System.out.println(result.getLastName());
		System.out.println(result.getAge());
	}

}
```

##Installation
Add the TypeSafeSQL plugin to your ```pom.xml```:
```
<plugin>
	<groupId>com.github.ryanholdren.typesafesql</groupId>
	<artifactId>plugin</artifactId>
	<version>2016-03-18</version>
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
	<version>2016-03-18</version>
</dependency>
```
