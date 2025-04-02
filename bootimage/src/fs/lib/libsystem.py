import threading
from typing import Callable


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
class ProcessAPI:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def setOnExit(self, callback: Callable[[int], None]):
        # Native API
        raise NotImplemented()

    def run(self):
        # Native API
        raise NotImplemented()


# noinspection PyPep8Naming
@readonly
class __BiosAPI:
    def __init__(self):
        # Native API
        raise NotImplemented()

    def getInventoryApi(self) -> InventoryApi:
        # Native API
        raise NotImplemented()

    def getUiApi(self) -> UiApi:
        # Native API
        raise NotImplemented()

    def spawnProcess(self, modules, command: list[str], env: dict[str, str]) -> ProcessAPI:
        # Native API
        raise NotImplemented()


@readonly
def get_inventory() -> InventoryApi:
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


def spawn_process(path: str):
    import os
    if not os.path.exists(path):
        raise Exception(f"File {path} does not exist")

    return Process(path)


class PowerState:
    __STATE = None

    @classmethod
    def shutdown(cls):
        cls.__STATE = 0

    @classmethod
    def reboot(cls):
        cls.__STATE = 1


def allows(privilege: str):
    return True


def run_privileged(privilege: str, func):
    pass


# noinspection PyUnresolvedReferences
def _bootinit(bios: __BiosAPI):
    """
    This method is inaccessible after boot setup
    :param bios: the bios api
    :return: None
    """
    import sys, os, os.path

    print("Booting...")

    globals()['get_inventory'] = bios.getInventoryApi
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

    sys.modules.__setitem__ = do_not_allow_setitem
    sys.modules.__setattr__ = do_not_allow_setattr
    sys.__setattr__ = do_not_allow_setattr_modules

    print("Launching processes...")

    processes: list[Process] = []

    while True:
        if globals()['__shutdown_requested']:
            print("Shutting down...")
            for p in processes:
                p.kill()
            break

        for p in processes:
            p.update()

    print("Shutting down...")
