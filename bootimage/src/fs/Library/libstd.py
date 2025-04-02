from typing import Any


def fstat(fd):
    """
    Get file status information for a file descriptor.

    Args:
        fd (int): File descriptor.

    Returns:
        tuple: A tuple containing the file status information.
    """


# noinspection PyShadowingNames,PyUnusedLocal
def syscall(self, syscall: str, *args) -> Any:
    raise NotImplemented  # The kernel overrides this
