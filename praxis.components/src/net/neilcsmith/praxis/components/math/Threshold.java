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
package net.neilcsmith.praxis.components.math;

import net.neilcsmith.praxis.core.ControlPort;
import net.neilcsmith.praxis.core.Port;
import net.neilcsmith.praxis.impl.AbstractComponent;
import net.neilcsmith.praxis.impl.DefaultControlOutputPort;
import net.neilcsmith.praxis.impl.FloatInputPort;
import net.neilcsmith.praxis.impl.NumberProperty;

/**
 *
 * @author Neil C Smith
 */
public class Threshold extends AbstractComponent {

    private NumberProperty threshold;
    private ControlPort.Output outputLow;
    private ControlPort.Output outputHigh;

    public Threshold() {    
        registerPort(Port.IN, FloatInputPort.create( new InputBinding()));
        outputLow = new DefaultControlOutputPort(this);
        outputHigh = new DefaultControlOutputPort(this);
        registerPort("out-low", outputLow);
        registerPort("out-high", outputHigh);
        threshold = NumberProperty.create( 0);
        registerControl("threshold", threshold);
        registerPort("threshold", threshold.createPort());
    }


    private class InputBinding implements FloatInputPort.Binding {

        public void receive(long time, double value) {
            if (value >= threshold.getValue()) {
                outputHigh.send(time, value);
            } else {
                outputLow.send(time, value);
            }
        }
    }
}
