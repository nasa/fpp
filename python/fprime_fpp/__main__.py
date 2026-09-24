import subprocess
import sys

from pathlib import Path

def main():
    """ Run fpp inferring a subcommand from the provided executable path

    This will invoke fpp inferring the name of the subcommand from the executable name that ran it. If the executable
    is named `fpp`, then it will run `fpp` without a subcommand
    """
    # Identify the subcommand being run
    name = Path(sys.argv[0]).name
    subcommand = name[len("fpp-"):] if name.startswith("fpp-") else name

    if name == "fpp-to-json":
        print(f"[ERROR] fpp-to-json is no longer supported. Please migrate to fprime-fpp-python", file=sys.stderr)
        sys.exit(-41)

    # Determine the arguments supplied to fpp/fpp.jar
    base_arguments = [] if subcommand == "fpp" else [subcommand]
    base_arguments += sys.argv[1:]


    # Locate the fpp binary
    binary_file = Path(__file__).parent / "fpp"

    # Prefer the binary file if it exists
    if binary_file.exists():
        process = subprocess.run([str(binary_file)] + base_arguments)
    else:
        print(f"[ERROR] {binary_file} could be found. Please ensure fpp is installed correctly.", file=sys.stderr)
        sys.exit(-42)
    sys.exit(process.returncode)

if __name__ == "__main__":
    main()
