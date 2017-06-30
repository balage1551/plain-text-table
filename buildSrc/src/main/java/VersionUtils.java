import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        System.out.println("Last realese hash: " + lastReleaseHash);

        output = runExternalCommand("git", "log", "--pretty=oneline", lastReleaseHash + "...");
        output.forEach(ol -> {
            String comment = ol.substring(ol.indexOf(' ') + 1).trim();
            switch (comment.charAt(0)) {
            case '+':
                newFeatures.add(comment.substring(1).trim());
                break;
            case '%':
                bugfixes.add(comment.substring(1).trim());
                break;
            case '!':
                backwardIncompabilities.add(comment.substring(1).trim());
                break;

            default:
                break;
            }
        });

        System.out.println("New features:\n" + newFeatures.stream().collect(Collectors.joining("\n   ", "   ", "\n")));
        System.out.println("Bugfixes:\n" + bugfixes.stream().collect(Collectors.joining("\n   ", "   ", "\n")));
        System.out.println("Backward incompabilities:\n" + backwardIncompabilities.stream().collect(Collectors.joining("\n   ", "   ", "\n")));

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
        if (Math.random() < 0.5) {
            throw new RuntimeException();
        }
    }

}