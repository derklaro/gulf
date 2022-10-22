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

package dev.derklaro.gulf.diff;

import dev.derklaro.gulf.path.ObjectPath;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class Change<T> {

  protected final T leftElement;
  protected final T rightElement;

  protected final ObjectPath path;

  public Change(@NonNull ObjectPath path, @Nullable T leftElement, @Nullable T rightElement) {
    this.path = path;
    this.leftElement = leftElement;
    this.rightElement = rightElement;
  }

  public @NonNull ObjectPath path() {
    return this.path;
  }

  public @Nullable T leftElement() {
    return this.leftElement;
  }

  public @Nullable T rightElement() {
    return this.rightElement;
  }
}
