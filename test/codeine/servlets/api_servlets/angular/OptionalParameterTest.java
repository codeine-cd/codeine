package codeine.servlets.api_servlets.angular;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by rezra3 on 3/29/16.
 */
public class OptionalParameterTest {

    @Test
    public void testNullValue() throws Exception {
        Assert.assertTrue(OptionalParameter.getValue(null));
    }

    @Test
    public void testTrueValue() throws Exception {
        Assert.assertTrue(OptionalParameter.getValue("true"));
        Assert.assertTrue(OptionalParameter.getValue("True"));
        Assert.assertTrue(OptionalParameter.getValue("TRUE"));
    }

    @Test
    public void testFalseValue() throws Exception {
        Assert.assertFalse(OptionalParameter.getValue("false"));
        Assert.assertFalse(OptionalParameter.getValue("False"));
        Assert.assertFalse(OptionalParameter.getValue("FALSE"));
        Assert.assertFalse(OptionalParameter.getValue("dfd"));
    }
}