package com.taviscratch.ponychatandroidclient.utility;

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



    public static boolean isChannel(String target) {
        if(target.startsWith("#"))
            return true;
        return false;
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
            "Twilight_Sparkle",
            "Rainbow_Dash",
            "Fluttershy",
            "Rarity",
            "Applejack",
            "Pinkie_Pie",
            "Derpy_Hooves",
            "Doctor_Whooves",
            "Princess_Luna",
            "Princess_Celestia",
            "Lyra_Heartstrings",
            "Bon_Bon",
            "Princess_Cadence",
            "Vinyl_Scratch",
            "Octavia",
            "Photo_Finish",
            "Saphire_Shores",
            "Nurse_Redheart",
            "Shining_Armor",
            "Big_Macintosh",
            "Apple_Bloom",
            "Scootaloo",
            "Sweetie_Belle",
            "Babs_Seed",
            "Chrysalis",
            "Changling_Drone",
            "Berry_Punch",
            "Coco_Pommel",
            "Suri_Polomare",
            "Junebug",
            "Filthy_Rich",
            "Tree_Hugger",
            "Cherrilee",
            "Lightning_Dust",
            "Roseluck",
            "Sheriff_Silverstar",
            "Moon_Dancer",
            "Ms_Harshwhinny",
            "Ms_Peachbottom",
            "Wild_Fire",
            "Minuette",
            "Flash_Sentry",
            "Night_Glider",
            "Double_Diamond",
            "Claude",
            "Fleur_Dis_Lee",
            "Jet_Set",
            "Upper_Crust",
            "Cloud_Kicker",
            "Cloud_Chaser",
            "Flitter",
            "Thunderlane",
            "Daring_Do",
            "Spitfire",
            "Soarin",
            "Fleetfoot",
            "Blossomforth",
            "Daisy",
            "Amethyst_Star",
            "Lotus_Blossom",
            "Aloe",
            "Lemon_Hearts",
            "Diamond_Tiara",
            "Silver_Spoon",
            "Bulk_Biceps",
            "Mayor_Mare",
            "Zecora",
            "Golden_Harvest",
            "Trixie_Lulamoon",
            "Mrs_Cake",
            "Mr_Cake",
            "Lightning_Dust",
            "Cherry_Jubilee",
            "Braeburn",
            "Troubleshoes",
            "Hoity_Toity",
            "Fancy_Pants",
            "Donut_Joe",
            "King_Sombra",
            "Prince_Blueblood",
            "Starlight_Glimmer",
            "Sunset_Shimmer",
            "Trenderhoof",
            "Featherweight",
            "Pipsqueak",
            "Rumble",
            "Maud_Pie",
            "Zipperwhill",
            "Hayseed_Turnip_Truck",
            "Night_Light",
            "Twilight_Velvet",
            "Screwball",
            "Adagio_Dazzle",
            "Aria_Blaze",
            "Sonata_Dusk",

            // Extra names
            "LittlePip",
            "SweetieBot",
            "myOC"
    };
}
