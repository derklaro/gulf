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
import dev.derklaro.gulf.diff.array.ArrayChange;
import dev.derklaro.gulf.diff.array.ArrayElementAddOrRemove;
import dev.derklaro.gulf.diff.array.ArrayElementChange;
import dev.derklaro.gulf.diff.array.IndexedChange;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class ArrayCompareTest {

  @Test
  void testNoChange() {
    Gulf gulf = Gulf.builder().build();

    String[] left = new String[]{"Hello", "World", "!"};
    String[] right = new String[]{"Hello", "World", "!"};

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertTrue(changes.isEmpty());
  }

  @Test
  void testElementAdd() {
    Gulf gulf = Gulf.builder().build();

    String[] left = new String[]{"Hello", "World", "!"};
    String[] right = new String[]{"Hello", "World", "!", " :)"};

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    ArrayChange<Object> arr = Assertions.assertInstanceOf(ArrayChange.class, change);

    Assertions.assertSame(left, arr.leftElement());
    Assertions.assertSame(right, arr.rightElement());

    Collection<IndexedChange<Object>> elementDiffs = arr.elementChanges();
    Assertions.assertEquals(1, elementDiffs.size());

    IndexedChange<Object> idxChange = elementDiffs.iterator().next();
    ArrayElementAddOrRemove<Object> add = Assertions.assertInstanceOf(ArrayElementAddOrRemove.class, idxChange);

    Assertions.assertEquals(3, add.index());
    Assertions.assertTrue(add.elementAdded());
    Assertions.assertFalse(add.elementRemoved());

    Assertions.assertNull(add.leftElement());
    Assertions.assertEquals(" :)", add.rightElement());
  }

  @Test
  void testElementRemove() {
    Gulf gulf = Gulf.builder().build();

    String[] left = new String[]{"Hello", "World", "!"};
    String[] right = new String[]{"Hello", "World"};

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    ArrayChange<Object> arr = Assertions.assertInstanceOf(ArrayChange.class, change);

    Assertions.assertSame(left, arr.leftElement());
    Assertions.assertSame(right, arr.rightElement());

    Collection<IndexedChange<Object>> elementDiffs = arr.elementChanges();
    Assertions.assertEquals(1, elementDiffs.size());

    IndexedChange<Object> idxChange = elementDiffs.iterator().next();
    ArrayElementAddOrRemove<Object> remove = Assertions.assertInstanceOf(ArrayElementAddOrRemove.class, idxChange);

    Assertions.assertEquals(2, remove.index());
    Assertions.assertTrue(remove.elementRemoved());
    Assertions.assertFalse(remove.elementAdded());

    Assertions.assertEquals("!", remove.leftElement());
    Assertions.assertNull(remove.rightElement());
  }

  @Test
  void testElementChange() {
    Gulf gulf = Gulf.builder().build();

    String[] left = new String[]{"Hello", "World", "!"};
    String[] right = new String[]{"Hello", "World", ":)"};

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    ArrayChange<Object> arr = Assertions.assertInstanceOf(ArrayChange.class, change);

    Assertions.assertSame(left, arr.leftElement());
    Assertions.assertSame(right, arr.rightElement());

    Collection<IndexedChange<Object>> elementDiffs = arr.elementChanges();
    Assertions.assertEquals(1, elementDiffs.size());

    IndexedChange<Object> idxChange = elementDiffs.iterator().next();
    ArrayElementChange<Object> elementChange = Assertions.assertInstanceOf(ArrayElementChange.class, idxChange);

    Assertions.assertEquals(1, elementChange.changes().size());
    Assertions.assertEquals(2, elementChange.index());

    Change<Object> diff = elementChange.changes().iterator().next();
    Assertions.assertEquals("!", diff.leftElement());
    Assertions.assertEquals(":)", diff.rightElement());
  }
}
