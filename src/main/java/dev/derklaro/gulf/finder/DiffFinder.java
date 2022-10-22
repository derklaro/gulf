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

import dev.derklaro.gulf.Gulf;
import dev.derklaro.gulf.diff.Change;
import dev.derklaro.gulf.diff.Changes;
import dev.derklaro.gulf.path.ObjectPath;
import java.lang.reflect.Type;
import java.util.Collection;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface DiffFinder<T> {

  default @NonNull Collection<Change<T>> findChanges(
    @NonNull Gulf gulf,
    @NonNull ObjectPath path,
    @NonNull Type fullType,
    @Nullable T left,
    @Nullable T right
  ) {
    // check if one of them is null
    if (left == null || right == null) {
      if (left == null && right == null) {
        return Changes.none();
      } else {
        Change<T> change = new Change<>(path, left, right);
        return Changes.singleton(change);
      }
    }
    // delegate to the null safe method
    return this.findChangesNullSafe(gulf, path, fullType, left, right);
  }

  @NonNull Collection<Change<T>> findChangesNullSafe(
    @NonNull Gulf gulf,
    @NonNull ObjectPath path,
    @NonNull Type fullType,
    @NonNull T left,
    @NonNull T right);
}
