package org.koulibrary.koulibraryreservationapp.configs;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.koulibrary.koulibraryreservationapp.exceptions.QrGenerationException;

import java.io.ByteArrayOutputStream;

public final class QrImageGenerator {

    private QrImageGenerator() {}

    public static byte[] toPng(String content, int size) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, size, size);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new QrGenerationException("Failed to generate QR image: " + e.getMessage());
        }
    }
}
