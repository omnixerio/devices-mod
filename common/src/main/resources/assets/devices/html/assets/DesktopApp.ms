def renderDesktop(gfx) {
    gfx.clear(51, 51, 51, 255)
}

def renderTaskbar(gfx) {
    gfx.clear(0, 0, 0, 128)
    gfx.drawString("Taskbar", 10, 10)
}

let desktop = sys.createWindow("Desktop", 0, 0, sys.screenWidth, sys.screenHeight)
desktop.setFocusable(1)
desktop.setBottomMost(1)
desktop.setDecorated(0)
desktop.setContentRenderer(renderDesktop)

let taskbar = sys.createWindow("Taskbar", 0, sys.screenHeight - 34, sys.screenWidth, 34)
taskbar.setFocusable(1)
taskbar.setTopMost(1)
taskbar.setDecorated(0)
taskbar.setTransparent(1)
taskbar.setContentRenderer(renderTaskbar)
