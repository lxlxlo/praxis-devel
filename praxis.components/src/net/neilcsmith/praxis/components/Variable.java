/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2010 Neil C Smith.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details.
 * 
 * You should have received a copy of the GNU General Public License version 3
 * along with this work; if not, see http://www.gnu.org/licenses/
 * 
 * 
 * Please visit http://neilcsmith.net if you need additional information or
 * have any questions.
 */

package net.neilcsmith.praxis.components;

import net.neilcsmith.praxis.core.Argument;
import net.neilcsmith.praxis.core.ControlPort;
import net.neilcsmith.praxis.core.Port;
import net.neilcsmith.praxis.core.types.PString;
import net.neilcsmith.praxis.impl.AbstractComponent;
import net.neilcsmith.praxis.impl.ArgumentProperty;
import net.neilcsmith.praxis.impl.DefaultControlOutputPort;
import net.neilcsmith.praxis.impl.TriggerControl;

/**
 *
 * @author Neil C Smith
 */
public class Variable extends AbstractComponent {
    
    private Argument value;
    private ControlPort.Output output;

    public Variable() {
        value = PString.EMPTY;
        ArgumentProperty p = ArgumentProperty.create( new ValueBinding(), value);      
        TriggerControl t = TriggerControl.create( new TriggerBinding());
        registerControl("trigger", t);
        registerPort("trigger", t.createPort());
        output = new DefaultControlOutputPort(this);
        registerPort(Port.OUT, output);
        registerControl("value", p);
        registerPort("value", p.createPort());
    }
    
    private class ValueBinding implements ArgumentProperty.Binding {

        public void setBoundValue(long time, Argument value) {
            Variable.this.value = value;
        }

        public Argument getBoundValue() {
            return Variable.this.value;
        }
        
    }
    
    private class TriggerBinding implements TriggerControl.Binding {

        public void trigger(long time) {
            output.send(time, value);
        }
        
    }
}
