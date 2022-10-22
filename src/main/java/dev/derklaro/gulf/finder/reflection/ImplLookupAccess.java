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

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import lombok.NonNull;
import sun.misc.Unsafe;

final class ImplLookupAccess {

  private static final Object SOME_FIELD = new Object();

  private ImplLookupAccess() {
    throw new UnsupportedOperationException();
  }

  public static @NonNull MethodHandles.Lookup findLookup() {
    try {
      // get the unsafe instance
      Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      Unsafe u = (Unsafe) theUnsafe.get(null);

      // get the field (inaccessible)
      Field inaccessibleField = ImplLookupAccess.class.getDeclaredField("SOME_FIELD");
      // get the field (accessible)
      Field accessibleField = ImplLookupAccess.class.getDeclaredField("SOME_FIELD");
      accessibleField.setAccessible(true);

      // find the offset for the "override" boolean
      long offset = -1;
      for (long off = 8; off < 128; off++) {
        if (u.getByte(inaccessibleField, off) == 0 && u.getByte(accessibleField, off) == 1) {
          offset = off;
          break;
        }
      }

      // check if we got the offset
      if (offset != -1) {
        // get the lookup field
        Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
        u.putByte(implLookupField, offset, (byte) 1);
        return (MethodHandles.Lookup) implLookupField.get(null);
      }
    } catch (Exception ignored) {
    }

    // unable to get the trusted lookup instance
    return MethodHandles.lookup();
  }
}
