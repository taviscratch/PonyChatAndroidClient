package com.taviscratch.ponychatandroidclient;

/**
 * Created by sam on 8/16/15.
 */
public class SwipeControls {

    public static SWIPE_DIRECTION interpretSwipe(float xStart, float xEnd, float yStart, float yEnd) {

        SWIPE_DIRECTION swipe = null;

        // swipe is more vertical than horizontal
        if(Math.abs(yStart-yEnd) > Math.abs(xStart-xEnd)*2.0) {

            // if downward swipe on screen
            if (yStart < yEnd)
                swipe = SWIPE_DIRECTION.DOWN;

                // if upward swipe on screen
            else if (yStart > yEnd)
                swipe = SWIPE_DIRECTION.UP;
        }

        // swipe is more horizontal than vertical
        else if(Math.abs(xStart-xEnd) > Math.abs(yStart-yEnd)*2.0) {

            // if left to right swipe on screen
            if (xStart < xEnd)
                swipe = SWIPE_DIRECTION.RIGHT;

                // if right to left swipe on screen
            else if (xStart > xEnd)
                swipe = SWIPE_DIRECTION.LEFT;
        }

        else {
            swipe = SWIPE_DIRECTION.DIAGONAL;
        }

        return swipe;
    }

    public enum SWIPE_DIRECTION {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        DIAGONAL
    }


}
