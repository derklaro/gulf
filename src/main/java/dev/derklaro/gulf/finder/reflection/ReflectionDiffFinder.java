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

package dev.derklaro.gulf.finder.reflection;

import dev.derklaro.gulf.Gulf;
import dev.derklaro.gulf.diff.Change;
import dev.derklaro.gulf.finder.DiffFinder;
import dev.derklaro.gulf.path.ObjectPath;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public final class ReflectionDiffFinder implements DiffFinder<Object> {

  private final Map<Class<?>, Collection<Map.Entry<Field, MethodHandle>>> reflectionCache = new ConcurrentHashMap<>();

  @Override
  public @NonNull Collection<Change<Object>> findChangesNullSafe(
    @NonNull Gulf gulf,
    @NonNull ObjectPath path,
    @NonNull Type fullType,
    @NonNull Object left,
    @NonNull Object right
  ) {
    Collection<Map.Entry<Field, MethodHandle>> reflectionData = this.reflectionCache.computeIfAbsent(
      left.getClass(),
      clazz -> ReflectionDataLookup.findFields(clazz, gulf.lookupPerType()));

    // compare both types based on the reflection data
    Collection<Change<Object>> changes = new ArrayList<>();
    for (Map.Entry<Field, MethodHandle> entry : reflectionData) {
      // get the field info
      Field field = entry.getKey();
      Type fieldType = field.getGenericType();

      // get both field values
      Object leftValue = this.getFieldValue(left, entry.getValue());
      Object rightValue = this.getFieldValue(right, entry.getValue());

      // get the defaulted field value
      Object normLeft = this.getDefaultFieldValue(gulf, fieldType, leftValue);
      Object normRight = this.getDefaultFieldValue(gulf, fieldType, rightValue);

      // get the diff between both objects (if any)
      ObjectPath fieldPath = path.append(field.getName());
      Collection<Change<Object>> detectedChanges = gulf.findChanges(fieldType, fieldPath, normLeft, normRight);

      // add all changes
      changes.addAll(detectedChanges);
    }

    // return all changes
    return changes;
  }

  private @Nullable Object getFieldValue(@NonNull Object instance, @NonNull MethodHandle fieldAccessor) {
    try {
      return fieldAccessor.invoke(instance);
    } catch (Throwable throwable) {
      return null;
    }
  }

  private @Nullable Object getDefaultFieldValue(@NonNull Gulf gulf, @NonNull Type type, @Nullable Object fieldValue) {
    if (fieldValue != null) {
      // field value already known, nothing to do
      return fieldValue;
    } else {
      // get the default supplier for the field type
      return gulf.getDefaultInstance(type);
    }
  }
}
