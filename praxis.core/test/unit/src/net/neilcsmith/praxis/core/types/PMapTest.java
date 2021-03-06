/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.neilcsmith.praxis.core.types;

import net.neilcsmith.praxis.core.Argument;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Neil C Smith <http://neilcsmith.net>
 */
public class PMapTest {
    
    public PMapTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

   

    /**
     * Test of coerce method, of class PMap.
     */
    @Test
    public void testCoerce() throws Exception {
        PMap m = PMap.create("template", "public void draw(){");
        String mStr = m.toString();
        System.out.println(mStr);
        PMap m2 = PMap.valueOf(mStr);
        assertTrue(Argument.equivalent(Argument.class, m, m2));
    }


}
