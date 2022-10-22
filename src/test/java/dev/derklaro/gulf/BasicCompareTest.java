/*
 * This file is part of gulf, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 Pasqual K. and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package dev.derklaro.gulf;

import dev.derklaro.gulf.diff.Change;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class BasicCompareTest {

  @Test
  void testBooleanCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges(true, true);
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges(true, false);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals(Boolean.TRUE, change.leftElement());
    Assertions.assertEquals(Boolean.FALSE, change.rightElement());
  }

  @Test
  void testIntCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges(999, 999);
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges(999, 9999);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals(999, change.leftElement());
    Assertions.assertEquals(9999, change.rightElement());
  }

  @Test
  void testDoubleCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges(1.7976931348623157D, 1.7976931348623157D);
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges(1.7976931348623157D, 1.7976931348623156D);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals(1.7976931348623157D, change.leftElement());
    Assertions.assertEquals(1.7976931348623156D, change.rightElement());
  }

  @Test
  void testCharCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges('C', 'C');
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges('C', 'D');
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals('C', change.leftElement());
    Assertions.assertEquals('D', change.rightElement());
  }

  @Test
  void testEnumCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges(Modifier.ABSTRACT, Modifier.ABSTRACT);
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges(Modifier.ABSTRACT, Modifier.PRIVATE);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals(Modifier.ABSTRACT, change.leftElement());
    Assertions.assertEquals(Modifier.PRIVATE, change.rightElement());
  }

  @Test
  void testStringCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges("Hello World", "Hello World");
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges("Hello World", "Hello World!");
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals("Hello World", change.leftElement());
    Assertions.assertEquals("Hello World!", change.rightElement());
  }

  @Test
  void testPatternCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges(Pattern.compile("."), Pattern.compile("."));
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges(Pattern.compile("."), Pattern.compile(".+"));
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals(".", Objects.toString(change.leftElement()));
    Assertions.assertEquals(".+", Objects.toString(change.rightElement()));
  }
}
