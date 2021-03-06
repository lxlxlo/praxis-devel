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
package net.neilcsmith.praxis.video.pgl.ops;

import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.neilcsmith.praxis.video.pgl.PGLGraphics;
import net.neilcsmith.praxis.video.pgl.PGLSurface;
import net.neilcsmith.praxis.video.render.Surface;
import net.neilcsmith.praxis.video.render.SurfaceOp;
import net.neilcsmith.praxis.video.render.ops.BlendMode;
import net.neilcsmith.praxis.video.render.ops.RectFill;

/**
 *
 * @author Neil C Smith <http://neilcsmith.net>
 */
public class PGLRectFillOp extends AbstractDrawOp {
    
    private final static Logger LOG = Logger.getLogger(PGLRectFillOp.class.getName());

    private Rectangle rect;

    PGLRectFillOp() {
        super(RectFill.class);
        rect = new Rectangle();
    }

    @Override
    public void process(SurfaceOp op, PGLSurface output, Bypass bypass, Surface... inputs) {
//        if (inputs.length > 0) {
        if (process((RectFill) op, output)) {
            return;
        }
//        }
        bypass.process(op, inputs);
    }

    private boolean process(RectFill op, PGLSurface dst) {
        try {
            BlendMode mode = op.getBlendMode();
            if (canProcess(mode)) {
                PGLGraphics pg = dst.getGraphics();
                pg.beginDraw();
                configure(pg, mode, (float) op.getOpacity(), op.getColor());
                op.getBounds(rect);
                pg.rect(rect.x, rect.y, rect.width, rect.height);
                return true;
            }
        } catch (Exception ex) {
            LOG.log(Level.FINE, "Error during rect blit", ex);
        }
        return false;
    }

}
