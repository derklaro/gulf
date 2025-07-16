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

package dev.derklaro.gulf.seed;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public final class SeedA {

  public static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

  private final UUID id;

  // random mix to verify that everything is working
  private final SeedB seedB;
  private final Collection<SeedB> colOfSeedB;
  private final Map<String, Collection<SeedB>> mapColOfSeedB;

  public SeedA(UUID id, SeedB seedB, Collection<SeedB> colOfSeedB, Map<String, Collection<SeedB>> mapColOfSeedB) {
    this.id = id;
    this.seedB = seedB;
    this.colOfSeedB = colOfSeedB;
    this.mapColOfSeedB = mapColOfSeedB;
  }

  public UUID id() {
    return this.id;
  }

  public SeedB seedB() {
    return this.seedB;
  }

  public Collection<SeedB> colOfSeedB() {
    return this.colOfSeedB;
  }

  public Map<String, Collection<SeedB>> mapColOfSeedB() {
    return this.mapColOfSeedB;
  }
}
