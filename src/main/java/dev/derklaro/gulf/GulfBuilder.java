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

package dev.derklaro.gulf;

import dev.derklaro.gulf.finder.DiffFinder;
import dev.derklaro.gulf.internal.Internals;
import dev.derklaro.gulf.supplier.DefaultSupplier;
import dev.derklaro.gulf.type.TypeMatcher;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;

public final class GulfBuilder {

  final Set<Map.Entry<TypeMatcher, DiffFinder<?>>> diffFinders = new LinkedHashSet<>();
  final Set<Map.Entry<TypeMatcher, DefaultSupplier>> defaultSuppliers = new LinkedHashSet<>();

  boolean registerDefaults = true;

  String rootPathIndicator = "$";
  String pathSeparatorIndicator = ".";

  DiffFinder<Object> defaultDiffFinder;
  DefaultSupplier defaultDefaultSupplier;

  GulfBuilder() {
  }

  public @NonNull GulfBuilder withoutDefaults() {
    this.registerDefaults = false;
    return this;
  }

  public @NonNull GulfBuilder rootPathIndicator(@NonNull String rootPathIndicator) {
    this.rootPathIndicator = rootPathIndicator;
    return this;
  }

  public @NonNull GulfBuilder pathSeparatorIndicator(@NonNull String pathSeparatorIndicator) {
    this.pathSeparatorIndicator = pathSeparatorIndicator;
    return this;
  }

  public @NonNull GulfBuilder withDefaultSupplier(@NonNull TypeMatcher matcher, @NonNull DefaultSupplier supplier) {
    this.defaultSuppliers.add(Internals.newMapEntry(matcher, supplier));
    return this;
  }

  public @NonNull GulfBuilder withDiffFinder(@NonNull TypeMatcher matcher, @NonNull DiffFinder<?> diffFinder) {
    this.diffFinders.add(Internals.newMapEntry(matcher, diffFinder));
    return this;
  }

  public @NonNull GulfBuilder defaultDiffFinder(@NonNull DiffFinder<Object> defaultDiffFinder) {
    this.defaultDiffFinder = defaultDiffFinder;
    return this;
  }

  public @NonNull GulfBuilder defaultDefaultSupplier(@NonNull DefaultSupplier defaultDefaultSupplier) {
    this.defaultDefaultSupplier = defaultDefaultSupplier;
    return this;
  }

  public @NonNull Gulf build() {
    return new Gulf(this);
  }
}
