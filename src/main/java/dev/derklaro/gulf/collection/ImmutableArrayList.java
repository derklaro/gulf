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

package dev.derklaro.gulf.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class ImmutableArrayList<T> extends AbstractList<T> implements RandomAccess, Serializable {

  private static final long serialVersionUID = 2474954729288366548L;

  private final T[] elements;

  private ImmutableArrayList(@NonNull T[] elements) {
    this.elements = elements;
  }

  @Unmodifiable
  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  public static @NonNull <T> Collection<T> fromCollection(@NonNull Collection<T> source) {
    // copy out all elements into the new list, as per definition this array has no references to the source collection
    Object[] elements = source.toArray();
    return (Collection<T>) new ImmutableArrayList<>(elements);
  }

  @Unmodifiable
  @Contract(pure = true)
  public static @NonNull <T> Collection<T> fromArray(@NonNull T[] elements) {
    return new ImmutableArrayList<>(elements.clone());
  }

  @Override
  public @Nullable T get(int index) {
    return this.elements[index];
  }

  @Override
  public int size() {
    return this.elements.length;
  }

  @Override
  public @NonNull Object[] toArray() {
    return this.elements.clone();
  }

  @Override
  @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
  public @NonNull <V> V[] toArray(@NonNull V[] target) {
    int size = this.elements.length;
    // create a new array if we can't copy into the target array
    if (target.length < size) {
      return Arrays.copyOf(this.elements, size, (Class<? extends V[]>) target.getClass());
    }

    // copy the elements of our array into the target array
    System.arraycopy(this.elements, 0, target, 0, size);
    return target;
  }

  @Override
  public int indexOf(Object o) {
    if (o == null) {
      for (int i = 0; i < this.elements.length; i++) {
        if (this.elements[i] == null) {
          return i;
        }
      }
    } else {
      for (int i = 0; i < this.elements.length; i++) {
        if (Objects.equals(this.elements[i], o)) {
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  @SuppressWarnings("ListIndexOfReplaceableByContains") // clueless
  public boolean contains(@NonNull Object o) {
    return this.indexOf(o) != -1;
  }

  @Override
  public Spliterator<T> spliterator() {
    return Spliterators.spliterator(this.elements, Spliterator.ORDERED);
  }

  @Override
  public void forEach(@NonNull Consumer<? super T> action) {
    for (T element : this.elements) {
      action.accept(element);
    }
  }

  @Override
  public void replaceAll(UnaryOperator<T> operator) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void sort(Comparator<? super T> c) {
    throw new UnsupportedOperationException();
  }
}
