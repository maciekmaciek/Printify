package com.maciekwski.lab7;

import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.PixelTableSharpener;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PixelTableSharpenerTest {
    @Test
    public void sharpenPixelTableEdgeUntouched() {
        //given
        int[][] oldTable = {
                {0, 1, 1, 1, 0},
                {0, 1, 2, 1, 0},
                {0, 1, 3, 1, 0},
                {0, 1, 2, 1, 0},
                {0, 1, 1, 1, 0}
        };
        //when
        int pixel = PixelTableSharpener.sharpenBWPixelTable(oldTable)[0][0];
        int oldPixel = oldTable[0][0];
        //then
        assertEquals(pixel, oldPixel);
    }

    @Test
    public void sharpenPixelTablePixelChanged() {
        //given
        int[][] oldTable = {
                {0, 1, 1, 1, 0},
                {0, 1, 2, 1, 0},
                {0, 1, 3, 1, 0},
                {0, 1, 2, 1, 0},
                {0, 1, 1, 1, 0}
        };
        //when
        int pixel = PixelTableSharpener.sharpenBWPixelTable(oldTable)[2][2];
        int result = 13;
        //then
        assertEquals(pixel, result);
    }
}
