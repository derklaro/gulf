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
import dev.derklaro.gulf.diff.array.ArrayChange;
import dev.derklaro.gulf.diff.array.ArrayElementAddOrRemove;
import dev.derklaro.gulf.diff.array.ArrayElementChange;
import dev.derklaro.gulf.diff.array.IndexedChange;
import dev.derklaro.gulf.finder.DiffFinder;
import dev.derklaro.gulf.path.ObjectPath;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import lombok.NonNull;

public final class ArrayDiffFinder implements DiffFinder<Object[]> {

  @Override
  public @NonNull Collection<Change<Object[]>> findChangesNullSafe(
    @NonNull Gulf gulf,
    @NonNull ObjectPath path,
    @NonNull Type fullType,
    @NonNull Object[] left,
    @NonNull Object[] right
  ) {
    int leftSize = left.length;
    int rightSize = right.length;
    // check if one collection has no more elements
    if (leftSize == 0 || rightSize == 0) {
      // check if there are any changes
      if (leftSize == 0 && rightSize == 0) {
        return Changes.none();
      }

      Collection<IndexedChange<Object>> elementChanges;
      // check if all elements were removed from the left or from the right collection
      if (leftSize == 0) {
        elementChanges = new ArrayList<>(rightSize);
        // all elements of the right collection were removed
        for (int idx = 0; idx < rightSize; idx++) {
          ArrayElementAddOrRemove<Object> change = new ArrayElementAddOrRemove<>(path, null, right[idx], idx);
          elementChanges.add(change);
        }
      } else {
        elementChanges = new ArrayList<>(leftSize);
        // all elements of the right collection were removed
        for (int idx = 0; idx < leftSize; idx++) {
          ArrayElementAddOrRemove<Object> change = new ArrayElementAddOrRemove<>(path, left[idx], null, idx);
          elementChanges.add(change);
        }
      }

      // push out the change
      return Changes.singleton(new ArrayChange<>(path, left, right, elementChanges));
    }

    // loop over both arrays and find the changes
    int idx;
    Collection<IndexedChange<Object>> elementChanges = new ArrayList<>();
    for (idx = 0; idx < rightSize && idx < leftSize; idx++) {
      Object leftElement = left[idx];
      Object rightElement = right[idx];

      // check if both element are null - no diff
      if (leftElement == null && rightElement == null) {
        continue;
      }

      // get the changes between the element on the left and right
      Type type = leftElement != null ? leftElement.getClass() : rightElement.getClass();
      Collection<Change<Object>> changes = gulf.findChanges(type, path, leftElement, rightElement);

      // add all changes for the current element if there are any
      if (!changes.isEmpty()) {
        // construct & register the change
        ArrayElementChange<Object> change = new ArrayElementChange<>(path, changes, leftElement, rightElement, idx);
        elementChanges.add(change);
      }
    }

    boolean leftAtEnd = idx == leftSize;
    boolean rightAtEnd = idx == rightSize;
    // check if both iterators reached the end
    if (leftAtEnd && rightAtEnd) {
      return this.packChanges(path, left, right, elementChanges);
    }

    if (leftAtEnd) {
      // only the right side contains more elements
      for (; idx < rightSize; idx++) {
        IndexedChange<Object> change = new ArrayElementAddOrRemove<>(path, null, right[idx], idx);
        elementChanges.add(change);
      }
    } else {
      // only the left side contains more elements
      for (; idx < leftSize; idx++) {
        IndexedChange<Object> change = new ArrayElementAddOrRemove<>(path, left[idx], null, idx);
        elementChanges.add(change);
      }
    }

    // build the full changes
    return this.packChanges(path, left, right, elementChanges);
  }

  private @NonNull Collection<Change<Object[]>> packChanges(
    @NonNull ObjectPath path,
    @NonNull Object[] left,
    @NonNull Object[] right,
    @NonNull Collection<IndexedChange<Object>> elementChanges
  ) {
    if (elementChanges.isEmpty()) {
      // no changes
      return Changes.none();
    } else {
      return Changes.singleton(new ArrayChange<>(path, left, right, elementChanges));
    }
  }
}
