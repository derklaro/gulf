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

package dev.derklaro.gulf.path;

import dev.derklaro.gulf.collection.ImmutableArrayList;
import java.util.Arrays;
import java.util.Collection;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

public final class ObjectPath {

  private final String[] elements;
  private final String currentSegment;
  private final ObjectPathFactory factory;

  public ObjectPath(@NonNull String[] elements, @NonNull String currentSegment, @NonNull ObjectPathFactory factory) {
    this.elements = elements;
    this.currentSegment = currentSegment;
    this.factory = factory;
  }

  @Unmodifiable
  public @NonNull Collection<String> elements() {
    return ImmutableArrayList.fromArray(this.elements);
  }

  public @NonNull String currentSegment() {
    return this.currentSegment;
  }

  @Contract(pure = true)
  public @NonNull ObjectPath append(@NonNull String element) {
    String[] newElements = ObjectPathUtil.addElement(this.elements, element);
    return new ObjectPath(newElements, element, this.factory);
  }

  public @NonNull String toFullPath() {
    return this.factory.joinPathElements(this.elements);
  }

  @Override
  public @NonNull String toString() {
    return this.toFullPath();
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.elements);
  }
}
