#!/usr/bin/env python
####
# fprime-fpp:
#
# This package is used as a simple installer for the FPP tool suite used alongside fprime. The intent is to allow users to
# install the FPP tools in the same manner that all of the other tools (python) are installed. It also give an easy way
# for the "fprime" package to specify a specific dependency on FPP tools versions
####
import shutil
from pathlib import Path
from setuptools import setup
from setuptools.command.sdist import sdist
from setuptools.command.install import install

from fprime_fpp.fprime_fpp_install import clean_install_fpp, clean_at_exit, WORKING_DIR


SHADOW_DIR = Path("__SHADOW__")
SHADOW_CACHE = None


def make_shadows(shadow_dir, executables):
    """Makes shadow copies of the fpp executables

    The PIP sdist package must be aware of the names and paths of installed FPP executables, even though they are
    downloaded during the install phase (on client). Thus this function when run with touch_only creates 0-size file
    "shadows" of the files for the distributions.

    When run with touch_only=False, this function realizes those shadows from the downloaded copy. This is done on
    install to force the files to be real executables.
    """
    SHADOW_DIR.mkdir(exist_ok=True)
    for executable in executables:
        shadow_path = shadow_dir / executable.name
        yield shadow_path, executable


with clean_install_fpp() as lazy_executables:

    def cache_shadows(name_only=False):
        """Caches the output of the shadow generator

        Since the shadow data is used multiple times and incurs a very expensive call to the Github download/cloning code,
        the output is cached here and subsequent calls for this data return the cached version. However, this function
        maintains the lazy nature of executables and make_shadows preventing early execution of the expensive calls.
        """
        global SHADOW_CACHE
        iterable = (
            SHADOW_CACHE
            if SHADOW_CACHE is not None
            else make_shadows(SHADOW_DIR, lazy_executables)
        )

        new_cache = []
        for shadow_pair in iterable:
            new_cache.append(shadow_pair)
            yield str(shadow_pair[0]) if name_only else shadow_pair
        SHADOW_CACHE = new_cache

    class FppSdist(sdist):
        """Command to run at 'sdist' stage

        During the 'sdist' stage we want to download an expected FPP tarball and understand what is included inside
        (in terms of files). Then create a shadow set of 0-byte files as placeholders in the distribution. The install
        stage (below) will handle the work of replacing the shadow placeholders with the actual platform-specific
        binaries.

        'sdist' is also the only case where the environment variable is required to be set for building these tools. In
        other cases it will be set by fprime and will default to package when not set. This here we check for that
        variable and error if not set.
        """

        def run(self):
            """sdist package run implementation"""
            for shadow, _ in cache_shadows():
                shadow.touch()
            sdist.run(self)

    class FppInstall(install):
        def run(self):
            """install scripts for giles"""
            clean_at_exit(WORKING_DIR)
            for shadow, executable in cache_shadows():
                shutil.copy(executable, shadow)
            install.run(self)

    setup(
        name="fprime-fpp",
        use_scm_version={"root": ".", "relative_to": __file__},
        license="Apache 2.0 License",
        description="FPP distribution package",
        long_description="""
    Package used to deploy the FPP tool suite in the same manner as all other tools. FPP will be installed in the user's
    virtual environment or python distribution alongside other tools being user (e.g. fprime-util).
        """,
        url="https://github.com/nasa/fprime",
        keywords=["fpp", "fprime", "embedded", "nasa"],
        project_urls={"Issue Tracker": "https://github.com/nasa/fprime/issues"},
        author="Michael Starch",
        author_email="Michael.D.Starch@jpl.nasa.gov",
        classifiers=[
            "Development Status :: 5 - Production/Stable",
            "Intended Audience :: Developers",
            "Operating System :: MacOS",
            "Operating System :: POSIX :: Linux",
            "Programming Language :: Python",
            "Programming Language :: Python :: 3",
        ],
        setup_requires=["setuptools_scm"],
        python_requires=">=3.7",
        data_files=[("bin", cache_shadows(name_only=True))],
        packages=["fprime_fpp"],
        cmdclass={"sdist": FppSdist, "install": FppInstall},
    )
