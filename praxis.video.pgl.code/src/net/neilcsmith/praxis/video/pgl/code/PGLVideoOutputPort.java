/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Neil C Smith.
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
 *
 */
package net.neilcsmith.praxis.video.pgl.code;

import net.neilcsmith.praxis.code.CodeContext;
import net.neilcsmith.praxis.code.PortDescriptor;
import net.neilcsmith.praxis.core.Port;
import net.neilcsmith.praxis.core.info.PortInfo;
import net.neilcsmith.praxis.core.types.PMap;
import net.neilcsmith.praxis.video.VideoPort;
import net.neilcsmith.praxis.video.impl.DefaultVideoOutputPort;
import net.neilcsmith.praxis.video.pipes.VideoPipe;
import net.neilcsmith.praxis.video.pipes.impl.Placeholder;

/**
 *
 * @author Neil C Smith <http://neilcsmith.net>
 */
class PGLVideoOutputPort extends DefaultVideoOutputPort {
    
    private Placeholder pipe;
    
    private PGLVideoOutputPort(Placeholder pipe) {
        super(pipe);
        this.pipe = pipe;
    }
     
    VideoPipe getPipe() {
        return pipe;
    }
    
    
    static class Descriptor extends PortDescriptor {
        
        private final static PortInfo INFO = PortInfo.create(VideoPort.class, PortInfo.Direction.OUT, PMap.EMPTY);
        
        private PGLVideoOutputPort port;
        
        Descriptor(String id, int index) {
            super(id, Category.Out, index);
        }

        @Override
        public void attach(CodeContext<?> context, Port previous) {
            if (previous instanceof PGLVideoOutputPort) {
                PGLVideoOutputPort vip = (PGLVideoOutputPort) previous;
                if (vip.pipe.getSourceCount() == 1) {
                    vip.pipe.removeSource(vip.pipe.getSource(0));
                }
                port = vip;
            } else {
                if (previous != null) {
                    previous.disconnectAll();
                }
                port = new PGLVideoOutputPort(new Placeholder());
            }
        }

        @Override
        public PGLVideoOutputPort getPort() {
            return port;
        }

        @Override
        public PortInfo getInfo() {
            return INFO;
        }
        
    }
    
}
