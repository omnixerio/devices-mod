import os

def create_dir(path, mode):
    if not os.path.exists(path):
        os.makedirs(path, exist_ok=True)


# noinspection PyProtectedMember,PyUnresolvedReferences
def boot():
    import sys
    create_dir("/Binaries", 0o755)
    create_dir("/System", 0o755)
    create_dir("/System/dev", 0o755)
    create_dir("/System/proc", 0o755)
    create_dir("/System/sys", 0o755)

    create_dir("/Device/Parallel", 0o755)
    create_dir("/Device/Serial", 0o755)
    create_dir("/Device/USB", 0o755)
    create_dir("/Device/Virtual", 0o755)

    create_dir("/User/bin", 0o755)
    create_dir("/User/lib", 0o755)
    create_dir("/User/sbin", 0o755)
    create_dir("/User/share", 0o755)
    create_dir("/User/local", 0o755)
    create_dir("/User/local/bin", 0o755)
    create_dir("/User/local/Library", 0o755)

    create_dir("/VariableData", 0o755)
    create_dir("/VariableData/log", 0o755)
    create_dir("/VariableData/lib", 0o755)
    create_dir("/VariableData/lock", 0o755)
    create_dir("/VariableData/run", 0o755)
    create_dir("/VariableData/tmp", 0o755)

    create_dir("/Config", 0o755)
    create_dir("/Media", 0o755)
    create_dir("/Temp", 0o755)
    create_dir("/OptSoftware", 0o755)
    create_dir("/Home", 0o755)
    create_dir("/Home/setup", 0o755)
    create_dir("/Root", 0o755)
    create_dir("/Runtime", 0o755)
    create_dir("/Software", 0o755)

    sys.path.append("/Library")
    sys.path.append("/User/Library")
    sys.path.append("/User/Local/Library")
    sys.path.append("/System/Library")
    sys.path.append("/VariableData/Library")

    print(sys.path)

    boot_path = "/System/Library/libsystem.py"

    import os
    if not os.path.exists(boot_path):
        raise Exception(f"Failed to find {boot_path}")

    import importlib
    from importlib import util as importutil
    from importlib.machinery import ModuleSpec
    from importlib.abc import MetaPathFinder
    importlib.invalidate_caches()

    class UltaOSPathFinder(MetaPathFinder):
        def find_spec(self, fullname, path=None, target=None) -> ModuleSpec | None:
            for p in sys.path:
                if os.path.exists(os.path.join(p, fullname.replace(".", "/") + ".py")):
                    return importutil.spec_from_file_location(fullname, os.path.join(p, fullname.replace(".", "/") + ".py"))
            return None

        def invalidate_caches(self):
            super().invalidate_caches()

    sys.meta_path.insert(0, UltaOSPathFinder())

    import libsystem

    # noinspection PyUnresolvedReferences
    try:
        libsystem._bootinit(__bios)
    except Exception as e:
        print(e)

        while True:
            pass


class SLF4JLogger:
    def __init__(self):
        pass

    def error(self, message):
        raise NotImplemented

    def info(self, message):
        raise NotImplemented

    def debug(self, message):
        raise NotImplemented


logger: SLF4JLogger


try:
    boot()
except Exception as e:
    import traceback
    traceback.print_exception(e)
