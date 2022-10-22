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
import dev.derklaro.gulf.diff.Changes;
import dev.derklaro.gulf.seed.SeedB;
import dev.derklaro.gulf.type.TypeMatchers;
import java.util.Collection;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class CustomGulfSettingsTest {

  @Test
  void testCustomPath() {
    SeedB left = new SeedB(1, 'c', UUID.randomUUID(), "Google");
    SeedB right = new SeedB(1, 'c', UUID.randomUUID(), "Google");

    Gulf gulf = Gulf.builder().rootPathIndicator("§").pathSeparatorIndicator("__").build();

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("§__id", change.path().toFullPath());
  }

  @Test
  void testEmptyRootIndicatorGetsEmitted() {
    SeedB left = new SeedB(1, 'c', UUID.randomUUID(), "Google");
    SeedB right = new SeedB(1, 'c', UUID.randomUUID(), "Google");

    Gulf gulf = Gulf.builder().rootPathIndicator("").pathSeparatorIndicator("__").build();

    Collection<Change<Object>> changes = gulf.findChanges(left, right);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("id", change.path().toFullPath());
  }

  @Test
  void testCustomDefaultSupplier() {
    Object def = new Object();
    Gulf gulf = Gulf.builder().defaultDefaultSupplier(type -> def).build();

    Assertions.assertSame(def, gulf.getDefaultInstance(Object.class));
  }

  @Test
  void testCustomDefaultDiffFinder() {
    SeedB element = new SeedB(123, 'a', UUID.randomUUID(), "Google");
    Gulf gulf = Gulf.builder().defaultDiffFinder((g, p, t, l, r) -> {
      Change<Object> change = new Change<>(p, l, r);
      return Changes.singleton(change);
    }).build();

    Collection<Change<Object>> changes = gulf.findChanges(element, element);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertSame(element, change.leftElement());
    Assertions.assertSame(element, change.rightElement());
  }

  @Test
  void testCustomDefaultSupplierForType() {
    SeedB element = new SeedB(1234, 'a', UUID.randomUUID(), "Google");
    Gulf gulf = Gulf.builder().withDefaultSupplier(TypeMatchers.exact(SeedB.class), type -> element).build();

    Object defaultValue = gulf.getDefaultInstance(SeedB.class);
    Assertions.assertSame(element, defaultValue);

    Object defaultValueForObject = gulf.getDefaultInstance(Object.class);
    Assertions.assertNull(defaultValueForObject);
  }

  @Test
  void testCustomDiffFinder() {
    SeedB element = new SeedB(123, 'a', UUID.randomUUID(), "Google");
    Gulf gulf = Gulf.builder().withDiffFinder(TypeMatchers.exact(SeedB.class), (g, p, t, l, r) -> {
      Change<Object> change = new Change<>(p, l, r);
      return Changes.singleton(change);
    }).build();

    Collection<Change<Object>> changes = gulf.findChanges(element, element);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertSame(element, change.leftElement());
    Assertions.assertSame(element, change.rightElement());
  }
}
