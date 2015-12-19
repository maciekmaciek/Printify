package com.maciekwski.lab7;

import android.graphics.Color;
import com.maciekwski.printify.Utils.ImageUtils.BWTableToARGBBufferConverter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BWTableToARGBTest {
    @Test
    public void convert0toARGBBlack() {  //does not work with stupid android test engine
        //given
        int[][] table = {{100}};
        //when
        int shouldBeBlack = (BWTableToARGBBufferConverter.convertPixelTable(table))[0];
        //then
        assertEquals(shouldBeBlack, Color.BLACK);
    }

    @Test
    public void matrixToTableLength() {
        //given
        int[][] table = {
                {0, 1, 2, 3, 4},
                {0, 1, 2, 3, 4}
        };
        //when
        int shouldBe10 = (BWTableToARGBBufferConverter.convertPixelTable(table)).length;
        //then
        assertEquals(shouldBe10, 10);
    }

}
