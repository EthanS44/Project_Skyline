package org.Skyline;

import org.junit.Assert;
import org.junit.Test;

public class TreeTest {

    @Test
    public void testGetHeight(){
        Tree tree = new Tree(100, 50, 40);
        Assert.assertEquals(180, tree.getHeight(), 0.001);
    }
}
