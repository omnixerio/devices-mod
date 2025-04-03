import libstd
import os
import sys


class Main:
    def __init__(self):
        self.texture: int = -1

    def render(self):
        pass

    def mainloop(self):
        gpu = libstd.GL()
        while True:
            if self.texture == -1:
                try:
                    with open("test.png", "rb") as f:
                        data = f.read()
                        self.texture = gpu.createTexture(data)
                except Exception as e:
                    raise Exception(f"Failed to load texture: {e}")

            if self.texture == -1:
                raise Exception("Failed to load texture")

            gpu.fill(0, 0, 50, 50, 0xFFFF0000)
            gpu.rect(10, 10, 100, 100, 0xFFFFFF00)
            gpu.fill(20, 20, 50, 50, 0xFF00FF00)

            gpu.drawTexture(self.texture, 0, 0, 16, 16)


def main():
    print("Hello, World!")

    os.chdir(os.path.dirname(__file__))

    Main().mainloop()


if __name__ == "__main__":
    main()