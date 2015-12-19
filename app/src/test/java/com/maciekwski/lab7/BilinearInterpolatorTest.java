package com.maciekwski.lab7;

import com.maciekwski.printify.Utils.ImageUtils.Step1PerspectiveTransform.BilinearInterpolator;
import org.junit.Test;

import static org.junit.Assert.*;

public class BilinearInterpolatorTest {

    @Test
    public void testInterpolatereturn5() throws Exception {
        //given
        double x = 0.5;
        double y = 0.5;
        int a = 10;
        int b = 10;
        int c = 0;
        int d = 0;
        //when
        int result = BilinearInterpolator.interpolate(x,y,a,b,c,d);
        int expected = 5;

        //then
        assertEquals(result, expected);
    }
}