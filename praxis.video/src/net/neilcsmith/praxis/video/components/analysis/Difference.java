/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2012 Neil C Smith.
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
package net.neilcsmith.praxis.video.components.analysis;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.neilcsmith.praxis.core.Port;
import net.neilcsmith.praxis.impl.AbstractComponent;
import net.neilcsmith.praxis.impl.FloatProperty;
import net.neilcsmith.praxis.impl.StringProperty;
import net.neilcsmith.praxis.video.impl.DefaultVideoInputPort;
import net.neilcsmith.praxis.video.impl.DefaultVideoOutputPort;
import net.neilcsmith.ripl.components.Placeholder;
import net.neilcsmith.ripl.SinkIsFullException;
import net.neilcsmith.ripl.SourceIsFullException;
import net.neilcsmith.ripl.Surface;
import net.neilcsmith.ripl.impl.MultiInputInOut;
import net.neilcsmith.ripl.ops.DifferenceOp;

/**
 *
 * @author Neil C Smith
 */
public class Difference extends AbstractComponent {

    private static enum Mode {

        Color, Mono, Threshold
    };
    private BackgroundDifference diff;

    public Difference() {
        try {
            diff = new BackgroundDifference();
            Placeholder pl1 = new Placeholder();
            Placeholder pl2 = new Placeholder();
            diff.addSource(pl1);
            diff.addSource(pl2);
            registerPort(Port.IN + "-1", new DefaultVideoInputPort(this, pl1));
            registerPort(Port.IN + "-2", new DefaultVideoInputPort(this, pl2));
            registerPort(Port.OUT, new DefaultVideoOutputPort(this, diff));
            StringProperty mode = StringProperty.create(new ModeBinding(), getModeStrings(), diff.getMode().name());
            registerControl("mode", mode);
            FloatProperty threshold = FloatProperty.create(new ThresholdBinding(), 0, 1, 0);
            registerControl("threshold", threshold);
            registerPort("threshold", threshold.createPort());
        } catch (SinkIsFullException ex) {
            Logger.getLogger(Difference.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SourceIsFullException ex) {
            Logger.getLogger(Difference.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String[] getModeStrings() {
        Mode[] modes = Mode.values();
        String[] strings = new String[modes.length];
        for (int i = 0; i < modes.length; i++) {
            strings[i] = modes[i].name();
        }
        return strings;
    }

    private class ModeBinding implements StringProperty.Binding {

        public void setBoundValue(long time, String value) {
            diff.setMode(Mode.valueOf(value));
        }

        public String getBoundValue() {
            return diff.getMode().name();
        }
    }

    private class ThresholdBinding implements FloatProperty.Binding {

        public void setBoundValue(long time, double value) {
            diff.setThreshold(value);
        }

        public double getBoundValue() {
            return diff.getThreshold();
        }
    }

    private class BackgroundDifference extends MultiInputInOut {

        private DifferenceOp op;

        public BackgroundDifference() {
            super(2, false);
            op = new DifferenceOp();
        }

        public void setMode(Mode mode) {
            switch (mode) {
                case Color:
                    op.setMode(DifferenceOp.Mode.Color);
                    break;
                case Mono:
                    op.setMode(DifferenceOp.Mode.Mono);
                    break;
                case Threshold:
                    op.setMode(DifferenceOp.Mode.Threshold);
                    break;
            }
        }

        public Mode getMode() {
            DifferenceOp.Mode opMode = op.getMode();
            switch (opMode) {
                case Color:
                    return Mode.Color;
                case Mono:
                    return Mode.Mono;
                case Threshold:
                    return Mode.Threshold;
                default:
                    return Mode.Color; // shouldn't be possible
            }
        }

        public void setThreshold(double threshold) {
            op.setThreshold(threshold);
        }

        public double getThreshold() {
            return op.getThreshold();
        }

        @Override
        protected void process(Surface surface, boolean rendering) {
            if (rendering) {
                surface.clear();
                Surface input;
                int count = getSourceCount();
                if (count > 0) {
                    input = getInputSurface(0);
                    surface.copy(input);
                    input.release();
                }
                if (count > 1) {
                    input = getInputSurface(1);
                    surface.process(op, input);
                    input.release();
                }
            }
        }

    }
}
