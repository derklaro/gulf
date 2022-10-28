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
import dev.derklaro.gulf.util.SomeNumber;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class NumberCompareTest {

  @Test
  void testNaNCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges(Double.NaN, Double.NaN);
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges(Double.NaN, Double.MAX_VALUE);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals(Double.NaN, change.leftElement());
    Assertions.assertEquals(Double.MAX_VALUE, change.rightElement());
  }

  @Test
  void testIntegerCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges(1230, 1230);
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges(10, 1020);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals(10, change.leftElement());
    Assertions.assertEquals(1020, change.rightElement());
  }

  @Test
  void testBigIntegerCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges(BigInteger.ONE, BigInteger.ONE);
    Assertions.assertTrue(changes.isEmpty());

    // change
    changes = gulf.findChanges(BigInteger.ONE, BigInteger.TEN);
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertEquals(BigInteger.ONE, change.leftElement());
    Assertions.assertEquals(BigInteger.TEN, change.rightElement());
  }

  @Test
  void testUnpredictableCompare() {
    Gulf gulf = Gulf.builder().build();

    // no change
    Collection<Change<Object>> changes = gulf.findChanges(new SomeNumber("nrwunwru"), new SomeNumber("gmreiger"));
    Assertions.assertTrue(changes.isEmpty());

    // special check - make sure we can actually parse a BigDecimal from our instance
    SomeNumber number = new SomeNumber("54785");
    Assertions.assertDoesNotThrow(() -> new BigDecimal(number.toString()));

    // change
    changes = gulf.findChanges(number, new SomeNumber("ngvurnbgure"));
    Assertions.assertEquals(1, changes.size());

    Change<Object> change = changes.iterator().next();
    Assertions.assertEquals("$", change.path().toFullPath());
    Assertions.assertInstanceOf(SomeNumber.class, change.leftElement());
    Assertions.assertInstanceOf(SomeNumber.class, change.rightElement());
  }
}
