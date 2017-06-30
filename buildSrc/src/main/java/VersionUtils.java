import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VersionUtils {
    private static final String RELEASE_PREFIX = "release";

    public class Version {
        private int major;
        private int minor;
        private int patch;

        Version(String versionString) {
            String[] ss = versionString.split("-")[1].split("\\.");
            major = Integer.parseInt(ss[0]);
            minor = Integer.parseInt(ss[1]);
            patch = Integer.parseInt(ss[2]);
        }



        public Version(int major, int minor, int patch) {
            super();
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }


        public Version incPatch() {
            return new Version(major, minor, patch + 1);
        }

        public Version incMinor() {
            return new Version(major, minor + 1, 0);
        }

        public Version incMajor() {
            return new Version(major + 1, 0, 0);
        }

        @Override
        public String toString() {
            return RELEASE_PREFIX + "-" + toVersionSequence();
        }

        public String toVersionSequence() {
            return "" + major + "." + minor + "." + patch;
        }

    }

    private List<String> newFeatures = new ArrayList<>();
    private List<String> bugfixes = new ArrayList<>();
    private List<String> backwardIncompabilities = new ArrayList<>();
    private Version lastRelease;
    private Version newRelease;

    public VersionUtils() {
        List<String> output = runExternalCommand("git", "describe", "--tags", "--long", "--match=\"" + RELEASE_PREFIX + "-*\"");
        String l = output.get(0);
        int p = l.indexOf('-', RELEASE_PREFIX.length() + 2);
        lastRelease = new Version((p == -1) ? l : l.substring(0, p));

        output = runExternalCommand("git", "rev-parse", "--verify", lastRelease.toString());
        String lastReleaseHash = output.get(0);
//        System.out.println("Last realese hash: " + lastReleaseHash);

        output = runExternalCommand("git", "log", "--pretty=\"%B\"", lastReleaseHash + "...");
        output.forEach(ol -> {
            String comment = ol.trim();
            if (!comment.isEmpty()) {
                switch (comment.charAt(0)) {
                case '+':
                    newFeatures.add(0, comment.substring(1).trim());
                    break;
                case '%':
                    bugfixes.add(0, comment.substring(1).trim());
                    break;
                case '!':
                    backwardIncompabilities.add(0, comment.substring(1).trim());
                    break;

                default:
                    break;
                }
            }
        });

        System.out.println(String.format("Release statistics: %d incompatibilities, %d new features, %d bugfixes",
                backwardIncompabilities.size(), newFeatures.size(), bugfixes.size()));

//        System.out.println("New features:\n" + newFeatures.stream().collect(Collectors.joining("\n   ", "   ", "\n")));
//        System.out.println("Bugfixes:\n" + bugfixes.stream().collect(Collectors.joining("\n   ", "   ", "\n")));
//        System.out.println("Backward incompabilities:\n" + backwardIncompabilities.stream().collect(Collectors.joining("\n   ", "   ", "\n")));

        newRelease = createNewRelease();
    }

    public void validateGitState() {
        List<String> output;
        runExternalCommand("git", "remote", "origin");
        output = runExternalCommand("git", "status", "-uno");
        String wholeOutput = output.stream().collect(Collectors.joining("\n"));
        if (!output.get(0).contains("On branch master")) {
            throw new IllegalStateException("Not on master branch.");
        }
        if (!wholeOutput.contains("nothing to commit")) {
            throw new IllegalStateException("Local repo is dirty (there are uncommited changes).");
        }

        if (wholeOutput.contains("branch is behind")) {
            throw new IllegalStateException("Local branch is behind remote.");
        }
    }

    public void updateChangeLog() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("changelog.md"), StandardCharsets.UTF_8);
            int ip = 0;
            while (ip < lines.size()) {
                if (lines.get(ip).trim().startsWith("### ")) {
                    break;
                }
                ip++;
            }
            if (lines.get(ip).contains(newRelease.toVersionSequence())) {
                do {
                    lines.remove(ip);
                } while (!lines.get(ip).trim().startsWith("### "));
            }

            lines.add(ip++, "### Version " + newRelease.toVersionSequence() + " (" +
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()).replaceAll("T", " ") + ")");
            lines.add(ip++, "");
            ip = addToLog(lines, ip, newFeatures, "New features");
            ip = addToLog(lines, ip, bugfixes, "Bugfixes");
            ip = addToLog(lines, ip, backwardIncompabilities, "Backward incompabilities");
            Files.write(Paths.get("changelog.md"), lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void updateRepository() {
        runExternalCommand("git", "add", ".");
        runExternalCommand("git", "commit", "-m", "releasing version " + newRelease.toVersionSequence() + "");
        runExternalCommand("git", "tag", "-a", newRelease.toString(), "-m", "Version " + newRelease.toVersionSequence() + " is released.");
        runExternalCommand("git", "push", "origin", "master", newRelease.toString());
    }

    private int addToLog(List<String> lines, int ip, List<String> data, String title) {
        if (!data.isEmpty()) {
            lines.add(ip++, "#### " + title);
            lines.add(ip++, "");
            for (String en : data) {
                lines.add(ip++, "- " + en.trim());
            }
            lines.add(ip++, "");
        }
        return ip;
    }

    public Version getLastRelease() {
        return lastRelease;
    }


    public Version getNewRelease() {
        return newRelease;
    }

    public void pushNewVersion() {
        runExternalCommand("git", "tag", "-a", newRelease.toString(), "-m", "New release created: " + newRelease.toVersionSequence());
        //runExternalCommand("git", "push", "origin", "--tags");
    }

    private Version createNewRelease() {
        if (backwardIncompabilities.isEmpty()) {
            if (newFeatures.isEmpty()) {
                return lastRelease.incPatch();
            } else {
                return lastRelease.incMinor();
            }
        } else {
            return lastRelease.incMajor();
        }


    }

    private List<String> runExternalCommand(String... params) {
        Process p;
        try {
            p = new ProcessBuilder(params).start();
            p.waitFor();

            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            List<String> output = new ArrayList<>();
            // bri may be empty or incomplete.
            while ((line = bri.readLine()) != null) {
                output.add(line);
            }
            return output;
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public void simulateUpload() {
        if (Math.random() > 10.5) {
            throw new RuntimeException();
        }
    }

}