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
import dev.derklaro.gulf.diff.array.ArrayElementChange;
import dev.derklaro.gulf.diff.array.CollectionChange;
import dev.derklaro.gulf.diff.array.IndexedChange;
import dev.derklaro.gulf.diff.map.KeyedChange;
import dev.derklaro.gulf.diff.map.MapChange;
import dev.derklaro.gulf.diff.map.MapEntryAddOrRemove;
import dev.derklaro.gulf.diff.map.MapEntryChange;
import dev.derklaro.gulf.seed.SeedA;
import dev.derklaro.gulf.seed.SeedB;
import dev.derklaro.gulf.util.MapUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
final class NestedCompareTest {

  private static void validateNextChange(Iterator<? extends Change<Object>> changes, Consumer<Change<Object>> checker) {
    checker.accept(changes.next());
  }

  @Test
  void testNoChange() {
    SeedA seedA = new SeedA(
      UUID.randomUUID(),
      new SeedB(1, 'a', UUID.randomUUID(), "Hello World"),
      Arrays.asList(
        new SeedB(1234, 'b', UUID.randomUUID(), "World"),
        new SeedB(12345, 'c', UUID.randomUUID(), "Hello")),
      MapUtil.of(
        "TestingA", Collections.singleton(new SeedB(1234567, 'd', UUID.randomUUID(), "Google")),
        "TestingB", Collections.singleton(new SeedB(1236745, 'e', UUID.randomUUID(), "Bing"))));

    Gulf gulf = Gulf.builder().build();
    Collection<Change<Object>> changes = gulf.findChanges(seedA, seedA);

    Assertions.assertTrue(changes.isEmpty());
  }

  @Test
  void testChanges() {
    UUID randomId = UUID.randomUUID();

    SeedA left = new SeedA(
      randomId,
      new SeedB(1, 'a', UUID.randomUUID(), "Hello World"),
      Arrays.asList(
        new SeedB(1234, 'b', randomId, "World"),
        new SeedB(12345, 'c', randomId, "Hello")),
      MapUtil.of(
        "TestingA", Collections.singleton(new SeedB(1234567, 'd', randomId, "Google")),
        "TestingB", Collections.singleton(new SeedB(1236745, 'e', randomId, "Bing"))));
    SeedA right = new SeedA(
      randomId,
      new SeedB(123, 'a', randomId, "Hello World"),
      Arrays.asList(
        new SeedB(1234, 'b', UUID.randomUUID(), "World"),
        new SeedB(12345, 'c', randomId, "Hello")),
      MapUtil.of(
        "TestingA", Collections.singleton(new SeedB(1234567, 'z', randomId, "Google!")),
        "TestingC", Collections.singleton(new SeedB(1236745, 'e', randomId, "Bing"))));

    Gulf gulf = Gulf.builder().build();
    Collection<Change<Object>> changes = gulf.findChanges(left, right);

    Assertions.assertEquals(3, changes.size());
    Iterator<Change<Object>> changeIterator = changes.iterator();

    // validate the change of the seedB id
    validateNextChange(changeIterator, change -> {
      Assertions.assertEquals("$.seedB.id", change.path().toFullPath());
      Assertions.assertEquals(left.seedB().id(), change.leftElement());
      Assertions.assertEquals(right.seedB().id(), change.rightElement());
    });

    // validate the collection change of seedB
    validateNextChange(changeIterator, change -> {
      Assertions.assertEquals("$.colOfSeedB", change.path().toFullPath());
      CollectionChange<Object, ?> collectionChange = Assertions.assertInstanceOf(CollectionChange.class, change);

      Collection<IndexedChange<Object>> elementDiffs = collectionChange.elementChanges();
      Assertions.assertEquals(1, elementDiffs.size());

      IndexedChange<Object> indexedChange = elementDiffs.iterator().next();
      ArrayElementChange<Object> elementChange = Assertions.assertInstanceOf(ArrayElementChange.class, indexedChange);

      Assertions.assertEquals(0, elementChange.index());
      Assertions.assertInstanceOf(SeedB.class, elementChange.leftElement());
      Assertions.assertInstanceOf(SeedB.class, elementChange.rightElement());

      Collection<Change<Object>> inElementChanges = elementChange.changes();
      Assertions.assertEquals(1, inElementChanges.size());

      Change<Object> theChange = inElementChanges.iterator().next();
      Assertions.assertEquals("$.colOfSeedB.id", theChange.path().toFullPath());
      Assertions.assertInstanceOf(UUID.class, theChange.leftElement());
      Assertions.assertInstanceOf(UUID.class, theChange.rightElement());
    });

    // validate the changes in the map
    validateNextChange(changeIterator, change -> {
      Assertions.assertEquals("$.mapColOfSeedB", change.path().toFullPath());
      MapChange<Object, Object, ?> mapChange = Assertions.assertInstanceOf(MapChange.class, change);

      Assertions.assertSame(left.mapColOfSeedB(), change.leftElement());
      Assertions.assertSame(right.mapColOfSeedB(), change.rightElement());

      Collection<KeyedChange<Object, Object>> entryChanges = mapChange.entryChanges();
      Assertions.assertEquals(3, entryChanges.size());

      Iterator<KeyedChange<Object, Object>> entryIterator = entryChanges.iterator();
      validateNextChange(entryIterator, entryChange -> {
        MapEntryChange<Object, Object> mec = Assertions.assertInstanceOf(MapEntryChange.class, entryChange);
        Assertions.assertEquals("TestingA", mec.key());

        Collection<Change<Object>> ec = mec.changes();
        CollectionChange<Object, ?> cc = Assertions.assertInstanceOf(CollectionChange.class, ec.iterator().next());

        Collection<IndexedChange<Object>> ccec = cc.elementChanges();
        ArrayElementChange<Object> arc = Assertions.assertInstanceOf(ArrayElementChange.class, ccec.iterator().next());

        Collection<Change<Object>> c = arc.changes();
        Assertions.assertEquals(2, c.size());

        Iterator<Change<Object>> iterator = c.iterator();
        validateNextChange(iterator, elementChange -> {
          Assertions.assertEquals("$.mapColOfSeedB.c", elementChange.path().toFullPath());
          Assertions.assertEquals('d', elementChange.leftElement());
          Assertions.assertEquals('z', elementChange.rightElement());
        });
        validateNextChange(iterator, elementChange -> {
          Assertions.assertEquals("$.mapColOfSeedB.str", elementChange.path().toFullPath());
          Assertions.assertEquals("Google", elementChange.leftElement());
          Assertions.assertEquals("Google!", elementChange.rightElement());
        });
      });

      validateNextChange(entryIterator, entryChange -> {
        MapEntryAddOrRemove<Object, Object> ear = Assertions.assertInstanceOf(MapEntryAddOrRemove.class, entryChange);
        Assertions.assertEquals("TestingB", ear.key());
        Assertions.assertTrue(ear.elementRemoved());
        Assertions.assertNotNull(ear.leftElement());
        Assertions.assertNull(ear.rightElement());
      });

      validateNextChange(entryIterator, entryChange -> {
        MapEntryAddOrRemove<Object, Object> ear = Assertions.assertInstanceOf(MapEntryAddOrRemove.class, entryChange);
        Assertions.assertEquals("TestingC", ear.key());
        Assertions.assertTrue(ear.elementAdded());
        Assertions.assertNull(ear.leftElement());
        Assertions.assertNotNull(ear.rightElement());
      });
    });
  }
}
