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

package dev.derklaro.gulf.finder.defaults;

import dev.derklaro.gulf.Gulf;
import dev.derklaro.gulf.diff.Change;
import dev.derklaro.gulf.diff.Changes;
import dev.derklaro.gulf.finder.DiffFinder;
import dev.derklaro.gulf.path.ObjectPath;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public final class NumberDiffFinder implements DiffFinder<Number> {

  @Override
  public @NonNull Collection<Change<Number>> findChangesNullSafe(
    @NonNull Gulf gulf,
    @NonNull ObjectPath path,
    @NonNull Type fullType,
    @NonNull Number left,
    @NonNull Number right
  ) {
    // check if the number is the same
    if (left.equals(right)) {
      return Changes.none();
    }

    // marker if the value changed
    boolean changed;

    // check if the number is special and needed extra handling
    Boolean specialNumberCompareResult = this.compareSpecialNumber(left, right);
    if (specialNumberCompareResult != null) {
      changed = specialNumberCompareResult;
    } else {
      BigDecimal leftValue = this.toBigDecimal(left);
      BigDecimal rightValue = this.toBigDecimal(right);

      // compare both numbers, if possible
      if (leftValue != null && rightValue != null) {
        changed = leftValue.compareTo(rightValue) != 0;
      } else {
        // There might be an issue with converting one of the numbers
        // If both numbers are null -> no change; If only one is null -> change
        changed = !(leftValue == null && rightValue == null);
      }
    }

    if (changed) {
      // the element changed
      Change<Number> change = new Change<>(path, left, right);
      return Changes.singleton(change);
    } else {
      // no change
      return Changes.none();
    }
  }

  private @Nullable Boolean compareSpecialNumber(@NonNull Number left, @NonNull Number right) {
    if (left instanceof Double && right instanceof Double) {
      double leftVal = left.doubleValue();
      double rightVal = right.doubleValue();
      // only compare now if the values are special
      if (Double.isNaN(leftVal) || Double.isNaN(rightVal) || Double.isFinite(leftVal) || Double.isFinite(rightVal)) {
        return Double.compare(leftVal, rightVal) != 0;
      }
    } else if (left instanceof Float && right instanceof Float) {
      float leftVal = left.floatValue();
      float rightVal = right.floatValue();
      // only compare now if the values are special
      if (Float.isNaN(leftVal) || Float.isNaN(rightVal) || Float.isFinite(leftVal) || Float.isFinite(rightVal)) {
        return Float.compare(leftVal, rightVal) != 0;
      }
    }

    // not special
    return null;
  }

  private @Nullable BigDecimal toBigDecimal(@NonNull Number number) {
    // check if we need to convert here
    if (number instanceof BigDecimal) {
      return (BigDecimal) number;
    }

    // convert a big integer
    if (number instanceof BigInteger) {
      return new BigDecimal((BigInteger) number);
    }

    // convert a non-floating number
    if (number instanceof Byte || number instanceof Short
      || number instanceof Integer || number instanceof Long) {
      return BigDecimal.valueOf(number.longValue());
    }

    // ensure the correctness of floating-point numbers
    if (number instanceof Float || number instanceof Double) {
      return BigDecimal.valueOf(number.doubleValue());
    }

    try {
      // try to convert the number from a string, if possible
      return new BigDecimal(number.toString());
    } catch (Exception ignored) {
      // no chance
      return null;
    }
  }
}
