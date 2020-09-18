package io.jenkins.plugins.jenkinswork.postbuild;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.*;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.sprints.Release;
import io.jenkins.plugins.util.Util;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class CreateRelease extends Recorder {
    private String prefix = null, itemPrefix = null, releaseName = null, description = null, stage = null, owner = null, period = null;
    public String getPrefix() {
        return prefix;
    }

    public String getItemPrefix() {
        return itemPrefix;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public String getDescription() {
        return description;
    }

    public String getStage() {
        return stage;
    }

    public String getOwner() {
        return owner;
    }

    public String getPeriod() {
        return period;
    }

    @DataBoundConstructor
    public CreateRelease(String prefix, String itemPrefix, String releaseName, String description, String stage, String owner, String period) {
        this.prefix = prefix;
        this.itemPrefix = itemPrefix;
        this.releaseName = releaseName;
        this.description = description;
        this.stage = stage;
        this.owner = owner;
        this.period = period;
    }
    /**
     *
     * @return Monitoring Service for BuildStep
     */
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return Release.getInstanceForCreate(build, listener, prefix, itemPrefix, releaseName, stage, description, owner, period).create();
    }
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        /**
         * Prefix valid check
         * @param prefix In which Sprint Item comment to be added
         * @return if prefix matches the regex the OK else Error
         */
        public FormValidation doCheckPrefix(@QueryParameter final String prefix) {

            if (prefix.matches(Util.PROJECT_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        public FormValidation doCheckReleaseName(@QueryParameter final String releaseName) {
            if(!releaseName.isEmpty()) {
                return FormValidation.ok();
            }
            return FormValidation.ok("error");
        }
        public FormValidation doCheckReleasePrefix(@QueryParameter final String releasePrefix) {
            if (releasePrefix.matches(Util.RELEASE_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        public FormValidation doCheckItemPrefix(@QueryParameter final String itemPrefix) {
            if (itemPrefix.matches(Util.ITEM_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        public FormValidation doCheckStage(@QueryParameter final String stage) {
            if (!isEmpty(stage)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        public FormValidation doCheckDescription(@QueryParameter final String description) {
            if (!isEmpty(description)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        public FormValidation doCheckOwner(@QueryParameter final String owner) {
            if (!isEmpty(owner)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.release_create();
        }
    }
}