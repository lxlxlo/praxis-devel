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

package net.neilcsmith.praxis.video.components;

import net.neilcsmith.praxis.core.Port;
import net.neilcsmith.praxis.impl.AbstractComponent;
import net.neilcsmith.praxis.video.impl.DefaultVideoInputPort;
import net.neilcsmith.praxis.video.impl.DefaultVideoOutputPort;
import net.neilcsmith.praxis.video.pipes.impl.Placeholder;

/**
 *
 * @author Neil C Smith
 */
public class Splitter extends AbstractComponent {
    
    private net.neilcsmith.praxis.video.pipes.impl.Splitter spl;
    private Placeholder out1;
    private Placeholder out2;
    
    public Splitter() {
//        try {
            spl = new net.neilcsmith.praxis.video.pipes.impl.Splitter(2);
            out1 = new Placeholder();
            out2 = new Placeholder();
            out1.addSource(spl);
            out2.addSource(spl);
            registerPort(Port.IN, new DefaultVideoInputPort(this, spl));
            registerPort(Port.OUT + "-1", new DefaultVideoOutputPort(this, out1));
            registerPort(Port.OUT + "-2", new DefaultVideoOutputPort(this, out2));
//            
//        } catch (SinkIsFullException ex) {
//            Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SourceIsFullException ex) {
//            Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }

}
