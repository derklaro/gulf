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

package dev.derklaro.gulf.diff;

import dev.derklaro.gulf.collection.ImmutableArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

public final class Changes {

  private Changes() {
    throw new UnsupportedOperationException();
  }

  @Unmodifiable
  @Contract(pure = true)
  public static @NonNull <T> Collection<Change<T>> none() {
    return Collections.emptyList();
  }

  @Unmodifiable
  @Contract(pure = true)
  public static @NonNull <T> Collection<Change<T>> singleton(@NonNull Change<T> change) {
    return Collections.singleton(change);
  }

  @SafeVarargs
  @Unmodifiable
  @Contract(pure = true)
  public static @NonNull <T> Collection<Change<T>> all(@NonNull Change<T>... changes) {
    // optimize the call to prevent clones when not needed
    int elements = changes.length;
    if (elements == 0) {
      return none();
    } else if (elements == 1) {
      return singleton(changes[0]);
    } else {
      return ImmutableArrayList.fromArray(changes);
    }
  }
}
