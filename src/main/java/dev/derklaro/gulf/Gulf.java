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

import static dev.derklaro.gulf.internal.Internals.newMapEntry;

import dev.derklaro.gulf.diff.Change;
import dev.derklaro.gulf.diff.Changes;
import dev.derklaro.gulf.finder.DiffFinder;
import dev.derklaro.gulf.finder.DiffFinders;
import dev.derklaro.gulf.finder.defaults.ArrayDiffFinder;
import dev.derklaro.gulf.finder.defaults.BooleanDiffFinder;
import dev.derklaro.gulf.finder.defaults.CharacterDiffFinder;
import dev.derklaro.gulf.finder.defaults.CollectionDiffFinder;
import dev.derklaro.gulf.finder.defaults.EnumDiffFinder;
import dev.derklaro.gulf.finder.defaults.MapDiffFinder;
import dev.derklaro.gulf.finder.defaults.NumberDiffFinder;
import dev.derklaro.gulf.finder.defaults.PatternDiffFinder;
import dev.derklaro.gulf.finder.reflection.ReflectionDiffFinder;
import dev.derklaro.gulf.internal.Internals;
import dev.derklaro.gulf.path.DefaultPathFactory;
import dev.derklaro.gulf.path.ObjectPath;
import dev.derklaro.gulf.path.ObjectPathFactory;
import dev.derklaro.gulf.supplier.DefaultSupplier;
import dev.derklaro.gulf.supplier.DefaultSuppliers;
import dev.derklaro.gulf.type.TypeMatcher;
import dev.derklaro.gulf.type.TypeMatchers;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public final class Gulf {

  private final Set<Map.Entry<TypeMatcher, DiffFinder<?>>> diffFinders;
  private final Set<Map.Entry<TypeMatcher, DefaultSupplier>> defaultSuppliers;

  private final ObjectPathFactory pathFactory;

  private final DiffFinder<Object> defaultDiffFinder;
  private final DefaultSupplier defaultDefaultSupplier;

  Gulf(@NonNull GulfBuilder builder) {
    // copy the maps
    this.diffFinders = new LinkedHashSet<>(builder.diffFinders);
    this.defaultSuppliers = new LinkedHashSet<>(builder.defaultSuppliers);

    // build the path factory
    this.pathFactory = new DefaultPathFactory(builder.rootPathIndicator, builder.pathSeparatorIndicator);

    // the default suppliers
    this.defaultDiffFinder = Internals.nonNullOrGet(builder.defaultDiffFinder, () -> new ReflectionDiffFinder());
    this.defaultDefaultSupplier = Internals.nonNullOrGet(
      builder.defaultDefaultSupplier,
      () -> DefaultSuppliers.normalizingPrimitives());

    // register the default equality checkers if requested
    if (builder.registerDefaults) {
      this.diffFinders.add(newMapEntry(TypeMatchers.extending(Map.class), new MapDiffFinder()));
      this.diffFinders.add(newMapEntry(TypeMatchers.exact(Pattern.class), new PatternDiffFinder()));
      this.diffFinders.add(newMapEntry(TypeMatchers.isClassAnd(Class::isEnum), new EnumDiffFinder()));
      this.diffFinders.add(newMapEntry(TypeMatchers.isClassAnd(Class::isArray), new ArrayDiffFinder()));
      this.diffFinders.add(newMapEntry(TypeMatchers.extending(Collection.class), new CollectionDiffFinder()));
      this.diffFinders.add(newMapEntry(TypeMatchers.inPackage("java"), DiffFinders.equalityBased()));
      this.diffFinders.add(newMapEntry(TypeMatchers.anyOf(Boolean.class, boolean.class), new BooleanDiffFinder()));
      this.diffFinders.add(newMapEntry(TypeMatchers.anyOf(Character.class, char.class), new CharacterDiffFinder()));
      this.diffFinders.add(newMapEntry(
        TypeMatchers.extending(Number.class)
          .or(TypeMatchers.anyOf(byte.class, short.class, int.class, long.class, float.class, double.class)),
        new NumberDiffFinder()));
    }
  }

  public static @NonNull GulfBuilder builder() {
    return new GulfBuilder();
  }

  public @Nullable Object getDefaultInstance(@NonNull Type type) {
    DefaultSupplier supplier = this.defaultDefaultSupplier;
    // find the matching default supplier for the given type, use the fallback one if none matches
    for (Map.Entry<TypeMatcher, DefaultSupplier> entry : this.defaultSuppliers) {
      if (entry.getKey().test(type)) {
        supplier = entry.getValue();
        break;
      }
    }
    // get the default value
    return supplier.apply(type);
  }

  public @NonNull Collection<Change<Object>> findChanges(@Nullable Object left, @Nullable Object right) {
    return this.findChanges(null, left, right);
  }

  public @NonNull Collection<Change<Object>> findChanges(
    @Nullable Type type,
    @Nullable Object left,
    @Nullable Object right
  ) {
    return this.findChanges(type, this.pathFactory.beginPath(), left, right);
  }

  public @NonNull Collection<Change<Object>> findChanges(
    @Nullable Type type,
    @NonNull ObjectPath path,
    @Nullable Object left,
    @Nullable Object right
  ) {
    // Check if both objects are null OR the type is specifically supplied
    // While it's unusual that when both objects are null there is a diff, there might be some checker which finds the
    // diff anyway, but we cannot detect the checker if both objects are null and no type is supplied, as the resolution
    // of the checker based on the type is impossible
    if (type == null && left == null && right == null) {
      return Changes.none();
    }

    // get the supplied element type or get if off the supplied objects' type
    Type objectType = Internals.nonNullOrGet(type, () -> Internals.getObjectType(left, right));

    // find the matching equality checker for the given type, use the fallback one if none matches
    DiffFinder<Object> diffFinder = this.defaultDiffFinder;
    for (Map.Entry<TypeMatcher, DiffFinder<?>> entry : this.diffFinders) {
      if (entry.getKey().test(objectType)) {
        //noinspection unchecked
        diffFinder = (DiffFinder<Object>) entry.getValue();
        break;
      }
    }

    // check the equality between the given objects
    return diffFinder.findChanges(this, path, objectType, left, right);
  }
}
