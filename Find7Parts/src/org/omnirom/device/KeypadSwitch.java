/*
* Copyright (C) 2013 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

public class KeypadSwitch implements OnPreferenceChangeListener {

    private static final String KEYPAD_FILE = "/proc/touchpanel/keypad_enable";
    private static final String BRIGHTNESS_FILE = "/sys/class/leds/button-backlight/brightness";
    private static final String DEFAULT_BRIGHTNESS = "128";
    private static String SAVED_BRIGHTNESS = DEFAULT_BRIGHTNESS;

    public static boolean isSupported() {
        return Utils.fileWritable(KEYPAD_FILE);
    }

    public static boolean isEnabled(Context context) {
        boolean enabled = !Utils.getFileValueAsBoolean(KEYPAD_FILE, false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getBoolean(DeviceSettings.KEY_KEYPAD_SWITCH, enabled);
    }

    /**
     * Restore setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        boolean disabled = isEnabled(context);
        if (disabled) {
            Utils.writeValue(BRIGHTNESS_FILE, "0");
            Utils.writeValue(KEYPAD_FILE, "0");
        } else {
            Utils.writeValue(BRIGHTNESS_FILE, DEFAULT_BRIGHTNESS);
            Utils.writeValue(KEYPAD_FILE, "1");
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean disabled = (Boolean) newValue;
        if (disabled) {
            SAVED_BRIGHTNESS = Utils.getFileValue(BRIGHTNESS_FILE, DEFAULT_BRIGHTNESS);
            Utils.writeValue(BRIGHTNESS_FILE, "0");
            Utils.writeValue(KEYPAD_FILE, "0");
        } else {
            Utils.writeValue(BRIGHTNESS_FILE, SAVED_BRIGHTNESS);
            Utils.writeValue(KEYPAD_FILE, "1");
        }
        return true;
    }

}
