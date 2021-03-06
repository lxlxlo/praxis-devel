/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Neil C Smith.
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
 *
 * Parts of the API of this package, as well as some of the code, is derived from
 * the Processing project (http://processing.org)
 *
 * Copyright (c) 2004-09 Ben Fry and Casey Reas
 * Copyright (c) 2001-04 Massachusetts Institute of Technology
 *
 */
package net.neilcsmith.praxis.video.java;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.neilcsmith.praxis.core.Argument;
import net.neilcsmith.praxis.core.types.PNumber;
import net.neilcsmith.praxis.java.CodeContext;
import net.neilcsmith.praxis.java.CodeDelegate;
import net.neilcsmith.praxis.video.render.Surface;
import net.neilcsmith.praxis.video.render.SurfaceOp;

/**
 *
 * @author Neil C Smith (http://neilcsmith.net)
 */
public class VideoCodeDelegate extends CodeDelegate {

    private final static Logger LOG = Logger.getLogger(VideoCodeDelegate.class.getName());
//    private final static Surface[] EMPTY_SOURCES = new Surface[0];
    private PImage dst;
    private PGraphics pg;
    private boolean setupRequired;
    private boolean queueSends;
    private Queue<QueueableSend> sendQueue = new LinkedList<QueueableSend>();
    private VideoCodeContext videoContext;
    public PImage src;
    public Surface[] sources;
    public int width;
    public int height;


    private void setVideoContext(VideoCodeContext videoContext) {
        this.videoContext = videoContext;
        setupRequired = true;
    }



    private void process(Surface surface, boolean setup, Surface... sources) {
        validateGraphics(surface);
        width = surface.getWidth();
        height = surface.getHeight();
        if (sources.length > 0) {
            Surface s = sources[0];
            if (src == null) {
                src = new PImage(s);
            } else {
                src.setSurface(s);
            }
        }
        this.sources = sources;
        if (setup) {
            try {
                setup();
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "", ex);
            }

        }
        try {
            pg.resetMatrix();
            draw();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "", ex);
        }
    }

    private void validateGraphics(Surface surface) {
        if (dst == null) {
            dst = new PImage(surface);
            pg = new PGraphics(dst);
        } else {
            dst.setSurface(surface);
        }
    }


    public void draw() {
    }


    @Override
    public void send(int idx, Argument arg) {
        if (queueSends) {
            sendQueue.add(new QueueableSend(idx, arg));
        } else {
            super.send(idx, arg);
        }
        
    }

    @Override
    public void send(int idx, double value) {
        if (queueSends) {
            sendQueue.add(new QueueableSend(idx, PNumber.valueOf(value)));
        } else {
            super.send(idx, value);
        }
    }
    
    private void drainSendQueue() {
        QueueableSend send;
        while ((send = sendQueue.poll()) != null) {
            super.send(send.channel, send.value);
        }
    }

    private class QueueableSend {

        private int channel;
        private Argument value;

        private QueueableSend(int channel, Argument value) {
            this.channel = channel;
            this.value = value;
        }

    }
//
//    @Deprecated
//    public PImage im(int idx) {
//        return videoContext.getImage(idx - 1);
//    }

    public final PImage img(int idx) {
        return videoContext.getImage(idx - 1);
    }

    // PGraphics impl

    public void background(double grey) {
        pg.background(grey);
    }

    public void background(double grey, double alpha) {
        pg.background(grey, alpha);
    }

    public void background(double r, double g, double b) {
        pg.background(r, g, b);
    }

    public void background(double r, double g, double b, double a) {
        pg.background(r, g, b, a);
    }

    public void beginShape() {
        pg.beginShape();
    }

    public void bezier(double x1, double y1,
                     double x2, double y2,
                     double x3, double y3,
                     double x4, double y4) {
        pg.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public void bezierVertex(double x1, double y1,
            double x2, double y2, double x3, double y3) {
        pg.bezierVertex(x1, y1, x2, y2, x3, y3);
    }

    @Deprecated
    public void blendMode(VideoConstants.Blend blend) {
        pg.blendMode(blend);
    }
    
    public void blendMode(VideoConstants.Blend blend, double opacity) {
        pg.blendMode(blend, opacity);
    }

    public void breakShape() {
        pg.breakShape();
    }

    public void clear() {
        pg.clear();
    }

    public void ellipse(double x, double y, double w, double h) {
        pg.ellipse(x, y, w, h);
    }

    public void endShape() {
        pg.endShape();
    }

    public void endShape(boolean close) {
        pg.endShape(close);
    }

    public void fill(double grey) {
        pg.fill(grey);
    }

    public void fill(double grey, double alpha) {
        pg.fill(grey, alpha);
    }

    public void fill(double r, double g, double b) {
        pg.fill(r, g, b);
    }

    public void fill(double r, double g, double b, double a) {
        pg.fill(r, g, b, a);
    }

    public void image(PImage image, double x, double y) {
        pg.image(image, x, y);
    }

    public void image(PImage src, double x, double y, double w, double h,
            double u, double v) {
        pg.image(src, x, y, w, h, u, v);
    }

    public void image(PImage src, double x, double y, double w, double h) {
        pg.image(src, x, y, w, h);
    }

    public void image(PImage src, double x, double y, double w, double h,
            double u1, double v1, double u2, double v2) {
        pg.image(src, x, y, w, h, u1, v1, u2, v2);
    }

    public void line(double x1, double y1, double x2, double y2) {
        pg.line(x1, y1, x2, y2);
    }

    public void noFill() {
        pg.noFill();
    }

    public void noSmooth() {
        pg.noSmooth();
    }

    public void noStroke() {
        pg.noStroke();
    }

    public void op(SurfaceOp op) {
        pg.op(op);
    }

    public void op(SurfaceOp op, Surface src) {
        pg.op(op, src);
    }

    public void op(SurfaceOp op, PImage src) {
        pg.op(op, src);
    }

    public void point(double x, double y) {
        pg.point(x, y);
    }

    public void quad(double x1, double y1, double x2, double y2,
            double x3, double y3, double x4, double y4) {
        pg.quad(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public void rect(double x, double y, double w, double h) {
        pg.rect(x, y, w, h);
    }
    
    public void resetMatrix() {
        pg.resetMatrix();
    }
    
    public void rotate(double angle) {
        pg.rotate(angle);
    }
    
    public void scale(double scale) {
        pg.scale(scale);
    }

    public void scale(double x, double y) {
        pg.scale(x, y);
    }

    public void smooth() {
        pg.smooth();
    }

    public void stroke(double grey) {
        pg.stroke(grey);
    }

    public void stroke(double grey, double alpha) {
        pg.stroke(grey, alpha);
    }

    public void stroke(double r, double g, double b) {
        pg.stroke(r, g, b);
    }

    public void stroke(double r, double g, double b, double a) {
        pg.stroke(r, g, b, a);
    }

    public void strokeWeight(double weight) {
        pg.strokeWeight(weight);
    }
    
    public void translate(double x, double y) {
        pg.translate(x, y);
    }

    

    

    public void triangle(double x1, double y1, double x2, double y2,
            double x3, double y3) {
        pg.triangle(x1, y1, x2, y2, x3, y3);
    }

    public void vertex(double x, double y) {
        pg.vertex(x, y);
    }

    // end of PGraphics impl
    
    
    public static class Controller extends CodeDelegate.Controller {
        
        private CodeContext context;
        private VideoCodeDelegate delegate;
        private boolean setupRequired;
        
        public Controller(VideoCodeDelegate delegate) {
            super(delegate);
            this.delegate = delegate;
        }

        @Override
        public void init(CodeContext context) throws Exception {
            super.init(context);
            this.context = context;
            if (context instanceof VideoCodeContext) {
                delegate.setVideoContext((VideoCodeContext) context);
            } else {
                delegate.setVideoContext(null);
            }
            setupRequired = true;
        }

        @Override
        public void update() throws Exception {
            if (context == null) {
                throw new IllegalStateException("Calling update() on an uninited code delegate");
            }
            delegate.drainSendQueue();
        }
        
        public void process(Surface surface, Surface[] sources) {
            if (setupRequired) {
                delegate.process(surface, true, sources);
                setupRequired = false;
            } else {
                delegate.process(surface, false, sources);
            }
        }

        @Override
        public void dispose() {
            super.dispose();
            context = null;
            setupRequired = false;
        }
        
        
        
    }
    
}
