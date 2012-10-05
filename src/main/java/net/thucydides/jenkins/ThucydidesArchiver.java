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

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A Publisher plugin for Jenkins which publishes the Thucydides test reports from the latest build
 * at a fixed URL, similar to the Javadoc archiver.
 * <p>
 * The plugin works both for Maven and freestyle builds. The only requirement is that Thucydides
 * reports were produced in a previous build step, e.g. by running the maven-failsafe-plugin and the
 * maven-thucydides-plugin in a Maven build.
 * <p>
 * The report directory (relative to this project's workspace folder) must be defined on the Jenkins
 * project configuration page.
 * <p>
 * This class has a config.jelly with a validation method in {@link ThucydidesArchiverDescriptor}.
 * 
 * @author Harald Wellmann
 */
@SuppressWarnings("unchecked")
public class ThucydidesArchiver extends Recorder {

    private String reportPath;


    /**
     * Constructs a ThucydidesArchiver with a parameter from the project configuration page.
     * 
     * @param reportPath root directory containing the Thucydides reports
     *        (.../target/site/thucydides when running the maven-thucydides-plugin). This parameter
     *        is bound by name to a field in config.jelly using the {@code DataBoundConstructor}
     *        annotation.
     */
    @DataBoundConstructor
    public ThucydidesArchiver(String reportPath) {
        this.reportPath = reportPath;
    }

    /**
     * This getter is required by config.jelly.
     * 
     * @return report path defined by user.
     */
    public String getReportPath() {
        return reportPath;
    }

    /**
     * For rendering a trend graph, we need the results of this build step from the previous build.
     */
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    /**
     * Copies the Thucydides reports of a given build to an archive child folder. The reports of the
     * last successful build are made available via a project action.
     * <p>
     * The build result may change from stable to unstable when there are Fit test failures or
     * exceptions.
     * <p>
     * If the build was successful, a {@link ThucydidesBuildAction} is added to the build to persist
     * the Thucydides test results. This is used for building a trend graph.
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();

        // Early exit when the build has failed. There's little chance of finding any reports
        // in this case anyway.
        if (build.getResult().isWorseOrEqualTo(Result.FAILURE)) {
            logger.println("[thucydides] not collecting results due to build failure");
            return true;
        }

        // Fail the build if the report directory does not exist. This is most probably caused
        // by incorrect input on the configuration page.
        FilePath report = build.getWorkspace().child(reportPath);
        if (!report.exists()) {
            logger.println("[thucydides] report directory " + report + " does not exist");
            build.setResult(Result.FAILURE);
            return true;
        }

        // Create an Action containing the test results counts to be persisted for this
        // build. This will be used for generating a trend graph.
        ThucydidesBuildAction action = new ThucydidesBuildAction(build);
        build.getActions().add(action);

        // Archive the entire Thucydides output folder.
        FilePath archive = new FilePath(ThucydidesPlugin.getBuildReportFolder(build));
        report.copyRecursiveTo(archive);

        return true;
    }

    /**
     * Returns the project-level actions of this plugin. This is required to make the actions
     * available to the user via relative URLs.
     * <p>
     * There is an action for displaying the latest test summary.
     */
    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        ThucydidesSummaryProjectAction summaryAction = new ThucydidesSummaryProjectAction(project);
        return Arrays.asList(summaryAction);
    }
}
