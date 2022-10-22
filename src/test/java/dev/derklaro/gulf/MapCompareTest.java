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
import dev.derklaro.gulf.diff.map.KeyedChange;
import dev.derklaro.gulf.diff.map.MapChange;
import dev.derklaro.gulf.diff.map.MapEntryAddOrRemove;
import dev.derklaro.gulf.diff.map.MapEntryChange;
import dev.derklaro.gulf.util.MapUtil;
import java.util.Collection;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
final class MapCompareTest {

  @Test
  void testNoChange() {
    Gulf gulf = Gulf.builder().build();

    Map<String, String> left = MapUtil.of("Hello", "World");
    Map<String, String> right = MapUtil.of("Hello", "World");

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertTrue(changes.isEmpty());
  }

  @Test
  void testElementAdd() {
    Gulf gulf = Gulf.builder().build();

    Map<String, String> left = MapUtil.of("Hello", "World");
    Map<String, String> right = MapUtil.of("Hello", "World", "Test", "Yes");

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    MapChange<Object, Object, Map<Object, Object>> map = Assertions.assertInstanceOf(MapChange.class, change);

    Assertions.assertSame(left, map.leftElement());
    Assertions.assertSame(right, map.rightElement());

    Collection<KeyedChange<Object, Object>> elementDiffs = map.entryChanges();
    Assertions.assertEquals(1, elementDiffs.size());

    KeyedChange<Object, Object> entryChange = elementDiffs.iterator().next();
    MapEntryAddOrRemove<Object, Object> add = Assertions.assertInstanceOf(MapEntryAddOrRemove.class, entryChange);

    Assertions.assertTrue(add.elementAdded());
    Assertions.assertFalse(add.elementRemoved());

    Assertions.assertNull(add.leftElement());
    Assertions.assertEquals("Yes", add.rightElement());
    Assertions.assertEquals("Test", add.key());
  }

  @Test
  void testElementRemove() {
    Gulf gulf = Gulf.builder().build();

    Map<String, String> left = MapUtil.of("Hello", "World", "Test", "Yes");
    Map<String, String> right = MapUtil.of("Hello", "World");

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    MapChange<Object, Object, Map<Object, Object>> map = Assertions.assertInstanceOf(MapChange.class, change);

    Assertions.assertSame(left, map.leftElement());
    Assertions.assertSame(right, map.rightElement());

    Collection<KeyedChange<Object, Object>> elementDiffs = map.entryChanges();
    Assertions.assertEquals(1, elementDiffs.size());

    KeyedChange<Object, Object> entryChange = elementDiffs.iterator().next();
    MapEntryAddOrRemove<Object, Object> remove = Assertions.assertInstanceOf(MapEntryAddOrRemove.class, entryChange);

    Assertions.assertTrue(remove.elementRemoved());
    Assertions.assertFalse(remove.elementAdded());

    Assertions.assertEquals("Yes", remove.leftElement());
    Assertions.assertNull(remove.rightElement());
    Assertions.assertEquals("Test", remove.key());
  }

  @Test
  void testElementChange() {
    Gulf gulf = Gulf.builder().build();

    Map<String, String> left = MapUtil.of("Hello", "World", "Test", "Yes");
    Map<String, String> right = MapUtil.of("Hello", "World", "Test", "No");

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    MapChange<Object, Object, Map<Object, Object>> map = Assertions.assertInstanceOf(MapChange.class, change);

    Assertions.assertSame(left, map.leftElement());
    Assertions.assertSame(right, map.rightElement());

    Collection<KeyedChange<Object, Object>> elementDiffs = map.entryChanges();
    Assertions.assertEquals(1, elementDiffs.size());

    KeyedChange<Object, Object> entryChange = elementDiffs.iterator().next();
    Assertions.assertInstanceOf(MapEntryChange.class, entryChange);

    Assertions.assertEquals("Yes", entryChange.leftElement());
    Assertions.assertEquals("No", entryChange.rightElement());
    Assertions.assertEquals("Test", entryChange.key());
  }
}
