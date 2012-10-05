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

import static net.thucydides.jenkins.ThucydidesPlugin.getBuildReportFolder;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.DirectoryBrowserSupport;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Build level action to display the Thucydides result summary of the last build.
 * 
 * @author Harald Wellmann
 * 
 */
public class ThucydidesBuildAction implements Action {

    private AbstractBuild<?, ?> build;


    public ThucydidesBuildAction(AbstractBuild<?, ?> build) {
        this.build = build;
    }


    /**
     * Returns the Thucydides icon URL. The icon is a plugin resource, not a global one. The icon
     * will be displayed only if the report directory exists.
     */
    public String getIconFileName() {
        return ThucydidesPlugin.getIconFileName();
    }


    /**
     * Label for the icon displayed on the project page.
     */
    @Override
    public String getDisplayName() {
        return "Thucydides Test Report";
    }

    /**
     * Relative URL for the report resource.
     */
    @Override
    public String getUrlName() {
        return "thucydidesReport";
    }


    /**
     * Creates a directory browser for the given icon. This browser maps Jenkins URLs to relative
     * paths in the report archive via Stapler and renders the corresponding files.
     * 
     * @param req
     * @param rsp
     * @return
     * @throws IOException
     * @throws ServletException
     * @throws InterruptedException
     */
    public DirectoryBrowserSupport doDynamic(StaplerRequest req, StaplerResponse rsp)
            throws IOException, ServletException, InterruptedException {

        AbstractProject<?, ?> project = build.getProject();
        String title = project.getDisplayName() + " Thucydides Result Summary";
        FilePath systemDirectory = new FilePath(getBuildReportFolder(build));
        return new DirectoryBrowserSupport(this, systemDirectory, title, null, false);
    }
}
