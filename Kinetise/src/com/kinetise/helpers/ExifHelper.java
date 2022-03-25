package com.kinetise.helpers;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.kinetise.support.logger.Logger;

import java.io.*;

/**
 * Klasa pomocnicza do obslugi metadanych obrazkow
 * <p/>
 * Created by Kuba Komorowski on 2014-07-18.
 */
public class ExifHelper {

    /**
     * Obrocenie bitmapy zgodnie z wartoscia taga EXIF
     *
     * @param bitmap           bitmapa, ktora zostanie obrocona
     * @param rotationTagValue wartosc tagu "Rotation" w metadanych
     */
    public static Bitmap rotateBitmapFromExifTag(Bitmap bitmap, int rotationTagValue) {
        Matrix matrix = new Matrix();

        switch (rotationTagValue) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                break;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int extractExifOrientationTagFromFile(String filePath) {
        try {
            ExifInterface ei = new ExifInterface(filePath);
            return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException ioe) {
            Logger.w("ExifHelper","extractExifOrientationTagFromFile","Exif data not found in file " + filePath);
            return 0;
        }
    }

    public static int extractExifOrientationTagFromStream(BufferedInputStream inputStream) {
        int orientation = 0;

        try {
            Metadata metadata;
            metadata = ImageMetadataReader.readMetadata(cloneInputeStream(inputStream));

            ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);

            orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orientation;
    }

    private static ByteArrayInputStream cloneInputeStream(BufferedInputStream inputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     *
     * @param stream to image to read orientation from
     * @return read orientation, or {@link ExifInterface#ORIENTATION_NORMAL}
     */
    public static int readExifOrientation(InputStream stream){
        try{
            Metadata metadata = ImageMetadataReader.readMetadata(stream);
            ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
            if(directory !=null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)){
                return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        } catch (ImageProcessingException ipe){
            Logger.w("Exif processing error","File type unknown, or exif data invalid");
            ipe.printStackTrace();
        } catch (MetadataException me){
            Logger.w("Exif processing error","No value exists for orientation tag or if it cannot be converted to an int");
            me.printStackTrace();
        } catch (NullPointerException npe){
            npe.printStackTrace();
        } catch (VerifyError ve){
            ve.printStackTrace();
        }
        return ExifInterface.ORIENTATION_NORMAL;
    }
}
