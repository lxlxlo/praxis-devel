/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.neilcsmith.praxis.core.info;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.neilcsmith.praxis.core.Argument;
import net.neilcsmith.praxis.core.Component;
import net.neilcsmith.praxis.core.ControlPort;
import net.neilcsmith.praxis.core.InterfaceDefinition;
import net.neilcsmith.praxis.core.interfaces.ComponentInterface;
import net.neilcsmith.praxis.core.interfaces.StartableInterface;
import net.neilcsmith.praxis.core.types.PMap;
import net.neilcsmith.praxis.core.types.PNumber;
import net.neilcsmith.praxis.core.types.PString;
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
public class ComponentInfoTest {
    
    private ComponentInfo info;
    
    public ComponentInfoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        Set<Class<? extends InterfaceDefinition>> interfaces = new LinkedHashSet<>(2);
        interfaces.add(ComponentInterface.class);
        interfaces.add(StartableInterface.class);
        
        Map<String, ControlInfo> controls = new LinkedHashMap<>();
        controls.put("p1", ControlInfo.createPropertyInfo(new ArgumentInfo[]{PNumber.info(0, 1)}, new Argument[]{PNumber.ONE}, PMap.create(ControlInfo.KEY_TRANSIENT, true)));
        controls.put("p1", ControlInfo.createPropertyInfo(new ArgumentInfo[]{ArgumentInfo.create(PString.class, PMap.create("template", "public void draw(){"))}, new Argument[]{PString.EMPTY}, PMap.EMPTY));
        controls.put("ro1", ControlInfo.createReadOnlyPropertyInfo(new ArgumentInfo[]{PNumber.info(0, 1)}, PMap.create(ControlInfo.KEY_TRANSIENT, true)));
        controls.put("t1", ControlInfo.createActionInfo(PMap.create("key", "value")));
        
        Map<String, PortInfo> ports = new LinkedHashMap<>();
        ports.put("in", PortInfo.create(ControlPort.class, PortInfo.Direction.IN, PMap.EMPTY));
        ports.put("out", PortInfo.create(ControlPort.class, PortInfo.Direction.OUT, PMap.EMPTY));
        
        PMap properties = PMap.create(ComponentInfo.KEY_DYNAMIC, true);
        
        info = ComponentInfo.create(controls, ports, interfaces, properties);
    }
    
    @After
    public void tearDown() {
        info = null;
    }

    /**
     * Test of coerce method, of class ComponentInfo.
     */
    @Test
    public void testCoerce() throws Exception {
        System.out.println("coerce");
        String ci = info.toString();
        System.out.println(ci);
        ComponentInfo info2 = ComponentInfo.coerce(PString.valueOf(ci));
        System.out.println(info2);
        System.out.println(info.getControlInfo("p1"));
        System.out.println(info2.getControlInfo("p1"));
        System.out.println(info.getControlInfo("ro1"));
        System.out.println(info2.getControlInfo("ro1"));
        assertTrue(Argument.equivalent(Argument.class, info.getControlInfo("p1").getOutputsInfo()[0], info2.getControlInfo("p1").getOutputsInfo()[0]));
        assertTrue(Argument.equivalent(Argument.class, info.getControlInfo("ro1").getOutputsInfo()[0], info2.getControlInfo("ro1").getOutputsInfo()[0]));
        assertTrue(Argument.equivalent(Argument.class, info, info2));
    }

   
}
