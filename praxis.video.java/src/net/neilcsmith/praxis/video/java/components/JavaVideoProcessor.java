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

package net.neilcsmith.praxis.video.java.components;

import net.neilcsmith.praxis.core.Port;
import net.neilcsmith.praxis.video.impl.DefaultVideoInputPort;
import net.neilcsmith.praxis.video.impl.DefaultVideoOutputPort;
import net.neilcsmith.praxis.video.pipes.impl.SingleInOut;
import net.neilcsmith.praxis.video.render.Surface;

/**
 *
 * @author Neil C Smith (http://neilcsmith.net)
 */
public class JavaVideoProcessor extends AbstractJavaVideoComponent {

    private Delegator delegator;


    public JavaVideoProcessor() {
        setupDelegator();
        setupCodeControl();
        buildParams("p", 16, 8);
        buildTriggers("t", 8, 4);
    }

    private void setupDelegator() {
        delegator = new Delegator();
        registerPort(Port.IN, new DefaultVideoInputPort(this, delegator));
        registerPort(Port.OUT, new DefaultVideoOutputPort(this, delegator));
    }

    @Override
    protected void installToDelegator(ControllerDelegate delegate) {
        delegator.delegate = delegate;
    }

    private class Delegator extends SingleInOut {
        
        private ControllerDelegate delegate;

        @Override
        protected void process(Surface surface, boolean rendering) {
            if (rendering && delegate != null) {
                delegate.process(surface);
            }
        }
        
    }
     
}
