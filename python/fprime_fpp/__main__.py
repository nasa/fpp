import subprocess
import sys
import shutil

from pathlib import Path

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
    if binary_file.exists():
        process = subprocess.run([str(binary_file)] + base_arguments)
    # Then check for the JAR file
    elif jar_file.exists():
        # Check for java availability when running the JAR file
        if not shutil.which("java"):
            print(f"[ERROR] {sys.argv[0]} requires 'java'. Please install 'java' and ensure it is available on the PATH.")
            sys.exit(-23)
        process = subprocess.run(["java", "-jar", str(jar_file)] + base_arguments)
    else:
        print(f"[ERROR] Neither {binary_file} nor {jar_file} could be found. Please ensure fpp is installed correctly.")
        sys.exit(-42)
    sys.exit(process.returncode)

if __name__ == "__main__":
    main()