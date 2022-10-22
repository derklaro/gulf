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
import dev.derklaro.gulf.diff.array.ArrayElementAddOrRemove;
import dev.derklaro.gulf.diff.array.ArrayElementChange;
import dev.derklaro.gulf.diff.array.CollectionChange;
import dev.derklaro.gulf.diff.array.IndexedChange;
import dev.derklaro.gulf.finder.DiffFinder;
import dev.derklaro.gulf.path.ObjectPath;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import lombok.NonNull;

public final class CollectionDiffFinder implements DiffFinder<Collection<Object>> {

  @Override
  public @NonNull Collection<Change<Collection<Object>>> findChangesNullSafe(
    @NonNull Gulf gulf,
    @NonNull ObjectPath path,
    @NonNull Type fullType,
    @NonNull Collection<Object> left,
    @NonNull Collection<Object> right
  ) {
    int leftSize = left.size();
    int rightSize = right.size();
    // check if one collection has no more elements
    if (leftSize == 0 || rightSize == 0) {
      // check if there are any changes
      if (leftSize == 0 && rightSize == 0) {
        return Changes.none();
      }

      int idx = 0;
      Collection<IndexedChange<Object>> elementChanges;
      // check if all elements were removed from the left or from the right collection
      if (leftSize == 0) {
        elementChanges = new ArrayList<>(rightSize);
        // all elements of the right collection were removed
        for (Object element : right) {
          ArrayElementAddOrRemove<Object> change = new ArrayElementAddOrRemove<>(path, null, element, idx++);
          elementChanges.add(change);
        }
      } else {
        elementChanges = new ArrayList<>(leftSize);
        // all elements of the right collection were removed
        for (Object element : left) {
          ArrayElementAddOrRemove<Object> change = new ArrayElementAddOrRemove<>(path, element, null, idx++);
          elementChanges.add(change);
        }
      }

      // push out the change
      return Changes.singleton(new CollectionChange<>(path, left, right, elementChanges));
    }

    // wrap the iterator for both collections
    IndexedIteratorWrapper<Object> leftIter = new IndexedIteratorWrapper<>(left.iterator());
    IndexedIteratorWrapper<Object> rightIter = new IndexedIteratorWrapper<>(right.iterator());

    // loop over both iterators and find the changes
    Collection<IndexedChange<Object>> elementChanges = new ArrayList<>();
    while (leftIter.hasNext() && rightIter.hasNext()) {
      Object leftElement = leftIter.next();
      Object rightElement = rightIter.next();

      // check if both elements are null - no diff
      if (leftElement == null && rightElement == null) {
        continue;
      }

      // get the changes between the element on the left and right
      Type type = leftElement != null ? leftElement.getClass() : rightElement.getClass();
      Collection<Change<Object>> changes = gulf.findChanges(type, path, leftElement, rightElement);

      // add all changes for the current element if there are any
      if (!changes.isEmpty()) {
        // construct the change, both iterators are at the same index so it doesn't matter from which we take the index
        int idx = leftIter.index;
        ArrayElementChange<Object> change = new ArrayElementChange<>(path, changes, leftElement, rightElement, idx);

        elementChanges.add(change);
      }
    }

    boolean leftAtEnd = (leftIter.index + 1) == leftSize;
    boolean rightAtEnd = (rightIter.index + 1) == rightSize;
    // check if both iterators reached the end
    if (leftAtEnd && rightAtEnd) {
      return this.packChanges(path, left, right, elementChanges);
    }

    if (leftAtEnd) {
      // only the right side contains more elements
      while (rightIter.hasNext()) {
        // build the change
        Object element = rightIter.next();
        IndexedChange<Object> change = new ArrayElementAddOrRemove<>(path, null, element, rightIter.index);

        elementChanges.add(change);
      }
    } else {
      // only the left side contains more elements
      while (leftIter.hasNext()) {
        // build the change
        Object element = leftIter.next();
        IndexedChange<Object> change = new ArrayElementAddOrRemove<>(path, element, null, leftIter.index);

        elementChanges.add(change);
      }
    }

    // build the full changes
    return this.packChanges(path, left, right, elementChanges);
  }

  private @NonNull Collection<Change<Collection<Object>>> packChanges(
    @NonNull ObjectPath path,
    @NonNull Collection<Object> left,
    @NonNull Collection<Object> right,
    @NonNull Collection<IndexedChange<Object>> elementChanges
  ) {
    if (elementChanges.isEmpty()) {
      // no changes
      return Changes.none();
    } else {
      return Changes.singleton(new CollectionChange<>(path, left, right, elementChanges));
    }
  }

  private static final class IndexedIteratorWrapper<T> implements Iterator<T> {

    private final Iterator<T> delegate;
    private int index = -1;

    public IndexedIteratorWrapper(@NonNull Iterator<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
      return this.delegate.hasNext();
    }

    @Override
    public T next() {
      this.index++;
      return this.delegate.next();
    }
  }
}
