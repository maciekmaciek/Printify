package com.maciekwski.printify.test;

import android.graphics.*;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityTestCase;
import com.maciekwski.printify.Activities.VerticesSetter.VerticesSetterActivity;
import com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool.BitmapTransformer;
import com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool.BitmapWithContentVertices;
import org.junit.Assert;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 05.11.2015.
 */
public class TransformBitmapActivityTest extends ActivityInstrumentationTestCase2<VerticesSetterActivity> {
    public TransformBitmapActivityTest(Class<VerticesSetterActivity> activityClass) {
        super(activityClass);
    }

    VerticesSetterActivity vsa;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        vsa = getActivity();

    }

    public void testBitmapTransform() throws Exception {


    }

    public void testSimpleBitmapTransform(){
        Bitmap bitmap = createBitmap();
        Point[] vertices = createVertices();
        BitmapTransformer bt = new BitmapTransformer(new BitmapWithContentVertices(vertices, bitmap));

        Bitmap result = bt.transformImage();
        Assert.assertEquals(result.getPixel(0, 0), Color.RED);
    }

    private Point[] createVertices() {
        Point[] vert = new Point[4];
        vert[0] = new Point(100, 100);
        vert[1] = new Point(200, 100);
        vert[2] = new Point(200, 400);
        vert[3] = new Point(100, 400);
        return vert;
    }

    private Bitmap createBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(300, 500, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);

        Paint p = new Paint(Color.RED);
        c.drawColor(Color.WHITE);
        c.drawCircle(100, 100, 100, p);
        return bitmap;
    }
}
