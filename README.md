# onix-tagname-converter
ONIX for Books 3.0 tagname converter in Java

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.legible</groupId>
  <artifactId>onix-tagname-converter</artifactId>
  <version>2.0.0</version>
</dependency>
```

```java
import com.github.legible.ONIXTagnameConverter;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

ONIXTagnameConverter converter = new ONIXTagnameConverter();

// From Reference Tags to Short Tags
converter.convertToShortTags(new StreamSource(new File("reference.xml")), new File("short.xml"));

// From Short Tags to Reference Tags
converter.convertToReferenceTags(new StreamSource(new File("short.xml")), new File("reference.xml"));
```
