package com.projet.rplace.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Random;

public enum Color {
    COLOR_6D001A("#6d001a"),
    COLOR_BE0039("#be0039"),
    COLOR_FF4500("#ff4500"),
    COLOR_FFA800("#ffa800"),
    COLOR_FFD635("#ffd635"),
    COLOR_FFF8B8("#fff8b8"),
    COLOR_00A368("#00a368"),
    COLOR_00CC78("#00cc78"),
    COLOR_7EED56("#7eed56"),
    COLOR_00756F("#00756f"),
    COLOR_009EAA("#009eaa"),
    COLOR_00CCC0("#00ccc0"),
    COLOR_2450A4("#2450a4"),
    COLOR_3690EA("#3690ea"),
    COLOR_51E9F4("#51e9f4"),
    COLOR_493AC1("#493ac1"),
    COLOR_6A5CFF("#6a5cff"),
    COLOR_94B3FF("#94b3ff"),
    COLOR_811E9F("#811e9f"),
    COLOR_B44AC0("#b44ac0"),
    COLOR_E4ABFF("#e4abff"),
    COLOR_DE107F("#de107f"),
    COLOR_FF3881("#ff3881"),
    COLOR_FF99AA("#ff99aa"),
    COLOR_6D482F("#6d482f"),
    COLOR_9C6926("#9c6926"),
    COLOR_FFB470("#ffb470"),
    COLOR_000000("#000000"),
    COLOR_515252("#515252"),
    COLOR_898D90("#898d90"),
    COLOR_D4D7D9("#d4d7d9"),
    COLOR_FFFFFF("#ffffff");

    private final String hexCode;

    Color(String hexCode) {
        this.hexCode = hexCode;
    }

    @JsonValue
    public String getHexCode() {
        return hexCode;
    }

    public static Color randomColor() {
        Random random = new Random();
        return Color.values()[random.nextInt(32)];
    }

    @JsonCreator
    public static Color fromString(String hexCode) {
        for (Color color : Color.values()) {
            if (color.getHexCode().equalsIgnoreCase(hexCode)) {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown color code: " + hexCode);
    }
}
