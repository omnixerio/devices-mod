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

    if "/Library" not in sys.path:
        raise Exception("Failed to add /Library to sys.path")

    print(sys.path)

    import os
    if not os.path.exists("/Library/libsystem.py"):
        raise Exception("Failed to find /Library/libsystem.py")

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
