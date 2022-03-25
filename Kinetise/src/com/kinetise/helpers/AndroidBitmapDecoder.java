package com.kinetise.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import java.io.*;

public class AndroidBitmapDecoder {

	public static final int BUFFER_SIZE = 8096;
    public static final int MINIMUM_MARK = BUFFER_SIZE*32;

    /**
	 * Decodes bitmap from file, with required Width and Height
	 * @param path of file from which we decode bitmap
	 * @param reqWidth width that we want bitmap to have
	 * @param reqHeight height of bitmap we want to get
	 * @return bitmap in given size
	 */
	public static Bitmap decodeBitmapFromFilePath(String path,
												  int reqWidth, int reqHeight) throws IOException {

			return decodeBitmapFromStream(new FileInputStream(path), reqWidth, reqHeight);
	}

    private static InputStream getInputStreamToBuffer(BufferedInputStream stream, int bufferSize) throws IOException{
        byte[] buffer = new byte[bufferSize];
        stream.read(buffer);
        return new ByteArrayInputStream(buffer);
    }

	public static Bitmap decodeBitmapFromStreamAndRotate(InputStream stream, int reqWidth, int reqHeight) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(stream,BUFFER_SIZE);
		bis.mark(BUFFER_SIZE);
		int orientation = ExifHelper.readExifOrientation(getInputStreamToBuffer(bis, BUFFER_SIZE));
        bis.reset();
		Bitmap bitmap = AndroidBitmapDecoder.decodeBitmapFromStream(bis, reqWidth, reqHeight);
		if(orientation!= ExifInterface.ORIENTATION_NORMAL && bitmap!=null){
			bitmap = ExifHelper.rotateBitmapFromExifTag(bitmap, orientation);
		}
		return bitmap;
	}

	public static Bitmap decodeBitmapFromStream(InputStream stream, int reqWidth, int reqHeight) throws IOException {
		//We use MinimumMarkBufferedInputStream because decodeStream marks stream at 1k, and this is sometimes too
		//low to read image size with inJustDecodeBounds https://code.google.com/p/android/issues/detail?id=57578
		MinimumMarkBufferedInputStream bis = null;
        Bitmap result = null;
		try {
			bis = new MinimumMarkBufferedInputStream(stream, BUFFER_SIZE);
			bis.mark(MINIMUM_MARK);
			if (reqWidth > 0 && reqHeight > 0) {
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(bis, null, options);
				bis.reset();
				bis.mark(0);
				options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
				options.inJustDecodeBounds = false;
				result = BitmapFactory.decodeStream(bis, null, options);
			} else {
				result = BitmapFactory.decodeStream(bis);
			}
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
        return result;
	}

	public static Bitmap decodeBitmapFromStreamForMax(InputStream stream, int maxSize) throws IOException {
		//We use MinimumMarkBufferedInputStream because decodeStream marks stream at 1k, and this is sometimes too
		//low to read image size with inJustDecodeBounds https://code.google.com/p/android/issues/detail?id=57578
		MinimumMarkBufferedInputStream bis = null;
		Bitmap result = null;
		try {
			bis = new MinimumMarkBufferedInputStream(stream, BUFFER_SIZE);
			bis.mark(MINIMUM_MARK);
			if (maxSize > 0) {
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(bis, null, options);
				bis.reset();
				bis.mark(0);
				options.inSampleSize = calculateInSampleSize(options, maxSize);
				options.inJustDecodeBounds = false;
				result = BitmapFactory.decodeStream(bis, null, options);
			} else {
				result = BitmapFactory.decodeStream(bis);
			}
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
		return result;
	}

	/**
	 * Calculates sample size of image
	 * @param options Bitmap options
	 * @param reqWidth width we want image to have
	 * @param reqHeight height of image that we want
	 * @return sample size. check {@link BitmapFactory.Options#inSampleSize} for information how sample size is used
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = Math.min(heightRatio,widthRatio);
		}

		return inSampleSize;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int maxSize) {
		final int height = options.outHeight;
		final int width = options.outWidth;

		int reqWidth;
		int reqHeight;

		if (width > height) {
			reqWidth = maxSize;
			reqHeight = Math.round(height * maxSize / (float) width);
		} else {
			reqHeight = maxSize;
			reqWidth = Math.round(width * maxSize / (float) height);
		}

		return calculateInSampleSize(options, reqWidth, reqHeight);
	}

	/**
	 * BufferedInputStream with minimum mark set to 64k
	 */
	static class MinimumMarkBufferedInputStream extends BufferedInputStream {


        private int mMinimumMark = MINIMUM_MARK;

        public MinimumMarkBufferedInputStream(InputStream in) {
			super(in);
		}

		public MinimumMarkBufferedInputStream(InputStream in, int size) {
            super(in, size);
        }

		@Override
		public synchronized void mark(int readlimit) {
			super.mark(Math.max(readlimit, mMinimumMark));
		}

    }
}
