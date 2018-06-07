package com.melesar;

import org.junit.Assert;
import org.junit.Test;

public class Vector2Test
{
    @Test
    public void createWithValues()
    {
        Vector2 v = new Vector2(5, 10);

        Assert.assertEquals(5, v.x, 0.01);
        Assert.assertEquals(10, v.y, 0.01);
    }

    @Test
    public void normalizeVector()
    {
        Vector2 v = new Vector2(4, 10);
        Vector2 normalized = v.normalized();

        Assert.assertEquals(1.0, normalized.magnitude(), 0.01);
    }

    @Test
    public void addTwoVectors()
    {
        Vector2 a = new Vector2(1.5, 0);
        Vector2 b = new Vector2(8.5, 4.3);

        Vector2 c = a.add(b);

        Assert.assertEquals(10.0, c.x, 0.01);
        Assert.assertEquals(4.3, c.y, 0.01);
    }

    @Test
    public void multiplyByValue()
    {
        Vector2 v = new Vector2 (1.5, 10);
        Vector2 u = v.multiply(2);

        Assert.assertEquals(3, u.x, 0.01);
        Assert.assertEquals(20, u.y, 0.01);
    }
}
