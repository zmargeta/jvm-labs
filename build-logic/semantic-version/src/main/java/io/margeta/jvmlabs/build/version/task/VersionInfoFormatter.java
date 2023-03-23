package io.margeta.jvmlabs.build.version.task;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.hocon.HoconFormat;
import com.electronwill.nightconfig.json.JsonFormat;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.yaml.YamlFormat;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static io.margeta.jvmlabs.build.version.Preconditions.checkNotNull;

public abstract sealed class VersionInfoFormatter {
    public static final VersionInfoFormatter TOML = new TomlFormatter();
    public static final VersionInfoFormatter YAML = new YamlFormatter();
    public static final VersionInfoFormatter HOCON = new HoconFormatter();
    public static final VersionInfoFormatter JSON = new JsonFormatter();

    protected VersionInfoFormatter() {}

    public static VersionInfoFormatter valueOf(String name) {
        checkNotNull(name, "name");
        return switch (name.toUpperCase()) {
            case "TOML" -> TOML;
            case "YAML" -> YAML;
            case "HOCON" -> HOCON;
            case "JSON" -> JSON;
            default -> throw new IllegalArgumentException("Unsupported formatter '%s'.".formatted(name));
        };
    }

    public String format(VersionInfo model) {
        final var format = makeFormat();
        final var config = format.createConfig();
        setVersion(config, model);
        setBranch(config, model);
        setCommitId(config, model);
        setCommitIdAbbrev(config, model);
        setCommitMessage(config, model);
        setCommitTime(config, model);
        setCommitUserName(config, model);
        setCommitUserEmail(config, model);
        setBuildNumber(config, model);
        setDirty(config, model);
        setHost(config, model);
        return format.createWriter().writeToString(config);
    }

    @SuppressWarnings("java:S1452")
    protected abstract ConfigFormat<? extends Config> makeFormat();

    private void setVersion(Config config, VersionInfo model) {
        setVersion(config, model.getVersion());
    }

    protected abstract void setVersion(Config config, String version);

    private void setBranch(Config config, VersionInfo model) {
        if (model.hasBranch()) {
            setBranch(config, model.getBranch());
        }
    }

    protected abstract void setBranch(Config config, String branch);

    private void setCommitId(Config config, VersionInfo model) {
        if (model.hasCommitId()) {
            setCommitId(config, model.getCommitId());
        }
    }

    protected abstract void setCommitId(Config config, String commitId);

    private void setCommitIdAbbrev(Config config, VersionInfo model) {
        if (model.hasCommitIdAbbrev()) {
            setCommitIdAbbrev(config, model.getCommitIdAbbrev());
        }
    }

    protected abstract void setCommitIdAbbrev(Config config, String commitIdAbbrev);

    private void setCommitMessage(Config config, VersionInfo model) {
        if (model.hasCommitMessage()) {
            setCommitMessage(config, model.getCommitMessage());
        }
    }

    protected abstract void setCommitMessage(Config config, String commitMessage);

    private void setCommitTime(Config config, VersionInfo model) {
        if (model.hasCommitTime()) {
            setCommitTime(config, model.getCommitTime());
        }
    }

    protected abstract void setCommitTime(Config config, OffsetDateTime commitTime);

    private void setCommitUserName(Config config, VersionInfo model) {
        if (model.hasCommitUserName()) {
            setCommitUserName(config, model.getCommitUserName());
        }
    }

    protected abstract void setCommitUserName(Config config, String commitUserName);

    private void setCommitUserEmail(Config config, VersionInfo model) {
        if (model.hasCommitUserEmail()) {
            setCommitUserEmail(config, model.getCommitUserEmail());
        }
    }

    protected abstract void setCommitUserEmail(Config config, String commitUserEmail);

    private void setDirty(Config config, VersionInfo model) {
        if (model.hasDirty()) {
            setDirty(config, model.getDirty());
        }
    }

    private void setBuildNumber(Config config, VersionInfo model) {
        if (model.hasBuildNumber()) {
            setBuildNumber(config, model.getBuildNumber());
        }
    }

    protected abstract void setBuildNumber(Config config, Long buildNumber);

    protected abstract void setDirty(Config config, Boolean dirty);

    private void setHost(Config config, VersionInfo model) {
        if (model.hasHost()) {
            setHost(config, model.getHost());
        }
    }

    protected abstract void setHost(Config config, String host);

    private static final class TomlFormatter extends VersionInfoFormatter {
        @Override
        protected ConfigFormat<? extends Config> makeFormat() {
            return TomlFormat.instance();
        }

        @Override
        protected void setBranch(Config config, String branch) {
            config.set("branch", branch);
        }

        @Override
        protected void setVersion(Config config, String version) {
            config.set("version", version);
        }

        @Override
        protected void setCommitId(Config config, String commitId) {
            config.set("commit_id", commitId);
        }

        @Override
        protected void setCommitIdAbbrev(Config config, String commitIdAbbrev) {
            config.set("commit_id_abbrev", commitIdAbbrev);
        }

        @Override
        protected void setCommitMessage(Config config, String commitMessage) {
            config.set("commit_message", commitMessage);
        }

        @Override
        protected void setCommitTime(Config config, OffsetDateTime commitTime) {
            config.set("commit_time", commitTime);
        }

        @Override
        protected void setCommitUserName(Config config, String commitUserName) {
            config.set("commit_user_name", commitUserName);
        }

        @Override
        protected void setCommitUserEmail(Config config, String commitUserEmail) {
            config.set("commit_user_email", commitUserEmail);
        }

        @Override
        protected void setBuildNumber(Config config, Long buildNumber) {
            config.set("build_number", buildNumber);
        }

        @Override
        protected void setDirty(Config config, Boolean dirty) {
            config.set("dirty", dirty);
        }

        @Override
        protected void setHost(Config config, String host) {
            config.set("host", host);
        }
    }

    private static final class YamlFormatter extends VersionInfoFormatter {
        @Override
        protected ConfigFormat<? extends Config> makeFormat() {
            final var dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            dumperOptions.setIndent(2);
            dumperOptions.setPrettyFlow(true);
            return YamlFormat.configuredInstance(new Yaml(dumperOptions));
        }

        @Override
        protected void setVersion(Config config, String version) {
            config.set("version", version);
        }

        @Override
        protected void setBranch(Config config, String branch) {
            config.set("branch", branch);
        }

        @Override
        protected void setCommitId(Config config, String commitId) {
            config.set("commitId", commitId);
        }

        @Override
        protected void setCommitIdAbbrev(Config config, String commitIdAbbrev) {
            config.set("commitIdAbbrev", commitIdAbbrev);
        }

        @Override
        protected void setCommitMessage(Config config, String commitMessage) {
            config.set("commitMessage", commitMessage);
        }

        @Override
        protected void setCommitTime(Config config, OffsetDateTime commitTime) {
            config.set("commitTime", commitTime.format(DateTimeFormatter.ISO_INSTANT));
        }

        @Override
        protected void setCommitUserName(Config config, String commitUserName) {
            config.set("commitUserName", commitUserName);
        }

        @Override
        protected void setCommitUserEmail(Config config, String commitUserEmail) {
            config.set("commitUserEmail", commitUserEmail);
        }

        @Override
        protected void setBuildNumber(Config config, Long buildNumber) {
            config.set("buildNumber", buildNumber);
        }

        @Override
        protected void setDirty(Config config, Boolean dirty) {
            config.set("dirty", dirty);
        }

        @Override
        protected void setHost(Config config, String host) {
            config.set("host", host);
        }
    }

    private static final class HoconFormatter extends VersionInfoFormatter {
        @Override
        protected ConfigFormat<? extends Config> makeFormat() {
            return HoconFormat.instance();
        }

        @Override
        protected void setVersion(Config config, String version) {
            config.set("version", version);
        }

        @Override
        protected void setBranch(Config config, String branch) {
            config.set("branch", branch);
        }

        @Override
        protected void setCommitId(Config config, String commitId) {
            config.set("commit-id", commitId);
        }

        @Override
        protected void setCommitIdAbbrev(Config config, String commitIdAbbrev) {
            config.set("commit-id-abbrev", commitIdAbbrev);
        }

        @Override
        protected void setCommitMessage(Config config, String commitMessage) {
            config.set("commit-message", commitMessage);
        }

        @Override
        protected void setCommitTime(Config config, OffsetDateTime commitTime) {
            config.set("commit-time", commitTime.format(DateTimeFormatter.ISO_INSTANT));
        }

        @Override
        protected void setCommitUserName(Config config, String commitUserName) {
            config.set("commit-user-name", commitUserName);
        }

        @Override
        protected void setCommitUserEmail(Config config, String commitUserEmail) {
            config.set("commit-user-email", commitUserEmail);
        }

        @Override
        protected void setBuildNumber(Config config, Long buildNumber) {
            config.set("build-number", buildNumber);
        }

        @Override
        protected void setDirty(Config config, Boolean dirty) {
            config.set("dirty", dirty);
        }

        @Override
        protected void setHost(Config config, String host) {
            config.set("host", host);
        }
    }

    private static final class JsonFormatter extends VersionInfoFormatter {
        @Override
        protected ConfigFormat<? extends Config> makeFormat() {
            return JsonFormat.fancyInstance();
        }

        @Override
        protected void setVersion(Config config, String version) {
            config.set("version", version);
        }

        @Override
        protected void setBranch(Config config, String branch) {
            config.set("branch", branch);
        }

        @Override
        protected void setCommitId(Config config, String commitId) {
            config.set("commitId", commitId);
        }

        @Override
        protected void setCommitIdAbbrev(Config config, String commitIdAbbrev) {
            config.set("commitIdAbbrev", commitIdAbbrev);
        }

        @Override
        protected void setCommitMessage(Config config, String commitMessage) {
            config.set("commitMessage", commitMessage);
        }

        @Override
        protected void setCommitTime(Config config, OffsetDateTime commitTime) {
            config.set("commitTime", commitTime.format(DateTimeFormatter.ISO_INSTANT));
        }

        @Override
        protected void setCommitUserName(Config config, String commitUserName) {
            config.set("commitUserName", commitUserName);
        }

        @Override
        protected void setCommitUserEmail(Config config, String commitUserEmail) {
            config.set("commitUserEmail", commitUserEmail);
        }

        @Override
        protected void setBuildNumber(Config config, Long buildNumber) {
            config.set("buildNumber", buildNumber);
        }

        @Override
        protected void setDirty(Config config, Boolean dirty) {
            config.set("dirty", dirty);
        }

        @Override
        protected void setHost(Config config, String host) {
            config.set("host", host);
        }
    }
}
