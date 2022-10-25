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

package dev.derklaro.gulf.path;

import lombok.NonNull;

public final class DefaultPathFactory implements ObjectPathFactory {

  private static final String[] EMPTY_ELEMENTS = new String[0];

  private final String rootIndicator;
  private final String pathDelimiter;

  public DefaultPathFactory(@NonNull String rootIndicator, @NonNull String pathDelimiter) {
    this.rootIndicator = rootIndicator;
    this.pathDelimiter = pathDelimiter;
  }

  @Override
  public @NonNull ObjectPath beginPath() {
    return new ObjectPath(EMPTY_ELEMENTS, this.rootIndicator, this);
  }

  @Override
  public @NonNull String joinPathElements(@NonNull String[] elements) {
    // check if only the root indicator is needed (if there are no elements yet)
    if (elements.length == 0) {
      return this.rootIndicator;
    }

    // connect the element and check if we need to prepend the root indicator
    String connectedElements = String.join(this.pathDelimiter, elements);
    if (this.rootIndicator.isEmpty()) {
      return connectedElements;
    }

    // append the root indicator
    return this.rootIndicator + this.pathDelimiter + connectedElements;
  }
}
