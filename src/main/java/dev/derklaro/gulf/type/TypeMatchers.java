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

package dev.derklaro.gulf.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Predicate;
import lombok.NonNull;

public final class TypeMatchers {

  private TypeMatchers() {
    throw new UnsupportedOperationException();
  }

  public static @NonNull TypeMatcher exact(@NonNull Type type) {
    return input -> input.equals(type);
  }

  public static @NonNull TypeMatcher isClassAnd(@NonNull Predicate<Class<?>> and) {
    return input -> {
      if (input instanceof Class<?>) {
        return and.test((Class<?>) input);
      } else if (input instanceof ParameterizedType) {
        Type raw = ((ParameterizedType) input).getRawType();
        if (raw instanceof Class<?>) {
          return and.test((Class<?>) raw);
        }
      }
      return false;
    };
  }

  public static @NonNull TypeMatcher extending(@NonNull Class<?> type) {
    return isClassAnd(clazz -> type.isAssignableFrom(clazz));
  }

  public static @NonNull TypeMatcher inPackage(@NonNull String packagePrefix) {
    return isClassAnd(clazz -> {
      Package pkg = clazz.getPackage();
      return pkg != null && pkg.getName().startsWith(packagePrefix);
    });
  }

  public static @NonNull TypeMatcher anyOf(@NonNull Type... types) {
    return type -> {
      for (Type candidate : types) {
        if (type.equals(candidate)) {
          return true;
        }
      }
      return false;
    };
  }
}
