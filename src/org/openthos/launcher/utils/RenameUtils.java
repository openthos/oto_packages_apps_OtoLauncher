package org.openthos.launcher.utils;

import android.content.Intent;
import android.view.KeyEvent;
import org.openthos.launcher.utils.OtoConsts;

public class RenameUtils {
    public static final String DELETE = "Delete";
    public static final String ENTER = "Enter";
    public static final String BACKSPACE = "Backspace";
    public static final String HOME = "Home";
    public static final String END = "End";
    public static final String LEFT = "Left";
    public static final String RIGHT = "Right";

    public static String switchKeyCodeToString(KeyEvent event, int keyCode) {
        int keyChar = 0;
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z
                && !event.isShiftPressed()
                && !event.isCapsLockOn()) {
            keyChar = keyCode - KeyEvent.KEYCODE_A + 'a';
        } else if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z
                && (event.isShiftPressed() || event.isCapsLockOn())) {
            keyChar = keyCode - KeyEvent.KEYCODE_A + 'A';
        } else if (keyCode >= KeyEvent.KEYCODE_0
                && keyCode <= KeyEvent.KEYCODE_9
                && !event.isShiftPressed()) {
            keyChar = keyCode - KeyEvent.KEYCODE_0 + '0';
        } else if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0
                && keyCode <= KeyEvent.KEYCODE_NUMPAD_9
                && !event.isShiftPressed()) {
            keyChar = keyCode - KeyEvent.KEYCODE_NUMPAD_0 + '0';
        } else if (keyCode == KeyEvent.KEYCODE_COMMA) {
            keyChar = ',';
        } else if (keyCode == KeyEvent.KEYCODE_PERIOD) {
            keyChar = '.';
        } else if (keyCode == KeyEvent.KEYCODE_SPACE) {
            keyChar = ' ';
        } else if (keyCode == KeyEvent.KEYCODE_APOSTROPHE) {
            keyChar = '\'';
        } else if (keyCode == KeyEvent.KEYCODE_SLASH || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
            keyChar = '/';
        } else if (keyCode == KeyEvent.KEYCODE_SEMICOLON) {
            keyChar = ';';
        } else if (keyCode == KeyEvent.KEYCODE_BACKSLASH) {
            keyChar = '\\';
        } else if (keyCode == KeyEvent.KEYCODE_LEFT_BRACKET) {
            keyChar = '[';
        } else if (keyCode == KeyEvent.KEYCODE_RIGHT_BRACKET) {
            keyChar = ']';
        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_MULTIPLY) {
            keyChar = '*';
        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_SUBTRACT) {
            keyChar = '-';
        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_ADD) {
            keyChar = '+';
        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
            keyChar = '.';
        } else if (keyCode == KeyEvent.KEYCODE_GRAVE && !event.isShiftPressed()) {
            keyChar = '`';
        } else if (keyCode == KeyEvent.KEYCODE_MINUS && !event.isShiftPressed()) {
            keyChar = '-';
        } else if (keyCode == KeyEvent.KEYCODE_EQUALS && !event.isShiftPressed()) {
            keyChar = '=';
        } else if (event.isShiftPressed()) {
            if (keyCode == KeyEvent.KEYCODE_0) {
                keyChar = ')';
            } else if (keyCode == KeyEvent.KEYCODE_1) {
                keyChar = '!';
            } else if (keyCode == KeyEvent.KEYCODE_2) {
                keyChar = '@';
            } else if (keyCode == KeyEvent.KEYCODE_3) {
                keyChar = '#';
            } else if (keyCode == KeyEvent.KEYCODE_4) {
                keyChar = '$';
            } else if (keyCode == KeyEvent.KEYCODE_5) {
                keyChar = '%';
            } else if (keyCode == KeyEvent.KEYCODE_6) {
                keyChar = '^';
            } else if (keyCode == KeyEvent.KEYCODE_7) {
                keyChar = '&';
            } else if (keyCode == KeyEvent.KEYCODE_8) {
                keyChar = '*';
            } else if (keyCode == KeyEvent.KEYCODE_9) {
                keyChar = '(';
            } else if (keyCode == KeyEvent.KEYCODE_GRAVE) {
                keyChar = '~';
            } else if (keyCode == KeyEvent.KEYCODE_MINUS) {
                keyChar = '_';
            } else if (keyCode == KeyEvent.KEYCODE_EQUALS) {
                keyChar = '+';
            } else {
                keyChar = -1;
            }
        } else {
            keyChar = -1;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            return ENTER;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            return BACKSPACE;
        } else if (keyCode == KeyEvent.KEYCODE_FORWARD_DEL) {
            return DELETE;
        } else if (keyCode == KeyEvent.KEYCODE_MOVE_HOME) {
            return HOME;
        } else if (keyCode == KeyEvent.KEYCODE_MOVE_END) {
            return END;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            return LEFT;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return RIGHT;
        } else {
            if (keyChar != -1) {
                return String.valueOf((char) keyChar);
            }
            return null;
        }
    }
}
