package org.kr.metods;

import org.kr.controllers.KRController;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {

    // Метод для получения настроенного логгера
    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());

        // Проверка на наличие обработчиков
        if (logger.getHandlers().length > 0) {
            return logger; // Возвращаем, если обработчики уже установлены
        }

        try {
            // Устанавливаем файл для логов
            FileHandler fileHandler = new FileHandler(KRController.path() +"/System/logfile.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false); // Отключаем вывод в консоль
        } catch (IOException e) {
            logger.severe("Ошибка при настройке обработчика логирования: " + e.getMessage());
            e.printStackTrace();
        }

        return logger;
    }
}
