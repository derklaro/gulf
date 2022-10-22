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

package dev.derklaro.gulf.finder;

import dev.derklaro.gulf.diff.Change;
import dev.derklaro.gulf.diff.Changes;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public final class DiffFinders {

  private static final DiffFinder<Object> EQUALITY_BASED = (gulf, path, type, left, right) -> {
    if (left.equals(right)) {
      // no diff
      return Changes.none();
    } else {
      Change<Object> change = new Change<>(path, left, right);
      return Changes.singleton(change);
    }
  };

  private DiffFinders() {
    throw new UnsupportedOperationException();
  }

  public static @NonNull <T> DiffFinder<T> equalityBased() {
    return (DiffFinder<T>) EQUALITY_BASED;
  }
}
