package utils;

import config.AppConfig;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static Image loadForTable(String photoPath) {
        return load(photoPath, AppConfig.TABLE_PHOTO_WIDTH, AppConfig.TABLE_PHOTO_HEIGHT);
    }

    public static Image loadForForm(String photoPath) {
        return load(photoPath, AppConfig.PHOTO_WIDTH, AppConfig.PHOTO_HEIGHT);
    }

    private static Image load(String photoPath, double w, double h) {
        if (photoPath != null && !photoPath.isBlank()) {
            File f = new File(photoPath);
            if (f.exists()) {
                return new Image(f.toURI().toString(), w, h, true, true);
            }
        }
        return loadPlaceholder(w, h);
    }

    private static Image loadPlaceholder(double w, double h) {
        File placeholder = new File(AppConfig.PLACEHOLDER_FILENAME);
        if (placeholder.exists()) {
            return new Image(placeholder.toURI().toString(), w, h, true, true);
        }
        // Пробуем загрузить из ресурсов classpath
        var resource = ImageUtils.class.getResource("/" + AppConfig.PLACEHOLDER_FILENAME);
        if (resource != null) {
            return new Image(resource.toString(), w, h, true, true);
        }
        return null;
    }

    // Копирует файл в папку images/ с изменением размера до PHOTO_WIDTH x PHOTO_HEIGHT.
    // Возвращает путь к новому файлу.
    public static String copyResized(File src) throws IOException {
        File dir = new File(AppConfig.IMAGES_DIR);
        dir.mkdirs();

        BufferedImage original = ImageIO.read(src);
        BufferedImage result = new BufferedImage(AppConfig.PHOTO_WIDTH, AppConfig.PHOTO_HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, AppConfig.PHOTO_WIDTH, AppConfig.PHOTO_HEIGHT);

        // Масштабируем с сохранением пропорций
        double scale = Math.min(
                (double) AppConfig.PHOTO_WIDTH / original.getWidth(),
                (double) AppConfig.PHOTO_HEIGHT / original.getHeight()
        );
        int sw = (int) (original.getWidth() * scale);
        int sh = (int) (original.getHeight() * scale);
        int x = (AppConfig.PHOTO_WIDTH - sw) / 2;
        int y = (AppConfig.PHOTO_HEIGHT - sh) / 2;
        g.drawImage(original, x, y, sw, sh, null);
        g.dispose();

        String ext = src.getName().toLowerCase().endsWith(".png") ? "png" : "jpg";
        String filename = System.currentTimeMillis() + "." + ext;
        String destPath = AppConfig.IMAGES_DIR + "/" + filename;
        ImageIO.write(result, ext, new File(destPath));
        return destPath;
    }

    public static void deleteFile(String path) {
        if (path != null && !path.isBlank()) {
            new File(path).delete();
        }
    }
}
