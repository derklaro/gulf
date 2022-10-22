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

package dev.derklaro.gulf.supplier;

import java.lang.reflect.Type;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

final class PrimitiveDefaultSupplier implements DefaultSupplier {

  public static final DefaultSupplier PRIMITIVE_DEFAULT_SUPPLIER = new PrimitiveDefaultSupplier();

  private static @Nullable Object getPrimitiveDefault(@NonNull Class<?> type) {
    if (type == boolean.class) {
      return Boolean.FALSE;
    } else if (type == char.class) {
      return '\0';
    } else if (type == byte.class) {
      return (byte) 0;
    } else if (type == short.class) {
      return (short) 0;
    } else if (type == int.class) {
      return 0;
    } else if (type == long.class) {
      return 0L;
    } else if (type == float.class) {
      return 0F;
    } else if (type == double.class) {
      return 0D;
    } else {
      return null;
    }
  }

  @Override
  public @Nullable Object apply(@NonNull Type type) {
    if (type instanceof Class<?>) {
      return getPrimitiveDefault((Class<?>) type);
    } else {
      return null;
    }
  }
}
