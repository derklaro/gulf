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

package dev.derklaro.gulf.internal;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class Internals {

  private Internals() {
    throw new UnsupportedOperationException();
  }

  public static @NonNull <K, V> Map.Entry<K, V> newMapEntry(@Nullable K key, @Nullable V value) {
    return new AbstractMap.SimpleImmutableEntry<>(key, value);
  }

  public static @NonNull <T> T nonNullOrGet(@Nullable T first, @NonNull Supplier<T> second) {
    return first != null ? first : Objects.requireNonNull(second.get());
  }

  public static @NonNull Class<?> getObjectType(@Nullable Object left, @Nullable Object right) {
    // check that not both objects are null
    if (left == null && right == null) {
      throw new IllegalArgumentException("Both given objects are null, unable to determine type");
    }
    // use the type of the left or right object depending on which one is present
    return left != null ? left.getClass() : right.getClass();
  }
}
