#FullContact4j

A Java client for the [FullContact API](http://www.fullcontact.com/docs)


##Maven
To fetch FullContact4j from Maven, you'll need to connect to [Bintray](https://bintray.com/) and fetch the artifact like this:
```xml
<repositories>
  <repository>
    <id>fullcontact</id>
    <url>http://dl.bintray.com/content/fullcontact/fullcontact-oss</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.fullcontact</groupId>
    <artifactId>fullcontact4j</artifactId>
    <version>${version}</version>
  </dependency>
</dependencies>
```

##Gradle
```groovy
repositories {
    maven {
        url "http://dl.bintray.com/content/fullcontact/fullcontact-oss"
    }
}

dependencies {
    compile group: "com.fullcontact", name: "fullcontact4j", version: "${version}"
}
```


##Usage:

* [Person API](/docs/person/)
* [Name API](/docs/name/)
* [Location API](/docs/location/)
* [CardReader API](/docs/cardReader/)
* [Batch API](/docs/batch/)
* [Email API](/docs/email/)
* [Icon API](/docs/icon/)

#####Set a Custom User-Agent
* FullContact fullContact = new FullContact("my-api-key").setUserAgent("My Custom Agent/1.0");

##Dependencies
* Google GSON (http://code.google.com/p/google-gson/)
