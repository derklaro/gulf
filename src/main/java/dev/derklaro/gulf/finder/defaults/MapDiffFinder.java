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
import dev.derklaro.gulf.diff.map.KeyedChange;
import dev.derklaro.gulf.diff.map.MapChange;
import dev.derklaro.gulf.diff.map.MapEntryAddOrRemove;
import dev.derklaro.gulf.diff.map.MapEntryChange;
import dev.derklaro.gulf.finder.DiffFinder;
import dev.derklaro.gulf.path.ObjectPath;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;

public final class MapDiffFinder implements DiffFinder<Map<Object, Object>> {

  @Override
  public @NonNull Collection<Change<Map<Object, Object>>> findChangesNullSafe(
    @NonNull Gulf gulf,
    @NonNull ObjectPath path,
    @NonNull Type fullType,
    @NonNull Map<Object, Object> left,
    @NonNull Map<Object, Object> right
  ) {
    // get the entries of the map
    Set<Map.Entry<Object, Object>> leftEntries = left.entrySet();
    Set<Map.Entry<Object, Object>> rightEntries = right.entrySet();

    // loop over the left entries and check for remove & changes
    Collection<KeyedChange<Object, Object>> entryChanges = new ArrayList<>();
    for (Map.Entry<Object, Object> entry : leftEntries) {
      Object key = entry.getKey();
      Object value = entry.getValue();

      // check if the element still exists in the right map
      if (right.containsKey(key)) {
        Object rightValue = right.get(entry.getKey());
        if (value == null && rightValue == null) {
          // no change
          continue;
        }

        // check if there are differences between the left and right value
        Type type = value != null ? value.getClass() : rightValue.getClass();
        Collection<Change<Object>> changes = gulf.findChanges(type, path, value, rightValue);

        // add the change if any way found
        if (!changes.isEmpty()) {
          MapEntryChange<Object, Object> change = new MapEntryChange<>(path, key, value, rightValue, changes);
          entryChanges.add(change);
        }
      } else {
        // the element was removed from the right map
        KeyedChange<Object, Object> change = new MapEntryAddOrRemove<>(path, key, value, null);
        entryChanges.add(change);
      }
    }

    // loop over the right entries to find removed elements
    for (Map.Entry<Object, Object> entry : rightEntries) {
      Object key = entry.getKey();
      Object value = entry.getValue();

      if (!left.containsKey(key)) {
        // the element was removed from the left map
        KeyedChange<Object, Object> change = new MapEntryAddOrRemove<>(path, key, null, value);
        entryChanges.add(change);
      }
    }

    // compile the changes if there are any
    if (!entryChanges.isEmpty()) {
      return Changes.singleton(new MapChange<>(path, entryChanges, left, right));
    } else {
      // no changes
      return Changes.none();
    }
  }
}
