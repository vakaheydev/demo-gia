package config;

// Все настройки, зависящие от предметной области, собраны здесь.
// Чтобы адаптировать приложение под другую область — изменяйте только этот файл.
public class AppConfig {

    // Приложение
    public static final String APP_TITLE = "Обувь";
    public static final String FONT_FAMILY = "Times New Roman";
    public static final String BACKGROUND_COLOR = "#FFFFFF";

    // База данных
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/test-db";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "123";

    // Роли пользователей
    public static final String ROLE_ADMIN = "Администратор";
    public static final String ROLE_MANAGER = "Менеджер";

    // Изображения
    public static final String IMAGES_DIR = "images";
    public static final String PLACEHOLDER_FILENAME = "picture.png";
    public static final int PHOTO_WIDTH = 300;
    public static final int PHOTO_HEIGHT = 200;
    public static final int TABLE_PHOTO_WIDTH = 75;
    public static final int TABLE_PHOTO_HEIGHT = 50;

    // Цвета подсветки строк
    public static final int HIGH_SALE_THRESHOLD = 15;
    public static final String HIGH_SALE_COLOR = "#2E8B57";
    public static final String OUT_OF_STOCK_COLOR = "lightblue";

    // Цвета UI
    public static final String LOGIN_BTN_COLOR = "#00FA9A";
    public static final String TOP_BAR_COLOR = "#7FFF00";
}
