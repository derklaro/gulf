# Gulf

![CI](https://github.com/derklaro/gulf/actions/workflows/ci.yml/badge.svg)
![MIT License](https://img.shields.io/badge/license-MIT-blue)
![Release Version](https://img.shields.io/maven-central/v/dev.derklaro.gulf/gulf)

### Dependencies

The gulf library is available in the maven central repository.

```xml

<dependencies>
  <dependency>
    <groupId>dev.derklaro.gulf</groupId>
    <artifactId>gulf</artifactId>
    <version>{release}</version>
  </dependency>
</dependencies>
```

```gradle
implementation group: 'dev.derklaro.gulf', name: 'gulf', version: '{version}'
```

### How to compare two objects

To compare two objects you need to obtain a configured Gulf instance for your needs as shown in the example below:

```java
package dev.derklaro.gulf.example;

import dev.derklaro.gulf.Gulf;
import dev.derklaro.gulf.diff.Change;
import java.util.Collection;
import lombok.NonNull;

public class GulfExample {

  public static @NonNull Collection<Change<Object>> compare(@NonNull Object left, @NonNull Object right) {
    // The gulf instance can be shared and is thread safe, so it can be used for multiple comparisons at the same time
    // For this example using a local variable is just easier
    Gulf gulf = Gulf.builder().build();
    return gulf.findChanges(left, right);
  }
}
```

Each change to the left and right object are wrapped per property. This means that changes to for example a collection
are not one-by-one in the return collection from `findChanges` but rather associated with the changed instance. This
means that (for example) a diff in a collection will be wrapped in a `CollectionChange` which then contains the changes
to all elements in the left and right collections.

### Compiling from source

Just executing `./gradlew` or `gradlew.bat` will execute the full build lifecycle including all tests. For local changes
and testing use `./gradlew publishToMavenLocal` to publish all artifacts into the local maven repository.

First time example:

```
git clone https://github.com/derklaro/gulf.git
cd gulf/
./gradlew
```

### Contributions

Open source lives from contributions so feel free to contribute! Before opening a pull request, please make sure that
all tests are still passing and checkstyle prints no warnings during compile. Please include a test case if necessary.

### License

This project is licensed under the terms of the [MIT License](../license.txt).
