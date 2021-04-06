package ru.home.mywizard_bot.botapi;

/**Возможные состояния бота
 */

public enum BotState {
    ASK_DESTINY,
    ASK_NAME,
    ASK_LAST_NAME,
    ASK_ADRES,
    ASK_COUNTRY,
    ASK_NUMBER,
    FILLING_PROFILE,
    PROFILE_FILLED,
    SHOW_USER_PROFILE,
    SHOW_MAIN_MENU,
    SHOW_PAYED_MENU;
}
