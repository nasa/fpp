""" install.py:

Installs the FPP tool suite based on the version of this installer package. It will use FPP_DOWNLOAD_CACHE environment
variable to pull up previously downloaded items for users that wish to install offline.
"""
import atexit
import os
import platform
import shutil
import subprocess
import sys
import tarfile
import tempfile
import time
import urllib.request
import urllib.error

from pathlib import Path
from typing import Iterable
from contextlib import contextmanager


def clean_at_exit(file_or_directory):
    """Register file or for cleanup at exit

    In order to be a nice citizen and ensure that the system does not break when rerunning pieces of the system this
    will clean-up files when the process is exiting.
    """

    def clean(path):
        """Clean up the path"""
        print(f"-- INFO  -- Removing: {path}")
        if os.path.isfile(path):
            os.remove(path)
        else:
            shutil.rmtree(path, ignore_errors=True)

    atexit.register(clean, file_or_directory)


def setup_version():
    """Setup the version information for the fpp tools that will be installed

    There are two cases that we care about with respect to the version:

    1. Building locally (e.g. pip install ., or python3 setup.py sdist): read version from the scm (e.g. git).
    2. Installing from existing package: read version from existing version file
    """
    try:
        from .fpp_version_info import FPP_TOOLS_VERSION
    except ImportError:
        process = subprocess.run(
            ["git", "describe", "--tag", "--always"], stdout=subprocess.PIPE, text=True
        )
        if process.returncode != 0:
            print(
                f"-- ERROR -- Cannot build locally without git and git repository",
                file=sys.stderr,
            )
            sys.exit(1)
        scm_version = process.stdout.strip()

        # Write-version file for package completeness
        version_path = Path(__file__).parent / "fpp_version_info.py"
        clean_at_exit(version_path)
        with open(version_path, "w") as file_handle:
            file_handle.write(f'FPP_TOOLS_VERSION="{scm_version}"\n')
        # Now the import should work as expected
        from .fpp_version_info import FPP_TOOLS_VERSION
    return FPP_TOOLS_VERSION


__FPP_TOOLS_VERSION__ = setup_version()
WORKING_DIR = Path(tempfile.gettempdir()) / "__FPP_WORKING_DIR__"
FPP_ARTIFACT_PREFIX = "native-fpp"
FPP_COMPRESSION_EXT = ".tar.gz"
GITHUB_URL = "https://github.com/fprime-community/fpp"
GITHUB_RELEASE_URL = "{GITHUB_URL}/releases/download/{version}/{artifact_string}"
SBT_URL = "https://github.com/sbt/sbt/releases/download/v1.6.2/sbt-1.6.2.tgz"


@contextmanager
def safe_chdir(path):
    """Safely change directory, returning when done."""
    origin = os.getcwd()
    try:
        os.chdir(path)
        yield
    finally:
        os.chdir(origin)


def get_artifact_string(version: str) -> str:
    """Gets the platform string for the package. e.g. Darwin-x86_64"""
    system = platform.system()
    architecture = platform.machine()
    # Always use x86 variants for Darwin
    if system == "Darwin":
        architecture = "x86_64"
    return f"{ FPP_ARTIFACT_PREFIX }-{ system }-{ architecture }{ FPP_COMPRESSION_EXT }"


def wget(url: str):
    """wget functionality to fetch a URL"""
    print(f"-- INFO  -- Fetching FPP tools at { url }", file=sys.stderr)
    try:
        urllib.request.urlretrieve(url, Path(url).name)
    except urllib.error.HTTPError as error:
        print(
            f"-- WARN  -- Failed to retrieve { url } with error: { error }",
            file=sys.stderr,
        )
        raise


def github_release_download(version: str):
    """Attempts to get FPP via the FPP release"""

    # Three download tries
    for _ in range(0, 3):
        try:
            release_url = GITHUB_RELEASE_URL.format(
                GITHUB_URL=GITHUB_URL,
                version=version,
                artifact_string=get_artifact_string(version),
            )
            wget(release_url)
        except urllib.error.HTTPError as error:
            retry_likely = "404" not in str(error)
            # Check if this is a real error or not available error
            if not retry_likely:
                raise
            print(f"-- INFO  -- Retrying download to resolve: {error}")
            time.sleep(5)  # Throttle


def prepare_cache_dir(version: str) -> Path:
    """Prepare the cache directory for the installation

    Detects a tar file of an expected version, and extracts the files from within it. This mimics the installation but
    from published artifacts rather than an install from source. This will delete the tar file to ensure it does not
    pollute the results.

    Args:
        version: version string of expected artifacts
    """
    expected_artifact = Path(os.getcwd()) / get_artifact_string(version)
    # Extract files from tar without any paths
    with tarfile.open(expected_artifact) as archive:
        for member in archive.getmembers():
            if member.isreg():
                member.name = os.path.basename(member.name)
                archive.extract(member, Path(os.getcwd()))
    os.remove(expected_artifact)


def verify_download(version: str):
    """Verify the download

    The downloaded products should be executable, and should produce a message with the --help flag that contains the
    expected version. This will verify that that happened properly.

    Args:
        version: version string of expected artifacts
    """
    for potential in Path(os.getcwd()).iterdir():
        process = subprocess.run(
            [str(potential), "--help"], stdout=subprocess.PIPE, text=True
        )
        process.check_returncode()
        if version not in process.stdout:
            raise Exception(f"Download not of expected version: {version}")


def install_fpp(working_dir: Path) -> Path:
    """Installs FPP of the specified version"""
    version = __FPP_TOOLS_VERSION__

    # Put everything in the current working directory
    with safe_chdir(working_dir):
        try:
            github_release_download(version)
            prepare_cache_dir(version)
            verify_download(version)
        except urllib.error.HTTPError:
            install_fpp_via_git(working_dir)
        except OSError as ose:
            print(f"-- ERROR -- Failed find expected download: {ose}", file=sys.stderr)
            sys.exit(-1)
        except Exception as exc:
            print(f"-- ERROR -- Failed to install tools: {exc}", file=sys.stderr)
        (working_dir / version).touch()
        return working_dir


def install_fpp_via_git(installation_directory: Path):
    """Installs FPP from git

    Should FPP not be available as a published version, this will clone the FPP repo, checkout, and build the FPP tools
    for the given version. This requires the following tools to exist on the system: git, sh, java, and sbt. These tools
    will be checked and then the process will run and intall into the specified directory.

    Args:
        installation_directory: directory to install into
    """
    tools = ["sh", "java"]
    for tool in tools:
        if not shutil.which(tool):
            print(
                f"-- ERROR -- {tool} must exist on PATH to build from source",
                file=sys.stderr,
            )
            sys.exit(-1)
    with tempfile.TemporaryDirectory() as tools_directory:
        os.chdir(tools_directory)
        wget(SBT_URL)
        with tarfile.open(os.path.basename(SBT_URL)) as archive:
            archive.extractall(".")
        sbt_path = Path(tools_directory) / "sbt" / "bin"
        subprocess_environment = os.environ.copy()
        subprocess_environment["PATH"] = f"{ sbt_path }:{ os.environ.get('PATH') }"
        steps = [
            [
                os.path.join(os.path.dirname(__file__), "..", "compiler", "install"),
                str(installation_directory),
            ],
        ]
        for step in steps:
            print(f"-- INFO  -- Running { ' '.join(step) }")
            completed = subprocess.run(step, env=subprocess_environment)
            if completed.returncode != 0:
                print(f"-- ERROR -- Failed to run { ' '.join(step) }", file=sys.stderr)
                sys.exit(-1)

    return installation_directory


def iterate_fpp_tools(working_dir: Path) -> Iterable[Path]:
    """Iterates through FPP tools"""
    executables = [
        os.access(executable, os.X_OK) for executable in working_dir.iterdir()
    ]
    # Check if executables exist and the version file was touched
    if executables and (working_dir / __FPP_TOOLS_VERSION__).exists():
        return working_dir.iterdir()
    # Clean up before a possible re-installation
    shutil.rmtree(working_dir)
    working_dir.mkdir()
    return install_fpp(working_dir).iterdir()


@contextmanager
def clean_install_fpp():
    """Cleanly installs FPP in subdirectory, cleaning when finished"""
    WORKING_DIR.mkdir(exist_ok=True)

    def lazy_loader():
        """Prevents the download of FPP items until actually enumerated"""
        yield from iterate_fpp_tools(WORKING_DIR)

    yield lazy_loader()
