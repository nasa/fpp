import re
import subprocess
import sys
import shutil

from pathlib import Path

# The JAR file is compiled for this version of Java
MINIMUM_JAVA_VERSION = 25

# Flags that suppress JVM warnings. These require JDK 23 or later.
JAVA_WARNING_FLAGS = [
    "--sun-misc-unsafe-memory-access=allow",
    "--enable-native-access=ALL-UNNAMED",
]

def java_major_version(java):
    """ Return the major version of the given java executable

    Returns None if the version cannot be determined, in which case we let java
    report its own error rather than refusing to run.
    """
    try:
        result = subprocess.run([java, "-version"], capture_output=True, text=True)
    except OSError:
        return None
    # Most JVMs report the version on stderr, but check both streams
    match = re.search(r'version "(\d+)(?:\.(\d+))?', result.stderr + result.stdout)
    if match is None:
        return None
    major = int(match.group(1))
    # Versions before 9 have the form 1.N
    if major == 1 and match.group(2) is not None:
        return int(match.group(2))
    return major

def find_java():
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidate = Path(java_home) / "bin" / ("java.exe" if os.name == "nt" else "java")
        if candidate.is_file():
            return str(candidate)
    return shutil.which("java")

def main():
    """ Run fpp inferring a subcommand from the provided executable path

    This will invoke fpp inferring the name of the subcommand from the executable name that ran it. If the executable
    is named `fpp`, then it will run `fpp` without a subcommand. This will work with both `fpp` and `fpp.jar` variants.
    """
    # Identify the subcommand being run
    name = Path(sys.argv[0]).name
    subcommand = name[len("fpp-"):] if name.startswith("fpp-") else name

    # Determine the arguments supplied to fpp/fpp.jar
    base_arguments = [] if subcommand == "fpp" else [subcommand]
    base_arguments += sys.argv[1:]

    # Locate the fpp binary and/or JAR files
    binary_file = Path(__file__).parent / "fpp"
    jar_file = Path(__file__).parent / "fpp.jar"

    # Prefer the binary file if it exists
    if binary_file.exists() and name != "fpp-to-json":
        process = subprocess.run([str(binary_file)] + base_arguments)
    # Then check for the JAR file
    elif jar_file.exists():
        # Check for java availability when running the JAR file
        java = find_java()
        if not java:
            print(f"[ERROR] {sys.argv[0]} requires 'java'. Please install 'java' and ensure it is available at JAVA_HOME or on the PATH.")
            sys.exit(-23)
        # Check the java version, so that a too-old JVM produces a clear message
        # instead of an UnsupportedClassVersionError
        version = java_major_version(java)
        if version is not None and version < MINIMUM_JAVA_VERSION:
            print(f"[ERROR] {sys.argv[0]} requires Java {MINIMUM_JAVA_VERSION} or later, but '{java}' is Java {version}.")
            sys.exit(-23)
        process = subprocess.run([java] + JAVA_WARNING_FLAGS + ["-jar", str(jar_file)] + base_arguments)
    else:
        print(f"[ERROR] Neither {binary_file} nor {jar_file} could be found. Please ensure fpp is installed correctly.")
        sys.exit(-42)
    sys.exit(process.returncode)

if __name__ == "__main__":
    main()
