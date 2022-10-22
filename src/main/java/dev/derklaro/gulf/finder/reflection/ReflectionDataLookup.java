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

import dev.derklaro.gulf.internal.Internals;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

final class ReflectionDataLookup {

  private static final MethodHandles.Lookup LOOKUP = ImplLookupAccess.findLookup();
  private static final MethodType GETTER_GENERIC_TYPE = MethodType.methodType(Object.class, Object.class);

  private ReflectionDataLookup() {
    throw new UnsupportedOperationException();
  }

  public static @NonNull Collection<Map.Entry<Field, MethodHandle>> findFields(@NonNull Class<?> clazz) {
    Collection<Map.Entry<Field, MethodHandle>> fields = new LinkedHashSet<>();
    do {
      for (Field field : clazz.getDeclaredFields()) {
        if (!Modifier.isStatic(field.getModifiers())) {
          MethodHandle handle = fieldHandleIfAccessible(field);
          if (handle != null) {
            fields.add(Internals.newMapEntry(field, handle));
          }
        }
      }
    } while ((clazz = clazz.getSuperclass()) != Object.class);

    return fields;
  }

  private static @Nullable MethodHandle fieldHandleIfAccessible(@NonNull Field field) {
    try {
      // unreflect the field and convert it to a generic handle
      return LOOKUP.unreflectGetter(field).asType(GETTER_GENERIC_TYPE);
    } catch (Exception exception) {
      return null;
    }
  }
}
