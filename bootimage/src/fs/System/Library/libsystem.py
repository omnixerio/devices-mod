import threading
from copy import deepcopy
from typing import Callable, Any

__all__ = [
    'readonly',
    'ProcessApi',
    'Process',
    'UiApi',
    'InventoryApi',
    'ButtonAPI',
    'LabelAPI',
    'Image',
    'FileSystemApi',
]


def readonly(self):
    __setattr__ = self.__setattr__

    def is_called_from(self):
        import inspect

        if inspect.ismodule(self):
            stack = inspect.stack()
            return len(stack) > 1 and inspect.stack()[1][0].f_globals.get('__qualname__', None) == self.__qualname__
        elif inspect.ismethod(self):
            stack = inspect.stack()
            return len(stack) > 1 and inspect.stack()[1][0].f_locals.get('self', None) is self
        elif inspect.isfunction(self):
            stack = inspect.stack()
            return len(stack) > 1 and inspect.stack()[1][0].f_locals.get('__qualname__', None) == self.__qualname__
        elif inspect.isclass(self):
            stack = inspect.stack()
            return len(stack) > 1 and inspect.stack()[1][0].f_locals.get('__qualname__', None) == self.__qualname__
        else:
            return False

    def __internal_setattr__(self, key, value):
        if key.startswith('_') and is_called_from(self):
            raise AttributeError(f"Cannot set attribute {key}, module '{type(self).__name__}' is read-only")

        __setattr__(self, key, value)

    self.__setattr__ = __internal_setattr__

    return self


# noinspection PyPep8Naming
@readonly
class InventoryApi:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def get(self, index: int) -> tuple[str, int]:
        # Native API
        raise NotImplemented()

    def count(self) -> int:
        # Native API
        raise NotImplemented()

    def find(self, name: str) -> int:
        # Native API
        raise NotImplemented()

    def findAll(self, name: str) -> list[int]:
        # Native API
        raise NotImplemented()

    def getName(self, index: int) -> str:
        # Native API
        raise NotImplemented()


# noinspection PyPep8Naming
@readonly
class ButtonAPI:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def setText(self, text: str):
        # Native API
        raise NotImplemented()

    def setEnabled(self, enabled: bool):
        # Native API
        raise NotImplemented()

    def setPressed(self, pressed: bool):
        # Native API
        raise NotImplemented()

    def isPressed(self) -> bool:
        # Native API
        raise NotImplemented()

    def isEnabled(self) -> bool:
        # Native API
        raise NotImplemented()

    def getText(self) -> str:
        # Native API
        raise NotImplemented()


# noinspection PyPep8Naming
@readonly
class LabelAPI:
    def setText(self, text: str):
        # Native API
        raise NotImplemented()

    def getText(self) -> str:
        # Native API
        raise NotImplemented()

    def setEnabled(self, enabled: bool):
        # Native API
        raise NotImplemented()

    def isEnabled(self) -> bool:
        # Native API
        raise NotImplemented()


# noinspection PyPep8Naming
@readonly
class Image:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def getWidth(self) -> int:
        # Native API
        raise NotImplemented()

    def getHeight(self) -> int:
        # Native API
        raise NotImplemented()

    def getData(self) -> bytes:
        # Native API
        raise NotImplemented()

    def destroy(self):
        # Native API
        raise NotImplemented()


# noinspection PyPep8Naming
@readonly
class UiApi:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def createButton(self, x: int, y: int, width: int, height: int, text: str, normal: Image, pressed: Image, disabled: Image) -> ButtonAPI:
        # Native API
        raise NotImplemented()

    def createLabel(self, x: int, y: int, width: int, height: int, text: str) -> LabelAPI:
        # Native API
        raise NotImplemented()

    def createImage(self, x: int, y: int, width: int, height: int, data: bytes) -> Image:
        # Native API
        raise NotImplemented()

    def getImageLimit(self) -> int:
        # Native API
        raise NotImplemented()

    def getImageCount(self) -> int:
        # Native API
        raise NotImplemented()


# noinspection PyPep8Naming
@readonly
class ProcessApi:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def onExit(self, callback: Callable[[int], None]):
        # Native API
        raise NotImplemented()

    def pid(self) -> int:
        # Native API
        raise NotImplemented()

    def kill(self, code: int):
        # Native API
        raise NotImplemented()


# noinspection PyPep8Naming
@readonly
class Gpu:
    def __init__(self):
        self.gl11: Any
        self.gl12: Any
        self.gl13: Any
        self.gl14: Any
        self.gl15: Any
        self.gl20: Any
        self.gl21: Any
        self.gl30: Any
        self.gl31: Any
        self.gl32: Any
        self.gl33: Any
        self.gl40: Any
        self.gl41: Any

    def getWidth(self) -> int:
        # Native API
        raise NotImplemented()

    def getHeight(self) -> int:
        # Native API
        raise NotImplemented()

    def createTexture(self, data: bytes) -> int:
        # Native API
        raise NotImplemented()

    def drawTexture(self, texture: int, x: int, y: int, width: int, height: int):
        # Native API
        raise NotImplemented()

    def deleteTexture(self, texture: int):
        # Native API
        raise NotImplemented()

    def fill(self, x: int, y: int, width: int, height: int, color: int):
        # Native API
        raise NotImplemented()

    def rect(self, x: int, y: int, width: int, height: int, color: int):
        # Native API
        raise NotImplemented()


# noinspection PyPep8Naming
@readonly
class __BiosApi:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def getUiApi(self) -> UiApi:
        # Native API
        raise NotImplemented()

    def spawnProcess(self, modules, init: str, command: list[str], env: dict[str, str]) -> ProcessApi:
        # Native API
        raise NotImplemented()

    def getProcess(self, pid: int) -> ProcessApi:
        # Native API
        raise NotImplemented()

    def fsCreateDir(self, path: str, mode: int):
        # Native API
        raise NotImplemented()

    def fsDelete(self, path: str):
        # Native API
        raise NotImplemented()

    def fsExists(self, path: str) -> bool:
        # Native API
        raise NotImplemented()

    def fsIsDir(self, path: str) -> bool:
        # Native API
        raise NotImplemented()

    def fsIsFile(self, path: str) -> bool:
        # Native API
        raise NotImplemented()

    def fsRead(self, path: str) -> bytes:
        # Native API
        raise NotImplemented()

    def fsWrite(self, path: str, data: bytes):
        # Native API
        raise NotImplemented()

    def fsRename(self, old_path: str, new_path: str):
        # Native API
        raise NotImplemented()

    def fsCopy(self, src: str, dest: str):
        # Native API
        raise NotImplemented()

    def getGpu(self) -> Gpu:
        # Native API
        raise NotImplemented()


@readonly
def get_ui() -> UiApi:
    # Native API
    raise NotImplemented()


@readonly
def request_shutdown():
    globals()['__shutdown_requested'] = True


class Process:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def wait(self):
        # Native API
        raise NotImplemented()

    def kill(self):
        # Native API
        raise NotImplemented()

    def update(self):
        # Native API
        raise NotImplemented()

    def __setattr__(self, key, value):
        if not hasattr(self, key):
            raise AttributeError(key)


class PowerState:
    __STATE = None

    @classmethod
    def shutdown(cls):
        cls.__STATE = 0

    @classmethod
    def reboot(cls):
        cls.__STATE = 1


class __SystemApi:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def syscall(self, syscall: str, *args) -> Any:
        # Native API
        raise NotImplemented()


system: __SystemApi


class FileSystemApi:
    class FileInfo:
        def __init__(self):
            self.name: str
            self.size: int
            self.is_dir: bool
            self.is_file: bool
            self.mode: int
            self.mtime: int
            self.ctime: int
            self.atime: int
            self.uid: int
            self.gid: int
            self.dev: int
            self.ino: int
            self.nlink: int
            self.rdev: int

    @staticmethod
    def create_dir(path: str, mode: int):
        system.syscall("filesystem", "mkdir", path, mode)

    @staticmethod
    def delete(path: str):
        system.syscall("filesystem", "delete", path)

    @staticmethod
    def exists(path: str) -> bool:
        return system.syscall("filesystem", "exists", path)

    @staticmethod
    def is_dir(path: str) -> bool:
        return system.syscall("filesystem", "is_dir", path)

    @staticmethod
    def is_file(path: str) -> bool:
        return system.syscall("filesystem", "is_file", path)

    @staticmethod
    def size(path: str) -> int:
        return system.syscall("filesystem", "size", path)

    @staticmethod
    def read(path: str, size: int) -> bytes:
        return system.syscall("filesystem", "read", path, size)

    @staticmethod
    def create_file(path: str, mode: int, data: bytes = b''):
        system.syscall("filesystem", "create_file", path, mode, data)

    @staticmethod
    def write(path: str, data: bytes, offset: int = 0, size: int = 0):
        system.syscall("filesystem", "write", path, data)

    @staticmethod
    def append(path: str, data: bytes):
        system.syscall("filesystem", "append", path, data)

    @staticmethod
    def rename(old_path: str, new_path: str):
        system.syscall("filesystem", "rename", old_path, new_path)

    @staticmethod
    def copy(src: str, dest: str):
        system.syscall("filesystem", "copy", src, dest)

    @staticmethod
    def move(src: str, dest: str):
        system.syscall("filesystem", "move", src, dest)

    @staticmethod
    def list_dir(path: str) -> list[str]:
        return system.syscall("filesystem", "list_dir", path)

    @staticmethod
    def get_file_info(path: str) -> FileInfo:
        return system.syscall("filesystem", "get_file_info", path)


class ProcessesApi:
    @staticmethod
    def get_process(pid: int) -> Process:
        return system.syscall("process", "get_process", pid)

    @staticmethod
    def get_all_processes() -> list[Process]:
        return system.syscall("process", "get_all_processes")

    @staticmethod
    def kill(pid: int):
        system.syscall("process", "kill_process", pid)

    @staticmethod
    def spawn(path: str, args: list[str], env: dict[str, str]) -> Process:
        """
        Spawn a new process

        :param path: The path to the executable
        :param args: The arguments to pass to the executable
        :param env: The environment variables to set for the new process
        :return: The new process
        :raises OSError: If the process could not be spawned
        """
        return system.syscall("process", "spawn_process", path, args, env)

    @staticmethod
    def get_current_process() -> Process:
        return system.syscall("process", "get_current_process")

    @staticmethod
    def run_as(user: str, func: Callable[[Any], Any], *args, **kwargs) -> Any:
        """
        Run a function as a different user
        :param user: The user to run the function as
        :param func: The function to run
        :param args: The arguments to pass to the function
        :return: The return value of the function
        """
        return system.syscall("process", "run_as", user, func, args, kwargs)


class OSApi:
    @staticmethod
    def get_env() -> dict[str, str]:
        return system.syscall("os", "get_env")

    @staticmethod
    def set_env(key: str, value: str):
        system.syscall("os", "set_env", key, value)

    @staticmethod
    def get_bios_version() -> str:
        return system.syscall("os", "get_bios_version")

    @staticmethod
    def get_kernel_version() -> str:
        return system.syscall("os", "get_kernel_version")

    @staticmethod
    def get_os_version() -> str:
        with open("/Config/os-release", "r") as f:
            data = f.read()
        return data


__FILE_SYSTEM = FileSystemApi()
__PROCESS = ProcessesApi()


def filesystem() -> FileSystemApi:
    """
    Get the filesystem API
    :return: The filesystem API
    """
    return __FILE_SYSTEM

def processes() -> ProcessesApi:
    """
    Get the process API
    :return: The process API
    """
    return __PROCESS

def shutdown():
    system.syscall("power_state", 0)

def reboot():
    system.syscall("power_state", 1)

def is_allowed(privilege: str) -> bool:
    return system.syscall("privilege", "check", privilege)

def glContext(func: Callable[[Gpu], Any], *args, **kwargs) -> Any:
    """
    Get the OpenGL context
    :return: The OpenGL context
    """
    return system.syscall("gl", "context", func, args, kwargs)

def run_privileged(privilege: str, func: Callable[[Any], Any], *args, **kwargs) -> Any:
    """
    Run a function with the given privilege.
    :param privilege: The privilege to run the function with.
    :param func: The function to run.
    :param args: The arguments to pass to the function
    :return: The return value of the function
    :raises PermissionError: If the privilege is not allowed
    """
    return system.syscall("privilege", "run", privilege, func, args, kwargs)

# noinspection PyUnresolvedReferences
def _bootinit(bios: __BiosApi):
    """
    This method is inaccessible after Boot setup
    :param bios: the bios api
    :return: None
    """
    import sys, os, os.path, copy

    print("Booting...")

    globals()['get_ui'] = bios.getUiApi
    globals()['bootinit'] = None
    globals()['__shutdown_requested'] = False

    print("Locking modules")

    current_process=None

    privileges = threading.local()
    privileges._privilege = None

    class Privilege:
        def __init__(self):
            self._privilege = ""

    def process_end(proc: Process | None, code: int):
        if proc is not None:
            proc.wait()
        sys.exit(code)

    sys.exit = lambda code: process_end(current_process, code)

    def do_not_allow_setattr(self, key, value):
        raise AttributeError(f"Cannot set attribute {key}, module '{type(self).__name__}' is read-only")

    sys.modules['libsystem'].__setattr__ = do_not_allow_setattr
    sys.modules['sys'].__setattr__ = do_not_allow_setattr
    sys.modules['os'].__setattr__ = do_not_allow_setattr
    sys.modules['os.path'].__setattr__ = do_not_allow_setattr

    item = sys.modules.__setitem__

    def do_not_allow_setitem(key, value):
        if key == 'libsystem' or key == 'sys' or key == 'os' or key == 'os.path':
            raise AttributeError(f"Cannot set attribute module is read-only")
        item(key, value)

    def do_not_allow_setattr_modules(self, key, value):
        if key == "modules" or key == "__setitem__" or key == "__setattr__":
            raise AttributeError(f"Cannot set attribute {key}, module '{type(self).__name__}' is read-only")

    print("Launching processes...")

    processes: list[ProcessApi] = []

    def filesystem_handler(*args) -> Any:
        if len(args) == 0:
            raise ValueError("No command provided")
        if args[0] == "mkdir":
            if len(args) != 3:
                raise ValueError("mkdir requires 2 arguments")
            return bios.fsCreateDir(args[1], args[2])
        elif args[0] == "delete":
            if len(args) != 2:
                raise ValueError("delete requires 1 argument")
            return bios.fsDelete(args[1])
        elif args[0] == "exists":
            if len(args) != 2:
                raise ValueError("exists requires 1 argument")
            return bios.fsExists(args[1])
        elif args[0] == "is_dir":
            if len(args) != 2:
                raise ValueError("is_dir requires 1 argument")
            return bios.fsIsDir(args[1])
        elif args[0] == "is_file":
            if len(args) != 2:
                raise ValueError("is_file requires 1 argument")
            return bios.fsIsFile(args[1])
        elif args[0] == "read":
            if len(args) != 2:
                raise ValueError("read requires 1 argument")
            return bios.fsRead(args[1])
        elif args[0] == "write":
            if len(args) != 3:
                raise ValueError("write requires 2 arguments")
            return bios.fsWrite(args[1], args[2])
        else:
            raise NotImplementedError(f"Filesystem command '{args[0]}' not implemented")

    def process_handler(*args):
        if len(args) == 0:
            raise ValueError("No command provided")
        elif args[0] == "spawn":
            if len(args) < 2:
                raise ValueError("spawn command requires at least 1 argument")
            spawn(args[1], args[2], args[3])

    def handle_syscall(name: str, *args) -> Any:
        if name == "gl":
            if args[0] == "call":
                return bios.getGpu()
            else:
                raise NotImplementedError(f"GL command '{args[0]}' not implemented")
        elif name == "filesystem":
            return filesystem_handler(*args)
        elif name == "process":
            return process_handler(*args)
        elif name == "power_state":
            pass
            # return power_state_handler(*args)
        elif name == "os":
            pass
            # return os_handler(*args)
        else:
            raise NotImplementedError(f"Syscall '{name}' not implemented")


    class SystemCallHandler:
        def syscall(self):
            handle_syscall()

        def __setattr__(self, key, value):
            raise AttributeError(f"Cannot set attribute {key}, class '{type(self).__name__}' is read-only")

    def spawn(path: str, args: list[str], env: dict[str, str]) -> ProcessApi:
        """
        Spawn a new process

        :param path: The path to the executable
        :param args: The arguments to pass to the executable
        :param env: The environment variables to set for the new process
        :return: The new process
        :raises OSError: If the process could not be spawned
        """

        print("Spawning process:", path)

        exposed_modules = [
            "libsystem",
        ]

        modules = {
            "path": path,
            "args": args,
            "env": env,
            "syscall": handle_syscall,
            "modules": {name: module for name, module in sys.modules.items() if name in exposed_modules},
            "syspath": [
                "/Library",
                "/User/Library",
                "/User/local/Library",
                "/VariableData/Library"
            ]
        }

        # noinspection PyUnboundLocalVariable,PyProtectedMember,PyUnresolvedReferences
        preinit = """
try:
    from typing import Any
    import sys

    sys.argv = shared["args"]
    sys.argv.insert(0, shared["path"])
    sys.env = shared["env"]
    
    path_index = 0
    for path in shared["syspath"]:
        sys.path.insert(path_index, path)
        path_index += 1
    
    import importlib
    importlib.invalidate_caches()
    
    from importlib.abc import MetaPathFinder
    from importlib.machinery import ModuleSpec
    from importlib import util as importutil
    import os
            
    class UltaOSPathFinder(MetaPathFinder):
        def find_spec(self, fullname, path=None, target=None) -> ModuleSpec | None:
        
            for p in sys.path:
                if os.path.exists(os.path.join(p, fullname.replace(".", "/") + ".py")):
                    return importutil.spec_from_file_location(fullname, os.path.join(p, fullname.replace(".", "/") + ".py"))
            return None
    
        def invalidate_caches(self):
            super().invalidate_caches()
            
        def __setattr__(self, key, value):
            raise AttributeError(f"Cannot set attribute {key}, class '{type(self).__name__}' is read-only")
    
    sys.meta_path.insert(0, UltaOSPathFinder())
    
    import libstd
    libstd.syscall = shared["syscall"]
    
    try:
        import tkinter
    except Exception as e:
        import traceback
        traceback.print_exception(e)
        # Ignore the error, as it's literally not be needed at all LMAO
    
    print("Starting process:", shared["path"])
    del sys, libstd, shared, importlib, importutil, MetaPathFinder, ModuleSpec, os
except Exception as e:
    raise e
"""

        print("Spawning process:", path)

        args.insert(0, path)

        process = bios.spawnProcess(modules, preinit, args, env)
        processes.append(process)

        print("Process spawned:", process.pid())
        return process

    spawn("/Software/dev.ultreon.ulta.test/main.py", [], {"MEOW": "true"})

    while True:
        if globals()['__shutdown_requested']:
            print("Shutting down...")
            for p in processes:
                p.kill(-1)
            break

        for p in processes:
            p.update()

    print("Shutting down...")
