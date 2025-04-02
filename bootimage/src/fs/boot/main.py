import os

def create_dir(path, mode):
    if not os.path.exists(path):
        os.makedirs(path, exist_ok=True)


# noinspection PyProtectedMember,PyUnresolvedReferences
def boot():
    import sys
    create_dir("/bin", 0o755)
    create_dir("/sys", 0o755)
    create_dir("/sys/dev", 0o755)
    create_dir("/sys/proc", 0o755)
    create_dir("/sys/sys", 0o755)

    create_dir("/dev/pts", 0o755)
    create_dir("/dev/serial", 0o755)
    create_dir("/dev/usb", 0o755)
    create_dir("/dev/virtual", 0o755)

    create_dir("/usr/bin", 0o755)
    create_dir("/usr/lib", 0o755)
    create_dir("/usr/sbin", 0o755)
    create_dir("/usr/share", 0o755)
    create_dir("/usr/local", 0o755)
    create_dir("/usr/local/bin", 0o755)
    create_dir("/usr/local/lib", 0o755)

    create_dir("/var", 0o755)
    create_dir("/var/log", 0o755)
    create_dir("/var/lib", 0o755)
    create_dir("/var/lock", 0o755)
    create_dir("/var/run", 0o755)
    create_dir("/var/tmp", 0o755)

    create_dir("/etc", 0o755)
    create_dir("/media", 0o755)
    create_dir("/tmp", 0o755)
    create_dir("/opt", 0o755)
    create_dir("/home", 0o755)
    create_dir("/home/setup", 0o755)
    create_dir("/root", 0o755)
    create_dir("/run", 0o755)
    create_dir("/sbin", 0o755)

    sys.path.append("/usr/bin")
    sys.path.append("/usr/lib")
    sys.path.append("/bin")
    sys.path.append("/lib")
    sys.path.append("/usr/local/bin")
    sys.path.append("/usr/local/lib")
    sys.path.append("/sbin")
    sys.path.append("/usr/sbin")
    sys.path.append("/usr/local/sbin")
    sys.path.append("/var/lib")

    if "/lib" not in sys.path:
        raise Exception("Failed to add /lib to sys.path")

    import os
    if not os.path.exists("/lib/libsystem.py"):
        raise Exception("Failed to find /lib/libsystem.py")

    import importlib

    importlib.invalidate_caches()
    libsystem = importlib.import_module("libsystem")

    # noinspection PyUnresolvedReferences
    try:
        libsystem._bootinit(bios)
    except Exception as e:
        print(e)

        while True:
            pass


boot()
