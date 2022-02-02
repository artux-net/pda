package net.artux.pda.ui.fragments.profile.helpers;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.Target;

import java.util.logging.Handler;

public final class GlideUtil {
  private static final int HASH_MULTIPLIER = 31;
  private static final int HASH_ACCUMULATOR = 17;
  private static final char[] HEX_CHAR_ARRAY = "0123456789abcdef".toCharArray();
  // 32 bytes from sha-256 -> 64 hex chars.
  private static final char[] SHA_256_CHARS = new char[64];
  @Nullable
  private static volatile Handler mainThreadHandler;

  private GlideUtil() {
    // Utility class.
  }

  /** Returns the hex string of the given byte array representing a SHA256 hash. */
  @NonNull
  public static String sha256BytesToHex(@NonNull byte[] bytes) {
    synchronized (SHA_256_CHARS) {
      return bytesToHex(bytes, SHA_256_CHARS);
    }
  }

  // Taken from:
  // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
  // /9655275#9655275
  @SuppressWarnings("PMD.UseVarargs")
  @NonNull
  private static String bytesToHex(@NonNull byte[] bytes, @NonNull char[] hexChars) {
    int v;
    for (int j = 0; j < bytes.length; j++) {
      v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_CHAR_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_CHAR_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  /**
   * Returns the allocated byte size of the given bitmap.
   *
   * @see #getBitmapByteSize(android.graphics.Bitmap)
   * @deprecated Use {@link #getBitmapByteSize(android.graphics.Bitmap)} instead. Scheduled to be
   *     removed in Glide 4.0.
   */
  @Deprecated
  public static int getSize(@NonNull Bitmap bitmap) {
    return getBitmapByteSize(bitmap);
  }

  /** Returns the in memory size of the given {@link Bitmap} in bytes. */
  @TargetApi(Build.VERSION_CODES.KITKAT)
  public static int getBitmapByteSize(@NonNull Bitmap bitmap) {
    // The return value of getAllocationByteCount silently changes for recycled bitmaps from the
    // internal buffer size to row bytes * height. To avoid random inconsistencies in caches, we
    // instead assert here.
    if (bitmap.isRecycled()) {
      throw new IllegalStateException(
          "Cannot obtain size for recycled Bitmap: "
              + bitmap
              + "["
              + bitmap.getWidth()
              + "x"
              + bitmap.getHeight()
              + "] "
              + bitmap.getConfig());
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // Workaround for KitKat initial release NPE in Bitmap, fixed in MR1. See issue #148.
      try {
        return bitmap.getAllocationByteCount();
      } catch (
          @SuppressWarnings("PMD.AvoidCatchingNPE")
          NullPointerException e) {
        // Do nothing.
      }
    }
    return bitmap.getHeight() * bitmap.getRowBytes();
  }

  /**
   * Returns the in memory size of {@link android.graphics.Bitmap} with the given width, height, and
   * {@link android.graphics.Bitmap.Config}.
   */
  public static int getBitmapByteSize(int width, int height, @Nullable Bitmap.Config config) {
    return width * height * getBytesPerPixel(config);
  }

  private static int getBytesPerPixel(@Nullable Bitmap.Config config) {
    // A bitmap by decoding a GIF has null "config" in certain environments.
    if (config == null) {
      config = Bitmap.Config.ARGB_8888;
    }

    int bytesPerPixel;
    switch (config) {
      case ALPHA_8:
        bytesPerPixel = 1;
        break;
      case RGB_565:
      case ARGB_4444:
        bytesPerPixel = 2;
        break;
      case RGBA_F16:
        bytesPerPixel = 8;
        break;
      case ARGB_8888:
      default:
        bytesPerPixel = 4;
        break;
    }
    return bytesPerPixel;
  }

  /** Returns true if width and height are both > 0 and/or equal to {@link Target#SIZE_ORIGINAL}. */
  public static boolean isValidDimensions(int width, int height) {
    return isValidDimension(width) && isValidDimension(height);
  }

  private static boolean isValidDimension(int dimen) {
    return dimen > 0 || dimen == Target.SIZE_ORIGINAL;
  }

  /**
   * Throws an {@link java.lang.IllegalArgumentException} if called on a thread other than the main
   * thread.
   */
  public static void assertMainThread() {
    if (!isOnMainThread()) {
      throw new IllegalArgumentException("You must call this method on the main thread");
    }
  }

  /** Throws an {@link java.lang.IllegalArgumentException} if called on the main thread. */
  public static void assertBackgroundThread() {
    if (!isOnBackgroundThread()) {
      throw new IllegalArgumentException("You must call this method on a background thread");
    }
  }

  /** Returns {@code true} if called on the main thread, {@code false} otherwise. */
  public static boolean isOnMainThread() {
    return Looper.myLooper() == Looper.getMainLooper();
  }

  /** Returns {@code true} if called on a background thread, {@code false} otherwise. */
  public static boolean isOnBackgroundThread() {
    return !isOnMainThread();
  }


}
