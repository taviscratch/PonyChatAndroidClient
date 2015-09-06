package com.taviscratch.ponychatandroidclient;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Random;

public class Util {



    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    // Returns a randomized pony name with numbers appended to the end
    public static String getRandomUsername() {
        Random random = new Random(System.currentTimeMillis());

        int nameIndex = random.nextInt(bigListOfPonyNames.length);
        int numberToAppend = random.nextInt(998)+1;

        String num2Append = new Integer(numberToAppend).toString();
        if(num2Append.length() == 1)
            num2Append = "00" + num2Append;
        else if(num2Append.length() == 2)
            num2Append = "0" + num2Append;

        return bigListOfPonyNames[nameIndex] + num2Append;
    }


    private static final String[] bigListOfPonyNames = {
            // Pony names
            "TwilightSparkle",
            "RainbowDash",
            "Fluttershy",
            "Rarity",
            "Appljack",
            "PinkiePie",
            "DerpyHooves",
            "DoctorWhooves",
            "PrincessLuna",
            "PrincessCelestia",
            "LyraHeartstrings",
            "BonBon",
            "PrincessCadence",
            "VinylScratch",
            "Octavia",
            "ShiningArmor",
            "BigMacintosh",
            "AppleBloom",
            "Scootaloo",
            "SweetieBelle",
            "Chrysalis",
            "ChanglingDrone",
            "BerryPunch",
            "Cherrilee",
            "Roseluck",
            "Moondancer",
            "Minuette",
            "CloudKicker",
            "CloudChaser",
            "Thunderlane",
            "Spitfire",
            "Soarin",
            "Blossomforth",
            "DiamondTiara",
            "SilverSpoon",
            "Snips",
            "Snails",
            "BulkBiceps",
            "MayorMare",
            "Zecora",
            "CarrotTop",
            "TrixieLulamoon",
            "MrsCake",
            "MrCake",
            "LightningDust",
            "CherryJubilee",
            "Braeburn",
            "Troubleshoes",
            "HoityToity",
            "FancyPants",
            "FleurDeLis",

            // Extra names
            "LittlePip",
            "SweetieBot",
            "myOC"
    };
}
