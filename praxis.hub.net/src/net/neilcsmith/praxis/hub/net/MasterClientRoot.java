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

import de.sciss.net.OSCClient;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCPacket;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.EnumSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.neilcsmith.praxis.core.Call;
import net.neilcsmith.praxis.core.CallArguments;
import net.neilcsmith.praxis.core.Control;
import net.neilcsmith.praxis.core.ExecutionContext;
import net.neilcsmith.praxis.core.PacketRouter;
import net.neilcsmith.praxis.core.info.ControlInfo;
import net.neilcsmith.praxis.core.interfaces.RootManagerService;
import net.neilcsmith.praxis.impl.AbstractRoot;

/**
 *
 * @author Neil C Smith <http://neilcsmith.net>
 */
class MasterClientRoot extends AbstractRoot {

    private final static Logger LOG = Logger.getLogger(MasterClientRoot.class.getName());
    private final static String HLO = "/HLO";
    private final static String BYE = "/BYE";

    private final PraxisPacketCodec codec;
    private final Dispatcher dispatcher;
    private final SocketAddress slaveAddress;

    private OSCClient client;
    private long lastPurgeTime;
    private Watchdog watchdog;

    MasterClientRoot(SocketAddress slaveAddress) {
        super(EnumSet.noneOf(Caps.class));
        this.slaveAddress = slaveAddress;
        codec = new PraxisPacketCodec();
        dispatcher = new Dispatcher(codec);
        registerControl(RootManagerService.ADD_ROOT, new RootControl(true));
        registerControl(RootManagerService.REMOVE_ROOT, new RootControl(false));
    }

    @Override
    protected void activating() {
        super.activating();
        getLookup().get(ExecutionContext.class).addClockListener(new ExecutionContext.ClockListener() {

            @Override
            public void tick(ExecutionContext source) {
                MasterClientRoot.this.tick(source);
            }
        });
    }

    @Override
    protected void terminating() {
        super.terminating();
        if (client != null) {
            LOG.fine("Terminating - sending /BYE");
            try {
                client.send(new OSCMessage(BYE));
            } catch (IOException ex) {
                LOG.log(Level.FINE, null, ex);
            }
        }
        clientDispose();
    }

    
    
    @Override
    protected void processCall(Call call) {
        if (getAddress().getRootID().equals(call.getRootID())) {
            super.processCall(call);
        } else {
            if (client != null) {
                dispatcher.handleCall(call);
            } else {
                connect();
                if (client != null) {
                    dispatcher.handleCall(call);
                } else {
                    getPacketRouter().route(Call.createErrorCall(call,
                            CallArguments.EMPTY));
                }
            }
        }
    }

    private void tick(ExecutionContext source) {
        if ((source.getTime() - lastPurgeTime) > TimeUnit.SECONDS.toNanos(1)) {
//            LOG.fine("Triggering dispatcher purge");
            dispatcher.purge(10, TimeUnit.SECONDS);
            lastPurgeTime = source.getTime();
        }
        if (watchdog != null) {
            watchdog.tick();
        }
    }

    private void messageReceived(OSCMessage msg, SocketAddress sender, long timeTag) {
        dispatcher.handleMessage(msg, timeTag);
    }

    private void send(OSCPacket packet) {
        if (client != null) {
            try {
                client.send(packet);
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "", ex);
                clientDispose();
            }
        }
    }

    private void connect() {
        LOG.fine("Connecting to slave");
        try {
            // connect to slave
            client = OSCClient.newUsing(codec, OSCClient.TCP);
            client.setBufferSize(65536);
            client.setTarget(slaveAddress);
            watchdog = new Watchdog(client);
            watchdog.start();
//            client.connect();
//            LOG.fine("Connected - sending /HLO");

            // HLO request
            CountDownLatch hloLatch = new CountDownLatch(1);
            client.addOSCListener(new Receiver(hloLatch));
            client.start();
            client.send(new OSCMessage(HLO));
            if (hloLatch.await(10, TimeUnit.SECONDS)) {
                LOG.fine("/HLO received OK");
            } else {
                LOG.severe("Unable to connect to slave");
                clientDispose();
            }
            
        } catch (IOException | InterruptedException ex) {
            LOG.log(Level.SEVERE, "Unable to connect to slave", ex);
            clientDispose();
        }
    }

    private void clientDispose() {
        if (client != null) {
            client.dispose();
            client = null;
        }
        if (watchdog != null) {
            watchdog.shutdown();
            watchdog = null;
        }
        dispatcher.purge(0, TimeUnit.NANOSECONDS);
    }

    private class Dispatcher extends OSCDispatcher {

        private Dispatcher(PraxisPacketCodec codec) {
            super(codec);
        }

        @Override
        void send(OSCPacket packet) {
            MasterClientRoot.this.send(packet);
        }

        @Override
        void send(Call call) {
            getPacketRouter().route(call);
        }

    }

    private class Watchdog extends Thread {

        private final OSCClient client;
        private volatile long lastTickTime;
        private volatile boolean active;

        private Watchdog(OSCClient client) {
            this.client = client;
            lastTickTime = System.nanoTime();
            setDaemon(true);
        }

        @Override
        public void run() {
            while (active) {
                if ((System.nanoTime() - lastTickTime) > TimeUnit.SECONDS.toNanos(10)) {
                    client.dispose();
                    active = false;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    // not a problem
                }
            }
        }

        private void tick() {
            lastTickTime = System.nanoTime();
        }

        private void shutdown() {
            active = false;
            interrupt();
        }

    }

    private class Receiver implements OSCListener {

        private CountDownLatch hloLatch;

        private Receiver(CountDownLatch hloLatch) {
            this.hloLatch = hloLatch;
        }

        @Override
        public void messageReceived(final OSCMessage msg, final SocketAddress sender,
                final long timeTag) {
            if (hloLatch != null && HLO.equals(msg.getName())) {
                hloLatch.countDown();
                hloLatch = null;
            }
            invokeLater(new Runnable() {

                @Override
                public void run() {
                    MasterClientRoot.this.messageReceived(msg, sender, timeTag);
                }
            });
        }

    }

    private class RootControl implements Control {

        private final boolean add;

        private RootControl(boolean add) {
            this.add = add;
        }

        @Override
        public void call(Call call, PacketRouter router) throws Exception {
            if (call.getType() == Call.Type.INVOKE
                    || call.getType() == Call.Type.INVOKE_QUIET) {

                if (client != null) {
                    dispatch(call);
                } else {
                    connect();
                    if (client != null) {
                        dispatch(call);
                    } else {
                        router.route(Call.createErrorCall(call,
                                CallArguments.EMPTY));
                    }
                }
            } else {
                // 
            }
        }

        private void dispatch(Call call) {
            if (add) {
                dispatcher.handleAddRoot(call);
            } else {
                dispatcher.handleRemoveRoot(call);
            }
        }

        @Override
        public ControlInfo getInfo() {
            return add ? RootManagerService.ADD_ROOT_INFO : RootManagerService.REMOVE_ROOT_INFO;
        }

    }

}
