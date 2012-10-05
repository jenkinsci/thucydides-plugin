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

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;

/**
 * Descriptor for the {@link ThucydidesArchiver}.
 * <p>
 * This class interacts with config.jelly and the configuration page.
 * 
 * @author Harald Wellmann
 * 
 */
@Extension
public class ThucydidesArchiverDescriptor extends BuildStepDescriptor<Publisher> {
    public ThucydidesArchiverDescriptor() {
        super(ThucydidesArchiver.class);
    }

    /**
     * This human readable name is used in the configuration screen.
     * <p>
     * TODO i18n
     */
    public String getDisplayName() {
        return "Publish Thucydides Test Reports";
    }

    /**
     * Checks the reportPath value entered in the configuration form by testing if the path exists.
     * This check is activated by a corresponding attribute in config.jelly.
     */
    public FormValidation doCheck(@AncestorInPath AbstractProject<?, ?> project,
            @QueryParameter String value) throws IOException, ServletException {
        FilePath ws = project.getSomeWorkspace();
        return ws != null ? ws.validateRelativeDirectory(value) : FormValidation.ok();
    }

    /**
     * This publisher is applicable to all job types, Maven or not.
     */
    @SuppressWarnings("rawtypes")
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }
}