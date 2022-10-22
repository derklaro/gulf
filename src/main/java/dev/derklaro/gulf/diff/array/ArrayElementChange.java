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

package dev.derklaro.gulf.diff.array;

import dev.derklaro.gulf.collection.ImmutableArrayList;
import dev.derklaro.gulf.diff.Change;
import dev.derklaro.gulf.path.ObjectPath;
import java.util.Collection;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class ArrayElementChange<T> extends IndexedChange<T> {

  private final Collection<Change<T>> changes;

  public ArrayElementChange(
    @NonNull ObjectPath path,
    @NonNull Collection<Change<T>> changes,
    @Nullable T leftElement,
    @Nullable T rightElement,
    int index
  ) {
    super(path, leftElement, rightElement, index);
    this.changes = ImmutableArrayList.fromCollection(changes);
  }

  @Unmodifiable
  public @NonNull Collection<Change<T>> changes() {
    return this.changes;
  }
}
