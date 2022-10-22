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

package dev.derklaro.gulf.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class MapUtil {

  public static <K, V> Map<K, V> of(K k1, V v1) {
    return decorate(map -> map.put(k1, v1));
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
    return decorate(map -> {
      map.put(k1, v1);
      map.put(k2, v2);
    });
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    return decorate(map -> {
      map.put(k1, v1);
      map.put(k2, v2);
      map.put(k3, v3);
    });
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return decorate(map -> {
      map.put(k1, v1);
      map.put(k2, v2);
      map.put(k3, v3);
      map.put(k4, v4);
    });
  }

  public static <K, V> Map<K, V> decorate(Consumer<Map<K, V>> decorator) {
    Map<K, V> map = new HashMap<>();
    decorator.accept(map);
    return map;
  }
}
