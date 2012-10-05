/*
 * The MIT License
 * 
 * Copyright (c) 2011, Harald Wellmann
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.thucydides.jenkins;

import hudson.model.ProminentProjectAction;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

import org.kohsuke.stapler.StaplerProxy;

/**
 * An action letting the user browse the archived Thucydides reports of the last successful build,
 * similar to the Javadoc action. The user activates this action by clicking on the Thucydides icon
 * which is displayed on the job page and on the sidebar of the job page and all associated build
 * pages.
 * 
 * @author Harald Wellmann
 * 
 */
public class ThucydidesSummaryProjectAction implements ProminentProjectAction, StaplerProxy {


    private AbstractProject<?, ?> project;

    public ThucydidesSummaryProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    /**
     * Returns the relative URL for browsing the Thucydides reports.
     */
    public String getUrlName() {
        return "thucydides";
    }

    /**
     * Returns the name of this action displayed on the job page.
     * <p>
     * TODO i18n
     */
    public String getDisplayName() {
        return "Thucydides Test Report";
    }

    /**
     * Returns the Thucydides icon URL. The icon is a plugin resource, not a global one. The icon
     * will be displayed only if the report directory exists.
     */
    public String getIconFileName() {
        return ThucydidesPlugin.getIconFileName();
    }

    /**
     * Delegates all Stapler requests to the {@link ThucydidesBuildAction} of the last successful
     * build. This avoid duplicating the do... methods.
     */
    @Override
    public Object getTarget() {
        AbstractBuild<?, ?> build = project.getLastSuccessfulBuild();
        return build.getAction(ThucydidesBuildAction.class);
    }
}