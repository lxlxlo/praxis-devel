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
 */
package net.neilcsmith.praxis.hub.net;

import java.net.UnknownHostException;
import java.util.List;
import net.neilcsmith.praxis.core.Root;
import net.neilcsmith.praxis.hub.Hub;


public class SlaveFactory extends Hub.CoreRootFactory {
    
    public final static int DEFAULT_PORT = 13178;
    
    private final int port;
    private final CIDRUtils clientValidator;
    
    public SlaveFactory(int port, String netMask) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port out of range");
        }
        if (netMask != null) {
            try {
                clientValidator = new CIDRUtils(netMask);
            } catch (UnknownHostException ex) {
                throw new IllegalArgumentException("Network mask invalid", ex);
            }
        } else {
            clientValidator = null;
        }
                
        this.port = port;
    }

    @Override
    public Root createCoreRoot(Hub.Accessor accessor, List<Root> extensions) {
        return new SlaveCoreRoot(accessor, extensions, port, clientValidator);
    }
    
}
