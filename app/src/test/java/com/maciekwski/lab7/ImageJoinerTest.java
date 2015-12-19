package com.maciekwski.lab7;

import com.maciekwski.printify.Utils.ImageUtils.Step3Joining.ImageJoiner;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImageJoinerTest {

    @Test
    public void joinBlackAndWhiteTableReturnAllBlack() throws Exception {
        //given
        int [][] black1 = {{0,255,255}};
        int [][] black2 = {{255,0,255}};
        int [][] black3 = {{255,255,0}};
        //when
        int result [][] = ImageJoiner.takeDarkest(black1,black2,black3);
        int expected [][] = {{0,0,0}};
        //then
        assertEquals(result[0][0], expected[0][0]);
        assertEquals(result[0][1], expected[0][1]);
        assertEquals(result[0][2], expected[0][2]);
    }

    @Test
    public void joinBlackAndWhiteTableOneAndThreeIsBlack() {
        //given
        int [][] black1 = {{0,255,255}};
        int [][] white = {{255,255,255}};
        int [][] black3 = {{255,255,0}};
        //when
        int result [][] = ImageJoiner.takeDarkest(black1,white,black3);
        int expected [][] = {{0,255,0}};
        //then
        assertEquals(result[0][0], expected[0][0]);
        assertEquals(result[0][1], expected[0][1]);
        assertEquals(result[0][2], expected[0][2]);
    }
}