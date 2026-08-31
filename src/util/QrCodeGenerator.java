package util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Generates QR code images for employees.
 *
 * The QR payload is simply the employee's ID (as text), which is what the
 * Time In/Out scanner expects to read back.
 */
public class QrCodeGenerator {

    private static final String QR_DIR = "qrcodes";
    private static final int QR_SIZE = 500;

    /**
     * Generates {@code qrcodes/<employeeId>.png}, creating the folder if needed.
     *
     * @param employeeId the employee ID encoded in the QR code
     * @return the path of the generated file
     */
    public static String generate(int employeeId) throws Exception {
        File qrDir = new File(QR_DIR);
        if (!qrDir.exists()) {
            qrDir.mkdir();
        }

        String filePath = QR_DIR + "/" + employeeId + ".png";
        BitMatrix matrix = new MultiFormatWriter()
                .encode(Integer.toString(employeeId), BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);
        System.out.println("QR Code created: " + filePath);
        return filePath;
    }

    /**
     * @return true if a QR code image already exists for the given employee ID
     */
    public static boolean exists(int employeeId) {
        return new File(QR_DIR, employeeId + ".png").exists();
    }
}
